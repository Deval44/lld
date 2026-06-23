package parkingLot;

import java.util.Set;

public enum VehicleType {
    BIKE(Set.of(SpotType.MOTORCYCLE)),
    CAR(Set.of(SpotType.COMPACT, SpotType.LARGE)),
    BUS(Set.of(SpotType.LARGE)),
    TRUCK(Set.of(SpotType.LARGE));

    private final Set<SpotType> spots;

    VehicleType(Set<SpotType> spots) {
        this.spots = spots;
    }
    public Set<SpotType> getSpots() {
        return spots;
    }
}
