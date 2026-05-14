# Introduction 

Our project tackles a problem every reader knows too well: not knowing what to read next. In 2021, Vogue published an article titled “Life's Too Short to Finish Books You Don't Like,” highlighting the very real frustration of picking up a book that just doesn't click. A 2017 UK study backed this up, finding that 35% of adults struggle to find a book they truly enjoy. We built BookBridge to change that.

BookBridge has three features wrapped in a Graphical User Interface, all designed to help readers spend less time searching and more time reading. The first feature uses a Hash Table so readers can instantly look up information about any book they're curious about, like descriptions, genres, ratings, and more. The second feature lets readers filter books by parameters like genre, author, language, publisher, and year, then sort the results by rating to find the best of the best. The third feature is a personalized book recommender. Just enter a book or genre you've enjoyed, and BookBridge will suggest what to read next.

## How to Run

Compile all files first:
javac -d bin src/*.java

## Feature 1: Book Lookup
Enter a book title to receive information about the book. The search is case-insensitive and supports partial title matching. Keep entering books, or type 'quit' to exit.

To compile and run:
javac -d bin src/*.java
java -cp bin Lookup

## Public API

### Lookup (Hash Table)

Lookup(String filepath) — constructor that loads the CSV file at the given filepath into the hash table.

search(String title) — takes a book title, returns a BookInfo object or null if not found. Lookup is case-insensitive.

suggest(String prefix) — takes a partial title, returns a List of titles that start with that prefix.

getAllBooks() — returns a Collection of all BookInfo objects in the dataset.

### BookInfo

BookInfo(String bookId, String title, String primaryAuthor, String authors, String publisher, String publicationDate, String language, String numPages, String isbn, String isbn13, String averageRating, String ratingsCount, String textReviewsCount, String genres, String description, String url) — constructor that creates a book object with all fields.

getTitle() — returns the book title.
getAverageRating() — returns the average rating.
getRatingsCount() — returns the number of ratings.
getPrimaryAuthor() — returns the primary author.
getAuthors() — returns all authors.
getNumPages() — returns the number of pages.
getGenres() — returns the genres.
getPublisher() — returns the publisher.
getLanguage() — returns the language code.
getPublicationDate() — returns the publication date.
getDescription() — returns the book description.
getUrl() — returns the Goodreads URL.
toString() — returns a formatted string with all book information for display.

### Usage Examples

```
Enter book title (or 'quit' to exit): harry potter
Did you mean:
1. harry potter and the half-blood prince (harry potter  #6)
2. harry potter and the order of the phoenix (harry potter  #5)
3. harry potter and the prisoner of azkaban (harry potter  #3)
Enter number: 1

============================================================
 Harry Potter and the Half-Blood Prince (Harry Potter  #6)
============================================================
 Author(s): J.K. Rowling
 Publisher: Scholastic Inc.
 Published: 2006-09-16
 Pages: 652
 Language: ENG
 ISBN: 0439785960
 Average Rating: 4.57 (2095690 ratings)
 Genres: Fantasy, Young Adult, Fiction, Magic, Childrens, Adventure
 Description: It is the middle of the summer, but there is an unseasonal mist...

 More Info: https://www.goodreads.com/book/show/1.Harry_Potter_and_the_Half_Blood_Prince
============================================================
```

## Feature 2:
Sort CSV by rating with the following CLI argument:
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

If you have not compiled, run the following CLI argument.
javac SortBooksByRatingQuickSort.java
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

Filter books and print results to the console.
java FilterBooks <input.csv> <filter> <value>
The available filtering options to choose from. (genre, author, language, publisher, year)

Examples:
java FilterBooks datasets/books_merged_clean.csv author "J.K. Rowling";
java FilterBooks datasets/books_merged_clean.csv year 2005

If you have not compiled, run the following CLI argument for Fantasy genre for example.
javac FilterBooks.java
java FilterBooks datasets/books_merged_clean.csv genre Fantasy
