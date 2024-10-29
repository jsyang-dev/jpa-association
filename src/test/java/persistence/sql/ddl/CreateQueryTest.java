package persistence.sql.ddl;

import domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateQueryTest {
    @Test
    @DisplayName("create 쿼리를 생성한다.")
    void create() {
        // given
        final Dialect dialect = new H2Dialect();
        final CreateQuery createQuery = new CreateQuery(EntityWithId.class, dialect);

        // when
        final List<String> sqls = createQuery.create();

        // then
        assertThat(sqls.get(0)).isEqualTo(
                "CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(20), old INTEGER, email VARCHAR(255) NOT NULL)");
    }

    @Test
    @DisplayName("연관관계가 존재하는 엔티티로 create 쿼리를 생성한다.")
    void create_withOneToMany() {
        // given
        final Dialect dialect = new H2Dialect();
        final CreateQuery createQuery = new CreateQuery(Order.class, dialect);

        // when
        final List<String> sqls = createQuery.create();

        // then
        assertAll(
                () -> assertThat(sqls).hasSize(2),
                () -> assertThat(sqls.get(0)).isEqualTo(
                        "CREATE TABLE orders (id BIGINT AUTO_INCREMENT PRIMARY KEY, orderNumber VARCHAR(255))"),
                () -> assertThat(sqls.get(1)).isEqualTo(
                        "CREATE TABLE order_items (id BIGINT AUTO_INCREMENT PRIMARY KEY, product VARCHAR(255), quantity INTEGER, order_id BIGINT)")
        );
    }
}
