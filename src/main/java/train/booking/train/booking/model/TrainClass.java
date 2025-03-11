package train.booking.train.booking.model;

public enum TrainClass {
    ECONOMY(200.0, 300.0, 200),
    VIP(300.0, 500.0, 400),
    FIRST_CLASS(400.0, 700.0, 100);

    private final double minorPrice;
    private final double adultPrice;
    private final int totalSeats;

    TrainClass(double minorPrice, double adultPrice, int totalSeats) {
        this.minorPrice = minorPrice;
        this.adultPrice = adultPrice;
        this.totalSeats = totalSeats;
    }

    public double getMinorPrice() {

        return minorPrice;
    }

    public double getAdultPrice() {
        return adultPrice;
    }

    public int getTotalSeats() {
        return totalSeats;
    }
}