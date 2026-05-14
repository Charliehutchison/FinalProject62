//so I used claude to show me an example of a gui and now i'm gonna try to recreate it WISH ME LUCK
// this is art

package bookbridge;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * Swing window for the Book Recommender
 * Builds: Search, Top Rated, Similar Books, Browse by Genre, Filter
 * using Lookup, BookRecommender, FilterBooks, and quicksort classes.
 *
 */
public class BookRecommenderGUI extends JFrame {

    private static final String BIBLE = "datasets/books_merged_clean.csv";
    private static final int GOATS = 100;

    private Lookup peep;
    private BookRecommender plug;

    /**
     * constructor
     * Loads the data and creates the window.
     *
     * @throws IOException if the csv file can't be read
     */
    public BookRecommenderGUI() throws IOException {
        peep = new Lookup(BIBLE);
        plug = new BookRecommender(BIBLE);

        setTitle("Book Recommender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 720);
        setLocationRelativeTo(null);

        JTabbedPane decks = new JTabbedPane();
        decks.addTab("Search", makeSearch());
        decks.addTab("Top Rated", makeGoats());
        decks.addTab("Similar Books", makeTwins());
        decks.addTab("Browse by Genre", makeGenre());
        decks.addTab("Filter", makeFilter());
        add(decks);

        setVisible(true);
    }

    // ------------------------------------------------------------
    // Search
    // ------------------------------------------------------------
    /**
     * Search tab
     * returns an input match/closest matches
     *
     * @return JPanel for the Search tab
     */
    private JPanel makeSearch() {
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        //search bar
        JTextField snooper = new JTextField();
        JButton yeet = new JButton("Search");
        JPanel crown = new JPanel(new BorderLayout(6, 0));
        crown.add(new JLabel("Search by Title:  "), BorderLayout.WEST);
        crown.add(snooper, BorderLayout.CENTER);
        crown.add(yeet, BorderLayout.EAST);
        vibe.add(crown, BorderLayout.NORTH);

        //results
        DefaultListModel<String> feed = new DefaultListModel<>();
        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Results"));

        // details
        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        tea.setLineWrap(true);
        tea.setWrapStyleWord(true);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book Details"));

        // divider
        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(300);
        vibe.add(beef, BorderLayout.CENTER);

        // look up..
        yeet.addActionListener(e -> {
            String OG = snooper.getText().trim();
            feed.clear();
            if (OG.isEmpty()) {
                return;
            }
            //find your OG
            BookInfo hit = peep.search(OG);
            if (hit != null) {
                feed.addElement(hit.getTitle());
            } else {
                List<String> pings = peep.suggest(OG);
                Collections.sort(pings);
                for (String ping : pings) {
                    BookInfo bookie = peep.search(ping);
                    if (bookie != null) {
                        feed.addElement(bookie.getTitle());
                    }
                }
            }
            if (!feed.isEmpty()) {
                lineup.setSelectedIndex(0);
            }
        });

        // etner
        snooper.addActionListener(e -> yeet.doClick());

        // show details of selected book
        lineup.addListSelectionListener(e -> {
            String handle = lineup.getSelectedValue();
            if (handle != null) {
                BookInfo bookie = peep.search(handle);
                if (bookie != null) {
                    tea.setText(bookie.toString());
                    tea.setCaretPosition(0);
                }
            }
        });

        return vibe;
    }

    //-----------------------------
    // GOATS
    //-----------------------------
    /**
     * Top Rated tab
     * uses our quicksort, shows top GOATS books
     *
     * @return JPanel for the Top Rated tab
     */
    private JPanel makeGoats(){
        //this stays the same because it's the big window
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));List<BookInfo> ranked = new ArrayList<>(peep.getAllBooks());
        
        SortBooksByRatingQuickSort.quickSortBooks(ranked, 0, ranked.size() - 1);
        // TOP 100
        List<BookInfo> cream = ranked.subList(0, GOATS);

        DefaultListModel<String> feed = new DefaultListModel<>();
        for (BookInfo bookie : cream){
            feed.addElement(bookie.getTitle());
        }

        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Top " + GOATS + " books by rating"));

        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        tea.setLineWrap(true);
        tea.setWrapStyleWord(true);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book details"));

        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(380);
        vibe.add(beef, BorderLayout.CENTER);

        lineup.addListSelectionListener(e -> {
            String handle = lineup.getSelectedValue();
            if (handle != null) {
                BookInfo bookie = peep.search(handle);
                if (bookie != null) {
                    tea.setText(bookie.toString());
                    tea.setCaretPosition(0);
                }
            }
        });
        return vibe;

    }

    //-----------------------------
    // BY GENRE!
    //-----------------------------
    //extract genres
    /**
     * iterates thru every book and extracts all existing genres
     * separated by comma
     *
     * @return alphabetically sorted list of unique genre names
     */
    private List<String> distinctGenres() {
        ArrayList<String> squad = new ArrayList<>();
        for (BookInfo bookie : peep.getAllBooks()) {
            for (String crumb : bookie.getGenres().split(",")) {
                String nugget = crumb.trim();
                if (!squad.contains(nugget)) {
                    squad.add(nugget);
                }
            }
        }
        Collections.sort(squad);
        return squad;
    }

    /**
     * Browse by Genre tab
     * filters by genre from dropdown meny we extrascted in the previous helper
     *
     * @return JPanel for the Browse by Genre tab
     */
    private JPanel makeGenre(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        //DROPDOWN!!!
        //needs a method to extract all existing genres
        List<String> genres = distinctGenres();
        JComboBox<String> grower = new JComboBox<>(genres.toArray(new String[0]));
        JButton yeet = new JButton("Search");
        JPanel crown = new JPanel(new BorderLayout(6, 0));
        crown.add(new JLabel("Search by Title:  "), BorderLayout.WEST);
        crown.add(grower, BorderLayout.CENTER);
        crown.add(yeet, BorderLayout.EAST);
        vibe.add(crown, BorderLayout.NORTH);

        //results
        DefaultListModel<String> feed = new DefaultListModel<>();
        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Results"));

        // details
        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        tea.setLineWrap(true);
        tea.setWrapStyleWord(true);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book Details"));

        // divider
        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(300);
        vibe.add(beef, BorderLayout.CENTER);

        yeet.addActionListener(e -> {
            String OG = (String) grower.getSelectedItem();
            feed.clear();
            tea.setText("");
            if (OG == null || OG.isEmpty()) {
                return;
            }
            List<String> bets = plug.getRecommendationsByGenre(OG);
            for (String handle : bets) {
                feed.addElement(handle);
            }
            if (!feed.isEmpty()) {
                lineup.setSelectedIndex(0);
            }
        });

        lineup.addListSelectionListener(e -> {
            String handle = lineup.getSelectedValue();
            if (handle != null) {
                BookInfo bookie = peep.search(handle);
                if (bookie != null) {
                    tea.setText(bookie.toString());
                    tea.setCaretPosition(0);
                }
            }
        });



        return vibe;
    }

    //-----------------------------
    // SIMILAR BOOKS
    //-----------------------------
    /**
     * Similar Books tab
     * uses book recommender to give recommendation on the inputted title or the closest match to it
     *
     * @return JPanel for the Similar Books tab
     */
    private JPanel makeTwins(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField og = new JTextField();
        JButton yeet = new JButton("Find Similar");
        JLabel vibes = new JLabel(" ");

        JPanel crown = new JPanel(new BorderLayout(6, 0));
        crown.add(new JLabel("Seed Title:  "), BorderLayout.WEST);
        crown.add(og, BorderLayout.CENTER);
        crown.add(yeet, BorderLayout.EAST);

        JPanel lid = new JPanel(new BorderLayout(0, 4));
        lid.add(crown, BorderLayout.NORTH);
        lid.add(vibes, BorderLayout.SOUTH);
        vibe.add(lid, BorderLayout.NORTH);

        DefaultListModel<String> feed = new DefaultListModel<>();
        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Recommended"));

        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        tea.setLineWrap(true);
        tea.setWrapStyleWord(true);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book Details"));

        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(340);
        vibe.add(beef, BorderLayout.CENTER);

        yeet.addActionListener(e -> {
            String lewk = og.getText().trim();
            feed.clear();
            tea.setText("");
            vibes.setText(" ");
            if (lewk.isEmpty()) {
                return;
            }
        
            String legit = lewk;
            if (!plug.containsBook(lewk)) {
                List<String> pings = peep.suggest(lewk);
                if (pings.isEmpty()) {
                    vibes.setText("No book matched \"" + lewk + "\".");
                    return;
                }
                legit = pings.get(0);
                vibes.setText("Using closest match: " + peep.search(legit).getTitle());
            }

            List<String> bets = plug.getRecommendations(legit);
            if (bets.isEmpty()) {
                vibes.setText("No related titles found.");
                return;
            }
            for (String handle : bets) {
                feed.addElement(handle);
            }
            lineup.setSelectedIndex(0);
        });

        og.addActionListener(e -> yeet.doClick());

        lineup.addListSelectionListener(e -> {
            String handle = lineup.getSelectedValue();
            if (handle != null) {
                BookInfo bookie = peep.search(handle);
                if (bookie != null) {
                    tea.setText(bookie.toString());
                    tea.setCaretPosition(0);
                }
            }
        });

        return vibe;
    }

    //-----------------------------
    // FIlter
    //-----------------------------
    /**
     * Filter tab
     * Tuses FilterBooks and optional quicksort by rating
     *
     * @return JPanel for the Filter tab
     */
    private JPanel makeFilter(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        // dropdown
        String[] flavas = { "genre", "author", "language", "year" };
        JComboBox<String> mood = new JComboBox<>(flavas);
        JTextField gab = new JTextField(15);
        JComboBox<String> lewks = new JComboBox<>(distinctGenres().toArray(new String[0]));
        JComboBox<String> language = new JComboBox<>(distinctLanguages().toArray(new String[0]));

        gab.setVisible(false);
        lewks.setVisible(true);
        language.setVisible(false);

        JCheckBox tiered = new JCheckBox("Sort by rating", true);
        JButton yeet = new JButton("Apply");
        JLabel vibes = new JLabel(" ");

        JPanel dash = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
        dash.add(new JLabel("Filter by:"));
        dash.add(mood);
        dash.add(gab);
        dash.add(lewks);
        dash.add(language);
        dash.add(tiered);
        dash.add(yeet);

        JPanel lid = new JPanel(new BorderLayout(0, 4));
        lid.add(dash, BorderLayout.CENTER);
        lid.add(vibes, BorderLayout.SOUTH);
        vibe.add(lid, BorderLayout.NORTH);

        DefaultListModel<String> feed = new DefaultListModel<>();
        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Matching Books"));

        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        tea.setLineWrap(true);
        tea.setWrapStyleWord(true);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book Details"));

        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(400);
        vibe.add(beef, BorderLayout.CENTER);

        mood.addActionListener(e -> {
            String pick = (String) mood.getSelectedItem();
            gab.setVisible(false);
            lewks.setVisible(false);
            language.setVisible(false);
            if ("genre".equals(pick)) {
                lewks.setVisible(true);
            } else if ("language".equals(pick)) {
                language.setVisible(true);
            } else {
                gab.setVisible(true);
            }
            dash.revalidate();
            dash.repaint();
        });

        final List<BookInfo> crew = new ArrayList<>();

        yeet.addActionListener(e -> {
            String pick = (String) mood.getSelectedItem();

            String lewk;
            if ("genre".equals(pick)) {
                Object sel = lewks.getSelectedItem();
                lewk = (sel == null) ? "" : sel.toString();
            } else if ("language".equals(pick)) {
                Object sel = language.getSelectedItem();
                lewk = (sel == null) ? "" : sel.toString();
            } else {
                lewk = gab.getText().trim();
            }

            feed.clear();
            tea.setText("");
            crew.clear();

            if (lewk.isEmpty()) {
                vibes.setText("Enter a value to filter by.");
                return;
            }

            // run the filter and optionally sort with our quicksort
            List<String[]> rows;
            try {
                rows = FilterBooks.filter(BIBLE, pick, lewk);
            } catch (IOException ex) {
                rows = new ArrayList<>();
            }

            List<BookInfo> dubs = new ArrayList<>();
            for (String[] row : rows) {
                BookInfo bookie = peep.search(row[1]);
                if (bookie != null) {
                    dubs.add(bookie);
                }
            }

            // optionally sort matches by rating using our quicksort
            if (tiered.isSelected()) {
                SortBooksByRatingQuickSort.quickSortBooks(dubs, 0, dubs.size() - 1);
            }
            crew.addAll(dubs);

            for (BookInfo bookie : dubs) {
                double clout = parseRating(bookie.getAverageRating());
                feed.addElement(String.format("%.2f  %s", clout, bookie.getTitle()));
            }
            vibes.setText(dubs.size() + " result" + (dubs.size() == 1 ? "" : "s")
                    + (tiered.isSelected() ? " (sorted by rating)" : ""));
        });

        gab.addActionListener(e -> yeet.doClick());

        lineup.addListSelectionListener(e -> {
            int slot = lineup.getSelectedIndex();
            if (slot >= 0 && slot < crew.size()) {
                BookInfo bookie = crew.get(slot);
                tea.setText(bookie.toString());
                tea.setCaretPosition(0);
            }
        });
        return vibe;
    }

    /**
     * same as genres but languages
     * creates a list of all existing languages separated b comma
     *
     * @return alphabetically sorted list of unique language codes
     */
    private List<String> distinctLanguages() {
        List<String> squad = new ArrayList<>();
        for (BookInfo bookie : peep.getAllBooks()) {
            String lingo = bookie.getLanguage().trim();
            if (!squad.contains(lingo)) {
                squad.add(lingo);
            }
        }
        Collections.sort(squad);
        return squad;
    }

    /**
     *parses a rating string into a double or 0.0 if can't be parsed
     *
     * @param clout the string rating
     * @return the double rating or 0.0 
     */
    private double parseRating(String clout) {
            if (clout == null) {
                return 0.0;
            }
            try {
                return Double.parseDouble(clout.trim());
            } catch (NumberFormatException e) {
                return 0.0;
            }
        }
        

    /**
     * main.
     *
     *
     * @param args command line args
     */
    public static void main(String[] args){
        try {
            new BookRecommenderGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
