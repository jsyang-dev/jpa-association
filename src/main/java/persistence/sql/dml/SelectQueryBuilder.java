package persistence.sql.dml;

public class SelectQueryBuilder {
    private static final String SELECT_CLAUSE = "SELECT";
    private static final String FROM_CLAUSE = "FROM";
    private static final String WHERE_CLAUSE = "WHERE";
    private static final String BLANK = " ";

    private final StringBuilder sql;

    public SelectQueryBuilder() {
        this.sql = new StringBuilder();
    }

    public SelectQueryBuilder select(String clause) {
        sql.append(SELECT_CLAUSE)
                .append(BLANK)
                .append(clause)
                .append(BLANK);
        return this;
    }

    public SelectQueryBuilder from(String clause) {
        sql.append(FROM_CLAUSE)
                .append(BLANK)
                .append(clause)
                .append(BLANK);
        return this;
    }

    public SelectQueryBuilder where(String clause) {
        sql.append(WHERE_CLAUSE)
                .append(BLANK)
                .append(clause)
                .append(BLANK);
        return this;
    }

    public String build() {
        return sql.toString().trim();
    }
}
