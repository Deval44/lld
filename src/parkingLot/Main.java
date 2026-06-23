import parkingLot.*;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    IO.println("Welcome to Parking Spot Calculator.");
    ParkingLot parkingLot = new ParkingLot(new HourlyRatePricing(10));

    //add spots
    parkingLot.addParkingSpot(new ParkingSpot("a1", SpotType.MOTORCYCLE, "a"));

    //try entering vehicles
    Vehicle bike1 = new Vehicle("01", VehicleType.BIKE);
    Vehicle bike2 = new Vehicle("02", VehicleType.BIKE);
    Vehicle car1 = new Vehicle("03", VehicleType.CAR);
    Vehicle car2 = new Vehicle("04", VehicleType.CAR);
    Vehicle bus = new Vehicle("05", VehicleType.BUS);
    Vehicle truck = new Vehicle("06", VehicleType.TRUCK);

    //test1 - valid empty spot
//    test1(parkingLot, bike1);

    //test2 - no valid spot
//    test2(parkingLot, car1);

    //test3 - car enters compact and large, different payments
    parkingLot.addParkingSpot(new ParkingSpot("a2", SpotType.COMPACT, "a"));
    parkingLot.addParkingSpot(new ParkingSpot("a3", SpotType.LARGE, "a"));
    test3(parkingLot, car1, car2, bus);
}

private static void test3(ParkingLot parkingLot, Vehicle car1, Vehicle car2, Vehicle bus) {
    IO.println("\n\nTest 3 car enters compact and large, different payments ==========================");
    Optional<ParkingSpot> parkingSpot = parkingLot.assignParkingSpot(car1);
    Optional<ParkingSpot> parkingSpot2 = parkingLot.assignParkingSpot(car2);
    Optional<ParkingSpot> parkingSpot3 = parkingLot.assignParkingSpot(bus);
    if (parkingSpot.isPresent()) {
        Ticket ticket = parkingLot.enter(car1, parkingSpot.get(), Instant.now().minus(Duration.ofMinutes(61)));
        double fee = parkingLot.exit(ticket);
        parkingLot.completePayment(fee, PaymentMode.CARD);
    }else{
        IO.println("Not able to enter");
    }
    if (parkingSpot2.isPresent()) {
        Ticket ticket = parkingLot.enter(car2, parkingSpot2.get(), Instant.now().minus(Duration.ofMinutes(10)));
        double fee = parkingLot.exit(ticket);
        parkingLot.completePayment(fee, PaymentMode.CARD);
    }else{
        IO.println("Not able to enter");
    }
    if (parkingSpot3.isPresent()) {
        Ticket ticket = parkingLot.enter(bus, parkingSpot3.get(), Instant.now().minus(Duration.ofMinutes(300)));
        double fee = parkingLot.exit(ticket);
        parkingLot.completePayment(fee, PaymentMode.CASH);
    }else{
        IO.println("Not able to enter");
    }
}

private static void test2(ParkingLot parkingLot, Vehicle car1) {
    IO.println("\n\nTest 2 no valid spot ==========================");
    Optional<ParkingSpot> parkingSpot = parkingLot.assignParkingSpot(car1);
    if (parkingSpot.isPresent()) {
        Ticket ticket = parkingLot.enter(car1, parkingSpot.get(), Instant.now().minus(Duration.ofMinutes(61)));
        double fee = parkingLot.exit(ticket);
        parkingLot.completePayment(fee, PaymentMode.CARD);
    }else{
        IO.println("Not able to enter");
    }
}

private static void test1(ParkingLot parkingLot, Vehicle bike1) {
    IO.println("\n\nTest 1 valid empty spot ==========================");
    Optional<ParkingSpot> parkingSpot = parkingLot.assignParkingSpot(bike1);
    if (parkingSpot.isPresent()) {
        Ticket ticket = parkingLot.enter(bike1, parkingSpot.get(), Instant.now().minus(Duration.ofMinutes(61)));
        double fee = parkingLot.exit(ticket);
        parkingLot.completePayment(fee, PaymentMode.CARD);
    }else{
        IO.println("Not able to enter");
    }
}
