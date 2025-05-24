package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.service.TourLogService;
import jakarta.persistence.NamedStoredProcedureQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/tours/logs")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class TourLogController {

    private final TourLogService tourLogService;

}
