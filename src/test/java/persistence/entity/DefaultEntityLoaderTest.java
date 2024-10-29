package persistence.entity;

import database.H2ConnectionFactory;
import domain.Order;
import domain.OrderItem;
import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;
import persistence.sql.ddl.CreateQuery;
import persistence.sql.ddl.DropQuery;
import persistence.sql.dml.SelectQuery;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class DefaultEntityLoaderTest {
    private JdbcTemplate jdbcTemplate;
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jdbcTemplate = new JdbcTemplate(H2ConnectionFactory.getConnection());
        entityManager = DefaultEntityManager.of(jdbcTemplate);

//        createTable(EntityWithId.class);
        createTable(Order.class);
    }

    @AfterEach
    void tearDown() {
        dropTable();
    }

    @Test
    @DisplayName("엔티티를 로드한다.")
    void load() {
        // given
        final EntityLoader entityLoader = new DefaultEntityLoader(jdbcTemplate, new SelectQuery());
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);
        insertData(entity);

        // when
        final EntityWithId managedEntity = entityLoader.load(entity.getClass(), entity.getId());

        // then
        assertAll(
                () -> assertThat(managedEntity).isNotNull(),
                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
                () -> assertThat(managedEntity.getIndex()).isNull()
        );
    }

    @Test
    @DisplayName("엔티티를 로드한다.")
    void load_() {
        // given
        final EntityLoader entityLoader = new DefaultEntityLoader(jdbcTemplate, new SelectQuery());
        final Order order = new Order("OrderNumber1");
        final OrderItem orderItem1 = new OrderItem("Product1", 10);
        final OrderItem orderItem2 = new OrderItem("Product2", 20);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);
        insertData(order);

//        // when
//        final EntityWithId managedEntity = entityLoader.load(entity.getClass(), entity.getId());
//
//        // then
//        assertAll(
//                () -> assertThat(managedEntity).isNotNull(),
//                () -> assertThat(managedEntity.getId()).isEqualTo(entity.getId()),
//                () -> assertThat(managedEntity.getName()).isEqualTo(entity.getName()),
//                () -> assertThat(managedEntity.getAge()).isEqualTo(entity.getAge()),
//                () -> assertThat(managedEntity.getEmail()).isEqualTo(entity.getEmail()),
//                () -> assertThat(managedEntity.getIndex()).isNull()
//        );
    }

    private void createTable(Class<?> entityType) {
        final CreateQuery createQuery = new CreateQuery(entityType, new H2Dialect());
        jdbcTemplate.execute(createQuery.create());
    }

    private void insertData(Object entity) {
        entityManager.persist(entity);
    }

    private void dropTable() {
        final DropQuery dropQuery = new DropQuery(EntityWithId.class);
        jdbcTemplate.execute(dropQuery.drop());
    }
}
