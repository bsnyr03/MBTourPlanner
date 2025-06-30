package at.fhtw.mbtourplanner.controller;

import at.fhtw.mbtourplanner.service.ReportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@Slf4j
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class ReportController {
    private final ReportService reportService;

    @GetMapping(value = "/tour/{tourId}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadTourReport(@PathVariable long tourId) throws Exception{
        log.info("Downloading tour report for tourId={}", tourId);
        byte[] pdfBytes = reportService.generateTourReportPDF(tourId);
        log.debug("Generated tour report PDF ({} bytes) for tourId={}", pdfBytes.length, tourId);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename= tour-" + tourId + "-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping(value = "/summary", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> downloadSummaryReport() throws Exception{
        log.info("Downloading summary report");
        byte[] pdfBytes = reportService.generateSummaryReportPDF();
        log.debug("Generated summary report PDF ({} bytes)", pdfBytes.length);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename= summary-report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

}
