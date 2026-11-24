package codecrafters.config;

import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public final class JPAUtil {

    private static EntityManagerFactory entityManagerFactory;

    private JPAUtil() {
    }

    public static synchronized EntityManagerFactory getEntityManagerFactory() {
        if (entityManagerFactory == null || !entityManagerFactory.isOpen()) {

            String password = System.getenv("MYSQL_PASSWORD");

            if (password == null || password.isBlank()) {
                throw new IllegalStateException(
                    "Falta configurar la variable de entorno MYSQL_PASSWORD"
                );
            }

            Map<String, Object> propiedades = new HashMap<>();
            propiedades.put("jakarta.persistence.jdbc.password", password);

            entityManagerFactory = Persistence.createEntityManagerFactory(
                "codecraftersPU",
                propiedades
            );
        }

        return entityManagerFactory;
    }

    public static synchronized void cerrar() {
        if (entityManagerFactory != null && entityManagerFactory.isOpen()) {
            entityManagerFactory.close();
        }
    }
}
