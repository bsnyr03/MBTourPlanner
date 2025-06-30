package at.fhtw.mbtourplanner.service;

import java.util.List;

public class PolyLineEncoder {
    public static String encode(List<double[]> path) {
        StringBuilder result = new StringBuilder();
        long prevLat = 0, prevLon = 0;

        for (double[] point : path) {
            long lat = Math.round(point[0] * 1e5);
            long lon = Math.round(point[1] * 1e5);
            long dLat = lat - prevLat;
            long dLon = lon - prevLon;
            encodeSignedNumber(dLat, result);
            encodeSignedNumber(dLon, result);
            prevLat = lat;
            prevLon = lon;
        }

        return result.toString();
    }

    private static void encodeSignedNumber(long num, StringBuilder sb) {
        long sgnNum = num < 0 ? ~(num << 1) : (num << 1);
        encodeUnsignedNumber(sgnNum, sb);
    }

    private static void encodeUnsignedNumber(long num, StringBuilder sb) {
        while (num >= 0x20) {
            sb.append((char)((0x20 | (num & 0x1f)) + 63));
            num >>= 5;
        }
        sb.append((char)(num + 63));
    }
}