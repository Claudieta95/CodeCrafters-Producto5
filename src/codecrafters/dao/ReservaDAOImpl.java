
package codecrafters.dao;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import codecrafters.config.JPAUtil;
import codecrafters.modelo.Cliente;
import codecrafters.modelo.Habitacion;
import codecrafters.modelo.Hotel;
import codecrafters.modelo.LineaReserva;
import codecrafters.modelo.Reserva;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.LockModeType;

public class ReservaDAOImpl implements ReservaDAO {

    // --------------------------------------------------
    // GUARDAR UNA RESERVA
    // --------------------------------------------------

   
@Override
public void guardar(Reserva reserva) {

    if (reserva == null) {
        throw new IllegalArgumentException(
            "La reserva no puede ser nula."
        );
    }

    if (reserva.getFechaEntrada() == null
            || reserva.getFechaSalida() == null
            || !reserva.getFechaSalida().isAfter(
                reserva.getFechaEntrada())) {
        throw new IllegalArgumentException(
            "La fecha de salida debe ser posterior a la entrada."
        );
    }

    if (reserva.getNumeroPersonas() <= 0) {
        throw new IllegalArgumentException(
            "El número de personas debe ser mayor que cero."
        );
    }

    if (reserva.getCliente() == null
            || reserva.getCliente().getIdPersona() <= 0) {
        throw new IllegalArgumentException(
            "Debes seleccionar un cliente válido."
        );
    }

    if (reserva.getLineasReserva() == null
            || reserva.getLineasReserva().isEmpty()) {
        throw new IllegalArgumentException(
            "La reserva debe contener al menos una habitación."
        );
    }

    // Ordenar las habitaciones y evitar duplicados.
    TreeSet<Integer> idsHabitaciones = new TreeSet<>();

    for (LineaReserva linea : reserva.getLineasReserva()) {

        if (linea == null || linea.getHabitacion() == null
                || linea.getHabitacion().getIdHabitacion() <= 0) {
            throw new IllegalArgumentException(
                "La reserva contiene una habitación no válida."
            );
        }

        int id = linea.getHabitacion().getIdHabitacion();

        if (!idsHabitaciones.add(id)) {
            throw new IllegalArgumentException(
                "La habitación " + id + " está duplicada."
            );
        }
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    EntityTransaction transaccion = em.getTransaction();

    try {
        transaccion.begin();

        // Bloquear las habitaciones en orden.
        Map<Integer, Habitacion> habitaciones =
                new HashMap<>();

        for (int id : idsHabitaciones) {

            Habitacion habitacion = em.find(
                Habitacion.class,
                id,
                LockModeType.PESSIMISTIC_WRITE
            );

            if (habitacion == null) {
                throw new IllegalArgumentException(
                    "No existe la habitación con ID " + id
                );
            }

            if (habitacion.getCapacidad()
                    < reserva.getNumeroPersonas()) {
                throw new IllegalArgumentException(
                    "La habitación " + id +
                    " no tiene capacidad suficiente."
                );
            }

            habitaciones.put(id, habitacion);
        }

        // Comprobar que el cliente existe.
        Cliente cliente = em.find(
            Cliente.class,
            reserva.getCliente().getIdPersona()
        );

        if (cliente == null) {
            throw new IllegalArgumentException(
                "El cliente seleccionado no existe."
            );
        }

        reserva.setCliente(cliente);

        // Comprobar que no existan reservas solapadas.
        String jpql = """
            SELECT COUNT(lr)
            FROM LineaReserva lr
            WHERE lr.habitacion.idHabitacion = :idHabitacion
              AND lr.reserva.estado <> :cancelada
              AND lr.reserva.fechaEntrada < :fechaSalida
              AND lr.reserva.fechaSalida > :fechaEntrada
            """;

        for (LineaReserva linea : reserva.getLineasReserva()) {

            int id = linea.getHabitacion().getIdHabitacion();

            Long ocupadas = em.createQuery(
                jpql, Long.class
            )
            .setParameter("idHabitacion", id)
            .setParameter("cancelada", "Cancelada")
            .setParameter("fechaEntrada",
                reserva.getFechaEntrada())
            .setParameter("fechaSalida",
                reserva.getFechaSalida())
            .getSingleResult();

            if (ocupadas > 0) {
                throw new IllegalStateException(
                    "La habitación " + id +
                    " ya está reservada para esas fechas."
                );
            }

            // Utilizar la entidad y el precio de MySQL.
            Habitacion habitacion = habitaciones.get(id);

            linea.setHabitacion(habitacion);
            linea.setPrecioNoche(
                habitacion.getPrecioNoche()
            );
            linea.setReserva(reserva);
        }

        if (reserva.getEstado() == null
                || reserva.getEstado().isBlank()) {
            reserva.setEstado("Confirmada");
        }

        // Calcular el importe total.
        reserva.calcularImporte();

        // Guardar reserva y líneas mediante JPA.
        em.persist(reserva);
        em.flush();

        // Confirmar todos los cambios.
        transaccion.commit();

    } catch (RuntimeException e) {

        if (transaccion.isActive()) {
            transaccion.rollback();
        }

        throw e;

    } finally {
        em.close();
    }
}

    // --------------------------------------------------
    // LISTAR TODAS LAS RESERVAS
    // --------------------------------------------------

    
@Override
public List<Reserva> listar() {

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        List<Reserva> reservas = em.createQuery(
            """
            SELECT DISTINCT r
            FROM Reserva r
            JOIN FETCH r.cliente
            LEFT JOIN FETCH r.lineasReserva
            ORDER BY r.idReserva
            """,
            Reserva.class
        ).getResultList();

        // Cargar las habitaciones antes de cerrar
        // el EntityManager.
        for (Reserva reserva : reservas) {
            for (LineaReserva linea :
                    reserva.getLineasReserva()) {

                if (linea.getHabitacion() != null) {
                    linea.getHabitacion().getNumero();
                }
            }
        }

        return reservas;

    } finally {
        em.close();
    }
}


    // --------------------------------------------------
    // BUSCAR RESERVAS POR CLIENTE
    // --------------------------------------------------

    
@Override
public List<Reserva> buscarPorCliente(Cliente cliente) {

    if (cliente == null || cliente.getIdPersona() <= 0) {
        throw new IllegalArgumentException(
            "El cliente no es válido."
        );
    }

    return listarReservasPorCliente(cliente.getIdPersona());
}


    
@Override
public List<Reserva> listarReservasPorCliente(int idCliente) {

    if (idCliente <= 0) {
        throw new IllegalArgumentException(
            "El ID del cliente no es válido."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        List<Reserva> reservas = em.createQuery(
            """
            SELECT DISTINCT r
            FROM Reserva r
            JOIN FETCH r.cliente
            LEFT JOIN FETCH r.lineasReserva
            WHERE r.cliente.idPersona = :idCliente
            ORDER BY r.idReserva
            """,
            Reserva.class
        )
        .setParameter("idCliente", idCliente)
        .getResultList();

        // Cargar las habitaciones antes de cerrar
        // el EntityManager.
        for (Reserva reserva : reservas) {
            for (LineaReserva linea :
                    reserva.getLineasReserva()) {

                if (linea.getHabitacion() != null) {
                    linea.getHabitacion().getNumero();
                }
            }
        }

        return reservas;

    } finally {
        em.close();
    }
}

    // --------------------------------------------------
    // BUSCAR RESERVAS POR HOTEL
    // --------------------------------------------------

@Override
public List<Reserva> buscarPorHotel(Hotel hotel) {

    if (hotel == null || hotel.getIdHotel() <= 0) {
        throw new IllegalArgumentException(
            "El hotel seleccionado no es válido."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        String jpql = """
            SELECT DISTINCT r
            FROM Reserva r
            JOIN FETCH r.cliente
            LEFT JOIN FETCH r.lineasReserva
            WHERE EXISTS (
                SELECT lr
                FROM LineaReserva lr
                WHERE lr.reserva = r
                  AND lr.habitacion.hotel.idHotel = :idHotel
            )
            ORDER BY r.idReserva
            """;

        List<Reserva> reservas = em.createQuery(
            jpql, Reserva.class
        )
        .setParameter("idHotel", hotel.getIdHotel())
        .getResultList();

        // Cargar las habitaciones antes de cerrar
        // el EntityManager.
        for (Reserva reserva : reservas) {

            for (LineaReserva linea :
                    reserva.getLineasReserva()) {

                if (linea.getHabitacion() != null) {
                    linea.getHabitacion().getNumero();
                }
            }
        }

        return reservas;

    } finally {
        em.close();
    }
}

    // --------------------------------------------------
    // BUSCAR POR FECHA DE ENTRADA
    // --------------------------------------------------

    @Override
    public List<Reserva> buscarPorFechaEntrada(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }

        return buscarPorFecha(
            "fecha_entrada", fecha
        );
    }

    // --------------------------------------------------
    // BUSCAR POR FECHA DE SALIDA
    // --------------------------------------------------

    @Override
    public List<Reserva> buscarPorFechaSalida(LocalDate fecha) {

        if (fecha == null) {
            throw new IllegalArgumentException("La fecha no puede ser nula.");
        }

        return buscarPorFecha(
            "fecha_salida", fecha
        );
    }

    
private List<Reserva> buscarPorFecha(
        String columna, LocalDate fecha) {

    if (fecha == null) {
        throw new IllegalArgumentException(
            "La fecha no puede ser nula."
        );
    }

    // Convertir los nombres de columnas SQL
    // en atributos de las entidades Java.
    String atributo;

    switch (columna) {
        case "fecha_entrada":
            atributo = "fechaEntrada";
            break;

        case "fecha_salida":
            atributo = "fechaSalida";
            break;

        default:
            throw new IllegalArgumentException(
                "La columna indicada no es válida."
            );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        String jpql =
            "SELECT DISTINCT r FROM Reserva r " +
            "JOIN FETCH r.cliente " +
            "LEFT JOIN FETCH r.lineasReserva " +
            "WHERE r." + atributo + " = :fecha " +
            "ORDER BY r.idReserva";

        List<Reserva> reservas = em.createQuery(
            jpql, Reserva.class
        )
        .setParameter("fecha", fecha)
        .getResultList();

        // Cargar las habitaciones relacionadas.
        for (Reserva reserva : reservas) {

            for (LineaReserva linea :
                    reserva.getLineasReserva()) {

                if (linea.getHabitacion() != null) {
                    linea.getHabitacion().getNumero();
                }
            }
        }

        return reservas;

    } finally {
        em.close();
    }
}
    // --------------------------------------------------
    // BUSCAR POR ESTADO
    // --------------------------------------------------

   
@Override
public List<Reserva> buscarPorEstado(String estado) {

    if (estado == null || estado.isBlank()) {
        throw new IllegalArgumentException(
            "El estado no puede estar vacío."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        String jpql = """
            SELECT DISTINCT r
            FROM Reserva r
            JOIN FETCH r.cliente
            LEFT JOIN FETCH r.lineasReserva
            WHERE r.estado = :estado
            ORDER BY r.idReserva
            """;

        List<Reserva> reservas = em.createQuery(
            jpql, Reserva.class
        )
        .setParameter("estado", estado.trim())
        .getResultList();

        // Cargar las habitaciones relacionadas
        // antes de cerrar el EntityManager.
        for (Reserva reserva : reservas) {

            for (LineaReserva linea :
                    reserva.getLineasReserva()) {

                if (linea.getHabitacion() != null) {
                    linea.getHabitacion().getNumero();
                }
            }
        }

        return reservas;

    } catch (RuntimeException e) {

        throw new IllegalStateException(
            "Error al buscar reservas por estado.", e
        );

    } finally {
        em.close();
    }
}


    // --------------------------------------------------
    // BUSCAR HABITACIONES DISPONIBLES
    // --------------------------------------------------
@Override
public List<Habitacion> buscarHabitacionesDisponibles(
        int idHotel,
        LocalDate fechaEntrada,
        LocalDate fechaSalida,
        int numeroPersonas) {

    if (idHotel <= 0 || numeroPersonas <= 0
            || fechaEntrada == null || fechaSalida == null
            || !fechaSalida.isAfter(fechaEntrada)) {

        throw new IllegalArgumentException(
            "Los datos de búsqueda de disponibilidad no son válidos."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    try {
        String jpql = """
            SELECT h FROM Habitacion h
            WHERE h.hotel.idHotel = :idHotel
              AND h.capacidad >= :numeroPersonas
              AND NOT EXISTS (
                  SELECT lr FROM LineaReserva lr
                  WHERE lr.habitacion = h
                    AND lr.reserva.estado <> :cancelada
                    AND lr.reserva.fechaEntrada < :fechaSalida
                    AND lr.reserva.fechaSalida > :fechaEntrada
              )
            ORDER BY h.precioNoche, h.numero
            """;

        return em.createQuery(jpql, Habitacion.class)
                .setParameter("idHotel", idHotel)
                .setParameter("numeroPersonas", numeroPersonas)
                .setParameter("cancelada", "Cancelada")
                .setParameter("fechaEntrada", fechaEntrada)
                .setParameter("fechaSalida", fechaSalida)
                .getResultList();

    } finally {
        em.close();
    }
}

    // --------------------------------------------------
    // MODIFICAR ESTADO
    // --------------------------------------------------
@Override
public void modificar(Reserva reserva, String nuevoEstado) {

    if (reserva == null || reserva.getIdReserva() <= 0
            || nuevoEstado == null
            || nuevoEstado.isBlank()) {

        throw new IllegalArgumentException(
            "La reserva y el nuevo estado son obligatorios."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    EntityTransaction transaccion = em.getTransaction();

    try {
        transaccion.begin();

        // Buscar la reserva existente en MySQL.
        Reserva reservaBD = em.find(
            Reserva.class,
            reserva.getIdReserva(),
            LockModeType.PESSIMISTIC_WRITE
        );

        if (reservaBD == null) {
            throw new IllegalArgumentException(
                "No existe la reserva indicada."
            );
        }

        // Modificar el estado de la entidad gestionada.
        reservaBD.setEstado(nuevoEstado.trim());

        // Hibernate actualizará la base de datos.
        transaccion.commit();

        // Actualizar también el objeto recibido.
        reserva.setEstado(reservaBD.getEstado());

    } catch (RuntimeException e) {

        if (transaccion.isActive()) {
            transaccion.rollback();
        }

        throw e;

    } finally {
        em.close();
    }
}
    // --------------------------------------------------
    // CANCELAR RESERVA
    // --------------------------------------------------
@Override
public boolean cancelar(int idReserva) {

    if (idReserva <= 0) {
        throw new IllegalArgumentException(
            "El ID de la reserva debe ser válido."
        );
    }

    EntityManager em = JPAUtil.getEntityManagerFactory()
            .createEntityManager();

    EntityTransaction transaccion = em.getTransaction();

    try {
        transaccion.begin();

        // Buscar la reserva y bloquearla durante
        // la modificación.
        Reserva reserva = em.find(
            Reserva.class,
            idReserva,
            LockModeType.PESSIMISTIC_WRITE
        );

        // Comprobar que existe y no está cancelada.
        if (reserva == null ||
                "Cancelada".equalsIgnoreCase(
                    reserva.getEstado())) {

            transaccion.commit();
            return false;
        }

        // Cambiar el estado de la reserva.
        reserva.cancelar();

        // Hibernate detecta el cambio en la entidad
        // gestionada y lo actualiza en MySQL.
        transaccion.commit();

        return true;

    } catch (RuntimeException e) {

        if (transaccion.isActive()) {
            transaccion.rollback();
        }

        throw new IllegalStateException(
            "Error al cancelar la reserva.", e
        );

    } finally {
        em.close();
    }
}
   
}
