import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.function.Supplier;

class ServeEvent extends Event {


    ServeEvent(double startOfService, Customer customer, BaseServer attachedServer) {
        super(startOfService, customer, attachedServer);
    }

    @Override
    public Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers) {
        //Generate Done Event 
        double serviceTime = this.getCustomer().getServiceTime();

        //this.getTimeOfEvent() is the time when the customer starts getting served
        double endTime = this.getTimeOfEvent() + serviceTime;

        Event done = new DoneEvent(endTime, this.getCustomer(), attachedServer);



        BaseServer attachedServer = this.getAttachedServer();
        //attached server is either a human server or sc server!

        /* For human server,
         * Change status to false and  
         * change busyTil to serviceTime + serve's startTime
         * return humanserver
         * 
         * For sc server,
         * Change megaServer
         * (obtain the position of sc server inside megaServer's list) 
         * create a new sc server - 
         * Change status to false and  
         * change busyTil to serviceTime + serve's startTime
         * insert new sc server into the original position to replace old sc server
         * return megaserver
         */

        double newBusyTil = endTime;
        int parent = attachedServer.getParent();
        BaseServer oldParent = tempServers.get(parent); 

        //updateServeToDone(attachedServer) invoked on either human or megaserver
        BaseServer newParent = oldParent.updateServeToDone(attachedServer,newBusyTil);

        tempServers = tempServers.set(parent, newParent);

        Pair<Event, ImList<BaseServer>> returnPair = 
            new Pair<Event, ImList<BaseServer>>(done, tempServers);

        return returnPair;
    }

    @Override
    public String toString() {
        DecimalFormat df1 = new DecimalFormat("0.000");

        return df1.format(this.getTimeOfEvent()) + " "
            + (this.getCusIndex() + 1) + " serves by "
            + this.getAttachedServer().toString() + "\n";
    }

}













