package at.fhtw.mbtourplanner;

import at.fhtw.mbtourplanner.controller.ReportController;
import at.fhtw.mbtourplanner.service.ReportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;

@ExtendWith(MockitoExtension.class)
class ReportControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReportService reportService;

    @InjectMocks
    private ReportController reportController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(reportController).build();
    }

    @Test
    void downloadTourReport_returnsPdfWithCorrectHeadersAndBody() throws Exception {
        byte[] pdfBytes = {0x25, 0x50, 0x44, 0x46};
        BDDMockito.given(reportService.generateTourReportPDF(123L)).willReturn(pdfBytes);

        mockMvc.perform(get("/api/reports/tour/123"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename= tour-123-report.pdf"))
                .andExpect(content().bytes(pdfBytes));
    }

    @Test
    void downloadSummaryReport_returnsPdfWithCorrectHeadersAndBody() throws Exception {
        byte[] pdfBytes = {0x25, 0x50, 0x44, 0x46, 0x2D}; // "%PDF-"
        BDDMockito.given(reportService.generateSummaryReportPDF()).willReturn(pdfBytes);

        mockMvc.perform(get("/api/reports/summary"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename= summary-report.pdf"))
                .andExpect(content().bytes(pdfBytes));
    }
}