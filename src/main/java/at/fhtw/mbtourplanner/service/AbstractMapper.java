package at.fhtw.mbtourplanner.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMapper <E, D> {
    public abstract D toDto(E entity);
    public abstract E toEntity(D dto);

    public List<D> toDto(Collection<E> entities) {
        List<D> dtos = new ArrayList<>();
        entities.forEach(entity -> {
            dtos.add(toDto(entity));
        });
        return dtos;
    }
}
