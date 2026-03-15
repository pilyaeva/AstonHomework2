package infrastructure.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.testcontainers.containers.PostgreSQLContainer;

public class HibernateTestUtil {

    private static SessionFactory testSessionFactory;

    public static void initTestSessionFactory(PostgreSQLContainer<?> postgres) {
        if (testSessionFactory != null && !testSessionFactory.isClosed()) {
            return;
        }

        Configuration configuration = new Configuration()
                .configure()
                .setProperty("hibernate.connection.url", postgres.getJdbcUrl())
                .setProperty("hibernate.connection.username", postgres.getUsername())
                .setProperty("hibernate.connection.password", postgres.getPassword())
                .addAnnotatedClass(org.homework.infrastructure.entity.UserEntity.class);

        testSessionFactory = configuration.buildSessionFactory();
    }

    public static SessionFactory getTestSessionFactory() {
        if (testSessionFactory == null || testSessionFactory.isClosed()) {
            throw new IllegalStateException("SessionFactory не инициализирован. Сначала нужно вызвать initTestSessionFactory().");
        }
        return testSessionFactory;
    }

    public static void shutdown() {
        if (testSessionFactory != null && !testSessionFactory.isClosed()) {
            testSessionFactory.close();
        }
    }
}