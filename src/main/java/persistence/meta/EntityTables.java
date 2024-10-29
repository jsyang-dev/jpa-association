package persistence.meta;

import domain.OrderItem;
import jakarta.persistence.OneToMany;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class EntityTables {
    private static final Logger logger = LoggerFactory.getLogger(EntityTables.class);

    private final List<EntityTable> entityTables;

    public EntityTables(Class<?> entityType, EntityTable masterEntityTable) {
        this.entityTables = Arrays.stream(entityType.getDeclaredFields())
                .filter(this::isOneToMany)
                .map(field -> {
                    final EntityColumn entityColumn = new EntityColumn(field);
                    return new EntityTable(entityColumn.getForeignTableType(), masterEntityTable);
                })
                .collect(Collectors.toList());
    }

    public EntityTables(Object entity, EntityTable masterEntityTable) {
        this.entityTables = Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(this::isOneToMany)
                .flatMap(field -> {
                    final Object value = getValue(field, entity);
                    return ((List<OrderItem>) value).stream();  // TODO 제네릭 사용
                })
                .map(subEntity -> new EntityTable(subEntity, masterEntityTable))
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityTables that = (EntityTables) o;
        return Objects.equals(entityTables, that.entityTables);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(entityTables);
    }

    private boolean isOneToMany(Field field) {
        return field.isAnnotationPresent(OneToMany.class);
    }

    private Object getValue(Field field, Object entity) {
        try {
            field.setAccessible(true);
            return field.get(entity);
        } catch (IllegalAccessException e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

    public EntityTable getFirst() {
        if (Objects.isNull(entityTables) || entityTables.isEmpty()) {
            return null;
        }
        return entityTables.get(0);
    }
}
