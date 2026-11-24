
package codecrafters.dao;

import java.util.List;

import codecrafters.config.JPAUtil;
import codecrafters.modelo.Habitacion;
import jakarta.persistence.EntityManager;

public class HabitacionDAOImpl implements HabitacionDAO {

    // Opción 6: listar habitaciones de un hotel
    @Override
    public List<Habitacion> listarPorHotel(int idHotel) {

        if (idHotel <= 0) {
            throw new IllegalArgumentException(
                "El ID del hotel debe ser válido"
            );
        }

        EntityManager em = JPAUtil.getEntityManagerFactory()
                .createEntityManager();

        try {
            return em.createQuery(
                "SELECT h FROM Habitacion h " +
                "WHERE h.hotel.idHotel = :idHotel " +
                "ORDER BY h.numero",
                Habitacion.class
            )
            .setParameter("idHotel", idHotel)
            .getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                "Error al listar las habitaciones del hotel", e
            );

        } finally {
            em.close();
        }
    }

    // Opción 7: buscar habitaciones por capacidad
    @Override
    public List<Habitacion> buscarPorCapacidad(
            int idHotel, int numeroPersonas) {

        if (idHotel <= 0) {
            throw new IllegalArgumentException(
                "El ID del hotel debe ser válido"
            );
        }

        if (numeroPersonas <= 0) {
            throw new IllegalArgumentException(
                "El número de personas debe ser mayor que cero"
            );
        }

        EntityManager em = JPAUtil.getEntityManagerFactory()
                .createEntityManager();

        try {
            return em.createQuery(
                "SELECT h FROM Habitacion h " +
                "WHERE h.hotel.idHotel = :idHotel " +
                "AND h.capacidad >= :numeroPersonas " +
                "ORDER BY h.capacidad, h.numero",
                Habitacion.class
            )
            .setParameter("idHotel", idHotel)
            .setParameter("numeroPersonas", numeroPersonas)
            .getResultList();

        } catch (RuntimeException e) {
            throw new IllegalStateException(
                "Error al buscar habitaciones por capacidad", e
            );

        } finally {
            em.close();
        }
    }
}
