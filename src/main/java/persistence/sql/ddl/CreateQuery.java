package persistence.sql.ddl;

import persistence.dialect.Dialect;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;
import persistence.meta.EntityTables;
import persistence.meta.JavaTypeConvertor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static persistence.sql.SqlConst.*;

public class CreateQuery {
    private static final String QUERY_TEMPLATE = "CREATE TABLE %s (%s)";
    private static final String NOT_NULL_COLUMN_DEFINITION = "NOT NULL";
    private static final String GENERATION_COLUMN_DEFINITION = "AUTO_INCREMENT";
    private static final String PRIMARY_KEY_COLUMN_DEFINITION = "PRIMARY KEY";

    private final EntityTable entityTable;
    private final Dialect dialect;

    public CreateQuery(Class<?> entityType, Dialect dialect) {
        this.entityTable = new EntityTable(entityType);
        this.dialect = dialect;
    }

    public List<String> create() {
        final List<String> sqls = new ArrayList<>();
        sqls.add(QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable)));

        final EntityTables entityTables = entityTable.getEntityTables();
        final EntityTable foriegnEntityTable = entityTables.getFirst();
        if (Objects.nonNull(foriegnEntityTable)) {
            sqls.add(QUERY_TEMPLATE.formatted(foriegnEntityTable.getTableName(), getColumnClause(foriegnEntityTable)));
        }

        return sqls;
    }

    private String getColumnClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(this::getColumnDefinition)
                .collect(Collectors.toList());

        final EntityTable masterEntityTable = entityTable.getMasterEntityTable();
        if (Objects.nonNull(masterEntityTable)) {
            columnDefinitions.add(masterEntityTable.getForeignColumnName() + " " +
                    getDbType(masterEntityTable.getIdColumnType(), masterEntityTable.getIdColumnLength()));
        }

        return String.join(COLUMN_DELIMITER, columnDefinitions);
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return Objects.nonNull(entityColumn) && !entityColumn.isOneToManyAssociation();
    }

    // TODO: create 쿼리 빌더 분리
    private String getColumnDefinition(EntityColumn entityColumn) {
        String columDefinition = entityColumn.getColumnName() + " " +
                getDbType(entityColumn.getType(), entityColumn.getColumnLength());

        if (entityColumn.isNotNull()) {
            columDefinition += " " + NOT_NULL_COLUMN_DEFINITION;
        }

        if (entityColumn.isGenerationValue()) {
            columDefinition += " " + GENERATION_COLUMN_DEFINITION;
        }

        if (entityColumn.isId()) {
            columDefinition += " " + PRIMARY_KEY_COLUMN_DEFINITION;
        }

        return columDefinition;
    }

    private String getDbType(Class<?> columnType, int columnLength) {
        final int sqlType = new JavaTypeConvertor().getSqlType(columnType);
        final String dbTypeName = dialect.getDbTypeName(sqlType);

        if (columnLength == 0) {
            return dbTypeName;
        }
        return "%s(%s)".formatted(dbTypeName, columnLength);
    }
}
