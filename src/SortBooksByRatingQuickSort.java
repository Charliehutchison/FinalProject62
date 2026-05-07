import java.io.*;
import java.nio.file.*;
import java.util.*;

public class SortBooksByRatingQuickSort {

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
            if (header == null || header.isBlank()) {
                throw new IllegalArgumentException("CSV file is empty or missing header row.");
            }
            String[] headerColumns = parseCsvLine(header);
            int ratingColumnIndex = findAverageRatingColumnIndex(headerColumns);

            String line;
            while ((line = br.readLine()) != null) {
                dataRows.add(parseCsvLine(line));
            }

            if (!dataRows.isEmpty()) {
                quickSortByRating(dataRows, 0, dataRows.size() - 1, ratingColumnIndex);
            }
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

    /**
     * Finds the 0-based column index for {@code average_rating} by parsing the header row.
     * Works for both original {@code books.csv} (rating at index 3) and merged CSVs
     * where rating appears later (e.g. index 10).
     *
     * @param headerLine first line of the CSV file
     * @return column index of average_rating
     */
    static int findAverageRatingColumnIndex(String headerLine) {
        String[] columns = parseCsvLine(headerLine);
        return findAverageRatingColumnIndex(columns);
    }

    /**
     * Finds the 0-based column index for {@code average_rating} from parsed header cells.
     */
    static int findAverageRatingColumnIndex(String[] headerColumns) {
        for (int i = 0; i < headerColumns.length; i++) {
            if ("average_rating".equalsIgnoreCase(headerColumns[i].trim())) {
                return i;
            }
        }
        throw new IllegalArgumentException(
            "CSV header must contain an \"average_rating\" column.");
    }

    static void quickSortByRating(List<String[]> rows, int left, int right, int ratingColumnIndex) {
        if (left >= right) {
            return;
        }

        int splitIndex = partition(rows, left, right, ratingColumnIndex);

        // These guards ensure we always shrink the range and avoid recursion loops.
        if (left < splitIndex - 1) {
            quickSortByRating(rows, left, splitIndex - 1, ratingColumnIndex);
        }
        if (splitIndex < right) {
            quickSortByRating(rows, splitIndex, right, ratingColumnIndex);
        }
    }
    
    static int partition(List<String[]> rows, int left, int right, int ratingColumnIndex) {
        // Use left element value as pivot (descending order).
        double pivotRating = getRating(rows.get(left), ratingColumnIndex);

        int i = left;
        int j = right;

        while (i <= j) {
            // Move i right while row[i] should stay on the left.
            while (getRating(rows.get(i), ratingColumnIndex) > pivotRating) {
                i++;
            }

            // Move j left while row[j] should stay on the right.
            while (getRating(rows.get(j), ratingColumnIndex) < pivotRating) {
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

    static double getRating(String[] row, int ratingColumnIndex) {
        if (ratingColumnIndex < 0 || ratingColumnIndex >= row.length) {
            return Double.NEGATIVE_INFINITY;
        }
        try {
            return Double.parseDouble(row[ratingColumnIndex].trim());
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