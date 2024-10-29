package persistence.sql.dml;

import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;

import java.util.List;
import java.util.stream.Collectors;

public class InsertQuery {
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    public String insert(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable), getValueClause(entityTable));
    }

    @SuppressWarnings("rawtypes")
    public String insert(Object entity, Object parentEntity) {
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTable parentEntityTable = new EntityTable(parentEntity);
        final EntityColumn joinEntityColumn = parentEntityTable.getJoinEntityColumn();
        final List joinEntities = (List) joinEntityColumn.getValue();

        if (joinEntities.contains(entity)) {
            return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable, parentEntityTable), getValueClause(entityTable, parentEntityTable));
        }
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable), getValueClause(entityTable));
    }

    private String getColumnClause(EntityTable entityTable) {
        final List<String> columnDefinitions = entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());

        return String.join(", ", columnDefinitions);
    }

    private String getValueClause(EntityTable entityTable) {
        final List<String> values = entityTable.getEntityColumns()
                .stream()
                .filter(this::isAvailable)
                .map(EntityColumn::getValueWithQuotes)
                .collect(Collectors.toList());

        return String.join(", ", values);
    }

    private String getColumnClause(EntityTable entityTable, EntityTable parentEntityTable) {
        final String columnClause = getColumnClause(entityTable);
        return columnClause + ", " + parentEntityTable.getJoinColumnName();
    }

    private String getValueClause(EntityTable entityTable, EntityTable parentEntityTable) {
        final String valueClause = getValueClause(entityTable);
        return valueClause + ", " + parentEntityTable.getIdValue();
    }

    private boolean isAvailable(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue() && !entityColumn.isOneToManyAssociation();
    }
}
