import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SortBooksByRatingQuickSort {
    // In books.csv, average_rating is the 4th column (0-based index 3).
    private static final int RATING_COLUMN = 3;

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java SortBooksByRatingQuickSort <input.csv> <output.csv>");
            return;
        }

        Path inputPath = Path.of(args[0]);
        Path outputPath = Path.of(args[1]);

        List<String[]> dataRows = new ArrayList<>();
        String header;

        try (BufferedReader br = Files.newBufferedReader(inputPath)) {
            header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                dataRows.add(parseCsvLine(line));
            }
        }

        if (!dataRows.isEmpty()) {
            quickSortByRating(dataRows, 0, dataRows.size() - 1);
        }

        try (BufferedWriter bw = Files.newBufferedWriter(outputPath)) {
            bw.write(header);
            bw.newLine();
            for (String[] row : dataRows) {
                bw.write(toCsvLine(row));
                bw.newLine();
            }
        }

        System.out.println("Sorted file written to: " + outputPath);
    }

    static void quickSortByRating(List<String[]> rows, int left, int right) {
        if (left >= right) {
            return;
        }

        int splitIndex = partition(rows, left, right);

        // These guards ensure we always shrink the range and avoid recursion loops.
        if (left < splitIndex - 1) {
            quickSortByRating(rows, left, splitIndex - 1);
        }
        if (splitIndex < right) {
            quickSortByRating(rows, splitIndex, right);
        }
    }
    
    static int partition(List<String[]> rows, int left, int right) {
        // Use left element value as pivot (descending order).
        double pivotRating = getRating(rows.get(left));

        int i = left;
        int j = right;

        while (i <= j) {
            // Move i right while row[i] should stay on the left.
            while (getRating(rows.get(i)) > pivotRating) {
                i++;
            }

            // Move j left while row[j] should stay on the right.
            while (getRating(rows.get(j)) < pivotRating) {
                j--;
            }

            if (i <= j) {
                Collections.swap(rows, i, j);
                i++;
                j--;
            }
        }

        return i;
    }

    static double getRating(String[] row) {
        if (RATING_COLUMN >= row.length) {
            return Double.NEGATIVE_INFINITY;
        }
        try {
            return Double.parseDouble(row[RATING_COLUMN].trim());
        } catch (Exception e) {
            // If rating is missing/bad, send it to the end of the sorted file.
            return Double.NEGATIVE_INFINITY;
        }
    }

    static String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        fields.add(current.toString());
        return fields.toArray(new String[0]);
    }

    static String toCsvLine(String[] fields) {
        StringBuilder line = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                line.append(',');
            }

            String value = fields[i] == null ? "" : fields[i];
            boolean needsQuotes = value.contains(",") || value.contains("\"")
                || value.contains("\n") || value.contains("\r");

            if (value.contains("\"")) {
                value = value.replace("\"", "\"\"");
            }

            if (needsQuotes) {
                line.append('"').append(value).append('"');
            } else {
                line.append(value);
            }
        }
        return line.toString();
    }
}