package bookbridge;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Picks related books from genre/author overlap.
 * Column style filtering is in FilterBooks instead.
 *
 * Run from repo root: javac src/*.java then java -cp src BookRecommender
 * (optional arg: path to the merged CSV).
 */
public class BookRecommender implements BookRecommenderInterface {

    private static final int DEFAULT_RECOMMENDATION_LIMIT = 15;
    private static final double POINTS_PER_SHARED_GENRE = 4.0;
    private static final double SAME_AUTHOR_BONUS = 6.0;

    private Lookup lookup;

    public BookRecommender() {
    }

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

    private void ensureLoaded() {
        if (lookup == null) {
            throw new IllegalStateException("Call loadData(filename) first.");
        }
    }


    /**
     * Other books that share genres with this one, or the same primary author.
     * Uses a basic score (overlap + author bump + avg rating).
     * Wrong title -> empty list; no matches -> empty list.
     */
    @Override
    public List<String> getRecommendations(String bookTitle) {
        ensureLoaded();
        BookInfo seed = lookup.search(bookTitle.trim());
        if (seed == null) {
            return new ArrayList<>();
        }

        String seedGenres = seed.getGenres();
        String seedAuthor = normalize(seed.getPrimaryAuthor());
        List<ScoredTitle> scored = new ArrayList<>();

        for (BookInfo cand : lookup.getAllBooks()) {
            if (cand.getTitle().equalsIgnoreCase(seed.getTitle())) {
                continue;
            }

            int shared = countSharedGenres(seedGenres, cand.getGenres());
            boolean sameAuthor = !seedAuthor.isEmpty()
                && seedAuthor.equals(normalize(cand.getPrimaryAuthor()));

            if (shared == 0 && !sameAuthor) {
                continue;
            }

            double score = shared * POINTS_PER_SHARED_GENRE;
            if (sameAuthor) {
                score += SAME_AUTHOR_BONUS;
            }
            score += ratingNumber(cand.getAverageRating());

            scored.add(new ScoredTitle(cand.getTitle(), score));
        }

        return topTitles(scored);
    }

    /** Books whose genre text mentions the query (simple substring match). Sorted by rating. */
    @Override
    public List<String> getRecommendationsByGenre(String genre) {
        ensureLoaded();
        String q = genre.trim().toLowerCase(Locale.ROOT);
        if (q.isEmpty()) {
            return new ArrayList<>();
        }

        List<ScoredTitle> scored = new ArrayList<>();
        for (BookInfo b : lookup.getAllBooks()) {
            String g = b.getGenres();
            if (g == null || g.isBlank() || "N/A".equalsIgnoreCase(g.trim())) {
                continue;
            }
            if (!g.toLowerCase(Locale.ROOT).contains(q)) {
                continue;
            }
            scored.add(new ScoredTitle(b.getTitle(), ratingNumber(b.getAverageRating())));
        }

        return topTitles(scored);
    }

    @Override
    public boolean containsBook(String bookTitle) {
        ensureLoaded();
        return lookup.search(bookTitle.trim()) != null;
    }

    // Interface stubs — real filtering is FilterBooks

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

    // helpers

    /** Counts seed genres, comma separated, that show up as substrings in the other book's genres field. */
    private static int countSharedGenres(String seedGenres, String candGenres) {
        if (seedGenres == null || candGenres == null
            || seedGenres.isBlank() || candGenres.isBlank()
            || "N/A".equalsIgnoreCase(seedGenres.trim())
            || "N/A".equalsIgnoreCase(candGenres.trim())) {
            return 0;
        }
        String candLower = candGenres.toLowerCase(Locale.ROOT);
        int count = 0;
        for (String piece : seedGenres.split(",")) {
            String t = piece.trim().toLowerCase(Locale.ROOT);
            if (!t.isEmpty() && candLower.contains(t)) {
                count++;
            }
        }
        return count;
    }

    private static String normalize(String s) {
        if (s == null || s.isBlank() || "N/A".equalsIgnoreCase(s.trim())) {
            return "";
        }
        return s.trim().toLowerCase(Locale.ROOT);
    }

    private static double ratingNumber(String s) {
        if (s == null || s.isBlank() || "N/A".equalsIgnoreCase(s.trim())) {
            return 0.0;
        }
        try {
            return Double.parseDouble(s.trim());
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    private static List<String> topTitles(List<ScoredTitle> scored) {
        scored.sort(Comparator.comparingDouble((ScoredTitle st) -> st.score).reversed());
        int n = Math.min(DEFAULT_RECOMMENDATION_LIMIT, scored.size());
        List<String> out = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            out.add(scored.get(i).title);
        }
        return out;
    }

    private static final class ScoredTitle {
        final String title;
        final double score;

        ScoredTitle(String title, double score) {
            this.title = title;
            this.score = score;
        }
    }

    public static void main(String[] args) {
        String csv = args.length > 0 ? args[0] : "datasets/books_merged_clean.csv";
        BookRecommender rec = new BookRecommender(csv);

        System.out.println("Book recommender — dataset: " + csv);
        System.out.println("Enter a title you’ve read.");
        System.out.println("Or: genre <keyword>   (example: genre fantasy)");
        System.out.println("Type quit when done.");
        System.out.println();

    try (Scanner in = new Scanner(System.in)) {
        while (true) {
            System.out.print("> ");
            String line = in.nextLine().trim();
            if (line.equalsIgnoreCase("quit")) {
                break;
            }
            if (line.isEmpty()) {
                continue;
            }
        
            if (line.regionMatches(true, 0, "genre ", 0, 6)) {
                String g = line.substring(6).trim();
                if (g.isEmpty()) {
                    System.out.println("Try: genre fantasy");
                    continue;
                }
                List<String> byGenre = rec.getRecommendationsByGenre(g);
                if (byGenre.isEmpty()) {
                    System.out.println("No books matched that genre substring.");
                } else {
                    printNumbered(byGenre);
                }
                System.out.println();
                continue;
            }
        
            if (!rec.containsBook(line)) {
                List<String> suggestions = rec.lookup.suggest(line);
                if (suggestions.isEmpty()) {
                    System.out.println("Book not found. Please try again.");
                    System.out.println();
                    continue;
                } else if (suggestions.size() == 1) {
                    line = suggestions.get(0);
                } else {
                    System.out.println("Did you mean:");
                    for (int i = 0; i < suggestions.size(); i++) {
                        System.out.println(i + 1 + ". " + suggestions.get(i));
                    }
                    System.out.print("Enter number: ");
                    int choice = Integer.parseInt(in.nextLine().trim());
                    line = suggestions.get(choice - 1);
                }
            }
        
            List<String> recs = rec.getRecommendations(line);
            if (recs.isEmpty()) {
                System.out.println("Found the book, but no related titles (missing genres / no overlap).");
            } else {
                printNumbered(recs);
            }
            System.out.println();
        }

        System.out.println("Goodbye.");
    }
    }
    private static void printNumbered(List<String> titles) {
        int i = 1;
        for (String t : titles) {
            System.out.println("  " + i++ + ". " + t);
        }
    }
}
