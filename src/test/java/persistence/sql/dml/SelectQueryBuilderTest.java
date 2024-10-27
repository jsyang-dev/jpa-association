package persistence.sql.dml;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;
import persistence.fixture.EntityWithoutID;
import persistence.meta.EntityColumns;

import static org.assertj.core.api.Assertions.*;

class SelectQueryBuilderTest {
    @Test
    @DisplayName("findAll 쿼리를 생성한다.")
    void findAll() {
        // given
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

        // when
        final String query = selectQueryBuilder.findAll(EntityWithId.class);

        // then
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users");
    }

    @Test
    @DisplayName("findById 쿼리를 생성한다.")
    void findById() {
        // given
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

        // when
        final String query = selectQueryBuilder.findById(EntityWithId.class, 1);

        // then
        assertThat(query).isEqualTo("SELECT id, nick_name, old, email FROM users WHERE id = 1");
    }

    @Test
    @DisplayName("@Id가 없는 엔티티로 findById 쿼리를 생성하면 예외를 발생한다.")
    void findById_exception() {
        // given
        final SelectQueryBuilder selectQueryBuilder = new SelectQueryBuilder();

        // when & then
        assertThatThrownBy(() -> selectQueryBuilder.findById(EntityWithoutID.class, 1))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(EntityColumns.NOT_ID_FAILED_MESSAGE);
    }
}
