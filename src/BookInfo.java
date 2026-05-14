/**
 * Stores all the data for a book object
 */
public class BookInfo {
    private String bookId;
    private String title;
    private String primaryAuthor;
    private String authors;
    private String publisher;
    private String publicationDate;
    private String language;
    private String numPages;
    private String isbn;
    private String isbn13;
    private String averageRating;
    private String ratingsCount;
    private String textReviewsCount;
    private String genres;
    private String description;
    private String url;

    /**
     * Constructor
     */

    public BookInfo (String bookId, String title, String primaryAuthor, String authors, String publisher, String publicationDate,String language, String numPages, String isbn, String isbn13, String averageRating,String ratingsCount, String textReviewsCount, String genres, String description, String url){
        this.bookId = bookId;
        this.title = title;
        this.primaryAuthor = primaryAuthor;
        this.authors = authors;
        this.publisher = publisher;
        this.publicationDate = publicationDate;
        this.language = language;
        this.numPages = numPages;
        this.isbn = isbn;
        this.isbn13 = isbn13;
        this.averageRating = averageRating;
        this.ratingsCount = ratingsCount;
        this.textReviewsCount = textReviewsCount;
        this.genres = genres;
        this.description = description;
        this.url = url;
    }

    /**
     * Methods that return variables
     * @return String title, String averageRating, String primaryAuthor, String numPages, String genres
     */

    public String getTitle(){return title;}
    public String getAverageRating(){return averageRating;}
    public String getRatingsCount() { return ratingsCount; }
    public String getPrimaryAuthor() { return primaryAuthor; }
    public String getAuthors() { return authors; }
    public String getNumPages()      { return numPages; }
    public String getGenres()        { return genres; }
    public String getPublisher()     { return publisher; }
    public String getLanguage()      { return language; }
    public String getPublicationDate() { return publicationDate; }
    public String getDescription()   { return description; }
    public String getUrl()           { return url; }

    /**
     * Overriding toString method to cleanly print book information
     * @return String with book information
     */
    @Override
    public String toString(){
        String separator = "=".repeat(60);

        return "\n" + separator + "\n" + " " + title + "\n" + separator + "\n" +
        " Author(s): " + primaryAuthor + "\n" +
        " Publisher: " + publisher + "\n" +
        " Published: " + publicationDate + "\n" +
        " Pages: " + numPages + "\n" +
        " Language: " + language + "\n" +
        " ISBN: " + isbn + "\n" +
        " Average Rating: " + averageRating + " ⭐  (" + ratingsCount + " ratings)\n" +
        " Genres:  " + genres + "\n" +
        " Description: " + description + "\n" + 
        "\n More Info: " + url + "\n" + separator + "\n";
    }
}