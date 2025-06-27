package at.fhtw.mbtourplanner.service;

import at.fhtw.mbtourplanner.model.Tour;
import at.fhtw.mbtourplanner.model.TourLog;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final TourService tourService;
    private final TourLogService tourLogService;

    public byte[] generateTourReportPDF(Long tourId) throws SQLException, IOException {
        Tour tour = tourService.getTourById(tourId);

        if(tour == null) {
            throw new SQLException("Tour not found with ID: " + tourId);
        }

        List<TourLog> tourlogs = tourLogService.getLogsForTour(tourId);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Titel
        Paragraph title = new Paragraph("Tour Report: " + tour.getName())
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(18)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(title);
        document.add(new Paragraph("\n"));

        // Tour Details
        Table detailsTable = new Table(new float[]{1,3});
        detailsTable.setWidth(100);
        detailsTable.addCell(new Cell().add(new Paragraph("Description:")));
        detailsTable.addCell(new Cell().add(new Paragraph(tour.getDescription())));
        detailsTable.addCell(new Cell().add(new Paragraph("From -> To:")));
        detailsTable.addCell(new Cell().add(new Paragraph(tour.getFromLocation() + " -> " + tour.getToLocation())));
        detailsTable.addCell(new Cell().add(new Paragraph("Distance (km):")));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getDistance()))));
        detailsTable.addCell(new Cell().add(new Paragraph("Estimated Time:")));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getEstimatedTime()))));
        detailsTable.addCell(new Cell().add(new Paragraph("Popularity:")));
        detailsTable.addCell(new Cell().add(new Paragraph(String.valueOf(tour.getPopularity()))));
        detailsTable.addCell(new Cell().add(new Paragraph("ChildFriendliness:")));
        detailsTable.addCell(new Cell().add(new Paragraph(String.format("%.2f", tour.getChildFriendliness()))));
        document.add(detailsTable);
        document.add(new Paragraph("\n"));

        // Tour Logs
        Paragraph subtitle = new Paragraph("Tour Logs")
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setFontSize(14);
        document.add(subtitle);

        Table table = new Table(new float[]{2, 4, 2, 2, 2});
        table.setWidth(100);

        // Header
        for(String h : List.of("Date/Time", "Comment", "Difficulty", "Total Distance", "Total Time", "Rating")) {
            table.addHeaderCell(new Cell().add(new Paragraph(h)));
        }

        // Content
        for (TourLog tourLog : tourlogs) {
            table.addCell(tourLog.getLogDateTime().toString());
            table.addCell(tourLog.getComment());
            table.addCell(String.valueOf(tourLog.getDifficulty()));
            table.addCell(String.valueOf(tourLog.getTotalDistance()));
            table.addCell(String.valueOf(tourLog.getTotalTime()));
        }
        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }
}
