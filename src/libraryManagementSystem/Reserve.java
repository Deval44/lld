package libraryManagementSystem;

import java.time.LocalDate;

public record Reserve(Copy copy, User user, LocalDate bookBefore) {
}
