package codecrafters.config;

import jakarta.persistence.EntityManager;

public class PruebaJPA {

    public static void main(String[] args) {

        try {
            EntityManager em = JPAUtil.getEntityManagerFactory()
                                      .createEntityManager();

            try {
                Object resultado = em.createNativeQuery("SELECT 1")
                                     .getSingleResult();

                System.out.println("CONEXIÓN JPA CORRECTA");
                System.out.println("Resultado de la consulta: " + resultado);

            } finally {
                em.close();
            }

        } catch (Exception e) {
            System.err.println("ERROR DE CONEXIÓN JPA: " + e.getMessage());
            e.printStackTrace();

        } finally {
            JPAUtil.cerrar();
        }
    }
}
