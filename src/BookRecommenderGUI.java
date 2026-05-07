import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.stream.*;

public class BookRecommenderGUI extends JFrame {

    private static final String DATASET = "datasets/books_merged_clean.csv";
    private static final int TOP_N = 100;

    private final Lookup lookup;

    public BookRecommenderGUI() throws IOException {
        lookup = new Lookup(DATASET);

        setTitle("Book Recommender");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1050, 680);
        setMinimumSize(new Dimension(700, 500));
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Search", buildSearchPanel());
        tabs.addTab("Top Rated", buildTopRatedPanel());
        add(tabs);
        setVisible(true);
    }

    private JPanel buildSearchPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTextField searchField = new JTextField();
        JButton searchBtn = new JButton("Search");
        JPanel topBar = new JPanel(new BorderLayout(6, 0));
        topBar.add(new JLabel("Search by Title:  "), BorderLayout.WEST);
        topBar.add(searchField, BorderLayout.CENTER);
        topBar.add(searchBtn, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> bookList = new JList<>(listModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(bookList);
        listScroll.setPreferredSize(new Dimension(300, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Results"));

        JTextArea detailArea = makeDetailArea();
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Book Details"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailScroll);
        split.setDividerLocation(300);
        panel.add(split, BorderLayout.CENTER);

        Runnable doSearch = () -> {
            String q = searchField.getText().trim();
            listModel.clear();
            detailArea.setText("");
            if (q.isEmpty()) return;

            BookInfo exact = lookup.search(q);
            if (exact != null) {
                listModel.addElement(exact.getTitle());
            } else {
                List<String> suggestions = lookup.suggest(q);
                Collections.sort(suggestions);
                for (String s : suggestions) {
                    BookInfo b = lookup.search(s);
                    if (b != null) listModel.addElement(b.getTitle());
                }
            }
            if (!listModel.isEmpty()) bookList.setSelectedIndex(0);
        };

        searchBtn.addActionListener(e -> doSearch.run());
        searchField.addActionListener(e -> doSearch.run());
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { SwingUtilities.invokeLater(doSearch); }
            public void removeUpdate(DocumentEvent e) { SwingUtilities.invokeLater(doSearch); }
            public void changedUpdate(DocumentEvent e) { SwingUtilities.invokeLater(doSearch); }
        });

        bookList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            String title = bookList.getSelectedValue();
            if (title == null) return;
            BookInfo book = lookup.search(title);
            if (book != null) {
                detailArea.setText(book.toString());
                detailArea.setCaretPosition(0);
            }
        });

        return panel;
    }

    private JPanel buildTopRatedPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        List<BookInfo> sorted = lookup.letBookHashMap.values().stream()
            .sorted((a, b) -> Double.compare(
                parseRating(b.getAverageRating()),
                parseRating(a.getAverageRating())))
            .limit(TOP_N)
            .collect(Collectors.toList());

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (BookInfo book : sorted) {
            listModel.addElement(String.format("%.2f  %s",
                parseRating(book.getAverageRating()), book.getTitle()));
        }

        JList<String> bookList = new JList<>(listModel);
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane listScroll = new JScrollPane(bookList);
        listScroll.setPreferredSize(new Dimension(380, 0));
        listScroll.setBorder(BorderFactory.createTitledBorder("Top " + TOP_N + " Books by Rating"));

        JTextArea detailArea = makeDetailArea();
        JScrollPane detailScroll = new JScrollPane(detailArea);
        detailScroll.setBorder(BorderFactory.createTitledBorder("Book Details"));

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailScroll);
        split.setDividerLocation(380);
        panel.add(split, BorderLayout.CENTER);

        bookList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            int idx = bookList.getSelectedIndex();
            if (idx < 0 || idx >= sorted.size()) return;
            BookInfo book = sorted.get(idx);
            detailArea.setText(book.toString());
            detailArea.setCaretPosition(0);
        });

        return panel;
    }

    private JTextArea makeDetailArea() {
        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        return area;
    }

    private double parseRating(String rating) {
        try {
            return Double.parseDouble(rating.trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                new BookRecommenderGUI();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
    }
}
