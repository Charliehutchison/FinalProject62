import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SortBooksByRatingQuickSort {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java SortBooksByRatingQuickSort <input.csv> <output.csv>");
            return;
        }

        Path input = Path.of(args[0]);
        Path output = Path.of(args[1]);

        List<String[]> rows = new ArrayList<>();
        String header;

        try (BufferedReader br = Files.newBufferedReader(input)) {
            header = br.readLine(); // first line = header
            String line;
            while ((line = br.readLine()) != null) {
                rows.add(parseCsvLine(line));
            }
        }

        // average_rating is column index 3 in your books.csv
        quickSort(rows, 0, rows.size() - 1, 3);

        try (BufferedWriter bw = Files.newBufferedWriter(output)) {
            bw.write(header);
            bw.newLine();
            for (String[] row : rows) {
                bw.write(toCsvLine(row));
                bw.newLine();
            }
        }

        System.out.println("Sorted file written to: " + output);
    }

    static void quickSort(List<String[]> arr, int low, int high, int ratingIdx) {
        if (low < high) {
            int p = partition(arr, low, high, ratingIdx);
            quickSort(arr, low, p - 1, ratingIdx);
            quickSort(arr, p + 1, high, ratingIdx);
        }
    }

    static int partition(List<String[]> arr, int low, int high, int ratingIdx) {
        double pivot = rating(arr.get(high), ratingIdx);
        int i = low - 1;

        for (int j = low; j < high; j++) {
            // descending order
            if (rating(arr.get(j), ratingIdx) >= pivot) {
                i++;
                Collections.swap(arr, i, j);
            }
        }
        Collections.swap(arr, i + 1, high);
        return i + 1;
    }

    static double rating(String[] row, int idx) {
        if (idx >= row.length) return Double.NEGATIVE_INFINITY;
        try {
            return Double.parseDouble(row[idx].trim());
        } catch (Exception e) {
            return Double.NEGATIVE_INFINITY; // malformed/missing ratings sink to bottom
        }
    }

    // Simple CSV parser supporting quoted fields
    static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    cur.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(cur.toString());
                cur.setLength(0);
            } else {
                cur.append(c);
            }
        }
        fields.add(cur.toString());
        return fields.toArray(new String[0]);
    }

    static String toCsvLine(String[] fields) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) sb.append(',');
            String v = fields[i] == null ? "" : fields[i];
            boolean q = v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r");
            if (v.contains("\"")) v = v.replace("\"", "\"\"");
            if (q) sb.append('"').append(v).append('"');
            else sb.append(v);
        }
        return sb.toString();
    }
}