/**
 * In this class, users can interact with the terminal by typing in the title of a book and recieving relevant information about the book.
 * This class utilizes a Hash Map to store book objects.
 **/

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Lookup {

    protected HashMap<String, BookInfo> letBookHashMap;

    public Lookup(String filepath) throws IOException {
        
        // Initialize the HashMap
        letBookHashMap = new HashMap<>();

        // Open the CSV file for reading
        BufferedReader br = new BufferedReader(new FileReader(filepath));
        br.readLine();

        String line;

        // Read each line until end of file
        while ((line = br.readLine()) != null){
            String[] rawFields = parseCSVLine(line);
            String[] fields = new String[16];
            
            // Replace any empty fields with "N/A"
            for (int i = 0; i < 16; i++){
                if (!rawFields[i].isEmpty()){
                    fields[i] = rawFields[i];
                } else {
                    fields[i] = "N/A";
                }
            }

            // Create a BookInfo object from the parsed fields
            BookInfo book = new BookInfo(
                fields[0],  // bookID
                fields[1],  // title
                fields[2],  // primary_author
                fields[3],  // authors
                fields[4],  // publisher
                fields[5],  // publication_date
                fields[6],  // language_code
                fields[7],  // num_pages
                fields[8],  // isbn
                fields[9],  // isbn13
                fields[10], // average_rating
                fields[11], // ratings_count
                fields[12], // text_reviews_count
                fields[13], // genres
                fields[14], // description
                fields[15]  // url
            );

            letBookHashMap.put(book.getTitle().toLowerCase(), book);
        }
        br.close();
    }

    /**
     * Searches for a book by title and returns its BookInfo, or null if not found
     * @param String title: book title
     * @return BookInfo, or null if not found
     */
    public BookInfo search(String title){
        return letBookHashMap.get(title.toLowerCase());
    }

    /**
     * All loaded books (copy of values). Used by {@link BookRecommender}.
     */
    public Collection<BookInfo> getAllBooks() {
        return new ArrayList<>(letBookHashMap.values());
    }
/**
 * Some fields have commas in them, but splitting on commas normally would break fields up that shouldn't be broken up.
 * This method checks to see if we are in quotes, meaning that the quote is part of the field and shouldn't be split
/**
 * @param String line
 * @return String array of all the fields from a CSV line
 */
    private String[] parseCSVLine(String line){
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (char c : line.toCharArray()){
            if (c == '"'){
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes){
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        fields.add(current.toString().trim());
        return fields.toArray(new String[0]);
    }

    /**
     * Returns a list of book titles that start with the given prefix
     * @param String prefix
     * @return list of book titles
     */
    public List<String> suggest(String prefix){
        List<String> matches = new ArrayList<>();
        String lowerPrefix = prefix.toLowerCase();
        for (String title: letBookHashMap.keySet()){
            if (title.startsWith(lowerPrefix)){
                matches.add(title);
            }

        }
        return matches;
    }
    
    /**
     * Loads the dataset, prompts the user for a book title, and either displays the result or offers suggestions if an exact match isn't found
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException{
        Lookup lookup = new Lookup("datasets/books_merged_clean.csv");
        Scanner scanner = new Scanner(System.in);

        while (true) { 
            
            System.out.println("Enter book title (or 'quit' to exit): ");
            String query = scanner.nextLine().trim();
    
            if (query.equalsIgnoreCase("quit")) break;

            BookInfo book = lookup.search(query);
    
            if (book != null){
                System.out.println(book);
                
            } else {
                
                // No exact match — try to find titles that start with the query
                List<String> suggestions = lookup.suggest(query);
                
                if (suggestions.isEmpty()){
                    System.out.println("Book not found.");
                    System.out.println("Enter book title: ");

                } else if (suggestions.size() == 1){

                    // Only one suggestion — show it automatically
                    System.out.println(lookup.search(suggestions.get(0)));

                } else {

                    // Multiple suggestions — let the user pick
                    System.out.println("Did you mean:");

                    for (int i = 0; i < suggestions.size(); i++){
                        System.out.println(i + 1 + ". " + suggestions.get(i));
                    }

                    System.out.println("Enter number: ");
                    int choice = Integer.parseInt(scanner.nextLine().trim());
                    System.out.println(lookup.search(suggestions.get(choice - 1)));
                }
            }
            
        }
        scanner.close();
    } 
}
