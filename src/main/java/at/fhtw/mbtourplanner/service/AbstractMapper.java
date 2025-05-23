package at.fhtw.mbtourplanner.service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMapper <E, D> {
    public abstract D toDto(E entity) throws SQLException;
    public abstract E toEntity(D dto) throws SQLException;

    public List<D> toDto(Collection<E> entities) {
        List<D> dtos = new ArrayList<>();
        entities.forEach(entity -> {
            try {
                dtos.add(toDto(entity));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        return dtos;
    }
}
