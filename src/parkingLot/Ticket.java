package parkingLot;

import java.time.Instant;

public record Ticket(Vehicle vehicle, ParkingSpot parkingSpot, Instant createdAt) {
}
