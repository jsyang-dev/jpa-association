package persistence.sql.dml;

import domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;

import static org.assertj.core.api.Assertions.*;

class InsertQueryTest {
    @Test
    @DisplayName("insert 쿼리를 생성한다.")
    void insert() {
        // given
        final InsertQuery insertQuery = new InsertQuery();
        final EntityWithId entity = new EntityWithId("Jaden", 30, "test@email.com", 1);

        // when
        final String sql = insertQuery.insert(entity);

        // then
        assertThat(sql).isEqualTo("INSERT INTO users (nick_name, old, email) VALUES ('Jaden', 30, 'test@email.com')");
    }

    @Test
    @DisplayName("연관관계가 존재하는 엔티티로 insert 쿼리를 생성한다.")
    void insert_withAssociation() {
        // given
        final InsertQuery insertQuery = new InsertQuery();
        final Order order = new Order("OrderNumber1");

        // when
        final String sql = insertQuery.insert(order);

        // then
        assertThat(sql).isEqualTo("INSERT INTO orders (orderNumber) VALUES ('OrderNumber1')");
    }
}
