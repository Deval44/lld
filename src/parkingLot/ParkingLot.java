package parkingLot;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ParkingLot {
    private final Set<ParkingSpot> availableParkingSpots;
    private PricingStrategy pricingStrategy;

    public ParkingLot(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
        availableParkingSpots = ConcurrentHashMap.newKeySet();
    }

    public void addParkingSpot(ParkingSpot parkingSpot) {
        availableParkingSpots.add(parkingSpot);
    }

    public void removeParkingSpot(ParkingSpot parkingSpot) {
        availableParkingSpots.remove(parkingSpot);
    }

    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }

    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }

    public Optional<ParkingSpot> assignParkingSpot(Vehicle vehicle) {
        Optional<ParkingSpot> availableSpot = availableParkingSpots.stream()
                .filter(p -> vehicle.vehicleType().getSpots().contains(p.spotType()))
                .findAny();

        availableSpot.ifPresent(this::removeParkingSpot);
        IO.println("Assigning parking spot to vehicle: " + availableSpot.orElse(null));
        return availableSpot;
    }

    public Ticket enter(Vehicle vehicle, ParkingSpot parkingSpot, Instant time) {
        Ticket ticket = new Ticket(vehicle, parkingSpot, time);
        IO.println("Entering parking spot: " + ticket);
        return ticket;
    }

    public double exit(Ticket ticket){
        IO.println("Exit for parkingLot.Vehicle: " + ticket.vehicle() + " , adding spot back: " + ticket.parkingSpot());
        addParkingSpot(ticket.parkingSpot());
        return pricingStrategy.calculatePrice(ticket);
    }

    public boolean completePayment(double price, PaymentMode paymentMode) {
        return PaymentFactory.completePayment(price, paymentMode);
    }

    public void displayAvailableParkingSpots() {
        IO.println("Available Parking Spots:");
        availableParkingSpots.forEach(IO::println);
    }
}

