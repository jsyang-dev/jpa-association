package persistence.sql.ddl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import persistence.dialect.Dialect;
import persistence.dialect.H2Dialect;
import persistence.fixture.EntityWithId;

import static org.assertj.core.api.Assertions.*;

class CreateQueryTest {
    @Test
    @DisplayName("create 쿼리를 생성한다.")
    void create() {
        // given
        final Dialect dialect = new H2Dialect();
        final CreateQuery createQuery = new CreateQuery(EntityWithId.class, dialect);

        // when
        final String query = createQuery.create();

        // then
        assertThat(query).isEqualTo(
                "CREATE TABLE users (id BIGINT AUTO_INCREMENT PRIMARY KEY, nick_name VARCHAR(20), old INTEGER, email VARCHAR(255) NOT NULL)");
    }
}
