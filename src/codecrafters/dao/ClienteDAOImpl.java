package codecrafters.dao;

import java.util.List;

import codecrafters.config.JPAUtil;
import codecrafters.modelo.Cliente;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public List<Cliente> listar() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Cliente c ORDER BY c.idPersona",
                Cliente.class
            ).getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error al listar los clientes", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void guardar(Cliente cliente) {
        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();
            em.persist(cliente);
            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw new IllegalStateException("Error al guardar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Cliente> buscar(String nombreOApellido) {
        if (nombreOApellido == null) {
            throw new IllegalArgumentException("Debes indicar un nombre o apellido");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                "SELECT c FROM Cliente c " +
                "WHERE LOWER(c.nombre) LIKE :busqueda " +
                "OR LOWER(c.apellido) LIKE :busqueda " +
                "ORDER BY c.idPersona",
                Cliente.class
            )
            .setParameter("busqueda", "%" + nombreOApellido.toLowerCase() + "%")
            .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error al buscar clientes", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void modificar(Cliente cliente) {
        if (cliente == null || cliente.getIdPersona() <= 0) {
            throw new IllegalArgumentException("El cliente debe tener un ID válido");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();

            Cliente existente = em.find(Cliente.class, cliente.getIdPersona());

            if (existente == null) {
                throw new IllegalStateException("No se ha encontrado el cliente");
            }

            existente.setNombre(cliente.getNombre());
            existente.setApellido(cliente.getApellido());
            existente.setDni(cliente.getDni());
            existente.setEmail(cliente.getEmail());
            existente.setTelefono(cliente.getTelefono());

            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw new IllegalStateException("Error al modificar el cliente", e);
        } finally {
            em.close();
        }
    }

    @Override
    public void eliminar(Cliente cliente) {
        if (cliente == null || cliente.getIdPersona() <= 0) {
            throw new IllegalArgumentException("El cliente debe tener un ID válido");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaccion = em.getTransaction();

        try {
            transaccion.begin();

            Cliente existente = em.find(Cliente.class, cliente.getIdPersona());

            if (existente == null) {
                throw new IllegalStateException("No se ha encontrado el cliente");
            }

            em.remove(existente);
            transaccion.commit();
        } catch (RuntimeException e) {
            if (transaccion.isActive()) {
                transaccion.rollback();
            }
            throw new IllegalStateException("Error al eliminar el cliente", e);
        } finally {
            em.close();
        }
    }
}
