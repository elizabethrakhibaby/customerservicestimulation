import java.text.DecimalFormat;
import java.util.function.Supplier;

abstract class Event {
    protected final double timeOfEvent;
    protected final Customer customer;
    protected final double cusArrivalTime;
    protected final int cusIndex;
    protected final BaseServer attachedServer;

    //this is to generate Arrival and Leave Events
    Event(double timeOfEvent, Customer customer) {
        this.timeOfEvent = timeOfEvent;
        this.customer = customer;
        this.cusArrivalTime = customer.getArrivalTime();
        this.cusIndex = customer.getIndexOfCustomer();

        //we are going to attach a fake server
        BaseServer fakeServer = new SCServer(1,1,1);
        this.attachedServer = fakeServer;
    }

    //Overloaded constructor
    //this is to generate Serve, Wait, W2 and Done Events
    Event(double timeOfEvent, Customer customer, BaseServer attachedServer) {
        this.timeOfEvent = timeOfEvent;
        this.customer = customer;
        this.cusArrivalTime = customer.getArrivalTime();
        this.cusIndex = customer.getIndexOfCustomer();
        this.attachedServer = attachedServer;
    }

    public abstract Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers);

    public Customer getCustomer() {
        return this.customer;
    }

    public double getCusArrivalTime() {
        return this.cusArrivalTime;
    }

    public int getCusIndex() {
        return this.cusIndex;
    }


    public double getTimeOfEvent() {
        return this.timeOfEvent;
    }


    public BaseServer getAttachedServer() {
        return this.attachedServer;
    }

    public String toString() {
        return "testing";
    }
}
