This is our Final Projects for Data Structures and Algorithms, Pomona College's CS 062 course.


### Part 1/Feature 1:
Sort CSV by rating with the following CLI argument:
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

If you have not compiled, run the following CLI argument.
javac SortBooksByRatingQuickSort.java
java SortBooksByRatingQuickSort datasets/books_merged_clean.csv datasets/books_merged_clean_sorted_by_rating.csv

### Feature 2:
Filter books and print results to the console.
java FilterBooks <input.csv> <filter> <value>
The available filtering options to choose from. (genre, author, language, publisher, year)

Examples:
java FilterBooks datasets/books_merged_clean.csv author "J.K. Rowling"
java FilterBooks datasets/books_merged_clean.csv year 2005

If you have not compiled, run the following CLI argument for Fantasy genre for example.
javac FilterBooks.java
java FilterBooks datasets/books_merged_clean.csv genre Fantasy
