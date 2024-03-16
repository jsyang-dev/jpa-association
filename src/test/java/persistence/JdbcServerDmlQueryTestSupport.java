package persistence;

import jdbc.JdbcTemplate;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import persistence.sql.JdbcServerTest;
import persistence.sql.TestJdbcServerExtension;
import persistence.sql.ddl.PersonV3;

import java.util.List;

@JdbcServerTest
public abstract class JdbcServerDmlQueryTestSupport extends EntityMetaDataTestSupport {

    protected static JdbcTemplate jdbcTemplate;

    @BeforeAll
    static void beforeAll() {
        jdbcTemplate = TestJdbcServerExtension.getJdbcTemplate();

        final String ddl = "create table users (\n" +
                "    id bigint generated by default as identity,\n" +
                "    nick_name varchar(255),\n" +
                "    old integer,\n" +
                "    email varchar(255) not null,\n" +
                "    primary key (id)\n" +
                ")";
        jdbcTemplate.execute(ddl);
    }

    @AfterAll
    static void afterAll() {
        List<String> tableNames = jdbcTemplate
                .query("SELECT table_name FROM information_schema.tables WHERE table_schema='PUBLIC'", resultSet -> resultSet.getString("table_name"));

        for (String tableName : tableNames) {
            jdbcTemplate.execute("DROP TABLE IF EXISTS " + tableName + " CASCADE");
        }
    }

    protected String generateUserTableStubInsertQuery(final PersonV3 person) {
        return  "insert\n" +
                "into\n" +
                "    users\n" +
                "    (nick_name, old, email, id)\n" +
                "values\n" +
                "    ('" + person.getName() + "', " + person.getAge() + ", '" + person.getEmail() + "', default)";
    }

}
