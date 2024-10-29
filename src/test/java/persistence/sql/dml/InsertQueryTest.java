package persistence.sql.dml;

import domain.Order;
import domain.OrderItem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

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
        final Order order = new Order(1L, "OrderNumber1");
        final OrderItem orderItem1 = new OrderItem("Product1", 10);
        final OrderItem orderItem2 = new OrderItem("Product2", 20);
        order.addOrderItem(orderItem1);
        order.addOrderItem(orderItem2);

        // when
        final List<String> sqls = insertQuery.insertChildren(order);

        // then
        assertAll(
                () -> assertThat(sqls).hasSize(2),
                () -> assertThat(sqls.get(0)).isEqualTo(
                        "INSERT INTO order_items (product, quantity, order_id) VALUES ('Product1', 10, 1)"),
                () -> assertThat(sqls.get(1)).isEqualTo(
                        "INSERT INTO order_items (product, quantity, order_id) VALUES ('Product2', 20, 1)")
        );
    }
}
