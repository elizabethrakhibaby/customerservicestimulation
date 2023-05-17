import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.function.Supplier;

class HumanServer extends BaseServer {
    private final boolean status;
    private final ImList<Integer> queue;
    private final double busyTil;
    private final int qMax;
    private final double waitingTimeTracker;
    private final int cusTracker;
    private final Supplier<Double> restTime;
    private final int parent;

    //creating default servers 
    //for human servers(may or may not rest)
    HumanServer(int indexOfServer, int qMax, Supplier<Double> restTime) {
        super(indexOfServer);
        this.status = true; 
        this.queue = new ImList<Integer>();
        this.busyTil = 0.00;
        this.qMax = qMax;
        this.waitingTimeTracker = 0.00;
        this.cusTracker = 0;
        this.restTime = restTime;
        this.parent = indexOfServer;
    }


    //overloaded constructor
    HumanServer(boolean status, ImList<Integer> queue, double busyTil, 
            int indexOfServer, int qMax, double waitingTimeTracker, 
            int cusTracker, Supplier<Double> restTime, int parent) {
        super(indexOfServer);
        this.status = status;
        this.queue = queue;
        this.busyTil = busyTil;
        this.qMax = qMax;
        this.waitingTimeTracker = waitingTimeTracker;
        this.cusTracker = cusTracker;
        this.restTime = restTime;
        this.parent = parent;
    }

    @Override
    public Pair<Boolean,BaseServer> canServeNow(double arrivalTime) {
        boolean result = false;
        if (this.getStatus() && this.getBusyTil() <= arrivalTime) {
            result = true;
        }

        return new Pair<Boolean,BaseServer>(result, this);
    }

    //private getter methods
    private boolean getStatus() {
        return this.status;
    }

    @Override
    public BaseServer updateArriveToServe(BaseServer attachedServer) {
        //return a new human server (status is false and increment cusTracker)
        boolean status = false;
        ImList<Integer> queue = this.getQueue();
        double busyTil = this.getBusyTil();
        int indexOfServer = this.getIndexOfServer();
        int qMax = this.getQMax();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        int cusTracker = this.getCusTracker() + 1;
        Supplier<Double> restTime = this.copyOverRestTime();
        int parent = this.getParent();

        HumanServer updatedHumanServer = new HumanServer(status, queue, busyTil, 
                indexOfServer, qMax, waitingTimeTracker, 
                cusTracker, restTime, parent);

        return updatedHumanServer;
    }

    @Override
    public Pair<Boolean,BaseServer> canJoinQ() {
        ImList<Integer> currentQ = this.getQueue();
        boolean doesQHaveSpace = currentQ.size() < this.getQMax();
        return new Pair<Boolean,BaseServer>(doesQHaveSpace, this);
    }

    @Override
    public BaseServer updateArriveToWait() {
        //add integer to queue and increment cusTracker
        boolean status = this.getStatus();
        ImList<Integer> currentQ = this.getQueue();
        currentQ = currentQ.add(1);
        double busyTil = this.getBusyTil();
        int indexOfServer = this.getIndexOfServer();
        int qMax = this.getQMax();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        int cusTracker = this.getCusTracker() + 1;
        Supplier<Double> restTime = this.copyOverRestTime();
        int parent = this.getParent();

        HumanServer updatedHumanServer = new HumanServer(status, currentQ, busyTil,
                indexOfServer, qMax, waitingTimeTracker,
                cusTracker, restTime, parent);

        return updatedHumanServer;
    }

    @Override
    public BaseServer updateServeToDone(BaseServer attachedServer, double newBusyTil) {
        boolean status = false; //
        ImList<Integer> queue = this.getQueue();
        double busyTil = newBusyTil; //
        int indexOfServer = this.getIndexOfServer();
        int qMax = this.getQMax();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        int cusTracker = this.getCusTracker();
        Supplier<Double> restTime = this.copyOverRestTime();
        int parent = this.getParent();

        HumanServer updatedHumanServer = new HumanServer(status, queue, busyTil,
                indexOfServer, qMax, waitingTimeTracker,
                cusTracker, restTime, parent);

        return updatedHumanServer;
    }

    @Override
    public BaseServer updateDoneToDone(BaseServer attachedServer,
            double actualRestingTime) {
        //newBusyTil = current busyTil + actualRestingTime
        //status is set to true

        boolean status = true; //
        ImList<Integer> queue = this.getQueue();
        double busyTil = this.getBusyTil() + actualRestingTime; //
        int indexOfServer = this.getIndexOfServer();
        int qMax = this.getQMax();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        int cusTracker = this.getCusTracker();
        Supplier<Double> restTime = this.copyOverRestTime();
        int parent = this.getParent();

        HumanServer updatedHumanServer = new HumanServer(status, queue, busyTil,
                indexOfServer, qMax, waitingTimeTracker,
                cusTracker, restTime, parent);

        return updatedHumanServer;
    }

    @Override
    public Pair<Pair<Boolean,Boolean>, Double> processWaitingCus(double currentTime) {
        boolean status = this.getStatus();
        double busyTilOfAttachedServer = this.getBusyTil();
        boolean canRetrieve = busyTilOfAttachedServer <= currentTime;
        double timeToAttachToWaitAgain = busyTilOfAttachedServer;

        /* value of timeToAttachToWaitAgain is only used after being returned if
         * (1) status = false 
         * Or
         * (2) status = true and canRetrieve = false
         */
        Pair<Boolean,Boolean> innerPair = new Pair<Boolean,Boolean>(status, canRetrieve);

        return new Pair<Pair<Boolean,Boolean>,Double>(innerPair, timeToAttachToWaitAgain);
    }

    @Override
    public BaseServer getActualServer(double currentTime) {
        return this;
    }    

    @Override
    public BaseServer updateWaitingToServe(double waitedFor, BaseServer actualServer) {
        /* change status to false
         * remove from queue
         * ADD onto current waitingTimeTracker, waitedFor
         */     

        boolean status = false; //
        ImList<Integer> queue = this.getQueue();
        queue = queue.remove(0); //
        double busyTil = this.getBusyTil();
        int indexOfServer = this.getIndexOfServer();
        int qMax = this.getQMax();
        double waitingTimeTracker = this.getWaitingTimeTracker();
        waitingTimeTracker = waitingTimeTracker + waitedFor; //
        int cusTracker = this.getCusTracker();
        Supplier<Double> restTime = this.copyOverRestTime();
        int parent = this.getParent();

        HumanServer updatedHumanServer = new HumanServer(status, queue, busyTil,
                indexOfServer, qMax, waitingTimeTracker,
                cusTracker, restTime, parent);

        return updatedHumanServer;

    }


    //getter methods
    private ImList<Integer> getQueue() {
        return this.queue;
    }

    private int getQMax() {
        return this.qMax;
    }

    @Override
    public int getParent() {
        return this.parent;
    }

    @Override
    public double getRestTime() {
        return this.restTime.get();
    }

    @Override
    public double getBusyTil() {
        return this.busyTil;
    }

    @Override
    public Supplier<Double> copyOverRestTime() {
        return this.restTime;
    }

    @Override
    public int getCusTracker() {
        return this.cusTracker;
    }

    @Override
    public double getWaitingTimeTracker() {
        return this.waitingTimeTracker;
    }

    @Override
    public String toString() {
        return "" + (this.getIndexOfServer() + 1);
    }
}
