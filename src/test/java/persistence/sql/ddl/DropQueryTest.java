package persistence.sql.ddl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.fixture.EntityWithId;

import static org.assertj.core.api.Assertions.*;

class DropQueryTest {
    @Test
    @DisplayName("drop 쿼리를 생성한다.")
    void drop() {
        // given
        final DropQuery dropQuery = new DropQuery(EntityWithId.class);

        // when
        final String query = dropQuery.drop();

        // then
        assertThat(query).isEqualTo("DROP TABLE IF EXISTS users");
    }
}
