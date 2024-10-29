package persistence.meta;

import jakarta.persistence.Entity;

import java.util.List;
import java.util.Objects;

public class EntityTable {
    public static final String NOT_ENTITY_FAILED_MESSAGE = "클래스에 @Entity 애노테이션이 없습니다.";
    private static final String ALIAS_PREFIX = "_";

    private final Class<?> type;
    private final TableName tableName;
    private final EntityColumns entityColumns;
    private EntityTables entityTables;
    private EntityTable masterEntityTable;

    public EntityTable(Class<?> entityType) {
        validate(entityType);
        this.type = entityType;
        this.tableName = new TableName(entityType);
        this.entityColumns = new EntityColumns(entityType);
        this.entityTables = new EntityTables(entityType, this);
    }

    public EntityTable(Object entity) {
        validate(entity.getClass());
        this.type = entity.getClass();
        this.tableName = new TableName(entity.getClass());
        this.entityColumns = new EntityColumns(entity);
        this.entityTables = new EntityTables(entity, this);
    }

    public EntityTable(Class<?> entityType, EntityTable masterEntityTable) {
        validate(entityType);
        this.type = entityType;
        this.tableName = new TableName(entityType);
        this.entityColumns = new EntityColumns(entityType);
        this.masterEntityTable = masterEntityTable;
    }

    public EntityTable(Object entity, EntityTable masterEntityTable) {
        validate(entity.getClass());
        this.type = entity.getClass();
        this.tableName = new TableName(entity.getClass());
        this.entityColumns = new EntityColumns(entity);
        this.masterEntityTable = masterEntityTable;
    }

    public String getTableName() {
        return tableName.value();
    }

    public List<EntityColumn> getEntityColumns() {
        return entityColumns.getEntityColumns();
    }

    public EntityTables getEntityTables() {
        return entityTables;
    }

    public EntityTable getMasterEntityTable() {
        return masterEntityTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityTable that = (EntityTable) o;
        return Objects.equals(type, that.type) && Objects.equals(tableName, that.tableName)
                && Objects.equals(entityColumns, that.entityColumns) && Objects.equals(entityTables, that.entityTables)
                && Objects.equals(masterEntityTable, that.masterEntityTable);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, tableName, entityColumns, entityTables, masterEntityTable);
    }

    public String getWhereClause() {
        return getIdEntityColumn().getColumnName() + " = " + getIdEntityColumn().getValueWithQuotes();
    }

    public String getWhereClause(Object id) {
        return getIdEntityColumn().getColumnName() + " = " + getValueWithQuotes(id);
    }

    public String getIdColumnName() {
        return getIdEntityColumn().getColumnName();
    }

    public Class<?> getIdColumnType() {
        return getIdEntityColumn().getType();
    }

    public int getIdColumnLength() {
        return getIdEntityColumn().getColumnLength();
    }

    public Object getIdValue() {
        return getIdEntityColumn().getValue();
    }

    public String getIdValueWithQuotes() {
        return getIdEntityColumn().getValueWithQuotes();
    }

    public boolean isIdGenerationFromDatabase() {
        return getIdEntityColumn().isIdGenerationFromDatabase();
    }

    public EntityKey toEntityKey() {
        return new EntityKey(type, getIdValue());
    }

    public String getValueWithQuotes(Object id) {
        if (id.getClass() == String.class) {
            return "'%s'".formatted(String.valueOf(id));
        }
        return String.valueOf(id);
    }

    public int getColumnCount() {
        return getEntityColumns().size();
    }

    public EntityColumn getEntityColumn(int index) {
        return getEntityColumns().get(index);
    }

    public boolean isOneToManyAssociation() {
        final EntityColumn foreignEntityColumn = entityColumns.getForeignEntityColumn();
        if (Objects.isNull(foreignEntityColumn)) {
            return false;
        }
        return foreignEntityColumn.isOneToManyAssociation();
    }

    public Class<?> getForeignTableType() {
        return entityColumns.getForeignEntityColumn().getForeignTableType();
    }

    public String getForeignColumnName() {
        return entityColumns.getForeignEntityColumn().getColumnName();
    }

    public String getAlias() {
        return ALIAS_PREFIX + getTableName();
    }

    public EntityColumn getIdEntityColumn() {
        return entityColumns.getIdEntityColumn();
    }

    private void validate(Class<?> entityType) {
        if (!entityType.isAnnotationPresent(Entity.class)) {
            throw new IllegalArgumentException(NOT_ENTITY_FAILED_MESSAGE);
        }
    }
}
