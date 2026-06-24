package libraryManagementSystem;

import java.time.LocalDate;

public record Ticket(Copy copy, User user, LocalDate returnDate) {
}
