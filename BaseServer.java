import java.util.function.Supplier;

abstract class BaseServer {
    protected final int indexOfServer;

    BaseServer(int indexOfServer) {
        this.indexOfServer = indexOfServer;
    }

    public abstract Pair<Boolean,BaseServer> canServeNow(double arrivalTime);

    public abstract BaseServer updateArriveToServe(BaseServer attachedServer); 

    public abstract Pair<Boolean,BaseServer> canJoinQ();

    public abstract BaseServer updateArriveToWait();

    public abstract BaseServer updateServeToDone(BaseServer attachedServer, 
            double newBusyTil);

    public abstract BaseServer updateDoneToDone(BaseServer attachedServer,
            double actualRestingTime);

    public abstract Pair<Pair<Boolean,Boolean>, Double> processWaitingCus(double currentTime);

    public abstract BaseServer getActualServer(double currentTime);

    public abstract BaseServer updateWaitingToServe(double waitedFor, BaseServer actualServer);

    public int getIndexOfServer() {
        return this.indexOfServer;
    }

    public double getBusyTil() {
        return 0.00;
    }

    public int getParent() {
        return -1;
    }

    public int getPosition() {
        return -1;
    }

    public double getRestTime() {
        return 0.00;
    }

    public Supplier<Double> copyOverRestTime() {
        return () -> 0.00;
    }

    public int getCusTracker() {
        return -1;
    }

    public double getWaitingTimeTracker() {
        return 0.00;
    }

    public String toString() {
        return "nothing";
    }

}
