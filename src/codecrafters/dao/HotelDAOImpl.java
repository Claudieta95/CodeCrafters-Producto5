package codecrafters.dao;

import java.util.List;

import codecrafters.config.JPAUtil;
import codecrafters.modelo.Hotel;
import jakarta.persistence.EntityManager;

public class HotelDAOImpl implements HotelDAO {

    @Override
    public List<Hotel> listarHoteles() {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                "SELECT h FROM Hotel h ORDER BY h.idHotel",
                Hotel.class
            ).getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error al listar los hoteles", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Hotel> listarHotelesPorCiudad(String ciudad) {
        if (ciudad == null || ciudad.isBlank()) {
            throw new IllegalArgumentException("Debes indicar una ciudad");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                "SELECT h FROM Hotel h " +
                "WHERE LOWER(h.ciudad) = :ciudad " +
                "ORDER BY h.idHotel",
                Hotel.class
            )
            .setParameter("ciudad", ciudad.trim().toLowerCase())
            .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error al buscar hoteles por ciudad", e);
        } finally {
            em.close();
        }
    }

    @Override
    public List<Hotel> listarHotelesPorEstrellas(int estrellas) {
        if (estrellas < 1 || estrellas > 5) {
            throw new IllegalArgumentException("Las estrellas deben estar entre 1 y 5");
        }

        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();

        try {
            return em.createQuery(
                "SELECT h FROM Hotel h " +
                "WHERE h.estrellas = :estrellas " +
                "ORDER BY h.nombre",
                Hotel.class
            )
            .setParameter("estrellas", estrellas)
            .getResultList();
        } catch (RuntimeException e) {
            throw new IllegalStateException("Error al buscar hoteles por estrellas", e);
        } finally {
            em.close();
        }
    }
}
