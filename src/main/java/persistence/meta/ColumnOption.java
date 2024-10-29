package persistence.meta;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

public class ColumnOption {
    private final boolean isNotNull;
    private final boolean isOneToManyAssociation;
    private final FetchType fetchType;
    private final String foreignColumnName;
    private final Class<?> foreignTableType;

    public ColumnOption(Field field) {
        this.isNotNull = isNotNull(field);
        this.isOneToManyAssociation = isOneToManyAssociation(field);
        this.fetchType = getFetchType(field);
        this.foreignColumnName = getForeignColumnName(field);
        this.foreignTableType = getForeignTableType(field);
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public boolean isOneToManyAssociation() {
        return isOneToManyAssociation;
    }

    public FetchType getFetchType() {
        return fetchType;
    }

    public String getForeignColumnName() {
        return foreignColumnName;
    }

    public Class<?> getForeignTableType() {
        return foreignTableType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ColumnOption that = (ColumnOption) o;
        return isNotNull == that.isNotNull && isOneToManyAssociation == that.isOneToManyAssociation
                && fetchType == that.fetchType && Objects.equals(foreignColumnName, that.foreignColumnName)
                && Objects.equals(foreignTableType, that.foreignTableType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isNotNull, isOneToManyAssociation, fetchType, foreignColumnName, foreignTableType);
    }

    private boolean isNotNull(Field field) {
        final Column column = field.getAnnotation(Column.class);
        if (Objects.isNull(column)) {
            return false;
        }
        return !column.nullable();
    }

    private boolean isOneToManyAssociation(Field field) {
        final OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        return Objects.nonNull(oneToMany);
    }

    private FetchType getFetchType(Field field) {
        final OneToMany oneToMany = field.getAnnotation(OneToMany.class);
        if (Objects.isNull(oneToMany)) {
            return null;
        }
        return oneToMany.fetch();
    }

    private String getForeignColumnName(Field field) {
        final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (Objects.isNull(joinColumn) || Objects.isNull(joinColumn.name()) || joinColumn.name().isBlank()) {
            return null;
        }
        return joinColumn.name();
    }

    private Class<?> getForeignTableType(Field field) {
        final JoinColumn joinColumn = field.getAnnotation(JoinColumn.class);
        if (Objects.isNull(joinColumn)) {
            return null;
        }
        return getGenericType(field);
    }

    private Class<?> getGenericType(Field field) {
        Type genericType = field.getGenericType();

        if (genericType instanceof ParameterizedType parameterizedType) {
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            if (typeArguments.length > 0 && typeArguments[0] instanceof Class<?>) {
                return (Class<?>) typeArguments[0];
            }
        }
        return null;
    }
}
