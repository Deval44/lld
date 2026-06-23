package parkingLot;

public interface PricingStrategy {
    double calculatePrice(Ticket ticket);
}
