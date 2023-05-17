import java.text.DecimalFormat;
import java.util.function.Supplier;

class ArrivalEvent extends Event {

    ArrivalEvent(double arrivalTime, Customer customer) {
        super(arrivalTime, customer);
    }

    @Override
    public Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers) {
        // We are now processing an arrival Event
        double arrivalTime = this.getCusArrivalTime();
        Pair<Pair<Boolean,BaseServer>, BaseServer> pair = canCreateServe(tempServers, arrivalTime);

        boolean canCreateServe = pair.first().first();
        if (canCreateServe) {
            BaseServer attachedServer = pair.first().second();
            Event serve = new ServeEvent(arrivalTime, this.getCustomer(), attachedServer);
            int parent = attachedServer.getParent();
            BaseServer replacedServer = pair.second();
            tempServers = tempServers.set(parent, replacedServer);
            Pair<Event, ImList<BaseServer>> returnThis = 
                new Pair<Event, ImList<BaseServer>>(serve, tempServers);

            return returnThis;
        }

        Pair<Pair<Boolean,BaseServer>, BaseServer> pair1 = canCreateWait(tempServers);
        boolean canCreateWait = pair1.first().first();
        if (canCreateWait) {
            BaseServer attachedServer = pair1.first().second();
            Event wait = new WaitEvent(arrivalTime, this.getCustomer(), attachedServer);
            int parent = attachedServer.getParent();
            BaseServer replacedServer = pair1.second();
            tempServers = tempServers.set(parent, replacedServer);
            Pair<Event, ImList<BaseServer>> returnThis1 = 
                new Pair<Event, ImList<BaseServer>>(wait, tempServers);

            return returnThis1;
        }

        Event leave = new LeaveEvent(arrivalTime, this.getCustomer());
        Pair<Event, ImList<BaseServer>> returnThis2 = 
            new Pair<Event, ImList<BaseServer>>(leave, tempServers);

        return returnThis2;
    }

    private Pair<Pair<Boolean, BaseServer>, BaseServer> canCreateServe(
            ImList<BaseServer> tempServers, double arrivalTime) {

        for (int a = 0; a < tempServers.size(); a++) {
            //we want to first find a server that is free currently - canServeNow(arrivalTime)
            Pair<Boolean, BaseServer> checking = tempServers.get(a).canServeNow(arrivalTime);
            if (checking.first()) {
                BaseServer attachedServer = checking.second();
                int parent = attachedServer.getParent();
                BaseServer oldParent = tempServers.get(parent);
                BaseServer newParent = oldParent.updateArriveToServe(attachedServer);

                Pair<Boolean, BaseServer> innerPair =
                    new Pair<Boolean, BaseServer>(true, attachedServer);
                Pair<Pair<Boolean, BaseServer>, BaseServer> pair =
                    new Pair<Pair<Boolean, BaseServer>, BaseServer>(innerPair, newParent);
                return pair;
            }
        }

        BaseServer fakeServer = new SCServer(1, 1, 1);
        Pair<Boolean, BaseServer> innerPair1 = new Pair<Boolean, BaseServer>(false, fakeServer);
        Pair<Pair<Boolean, BaseServer>, BaseServer> pair1 =
            new Pair<Pair<Boolean, BaseServer>, BaseServer>(innerPair1, fakeServer);

        return pair1;
    }

    private Pair<Pair<Boolean,BaseServer>, BaseServer> canCreateWait(
            ImList<BaseServer> tempServers) {
        for (int a = 0; a < tempServers.size(); a++) {
            //canJoinQ() will be invoked on either human server or mega server!

            Pair<Boolean,BaseServer> checking = tempServers.get(a).canJoinQ();

            if (checking.first()) {
                BaseServer attachedServer = checking.second();
                int parent = attachedServer.getParent();
                BaseServer oldParent = tempServers.get(parent);
                //updateArriveToWait() will be invoked on either human server or mega server!
                BaseServer newParent = oldParent.updateArriveToWait(); 
                Pair<Boolean,BaseServer> innerPair = 
                    new Pair<Boolean,BaseServer>(true, attachedServer);
                Pair<Pair<Boolean,BaseServer>, BaseServer> pair =
                    new Pair<Pair<Boolean,BaseServer>, BaseServer>(innerPair, newParent);
                return pair;
            }
        }

        BaseServer fakeServer = new SCServer(1,1,1);

        Pair<Boolean,BaseServer> innerPair1 = new Pair<Boolean,BaseServer>(false, fakeServer);
        Pair<Pair<Boolean,BaseServer>, BaseServer> pair1 = 
            new Pair<Pair<Boolean,BaseServer>, BaseServer>(innerPair1,fakeServer);
        return pair1;
    }

    @Override
    public String toString() {
        DecimalFormat df = new DecimalFormat("0.000");
        return df.format(this.getTimeOfEvent()) + " " + (this.getCusIndex() + 1) + " arrives\n";
    }

}


