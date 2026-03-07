package org.homework.infrastructure.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.homework.infrastructure.entity.UserEntity;
import org.homework.infrastructure.exception.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HibernateUtil {

    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            var configuration = new Configuration();

            configuration.addAnnotatedClass(UserEntity.class);

            configuration.configure("hibernate.cfg.xml");

            var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySettings(configuration.getProperties())
                .build();

            logger.info("Соединение с БД установлено");
            return configuration.buildSessionFactory(serviceRegistry);
        } catch (Throwable ex) {
            logger.error("Соединение с БД не установлено", ex);
            throw new InfrastructureException("Ошибка подключения к БД");
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Соединение с БД закрыто");
        }
    }
}