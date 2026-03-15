package org.homework.infrastructure.repository;

import org.hibernate.Session;
import org.hibernate.SessionFactory; // ← добавляем импорт
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.homework.infrastructure.entity.UserEntity;
import org.homework.infrastructure.exception.InfrastructureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

public class UserRepositoryImpl implements UserRepository {

    private static final Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);

    private final SessionFactory sessionFactory;

    public UserRepositoryImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session openSession() {
        return sessionFactory.openSession();
    }

    @Override
    public void create(UserEntity user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = openSession();
            transaction = session.beginTransaction();
            session.persist(user);
            transaction.commit();
            logger.info("Пользователь сохранён: {}", user.getEmail());
        } catch (ConstraintViolationException e) {
            if (transaction != null) transaction.rollback();
            logger.error("Ошибка ограничений при создании: {}", e.getMessage());
            throw new InfrastructureException("Ошибка ограничений при создании");
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Ошибка отката транзакции", rollbackEx);
                }
            }
            logger.error("Ошибка сохранения пользователя", e);
            throw new InfrastructureException("Ошибка сохранения пользователя");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public Optional<UserEntity> findById(Long id) {
        try (var session = openSession()) {
            var user = session.get(UserEntity.class, id);
            if (user != null) {
                logger.debug("Пользователь найден: {}", id);
            }
            return Optional.ofNullable(user);
        } catch (Exception e) {
            logger.error("Ошибка поиска пользователя по ID", e);
            throw new InfrastructureException("Ошибка поиска пользователя по ID");
        }
    }

    @Override
    public List<UserEntity> findAll() {
        try (var session = openSession()) {
            return session.createQuery("FROM org.homework.infrastructure.entity.UserEntity", UserEntity.class).list();
        } catch (Exception e) {
            logger.error("Ошибка поиска всех пользователей", e);
            throw new InfrastructureException("Ошибка поиска всех пользователей");
        }
    }

    @Override
    public void update(UserEntity user) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = openSession();
            transaction = session.beginTransaction();
            session.merge(user);
            transaction.commit();
            logger.info("Пользователь обновлён: {}", user.getId());
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Ошибка отката транзакции", rollbackEx);
                }
            }
            logger.error("Ошибка обновления пользователя", e);
            throw new InfrastructureException("Ошибка обновления пользователя");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public void delete(Long id) {
        Session session = null;
        Transaction transaction = null;

        try {
            session = openSession();
            transaction = session.beginTransaction();

            var user = session.get(UserEntity.class, id);
            if (user != null) {
                session.remove(user);
                transaction.commit();
                logger.info("Пользователь удалён: {}", id);
            } else {
                transaction.commit();
                logger.warn("Пользователь для удаления не найден: {}", id);
            }
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    logger.error("Ошибка отката транзакции", rollbackEx);
                }
            }
            logger.error("Ошибка удаления пользователя", e);
            throw new InfrastructureException("Ошибка удаления пользователя");
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}