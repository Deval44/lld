package libraryManagementSystem;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Library implements AutoCloseable {
    private static final AtomicInteger nextCopyId = new AtomicInteger(1);

    private final Integer defaultBorrowDays;
    private final double lateFine;
    private final double maxFineAllowed;

    private final Map<Book, Queue<User>> bookQueueMap = new ConcurrentHashMap<>();
    private final Queue<Reserve> reserves = new ConcurrentLinkedQueue<>();
    private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();

    //for book issue and return. reads = writes
    private final Set<Ticket> tickets = ConcurrentHashMap.newKeySet();
    private final Map<Book, Set<Copy>> bookToCopiesMap = new ConcurrentHashMap<>();

    //for display, section reads >> writes
    private final Set<Book> books = ConcurrentHashMap.newKeySet();
    private final Map<String, Set<Book>> titleToBooksMap = new ConcurrentHashMap<>();
    private final Map<String, Set<Book>> authorToBooksMap = new ConcurrentHashMap<>();
    private final Map<String, Book> isbnToBookMap = new ConcurrentHashMap<>();

    public Library(Integer defaultBorrowDays,  double lateFine, double maxFineAllowed) {
        this.defaultBorrowDays = defaultBorrowDays;
        this.lateFine = lateFine;
        this.maxFineAllowed = maxFineAllowed;
        scheduleCleaner();
    }

    private void scheduleCleaner() {
        scheduledExecutorService.scheduleWithFixedDelay(
                this::cleanReserves,
                10,
                10,
                TimeUnit.SECONDS
        );
    }

    private void cleanReserves() {
        IO.println("Cleaning up reserves...");
        while(!reserves.isEmpty() && reserves.peek().bookBefore().isAfter(LocalDate.now())) {
            Reserve reserve = reserves.poll();
            IO.println("This reserve is no longer valid: " + reserve);
        }
        IO.println("Cleaning up reserves... done");
    }

    //display methods
    public Set<Book> getBooks() {
        return books;
    }

    public Set<Book> getBooksByAuthor(String author) {
        if(null == author) return new HashSet<>();
        return authorToBooksMap.getOrDefault(author, new HashSet<>());
    }

    public Set<Book> getBooksByTitle(String title) {
        if(null == title) return new HashSet<>();
        return titleToBooksMap.getOrDefault(title, new HashSet<>());
    }

    public Book getBooksByIsbn(String isbn) {
        if(null == isbn) return null;
        return isbnToBookMap.get(isbn);
    }

    public void addBook(Book book) {
        books.add(book);
        Set<Book> bookSet1 = titleToBooksMap.getOrDefault(book.title(), ConcurrentHashMap.newKeySet());
        bookSet1.add(book);
        titleToBooksMap.put(book.title(), bookSet1);

        Set<Book> bookSet2 = authorToBooksMap.getOrDefault(book.author(), ConcurrentHashMap.newKeySet());
        bookSet2.add(book);
        authorToBooksMap.put(book.author(), bookSet2);

        isbnToBookMap.put(book.isbn(), book);
    }

    public void removeBook(Book book) {
        books.remove(book);
        titleToBooksMap.getOrDefault(book.title(), new HashSet<>()).remove(book);
        authorToBooksMap.getOrDefault(book.author(), new HashSet<>()).remove(book);
        isbnToBookMap.remove(book.isbn());
        bookToCopiesMap.remove(book);
    }

    public Copy addCopy(Book book) {
        Set<Copy> copies = bookToCopiesMap.getOrDefault(book, ConcurrentHashMap.newKeySet());
        Copy copy = new Copy(nextCopyId.getAndIncrement(), book);
        copies.add(copy);
        bookToCopiesMap.put(book, copies);
        checkReservation(copy.book());
        return copy;
    }

    public void removeCopy(Copy copy) {
        bookToCopiesMap.get(copy.book()).remove(copy);
    }

    public Optional<Ticket> borrowBook(Book book, User user, Integer borrowDays) {
        if(!books.contains(book) || userRestricted(user)){
            IO.println("Invalid book or user not allowed");
            return Optional.empty();
        }

        Optional<Copy> copy = borrowBook(book);
        return copy.map(c -> {
            Ticket ticket = new Ticket(c, user, LocalDate.now().plusDays(Math.min(borrowDays, defaultBorrowDays)));
            user.addTicket(ticket);
            tickets.add(ticket);
            IO.println("Ticket: " +  ticket + " ,created for user: " + user.getName());
            return ticket;
        });
    }

    //could use strategy here
    private boolean userRestricted(User user) {
        Set<Ticket> tickets = user.getTickets();
        Double fine = tickets.stream()
                .map(this::calculateFine)
                .reduce(Double::sum).orElse(0.0);

        if(fine > maxFineAllowed) {
            IO.println("User not allowed to borrow books! Current fine is " + fine);
            return true;
        }

        IO.println("User allowed to borrow books!");
        return false;
    }

    //could use strategy here
    private double calculateFine(Ticket ticket) {
        LocalDate now = LocalDate.now();
        if(now.isAfter(ticket.returnDate())) {
            IO.println("Penalizing ticket with fine: " + lateFine);
            return lateFine;
        }

        return 0.0;
    }

    private Optional<Copy> borrowBook(Book book) {
        IO.println("Borrowing book " + book);
        Optional<Copy> optionalCopy = bookToCopiesMap.get(book).stream().findAny();
        if(optionalCopy.isPresent()) {
            IO.println("Copy borrowed " + optionalCopy.get());
            bookToCopiesMap.get(book).remove(optionalCopy.get());
        }else  {
            IO.println("No active copy found for " + book);
        }
        return optionalCopy;
    }

    public void returnBook(Ticket ticket) {
        IO.println("Returning copy " + ticket.copy());
        bookToCopiesMap.get(ticket.copy().book()).add(ticket.copy());
        tickets.remove(ticket);

        checkReservation(ticket.copy().book());
    }

    public boolean reserve(Book book, User user) {
        IO.println("Queuing user " + user.getName() + " for book " + book);
        if(userRestricted(user)) {
            IO.println("User not allowed to reserve books!");
            return false;
        }

        Queue<User> reserveQueue = bookQueueMap.getOrDefault(book, new LinkedBlockingQueue<>());
        reserveQueue.add(user);
        bookQueueMap.put(book, reserveQueue);
        IO.println("User: " + user.getName() + " queued for reservation for book " + book);
        checkReservation(book);
        return true;
    }

    private void checkReservation(Book book) {
        new Thread(() -> {
            IO.println("Reserving " + book);
            Optional<Copy> copy = bookToCopiesMap.get(book).stream().findAny();
            if(copy.isPresent()) {
                //remove copy from set
                bookToCopiesMap.get(book).remove(copy.get());

                //reserve it for user and send him notification
                Optional<User> user = Optional.ofNullable(bookQueueMap.getOrDefault(book, new ConcurrentLinkedQueue<>()).poll());
                user.ifPresent(u -> {
                    //sent user notification
                    Reserve reserve = new Reserve(copy.get(), u, LocalDate.now().plusDays(2));

                    //add it to reserves
                    reserves.offer(reserve);

                    IO.println("Copy: " + copy + " for User: " + u.getName() + " reserved!. Please book before: " + reserve.bookBefore());
                });

                //add back to set
                if(user.isEmpty()){
                    IO.println("Adding back");
                    bookToCopiesMap.get(book).add(copy.get());
                }
            }
        });
    }

    @Override
    public void close() {
        IO.println("Closing library. Shutting down cleaner");
        scheduledExecutorService.shutdownNow();
    }
}
