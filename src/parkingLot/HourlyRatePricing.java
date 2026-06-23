package parkingLot;

import java.time.Duration;
import java.time.Instant;

public class HourlyRatePricing implements PricingStrategy {
    private final double hourlyPrice;

    public HourlyRatePricing(double hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    @Override
    public double calculatePrice(Ticket ticket) {
        Instant now = Instant.now();
        double duration = Math.ceil(Duration.between(ticket.createdAt(), now).toMinutes()/60d);
        double price = duration * hourlyPrice;

        IO.println("Price for parkingLot.Ticket: " + ticket + " => "  + price);
        return price;
    }
}
