package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class SelectQuery {
    public String findAll(Class<?> entityType) {
        final EntityTable entityTable = new EntityTable(entityType);
        return findAll(entityTable)
                .build();
    }

    public String findById(Class<?> entityType, Object id) {
        final EntityTable entityTable = new EntityTable(entityType);
        return findAll(entityTable)
                .where(entityTable.getWhereClause(id))
                .build();
    }

    private SelectQueryBuilder findAll(EntityTable entityTable) {
        return new SelectQueryBuilder()
                .select(getColumnClause(entityTable))
                .from(entityTable.getTableName());
    }

    private String getColumnClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }
}
