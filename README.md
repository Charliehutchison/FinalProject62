This is our Final Projects for Data Structures and Algorithms, Pomona College's CS 062 course.


### Feature 1:
Run Lookup.java and enter a book title to recieve information about the book.
Keep entering books, or type 'quit' to exit.

If you have not compiled, run the following CLI arguments:
javac BookInfo.java
javac Lookup.java
java Lookup

### Feature 2:
Sort CSV by rating with the following CLI argument:
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

If you have not compiled, run the following CLI argument.
javac SortBooksByRatingQuickSort.java
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

Filter books and print results to the console.
java FilterBooks <input.csv> <filter> <value>
The available filtering options to choose from. (genre, author, language, publisher, year)

Examples:
java FilterBooks datasets/books_merged_clean.csv author "J.K. Rowling"
java FilterBooks datasets/books_merged_clean.csv year 2005

If you have not compiled, run the following CLI argument for Fantasy genre for example.
javac FilterBooks.java
java FilterBooks datasets/books_merged_clean.csv genre Fantasy
