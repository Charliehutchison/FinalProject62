//so I used claude to show me an example of a gui and now i'm gonna try to recreate it WISH ME LUCK
// this is art

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * window for the Book Recommender
 */
public class BookRecommenderGUI extends JFrame {

    private static final String BIBLE = "datasets/books_merged_clean.csv";
    private static final int GOATS = 100;

    private Lookup peep;
    private BookRecommender plug;

    public BookRecommenderGUI() throws IOException {
        peep = new Lookup(BIBLE);
        plug = new BookRecommender(BIBLE);

        setTitle("Book Recommender");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1920, 1080);
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
    // Top rated GOATS
    //-----------------------------
    private JPanel makeGoats(){
        //this stays the same because it's the big window
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));List<BookInfo> ranked = new ArrayList<>(peep.getAllBooks());
        
        SortBooksByRatingQuickSort.quickSortBooks(ranked, 0, ranked.size() - 1);
        // TOP 100
        List<BookInfo> cream = ranked.subList(0, GOATS-1);

        DefaultListModel<String> feed = new DefaultListModel<>();
        for (BookInfo bookie : cream){
            feed.addElement(bookie.getTitle());
        }

        JList<String> lineup = new JList<>(feed);
        lineup.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane swipe = new JScrollPane(lineup);
        swipe.setBorder(BorderFactory.createTitledBorder("Top" + GOATS + "books by rating"));
        
        JTextArea tea = new JTextArea();
        tea.setEditable(false);
        JScrollPane tealeaf = new JScrollPane(tea);
        tealeaf.setBorder(BorderFactory.createTitledBorder("Book details"));
        
        JSplitPane beef = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, swipe, tealeaf);
        beef.setDividerLocation(380);
        vibe.add(beef, BorderLayout.CENTER);

        lineup.addListSelectionListener(e -> {
            String handle = lineup.getSelectedValue();
            BookInfo bookie = peep.search(handle);
            tea.setText(bookie.toString());
            tea.setCaretPosition(0);
        });
        return vibe;

    }

    //-----------------------------
    // BY GENRE!
    //-----------------------------
    //extract genres
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

    private JPanel makeGenre(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));


        //DROPDOWN!!!
        //needs a method to extract all existing genres
        List<String> genres = distinctGenres();
        JComboBox grower = new JComboBox<>(genres.toArray(new String[0]));
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
                tea.setText(bookie.toString());
                tea.setCaretPosition(0);
            }
        });



        return vibe;
    }

    //-----------------------------
    // SIMILAR BOOKS
    //-----------------------------
    private JPanel makeTwins(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        


        return vibe;
    }

    //-----------------------------
    // FIlter
    //-----------------------------
    private JPanel makeFilter(){
        JPanel vibe = new JPanel(new BorderLayout(8, 8));
        vibe.setBorder(new EmptyBorder(10, 10, 10, 10));

        


        return vibe;
    }

    public static void main(String[] args){
        try {
            new BookRecommenderGUI();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
