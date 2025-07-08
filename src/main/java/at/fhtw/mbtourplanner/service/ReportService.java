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
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.properties.HorizontalAlignment;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.constraintvalidators.hv.URLValidator;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.SQLException;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    private final TourService tourService;
    private final TourLogService tourLogService;
    private final TourRepository tourRepository;
    private final OpenRouteService openRouteService;

    public byte[] generateTourReportPDF(Long tourId) throws Exception {
        log.info("Starting generation of tour report PDF for tourId={}", tourId);
        Tour tour = tourService.getTourById(tourId);

        if(tour == null) {
            throw new SQLException("Tour not found with ID: " + tourId);
        }


        var routeInfo = openRouteService.getRouteInfo("foot-walking",
                List.of(
                        List.of(tour.getFromLon(), tour.getFromLat()),
                        List.of(tour.getToLon(), tour.getToLat())
                ));

        List<List<Double>> coords = (List<List<Double>>) routeInfo.get("route");

        List<double[]> routeCoords = new ArrayList<>();

        for(List<Double> coord : coords) {
            routeCoords.add(new double[]{coord.get(0), coord.get(1)});
        }


        List<TourLog> tourlogs = tourLogService.getLogsForTour(tourId);
        log.debug("Fetched tour: {} with {} logs", tour, tourlogs.size());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        document.setMargins(20, 20, 20, 20);

        // Image
        TourEntity entity = tourRepository.findById(tourId)
                .orElseThrow(() -> new SQLException("Tour not found with ID: " + tourId));
        byte[] imgData = getStitchedOSMTiles(
                entity.getFromLat(), entity.getFromLon(),
                entity.getToLat(), entity.getToLon(),
                routeCoords
        );

        ImageData img = ImageDataFactory.create(imgData);
        document.add(new Image(img)
                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                .setWidth(UnitValue.createPercentValue(100))
                .setHeight(UnitValue.createPercentValue(30)));
        document.add(new Paragraph("\n"));

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

    private BufferedImage downloadTile(int zoom, int x, int y) throws IOException, InterruptedException {
        String url = String.format("https://tile.openstreetmap.org/%d/%d/%d.png", zoom, x, y);
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", "mbtourplanner/1.0 (barisenyer@gmail.com)")
                .build();
        HttpResponse<byte[]> response = client.send(request, HttpResponse.BodyHandlers.ofByteArray());
        if (response.statusCode() != 200) {
            throw new IOException("Failed to download tile: " + url + " (HTTP " + response.statusCode() + ")");
        }
        return ImageIO.read(new java.io.ByteArrayInputStream(response.body()));
    }


    private byte[] getStitchedOSMTiles(double fromLat, double fromLon, double toLat, double toLon, List<double[]> routeCoords) throws IOException, InterruptedException {
        int zoom = 14;
        int tileSize = 256;

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        for (double[] coord : routeCoords) {
            minLat = Math.min(minLat, coord[0]);
            maxLat = Math.max(maxLat, coord[0]);
            minLon = Math.min(minLon, coord[1]);
            maxLon = Math.max(maxLon, coord[1]);
        }

        int[] minTile = latLonToTileXY(minLat, minLon, zoom);
        int[] maxTile = latLonToTileXY(maxLat, maxLon, zoom);

        int minX = Math.min(minTile[0], maxTile[0]);
        int maxX = Math.max(minTile[0], maxTile[0]);
        int minY = Math.min(minTile[1], maxTile[1]);
        int maxY = Math.max(minTile[1], maxTile[1]);

        int tileWidth = maxX - minX + 1;
        int tileHeight = maxY - minY + 1;

        BufferedImage stitched = new BufferedImage(tileWidth * tileSize, tileHeight * tileSize, BufferedImage.TYPE_INT_RGB);
        Graphics g = stitched.getGraphics();

        for (int x = 0; x < tileWidth; x++) {
            for (int y = 0; y < tileHeight; y++) {
                BufferedImage tile = downloadTile(zoom, minX + x, minY + y);
                g.drawImage(tile, x * tileSize, y * tileSize, null);
            }
        }
        g.dispose();

        if (routeCoords != null && !routeCoords.isEmpty()) {
            drawRouteOnImage(stitched, routeCoords, zoom, minX, minY, tileSize);
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(stitched, "png", baos);
        return baos.toByteArray();
    }

    private int[] latLonToTileXY(double lat, double lon, int zoom) {
        int x = (int) Math.floor((lon + 180) / 360 * (1 << zoom));
        int y = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom));
        return new int[]{x, y};
    }

    private Point latLonToPixel(double lat, double lon, int zoom, int minX, int minY, int tileSize) {
        double x = (lon + 180) / 360 * (1 << zoom);
        double y = (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI) / 2 * (1 << zoom);
        int pixelX = (int) Math.round((x - minX) * tileSize);
        int pixelY = (int) Math.round((y - minY) * tileSize);
        return new Point(pixelX, pixelY);
    }

    private void drawRouteOnImage(BufferedImage image, List<double[]> routeCoords, int zoom, int minX, int minY, int tileSize) {
        Graphics2D g2 = image.createGraphics();
        g2.setColor(Color.BLUE);
        g2.setStroke(new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        Point prev = null;
        for (double[] coord : routeCoords) {
            Point p = latLonToPixel(coord[0], coord[1], zoom, minX, minY, tileSize);
            if (prev != null) {
                g2.drawLine(prev.x, prev.y, p.x, p.y);
            }
            prev = p;
        }
        g2.dispose();
    }

    private List<double[]> decodePolyline(String polyline) {
        List<double[]> coords = new java.util.ArrayList<>();
        int index = 0, len = polyline.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyline.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            coords.add(new double[]{lat / 1E5, lng / 1E5});
        }
        return coords;
    }


}
