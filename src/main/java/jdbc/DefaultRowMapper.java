package jdbc;

import jdbc.mapping.AssociationMapping;
import jdbc.mapping.Mapping;
import jdbc.mapping.SimpleMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DefaultRowMapper<T> implements RowMapper<T> {
    private static final Logger logger = LoggerFactory.getLogger(DefaultRowMapper.class);

    private static final String NOT_SUPPORTS_MAPPING_MESSAGE = "지원하는 mapping이 존재하지 않습니다.";

    private final List<Mapping> mappings = new ArrayList<>();
    private final Class<T> entityType;

    public DefaultRowMapper(Class<T> entityType) {
        this.entityType = entityType;

        initMappings();
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException, IllegalAccessException {
        final Mapping mapping = getMapping();
        return mapping.getRow(resultSet, entityType);
    }

    private Mapping getMapping() {
        return mappings.stream()
                .filter(mapping -> mapping.supports(entityType))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(NOT_SUPPORTS_MAPPING_MESSAGE));
    }

    private void initMappings() {
        mappings.add(new SimpleMapping());
        mappings.add(new AssociationMapping());
    }
}
