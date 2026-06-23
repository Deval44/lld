package parkingLot;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;

public class FlatRatePricing implements PricingStrategy {
    private final double morningPrice, eveningPrice, moreThan6HoursPrice;

    FlatRatePricing(double morningPrice, double eveningPrice, double moreThan6HoursPrice) {
        this.morningPrice = morningPrice;
        this.eveningPrice = eveningPrice;
        this.moreThan6HoursPrice = moreThan6HoursPrice;
    }

    @Override
    public double calculatePrice(Ticket ticket) {
        Instant now = Instant.now();
        double duration = Duration.between(now, Instant.now()).toHours();
        double price;
        if(duration > 6){
            price = moreThan6HoursPrice;
            IO.println("Using moreThan6HoursPrice Price for parkingLot.Ticket: " + ticket + " => "  + price);
            return price;
        }

        LocalTime time = LocalTime.ofInstant(ticket.createdAt(), ZoneId.systemDefault());
        int hour = time.getHour();
        if(hour < 12){
            price = morningPrice;
            IO.println("Using morningPrice Price for parkingLot.Ticket: " + ticket + " => " + price);
            return price;
        }

        price = eveningPrice;
        IO.println("Using eveningPrice Price for parkingLot.Ticket: " + ticket + " => " + price);
        return price;
    }
}
