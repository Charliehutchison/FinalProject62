import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class Lookup {

    protected HashMap<String, BookInfo> letBookHashMap;

    public Lookup(String filepath) throws IOException {
        letBookHashMap = new HashMap<>();

        BufferedReader br = new BufferedReader(new FileReader(filepath));
        br.readLine();

        String line;
        while ((line = br.readLine()) != null){
            String[] rawFields = parseCSVLine(line);
            String[] fields = new String[16];
            for (int i = 0; i < 16; i++){
                if (!rawFields[i].isEmpty()){
                    fields[i] = rawFields[i];
                } else {
                    fields[i] = "N/A";
                }
            }

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

    public BookInfo search(String title){
        return letBookHashMap.get(title.toLowerCase());
    }

    // some fields have commas in them, but splitting on commas normally would break fields up that shouldn't be broken up.
    // this method checks to see if we are in quotes, meaning that the quote is part of the field and shouldn't be split

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
                List<String> suggestions = lookup.suggest(query);
                if (suggestions.isEmpty()){
                    System.out.println("Book not found.");
                    System.out.println("Enter book title: ");
                } else if (suggestions.size() == 1){
                    System.out.println(lookup.search(suggestions.get(0)));
                } else {
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
