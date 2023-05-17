import java.util.Scanner;
import java.util.function.Supplier;

class MegaServer extends BaseServer {
    private final ImList<Integer> queue;
    private final int qMax;
    private final int cusTracker;
    private final double waitingTimeTracker;
    private final ImList<SCServer> listOfSCServers;

    //constructor
    MegaServer(int overallIndex, ImList<SCServer> listOfSCServers,
            int qmax) {
        super(overallIndex);
        this.queue = new ImList<Integer>();
        this.qMax = qmax;
        this.cusTracker = 0;
        this.waitingTimeTracker = 0.00;
        this.listOfSCServers = listOfSCServers;
    }

    //overloaded constructor
    MegaServer(int overallIndex, ImList<Integer> queue, int qMax,
            int cusTracker, double waitingTimeTracker,
            ImList<SCServer> listOfSCServers) {
        super(overallIndex);
        this.queue = queue;
        this.qMax = qMax;
        this.cusTracker = cusTracker;
        this.waitingTimeTracker = waitingTimeTracker;
        this.listOfSCServers = listOfSCServers;
    }

    @Override
    public Pair<Boolean,BaseServer> canServeNow(double arrivalTime) {

        //we have to look at entire list of sc servers 
        ImList<SCServer> listOfSCServers = this.getListOfSCServers();
        int n = listOfSCServers.size();

        for (int a = 0; a < n; a++) {
            SCServer selfCheckServer = listOfSCServers.get(a);
            Pair<Boolean,BaseServer> checking = selfCheckServer.canServeNow(arrivalTime);

            if (checking.first()) {
                return new Pair<Boolean,BaseServer>(true, checking.second());
            }
        }

        return new Pair<Boolean,BaseServer>(false, this);
    }

    @Override
    public BaseServer updateArriveToServe(BaseServer attachedServer) {
        MegaServer oldMegaServer = this;
        ImList<SCServer> oldListOfSCServers = this.getListOfSCServers();


        //gotta create a new SC Server!
        int indexOfSCServer = attachedServer.getIndexOfServer();
        boolean status = false;
        double busyTil = attachedServer.getBusyTil();
        Supplier<Double> restTime = attachedServer.copyOverRestTime();
        int parent = attachedServer.getParent();
        int position = attachedServer.getPosition();

        SCServer updatedSCServer = new SCServer(indexOfSCServer, status, busyTil,
                restTime, parent, position);

        oldListOfSCServers = oldListOfSCServers.set(position, updatedSCServer);


        //gotta return new updated mega server!
        int overallIndex = this.getOverallIndex();
        ImList<Integer> queue = this.getQueue();
        int qMax = this.getQMax();
        int cusTracker = this.getCusTracker() + 1;
        double waitingTimeTracker = this.getWaitingTimeTracker();

        MegaServer updatedMegaServer = new MegaServer(overallIndex, queue, qMax,
                cusTracker, waitingTimeTracker,
                oldListOfSCServers);

        return updatedMegaServer;
    }

    @Override
    public Pair<Boolean,BaseServer> canJoinQ() {
        ImList<Integer> currentQ = this.getQueue();
        boolean doesQHaveSpace = currentQ.size() < this.getQMax();
        BaseServer defaultSCServer = this.getListOfSCServers().get(0);
        return new Pair<Boolean,BaseServer>(doesQHaveSpace,defaultSCServer);
    }

    @Override
    public BaseServer updateArriveToWait() {
        //add integer to queue and increment cusTracker
        int overallIndex = this.getIndexOfServer();
        ImList<Integer> currentQ = this.getQueue();
        currentQ = currentQ.add(1);
        int qMax = this.getQMax();
        int cusTracker = this.getCusTracker() + 1; 
        double waitingTimeTracker = this.getWaitingTimeTracker();
        ImList<SCServer> listOfSCServers = this.getListOfSCServers();

        MegaServer updatedMegaServer = new MegaServer(overallIndex, currentQ, qMax,
                cusTracker, waitingTimeTracker,
                listOfSCServers);

        return updatedMegaServer;
    }

    @Override
    public BaseServer updateServeToDone(BaseServer attachedServer, double newBusyTil) {
        ImList<SCServer> oldList = this.getListOfSCServers();
        int position = attachedServer.getPosition();
        //create a new SC Server
        int indexOfSCServer = attachedServer.getIndexOfServer();
        boolean status = false;
        double busyTil = newBusyTil;
        Supplier<Double> restTime = attachedServer.copyOverRestTime();
        int parent = attachedServer.getParent();
        //int position = attachedServer.getPosition();
        SCServer updatedSCServer = new SCServer(indexOfSCServer, status, busyTil,
                restTime, parent, position);


        oldList = oldList.set(position, updatedSCServer);

        //gotta return new updated mega server!
        int overallIndex = this.getOverallIndex();
        ImList<Integer> queue = this.getQueue();
        int qMax = this.getQMax();
        int cusTracker = this.getCusTracker();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        MegaServer updatedMegaServer = new MegaServer(overallIndex, queue, qMax,
                cusTracker, waitingTimeTracker,
                oldList);

        return updatedMegaServer;
    }

    @Override
    public BaseServer updateDoneToDone(BaseServer attachedServer,
            double actualRestingTime) {
        //newBusyTil = current busyTil + actualRestingTime
        //status is set to true    

        ImList<SCServer> oldList = this.getListOfSCServers();
        int position = attachedServer.getPosition();
        //create a new SC Server
        int indexOfSCServer = attachedServer.getIndexOfServer();
        boolean status = true;
        double busyTil = attachedServer.getBusyTil() + actualRestingTime;
        Supplier<Double> restTime = attachedServer.copyOverRestTime();
        int parent = attachedServer.getParent();
        //int position = attachedServer.getPosition();
        SCServer updatedSCServer = new SCServer(indexOfSCServer, status, busyTil,
                restTime, parent, position);


        oldList = oldList.set(position, updatedSCServer);

        //gotta return new updated mega server!
        int overallIndex = this.getOverallIndex();
        ImList<Integer> queue = this.getQueue();
        int qMax = this.getQMax();
        int cusTracker = this.getCusTracker();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        MegaServer updatedMegaServer = new MegaServer(overallIndex, queue, qMax,
                cusTracker, waitingTimeTracker,
                oldList);

        return updatedMegaServer; 
    }  

    @Override
    public Pair<Pair<Boolean,Boolean>, Double> processWaitingCus(double currentTime) {
        boolean status = this.checkIfAnySCIsAvail();
        boolean canRetrieve = this.checkIfAnySCCanBeUsed(currentTime);
        double waitTilAtLeastTime = this.smallestBusyTil();

        /* the value of waitTilAtLeastTime is only used because
         * status = false and canRetrieve is also false!
         */

        Pair<Boolean,Boolean> innerPair = new Pair<Boolean,Boolean>(status, canRetrieve);

        return new Pair<Pair<Boolean,Boolean>,Double>(innerPair, waitTilAtLeastTime);

    }

    private boolean checkIfAnySCIsAvail() {
        ImList<SCServer> list = this.getListOfSCServers();

        for (int a = 0; a < list.size(); a++) {
            SCServer selfCheckout = list.get(a);
            boolean isFree = selfCheckout.getStatus();
            if (isFree) {
                return true;
            }
        }

        return false;
    }

    private boolean checkIfAnySCCanBeUsed(double currentTime) {
        ImList<SCServer> list = this.getListOfSCServers();

        for (int a = 0; a < list.size(); a++) {

            SCServer selfCheckout = list.get(a);

            if (selfCheckout.getStatus() == true) {

                if (selfCheckout.getBusyTil() <= currentTime) {

                    return true;
                }
            }
        }
        return false;   
    }

    private double smallestBusyTil() {

        ImList<SCServer> list = this.getListOfSCServers();
        double smallest = list.get(0).getBusyTil();

        for (int a = 1; a < list.size(); a++) {
            if (smallest > list.get(a).getBusyTil()) {
                smallest = list.get(a).getBusyTil();
            }
        }

        return smallest;
    }

    //this method may have a problem
    @Override
    public BaseServer getActualServer(double currentTime) {
        //this method will return an SC Server!
        ImList<SCServer> list = this.getListOfSCServers();  

        for (int a = 0; a < list.size(); a++) {
            SCServer selfCheckout = list.get(a);

            if (selfCheckout.getStatus() == true && selfCheckout.getBusyTil() <= currentTime) {
                return selfCheckout;
            }

        }
        return this;
    }

    @Override
    public BaseServer updateWaitingToServe(double waitedFor, BaseServer actualServer) {
        /* remove from queue
         * ADD onto current waitingTimeTracker, waitedFor
         * at CORRECT position, change the status to false!
         */

        ImList<SCServer> oldList = this.getListOfSCServers();
        int position = actualServer.getPosition();
        //create a new SC Server
        int indexOfSCServer = actualServer.getIndexOfServer();
        boolean status = false; //
        double busyTil = actualServer.getBusyTil(); 
        Supplier<Double> restTime = actualServer.copyOverRestTime();
        int parent = actualServer.getParent();

        SCServer updatedSCServer = new SCServer(indexOfSCServer, status, busyTil,
                restTime, parent, position);


        oldList = oldList.set(position, updatedSCServer);

        //gotta return new updated mega server!
        int overallIndex = this.getOverallIndex();
        ImList<Integer> queue = this.getQueue();
        queue = queue.remove(0);
        int qMax = this.getQMax();
        int cusTracker = this.getCusTracker();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        waitingTimeTracker = waitingTimeTracker + waitedFor;

        MegaServer updatedMegaServer = new MegaServer(overallIndex, queue, qMax,
                cusTracker, waitingTimeTracker,
                oldList);

        return updatedMegaServer; 

    }



    //getter methods
    private int getOverallIndex() {
        return this.getIndexOfServer();
    }

    private ImList<SCServer> getListOfSCServers() {
        return this.listOfSCServers;
    }

    private ImList<Integer> getQueue() {
        return this.queue;
    }

    private int getQMax() {
        return this.qMax;
    }

    @Override
    public int getCusTracker() {
        return this.cusTracker;
    }

    @Override
    public double getWaitingTimeTracker() {
        return this.waitingTimeTracker;
    }

}
