package libraryManagementSystem;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class User {
    private final String id;
    private final String name;

    private final Set<Ticket> tickets;
    public User(String id, String name) {
        this.id = id;
        this.name = name;
        tickets = ConcurrentHashMap.newKeySet();
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public void addTicket(Ticket ticket) {
        tickets.add(ticket);
    }

    public void removeTicket(Ticket ticket) {
        tickets.remove(ticket);
    }

    public Set<Ticket> getTickets() {
        return tickets;
    }
}
