import java.text.DecimalFormat;
import java.util.function.Supplier;

class LeaveEvent extends Event {

    LeaveEvent(double arrivalTime, Customer customer) {
        super(arrivalTime, customer);
    }

    @Override
    public Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers) {
        Pair<Event, ImList<BaseServer>> returnPair = 
            new Pair<Event, ImList<BaseServer>>(this,tempServers);
        return returnPair;
    }


    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(this.getTimeOfEvent()) + " " + (this.getCusIndex() + 1)  + " leaves\n";
    }

}
