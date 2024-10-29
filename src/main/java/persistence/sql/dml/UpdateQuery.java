package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

import static persistence.sql.QueryConst.*;

public class UpdateQuery {
    private static final String QUERY_TEMPLATE = "UPDATE %s SET %s WHERE %s";

    public String update(Object entity, List<EntityColumn> entityColumns) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getSetClause(entityColumns),
                entityTable.getWhereClause());
    }

    private String getSetClause(List<EntityColumn> entityColumns) {
        final List<String> columnDefinitions = entityColumns.stream()
                .map(this::getSetClause)
                .collect(Collectors.toList());

        return String.join(COLUMN_DELIMITER, columnDefinitions);
    }

    private String getSetClause(EntityColumn entityColumn) {
        return entityColumn.getColumnName() + EQUAL + entityColumn.getValueWithQuotes();
    }
}
