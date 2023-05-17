import java.text.DecimalFormat;
import java.util.function.Supplier;

class DoneEvent extends Event {

    DoneEvent(double endTime, Customer customer, BaseServer attachedServer) {
        super(endTime,customer, attachedServer);
    }

    @Override
    public Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers) {
        BaseServer attachedServer = this.getAttachedServer();


        //CALL GETRESTTIME 
        //if it is a humanserver, getRestTime() will return 0 or a double value
        //if it is a self-checkout server, getRestTime() will return 0 always
        double actualRestingTime = attachedServer.getRestTime(); 

        //newBusyTil = current busyTil + actualRestingTime
        //status is set to true

        //status might be true but still resting/look at busyTil for confirmation..(??)

        int parent = attachedServer.getParent();
        BaseServer oldParent = tempServers.get(parent);
        //updateDoneToDone(attachedServer, actualRestingTime)
        //will be invoked on either human server or megaserver!


        BaseServer newParent = oldParent.updateDoneToDone(attachedServer, actualRestingTime);

        tempServers = tempServers.set(parent, newParent);

        Pair<Event, ImList<BaseServer>> returnPair = 
            new Pair<Event, ImList<BaseServer>>(this, tempServers);

        return returnPair;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(this.getTimeOfEvent()) +
            " " + (this.getCusIndex() + 1)
            + " done serving by "
            + this.getAttachedServer().toString() + "\n";
    }


}

