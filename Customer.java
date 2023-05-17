import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.function.Supplier;

class Customer {
    private final int indexOfCustomer;
    private final double arrivalTime;
    private final Supplier<Double> serviceTime;

    Customer(int indexOfCustomer, double arrivalTime, Supplier<Double> serviceTime) {
        this.indexOfCustomer = indexOfCustomer;
        this.arrivalTime = arrivalTime;
        //do i need to initialise servicetime here?
        this.serviceTime = serviceTime;
    }

    // accessor methods

    public int getIndexOfCustomer() {
        return this.indexOfCustomer;
    }

    public double getArrivalTime() {
        return this.arrivalTime;
    }

    public Double getServiceTime() {
        return this.serviceTime.get();
    }

}
