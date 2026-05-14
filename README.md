# BookBridge
This ReadMe was written entirely by our group. However,  I, Charlie, wanted the ReadMe to look as stylish as our assignments do so I gave AI the following prompt: "Help me style this ReadMe so it looks clean when reading on GitHub." 

The content is all from our group, it's just formatting and syntax that was changed.

This project reads a merged books CSV. There is a lookup program (hash map by title), command-line filter/sort tools, a recommender that suggests similar books or books by genre keyword, and a simple Swing GUI.

**Dataset:** `datasets/books_merged_clean.csv` is in the repo.

**Libraries:** JDK only. No extra jars, no `/lib` folder.

**Package:** All classes are in package `bookbridge` under `src/bookbridge/`.

## How to run

## Feature 1: Book Lookup
Enter a book title to receive information about the book. The search is case-insensitive and supports partial title matching. Keep entering books, or type 'quit' to exit.

Compile once from the repo root (creates `bin/bookbridge/*.class`):

```
mkdir -p bin
javac -encoding UTF-8 -d bin src/bookbridge/*.java
```

Programs — run with the **fully qualified class name** (`bookbridge.` …):

- **Lookup.java** (`main`): `java -cp bin bookbridge.Lookup`
- **BookRecommender.java** (`main`): `java -cp bin bookbridge.BookRecommender datasets/books_merged_clean.csv` — or leave off the path; it defaults to `datasets/books_merged_clean.csv`
- **BookRecommenderGUI.java** (`main`): `java -cp bin bookbridge.BookRecommenderGUI`
- **SortBooksByRatingQuickSort.java** (`main`): `java -cp bin bookbridge.SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/out_sorted.csv`
- **FilterBooks.java** (`main`): `java -cp bin bookbridge.FilterBooks datasets/books_merged_clean.csv genre Fantasy`

Filter types for FilterBooks: `genre`, `author`, `language`, `publisher`, `year`.

Sample Lookup output (real run for title `1984`):

```
Enter book title (or 'quit' to exit): 
1984

============================================================
 1984
============================================================
 Author(s): George Orwell
 Publisher: Signet Classics
 Published: 1981-07-01
 Pages: 268
 Language: ENG
 ISBN: 0451516753
 Average Rating: 4.18 ⭐  (1322 ratings)
 Genres:  Classics, Fiction, Science Fiction, ...
 Description: The new novel by George Orwell ...
 More Info: https://www.goodreads.com/book/show/61439040-1984
============================================================
```

## Public API (methods + examples)

### Lookup

Constructor `Lookup(String filepath)` loads the CSV into a HashMap keyed by lowercased title.

- `BookInfo search(String title)`: exact match on title (case-insensitive). Returns `null` if missing. Example: `search("1984")` returns the BookInfo for 1984 or null.
- `List<String> suggest(String prefix)`: titles whose key starts with `prefix` (lowercased). Example: `suggest("harry")` returns keys like harry potter entries.
- `Collection<BookInfo> getAllBooks()`: all loaded books (used by the recommender).

### BookInfo

Constructor takes the CSV fields as strings (see source). Getters like `getTitle()`, `getAverageRating()`, `getPrimaryAuthor()`, etc. `toString()` prints a readable block for the terminal.

Example: if `book` is the row for 1984, `book.getAverageRating()` might return 
`"4.19"`.

### BookRecommender

Implements `BookRecommenderInterface`. Keeps data in an internal Lookup.

- `BookRecommender()` — empty; call `loadData(String filename)` before using.
- `BookRecommender(String filename)` — loads from file.
- `loadData(String filename)` — reload catalog (throws UncheckedIOException on bad IO).
- `List<String> getRecommendations(String bookTitle)` — up to 15 other titles (genre overlap / same primary author), sorted by our score + rating. Bad title → empty list.
- `List<String> getRecommendationsByGenre(String genre)` — up to 15 titles whose genres field contains the substring (case-insensitive), sorted by average rating.
- `boolean containsBook(String bookTitle)` — true if exact title is in the catalog.

Examples:

- `new BookRecommender("datasets/books_merged_clean.csv").containsBook("1984")` → `true` if that title exists.
- `getRecommendations("Harry Potter and the Half-Blood Prince (Harry Potter  #6)")` → list of similar titles (max 15), not including the seed; spelling must match CSV.
- `getRecommendationsByGenre("fantasy")` → up to 15 fantasy-ish rows by rating.

`searchByTitle`, `searchByAuthor`, `filterByGenre`, `getTopRatedBooks`, `getAllBooksAlphabetically` are interface stubs returning empty lists — use FilterBooks / Lookup for that stuff.

`main`: optional `args[0]` = CSV path. Prompts for exact title, or `genre something`, or `quit`.

### SortBooksByRatingQuickSort

`main` takes input CSV path and output CSV path. Finds `average_rating` from the header, quicksorts rows descending by rating, writes new file.

Example: `java -cp bin bookbridge.SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/sorted.csv` creates `datasets/sorted.csv`.

### FilterBooks

`main` takes: CSV path, filter name ,`genre`, `author`, `language`, `publisher`, `year`, value. Prints matching rows to stdout (values compared lowercase where it matters).

Examples:

- `java -cp bin bookbridge.FilterBooks datasets/books_merged_clean.csv author "j.k. rowling"`
- `java -cp bin bookbridge.FilterBooks datasets/books_merged_clean.csv year 2005`

### BookRecommenderGUI

- `BookRecommenderGUI()` — builds the tabs (search, top rated, similar books, genre).
- `main` — starts the Swing window.

Example: `java -cp bin bookbridge.BookRecommenderGUI` (needs a display).

## Features (assignment mapping)

1. Lookup: run **`bookbridge.Lookup`** (`Lookup.java`), CSV path is wired in `main`.
2. Filter/sort: **`bookbridge.FilterBooks`** and **`bookbridge.SortBooksByRatingQuickSort`** (`FilterBooks.java`, `SortBooksByRatingQuickSort.java`).
3. Recommendations: **`bookbridge.BookRecommender`** (`BookRecommender.java`) or the Browse by Genre tab in the GUI (`BookRecommenderGUI.java`).
