package jdbc.mapping;

import jdbc.InstanceFactory;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
public class SimpleFieldMapping implements FieldMapping {
    @Override
    public <T> boolean supports(Class<T> entityType) {
        return getJoinColumnType(entityType) == Object.class;
    }

    @Override
    public <T> T getRow(ResultSet resultSet, Class<T> entityType) throws SQLException, IllegalAccessException {
        final List<Field> fields = getPersistentFields(entityType);
        final T entity = new InstanceFactory<>(entityType).createInstance();
        for (int i = 0; i < fields.size(); i++) {
            mapField(resultSet, entity, fields.get(i), i + 1);
        }
        return entity;
    }
}
