import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Book recommender scaffold: wiring {@link Lookup} and {@link BookRecommenderInterface}.
 * Recommendation/search logic can be filled in later.
 */
public class BookRecommender implements BookRecommenderInterface {
    private static final int Default_recommendation_limit = 15;
    private static final double POINTS_PER_SHARED_GENRE = 4.0;
    private static final double SAME_PRIMARY_AUTHOR_BONUS = 6.0;

    /** Filled when you call {@link #loadData(String)} or the path-taking constructor. */
    private Lookup lookup;
    private final Map<String, Double> userRatings = new HashMap<>();

    /** Default — call {@code loadData("datasets/books_merged_clean.csv")} before other methods. */
    public BookRecommender() {
    }

    /** Loads the CSV immediately (same as default constructor + {@link #loadData}. */
    public BookRecommender(String filename) {
        loadData(filename);
    }

    @Override
    public void loadData(String filename) {
        try {
            lookup = new Lookup(filename);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /** Shared guard so interface methods fail clearly if data was never loaded. */
    private void ensureLoaded() {
        if (lookup == null) {
            throw new IllegalStateException("Call loadData(filename) first.");
        }
    }

    // --- Below: stubs only (no recommendation algorithm yet) ---

    @Override
    public void addUserRating(String bookTitle, double rating) {
        ensureLoaded();
    }

    @Override
    public List<String> getRecommendations(String bookTitle) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> getRecommendationsByGenre(String genre) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> searchByTitle(String titleKeyword) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> searchByAuthor(String author) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> filterByGenre(String genre) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> getTopRatedBooks(int limit) {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public List<String> getAllBooksAlphabetically() {
        ensureLoaded();
        return new ArrayList<>();
    }

    @Override
    public boolean containsBook(String bookTitle) {
        ensureLoaded();
        return lookup.search(bookTitle) != null;
    }
}
