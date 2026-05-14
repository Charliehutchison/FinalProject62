package bookbridge;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FilterBooks {

    public static void main(String[] args) throws IOException {

        // filtering system
        if (args.length != 3) {
            System.out.println("Usage: java FilterBooks <input.csv> <filter> <value>");
            System.out.println("filter options: genre, author, language, publisher, year");
            return;
        }

        Path input = Path.of(args[0]);
        // variables
        String filter = args[1].toLowerCase();
        String value = args[2].toLowerCase();

        // maps friendly names
        Map<String, String> names = new HashMap<>();
        names.put("author", "primary_author");
        names.put("genre", "genres");
        names.put("language", "language_code");
        names.put("year", "publication_date");

        // column
        String column = names.getOrDefault(filter, filter);
        int col = -1;

        String header;
        String[] headers;

        // index maps each value to a list of matching rows
        HashMap<String, List<String[]>> index = new HashMap<>();

        try (BufferedReader bufferedreader = Files.newBufferedReader(input)) {

            // parse header to find which column index to filter on
            header = bufferedreader.readLine();
            headers = parseCsvLine(header);

            for (int i = 0; i < headers.length; i++) {

                if (headers[i].equalsIgnoreCase(column)) {
                    col = i;
                    break;

                }
            }

            if (col == -1) {
                System.out.println("Error: could not find " + column);
                System.out.println("Available choices: " + String.join(", ", headers));
                return;
            }

            // check what type of filter is being used
            boolean genre = column.equalsIgnoreCase("genres");
            boolean year = column.equalsIgnoreCase("publication_date");


            
            String line;

            while ((line = bufferedreader.readLine()) != null) {
                // row
                String[] row = parseCsvLine(line);

                // if less than row length
                if (col < row.length) {
                    if (genre) {
                        // books can have multiple genres like "Fantasy, Adventure" for example
                        for (String g : row[col].split(",")) {
                            String key = g.toLowerCase();

                            if (!key.isEmpty()) {

                                if (!index.containsKey(key)) {
                                    index.put(key, new ArrayList<>());
                                }

                                index.get(key).add(row);
                            }
                        }
                    } 
                    else if (year) {
                        // get just the year
                        String date = row[col];
                        String yr;

                        if (date.length() >= 4) {
                            yr = date.substring(0, 4);
                        } 
                        else {
                            yr = date;
                        }

                        if (!index.containsKey(yr)) {
                            index.put(yr, new ArrayList<>());
                        }

                        index.get(yr).add(row);
                    } 
                    else {
                        String key = row[col].toLowerCase();

                        if (!index.containsKey(key)) {
                            index.put(key, new ArrayList<>());
                        }

                        index.get(key).add(row);
                    }
                }
            }
        }

        // get all rows that matched the search value
        List<String[]> matches = index.getOrDefault(value, new ArrayList<>());

        // print header then each matching row
        System.out.println(header);

        for (String[] row : matches) {
            System.out.println(csvline(row));
        }

        System.out.println("\nTotal " + matches.size() + " results.");
    }

    // parses into fields, handling quoted fields that contain commas
    static String[] parseCsvLine(String line) {
        List<String> field = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean x = false;

        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);

            // if it is a '"'
            if (character == '"') {
                // two consecutive quotes
                if ((x && (i+1<line.length())) && (line.charAt(i+1)=='"')) {
                    current.append('"');
                    i++;
                } 
                else {
                    x = !x;
                }
            } 
            else if (character == ',' && !x) {
                field.add(current.toString());
                current.setLength(0);
            } 
            else {
                current.append(character);
            }
        }

        // add after
        field.add(current.toString());

        return field.toArray(new String[0]);
    }

    // quotes fields that contain commas or quotes
    static String csvline(String[] fields) {
        StringBuilder line = new StringBuilder();

        for (int i = 0; i < fields.length; i++) {
            String value;

            if (i > 0) {
                line.append(',');
            }

            if (fields[i] == null) {
                value = "";
            } 
            else {
                value = fields[i];
            }

            // wrap in quotes if the value contains these
            boolean wrap = false;
            if (value.contains(",")) {
                wrap = true;
            }
            if (value.contains("\"")) {
                wrap = true;
            }
            if (value.contains("\n")) {
                wrap = true;
            }
            if (value.contains("\r")) {
                wrap = true;
            }
        
            if (value.contains("\"")) {
                value = value.replace("\"", "\"\"");
            }

            // if true for wrap variable
            if (wrap) {
                line.append('"');
                line.append(value);
                line.append('"');
            } 
            else {
                line.append(value);
            }
        }

        return line.toString();
    }
}
