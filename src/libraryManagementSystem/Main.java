package libraryManagementSystem;

public class Main {
    void main() {
        try(Library library = new Library(10, 10, 20)){
            Book book1 = new Book("i1", "t1", "a1");
            Book book2 = new Book("i2", "t2", "a1");
            Book book3 = new Book("i3", "t1", "a2");

            library.addBook(book1);
            library.addBook(book2);
            library.addBook(book3);

            Copy copy1 = library.addCopy(book1);
            Copy copy2 = library.addCopy(book2);
            Copy copy3 = library.addCopy(book3);

            IO.println("Books in library");
            library.getBooks().forEach(System.out::println);

            IO.println("Books in library by author: " + "a1");
            library.getBooksByAuthor("a1").forEach(System.out::println);

            IO.println("Books in library by isbn: " + "i1");
            IO.println(library.getBooksByIsbn("i1"));

            IO.println("Books in library by title: " + "t1");
            library.getBooksByTitle("t1").forEach(System.out::println);


            //add other tests -> check -> improve
        }

        //also this system can be extended to cater strategy pattern as well.
        //strategy -> fine strategy, recommendation strategy
    }
}
