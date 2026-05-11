import java.util.List;

public interface BookRecommenderInterface {

    // Loads book data from a file into the data structure
    void loadData(String filename);

    // Adds a user rating for a specific book
    void addUserRating(String bookTitle, double rating);

    // Returns a list of recommended books based on a title the user likes
    List<String> getRecommendations(String bookTitle);

    // Returns a list of recommended books based on genre
    List<String> getRecommendationsByGenre(String genre);


    //The Filtering Methods and reccomendation methods were implemented in 
    //different files by different group members. So the interface is slightly modified to include the filtering methods.
    // Searches for books by title keyword
    List<String> searchByTitle(String titleKeyword);

    // Searches for books by author
    List<String> searchByAuthor(String author);

    // Filters books by genre
    List<String> filterByGenre(String genre);

    // Returns books sorted by average rating
    List<String> getTopRatedBooks(int limit);

    // Returns all books sorted alphabetically
    List<String> getAllBooksAlphabetically();

    // Returns whether a given book exists in the dataset
    boolean containsBook(String bookTitle);
}
