package persistence.sql.dml;

import org.jetbrains.annotations.NotNull;
import persistence.meta.EntityColumn;
import persistence.meta.EntityTable;
import persistence.meta.EntityTables;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static persistence.sql.SqlConst.*;

public class InsertQuery {
    private static final String QUERY_TEMPLATE = "INSERT INTO %s (%s) VALUES (%s)";

    public String insert(Object entity) {
        final EntityTable entityTable = new EntityTable(entity);
        return QUERY_TEMPLATE.formatted(entityTable.getTableName(), getColumnClause(entityTable), getValueClause(entityTable));
    }

    public List<String> insertChildren(Object entity) {
        final List<String> sqls = new ArrayList<>();
        final EntityTable entityTable = new EntityTable(entity);
        final EntityTables entityTables = entityTable.getEntityTables();

        for (EntityTable childEntityTable : entityTables.getEntityTables()) {
            sqls.add(QUERY_TEMPLATE.formatted(childEntityTable.getTableName(),
                    getColumnClauseChildren(childEntityTable), getValueClauseChildren(childEntityTable)));
        }
        return sqls;
    }

    private String getColumnClause(EntityTable entityTable) {
        return String.join(COLUMN_DELIMITER, getColumnDefinitions(entityTable));
    }

    private String getValueClause(EntityTable entityTable) {
        return String.join(COLUMN_DELIMITER, getColumnValues(entityTable));
    }

    private String getColumnClauseChildren(EntityTable entityTable) {
        final EntityTable masterEntityTable = entityTable.getMasterEntityTable();
        final List<String> columnDefinitions = getColumnDefinitions(entityTable);

        if (Objects.nonNull(masterEntityTable)) {
            columnDefinitions.add(masterEntityTable.getForeignColumnName());
        }

        return String.join(COLUMN_DELIMITER, columnDefinitions);
    }

    private String getValueClauseChildren(EntityTable entityTable) {
        final EntityTable masterEntityTable = entityTable.getMasterEntityTable();
        final List<String> columnValues = getColumnValues(entityTable);

        if (Objects.nonNull(masterEntityTable)) {
            columnValues.add(String.valueOf(masterEntityTable.getIdValue()));
        }

        return String.join(COLUMN_DELIMITER, columnValues);
    }

    private List<String> getColumnDefinitions(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getColumnName)
                .collect(Collectors.toList());
    }

    @NotNull
    private List<String> getColumnValues(EntityTable entityTable) {
        return entityTable.getEntityColumns()
                .stream()
                .filter(this::isNotNeeded)
                .map(EntityColumn::getValueWithQuotes)
                .collect(Collectors.toList());
    }

    private boolean isNotNeeded(EntityColumn entityColumn) {
        return !entityColumn.isGenerationValue() && !entityColumn.isOneToManyAssociation();
    }
}
