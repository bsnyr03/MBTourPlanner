// src/test/java/at/fhtw/mbtourplanner/service/TourMapperTest.java
package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.repository.TourEntity;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import static org.assertj.core.api.Assertions.*;

class TourMapperTest {
    private final TourMapper mapper = new TourMapper();

    @Test
    void toDto_mapsEntityToDto() throws SQLException {
        TourEntity entity = new TourEntity(1L, "name", "desc", "from", "to", "type", 10.0, "1:00:00", "url");
        Tour dto = mapper.toDto(entity);
        assertThat(dto.getName()).isEqualTo("name");
    }

    @Test
    void toDtoList_mapsList() throws SQLException {
        List<TourEntity> entities = List.of(new TourEntity(1L, "n", "d", "f", "t", "ty", 1.0, "1", "u"));
        List<Tour> dtos = mapper.toDto(entities);
        assertThat(dtos).hasSize(1);
    }
}