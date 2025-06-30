package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.model.TourLog;
import at.fhtw.mbtourplanner.repository.TourRepository;
import at.fhtw.mbtourplanner.repository.TourEntity;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final TourService tourService;
    private final TourLogService tourLogService;
    private final TourRepository tourRepository;

    public byte[] generateTourReportPDF(Long tourId) throws Exception {
        log.info("Starting generation of tour report PDF for tourId={}", tourId);
        Tour tour = tourService.getTourById(tourId);

        if(tour == null) {
            throw new SQLException("Tour not found with ID: " + tourId);
        }

        List<TourLog> tourlogs = tourLogService.getLogsForTour(tourId);
        log.debug("Fetched tour: {} with {} logs", tour, tourlogs.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.setMargins(20, 20, 20, 20);

        // Titel
        Paragraph title = new Paragraph("Tour Report: " + tour.getName())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);

        document.add(new LineSeparator(new SolidLine()));

        document.add(new Paragraph("\n"));

        // Tour Details
        Table detailsTable = new Table(UnitValue.createPercentArray(new float[]{1,3})).useAllAvailableWidth();
        detailsTable.addCell(new Cell().add(new Paragraph("Description").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(tour.getDescription()))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("From → To").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(
                        tour.getFromLocation() + " → " + tour.getToLocation()))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("Transport").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(tour.getTransportType()))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("Distance (km)").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance())))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("Est. Time").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getEstimatedTime())))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("Popularity").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getPopularity())))
                .setBorder(Border.NO_BORDER).setPadding(4));

        detailsTable.addCell(new Cell().add(new Paragraph("Child-Friendliness").setBold())
                .setBorder(Border.NO_BORDER).setPadding(4));
        detailsTable.addCell(new Cell().add(new Paragraph(
                        String.format("%.2f", tour.getChildFriendliness())))
                .setBorder(Border.NO_BORDER).setPadding(4));

        document.add(detailsTable);
        document.add(new Paragraph("\n"));

        TourEntity entity = tourRepository.findById(tourId)
            .orElseThrow(() -> new SQLException("Tour not found with ID: " + tourId));
        byte[] imgData = entity.getRouteImageData();
        if (imgData != null) {
            ImageData img = ImageDataFactory.create(imgData);
            document.add(new Image(img));
        }

        document.add(new Paragraph("\n"));

        // Tour Logs
        Paragraph subtitle = new Paragraph("Tour Logs")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(14)
                .setMarginTop(10);
        document.add(subtitle);

        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 4, 2, 2, 2, 2}))
                .useAllAvailableWidth();

        // Header
        for(String h : List.of("Date/Time", "Comment", "Difficulty", "Total Distance", "Total Time", "Rating")) {
            table.addHeaderCell(new Cell()
                    .add(new Paragraph(h))
                    .setBold()
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setPadding(4));
        }

        // Content
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        for (TourLog l : tourlogs) {
            table.addCell(new Cell()
                    .add(new Paragraph(l.getLogDateTime().format(dtf)))
                    .setPadding(3));
            table.addCell(new Cell()
                    .add(new Paragraph(l.getComment()))
                    .setPadding(3));
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(l.getDifficulty())))
                    .setPadding(3));
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(l.getTotalDistance())))
                    .setPadding(3));
            table.addCell(new Cell()
                    .add(new Paragraph(l.getTotalTime().toString()))
                    .setPadding(3));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(l.getRating()))).setPadding(3));


        }

        document.add(table);
        log.info("Generated tour report PDF for tourId={} ({} bytes)", tourId, outputStream.size());
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] generateSummaryReportPDF() throws Exception {
        log.info("Starting generation of summary report PDF");
        List<Tour> tours = tourService.getAllTours();
        log.debug("Fetched {} tours for summary report", tours.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        Paragraph title = new Paragraph("Summary Tour Report")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);
        document.add(new Paragraph("\n"));

        Table table = new Table(UnitValue.createPercentArray(new float[]{4, 4, 4, 4}))
                .useAllAvailableWidth();

        // Header
        for (String h : List.of("Tourname", "Average Time", "Average Distance", "Average Rating")){
            table.addHeaderCell(new Cell().add(new Paragraph(h)));
        }

        // Rows
        for (Tour tour : tours) {
            List<TourLog> logs = tourLogService.getLogsForTour(tour.getId());

            double avgDistance = logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0);
            double avgRating = logs.stream().mapToDouble(TourLog::getRating).average().orElse(0);

            long avgTimeMinutes = (long) logs.stream()
                .mapToLong(log -> log.getTotalTime().toMinutes())
                .average()
                .orElse(0);
            Duration avgTime = Duration.ofMinutes(avgTimeMinutes);

            String avgTimeFormatted = String.format("%02d:%02d:%02d",
                avgTime.toHours(),
                avgTime.toMinutesPart(),
                avgTime.toSecondsPart()
            );

            table.addCell(new Cell().add(new Paragraph(tour.getName())));
            table.addCell(new Cell().add(new Paragraph(avgTimeFormatted)));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgDistance))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgRating))));
        }

        document.add(table);
        log.info("Generated summary report PDF ({} bytes)", outputStream.size());
        document.close();
        return outputStream.toByteArray();
    }


}
