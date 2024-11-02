package persistence.entity.proxy;

import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import persistence.entity.EntityLoader;
import persistence.meta.EntityTable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LazyLoadingHandler<T> implements InvocationHandler {
    private static final String NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE = "@OneToMany 연관관계이면서 LAZY 타입인 컬럼이 존재하지 않습니다.";
    private final EntityLoader entityLoader;
    private final Class<T> entityType;
    private final Object parentEntity;

    public LazyLoadingHandler(EntityLoader entityLoader, Class<T> entityType, Object parentEntity) {
        this.entityLoader = entityLoader;
        this.entityType = entityType;
        this.parentEntity = parentEntity;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Field associationField = Arrays.stream(parentEntity.getClass().getDeclaredFields())
                .filter(this::isOneToManyAndLazy)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NO_ONE_TO_ONE_LAZY_FAILED_MESSAGE));

        if (Objects.equals(method.getName(), "get")) {
            final EntityTable parentEntityTable = new EntityTable(parentEntity);

            final List<T> loadedList = entityLoader.loadCollection(entityType, parentEntityTable.getJoinColumnName(),
                    parentEntityTable.getIdValue());

            associationField.setAccessible(true);
            associationField.set(parentEntity, loadedList);

            return method.invoke(loadedList, args);
        }

        associationField.setAccessible(true);
        final Object associationFieldValue = associationField.get(parentEntity);
        return method.invoke(associationFieldValue, args);
    }

    private boolean isOneToManyAndLazy(Field field) {
        final OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (Objects.isNull(oneToMany)) {
            return false;
        }
        return oneToMany.fetch() == FetchType.LAZY;
    }
}
