package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.model.TourLog;
import com.itextpdf.io.font.constants.StandardFonts;
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
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TourService tourService;
    private final TourLogService tourLogService;

    public byte[] generateTourReportPDF(Long tourId) throws Exception {
        Tour tour = tourService.getTourById(tourId);

        if(tour == null) {
            throw new SQLException("Tour not found with ID: " + tourId);
        }

        List<TourLog> tourlogs = tourLogService.getLogsForTour(tourId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.setMargins(20, 20, 20, 20);

        if(tour.getRouteImageUrl() != null && !tour.getRouteImageUrl().isEmpty()) {
            try{
                Image img = new Image(ImageDataFactory.create(tour.getRouteImageUrl()))
                        .scaleToFit(500, 300)
                        .setHorizontalAlignment(HorizontalAlignment.CENTER);
                document.add(img);
                document.add(new Paragraph("\n"));
            }catch (Exception ignore){}
        }

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
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] generateSummaryReportPDF() throws Exception {
        List<Tour> tours = tourService.getAllTours();

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
            List <TourLog> logs = tourLogService.getLogsForTour(tour.getId());

            double avgTime = logs.stream().mapToDouble(log -> log.getTotalTime().toMinutes()).average().orElse(0);
            double avgDistance = logs.stream().mapToDouble(TourLog::getTotalDistance).average().orElse(0);
            double avgRating = logs.stream().mapToDouble(TourLog::getRating).average().orElse(0);

            table.addCell(new Cell().add(new Paragraph(tour.getName())));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f min", avgTime))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgDistance))));
            table.addCell(new Cell().add(new Paragraph(String.format("%.2f", avgRating))));
        }

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }


}
