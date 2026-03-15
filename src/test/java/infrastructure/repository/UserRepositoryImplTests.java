package infrastructure.repository;

import infrastructure.util.HibernateTestUtil;
import org.hibernate.Transaction;
import org.homework.infrastructure.entity.UserEntity;
import org.homework.infrastructure.repository.UserRepository;
import org.homework.infrastructure.repository.UserRepositoryImpl;
import org.instancio.Instancio;
import org.instancio.Select;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class UserRepositoryImplTests {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine")
            .withDatabaseName("test_db")
            .withUsername("test")
            .withPassword("test");

    protected UserRepository userRepository;

    @BeforeAll
    static void initAll() {
        HibernateTestUtil.initTestSessionFactory(postgres);
    }

    @BeforeEach
    void setUp() {
        userRepository = new UserRepositoryImpl(HibernateTestUtil.getTestSessionFactory());
        cleanDatabase();
    }

    @AfterEach
    void tearDown() {
        cleanDatabase();
    }

    protected void cleanDatabase() {
        var sessionFactory = HibernateTestUtil.getTestSessionFactory();
        try (var session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.createNativeQuery("TRUNCATE TABLE users RESTART IDENTITY CASCADE").executeUpdate();
            tx.commit();
        }
    }

    protected UserEntity saveUserDirectly(UserEntity user) {
        var sessionFactory = HibernateTestUtil.getTestSessionFactory();
        try (var session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            session.persist(user);
            tx.commit();
            return user;
        }
    }

    protected UserEntity findUserDirectly(Long id) {
        var sessionFactory = HibernateTestUtil.getTestSessionFactory();
        try (var session = sessionFactory.openSession()) {
            return session.get(UserEntity.class, id);
        }
    }

    @Nested
    @DisplayName("create. Создание пользователя")
    class CreateTests {

        @Test
        @DisplayName("Успешное создание пользователя")
        void shouldCreateUserSuccessfully() {
            var user = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();

            assertDoesNotThrow(() -> userRepository.create(user));

            var saved = findUserDirectly(user.getId());
            assertNotNull(saved);
            assertEquals(user.getEmail(), saved.getEmail());
            assertEquals(user.getName(), saved.getName());
        }
    }

    @Nested
    @DisplayName("findById. Поиск по ID")
    class FindByIdTests {

        @Test
        @DisplayName("Найти существующего пользователя")
        void shouldFindExistingUser() {
            var user = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();
            saveUserDirectly(user);

            var result = userRepository.findById(user.getId());

            assertTrue(result.isPresent());
            UserEntity found = result.get();
            assertEquals(user.getEmail(), found.getEmail());
            assertEquals(user.getName(), found.getName());
        }

        @Test
        @DisplayName("Вернуть пустой Optional для несуществующего ID")
        void shouldReturnEmptyForNonExistentId() {
            var result = userRepository.findById(999L);
            assertTrue(result.isEmpty());
        }
    }

    @Nested
    @DisplayName("findAll. Получение всех пользователей")
    class FindAllTests {

        @Test
        @DisplayName("Вернуть пустой список, если пользователей нет")
        void shouldReturnEmptyListWhenNoUsers() {
            var users = userRepository.findAll();
            assertTrue(users.isEmpty());
        }

        @Test
        @DisplayName("Вернуть всех пользователей")
        void shouldReturnAllUsers() {
            var user1 = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();

            var user2 = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();

            // when
            userRepository.create(user1);
            userRepository.create(user2);
            var users = userRepository.findAll();

            // then
            assertEquals(2, users.size());

            var names = users.stream().map(UserEntity::getName).toList();
            assertAll("Name check",
                    () -> assertTrue(names.contains(user1.getName()), "Should contain " + user1.getName()),
                    () -> assertTrue(names.contains(user2.getName()), "Should contain " + user2.getName())
            );

            var emails = users.stream().map(UserEntity::getEmail).toList();
            assertAll("Emails check",
                    () -> assertTrue(emails.contains(user1.getEmail()), "Should contain " + user1.getEmail()),
                    () -> assertTrue(emails.contains(user2.getEmail()), "Should contain " + user2.getEmail())
            );
        }
    }

    @Nested
    @DisplayName("update. Обновление пользователя")
    class UpdateTests {

        @Test
        @DisplayName("Успешное обновление существующего пользователя")
        void shouldUpdateExistingUser() {
            var user = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();
            saveUserDirectly(user);

            var newName = String.valueOf(Instancio.of(String.class));
            user.setName(newName);
            userRepository.update(user);

            var updated = findUserDirectly(user.getId());
            assertNotNull(updated);
            assertEquals(newName, updated.getName());
            assertEquals(user.getEmail(), updated.getEmail());
        }
    }

    @Nested
    @DisplayName("delete. Удаление пользователя")
    class DeleteTests {

        @Test
        @DisplayName("Удаление существующего пользователя")
        void shouldDeleteExistingUser() {
            var user = Instancio.of(UserEntity.class)
                    .ignore(Select.field(UserEntity::getId))
                    .create();
            saveUserDirectly(user);

            userRepository.delete(user.getId());

            assertTrue(userRepository.findById(user.getId()).isEmpty());
            assertTrue(userRepository.findAll().isEmpty());
        }

        @Test
        @DisplayName("Удаление несуществующего пользователя не вызывает ошибку")
        void shouldNotThrowWhenDeletingNonExistent() {
            assertDoesNotThrow(() -> userRepository.delete(999L));
        }
    }
}