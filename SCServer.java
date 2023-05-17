import java.util.function.Supplier;

class SCServer extends BaseServer {
    private final boolean status;
    private final double busyTil;
    private final Supplier<Double> restTime;
    private final int parent;
    private final int position;

    SCServer(int indexOfServer, int parent, int position) {
        super(indexOfServer);
        this.status = true;
        this.busyTil = 0.00;
        this.restTime = () -> 0.00;
        this.parent = parent;
        this.position = position;
    }

    //Overloaded constructor
    SCServer(int indexOfServer, boolean status, double busyTil,
            Supplier<Double> restTime, int parent, int position) {
        super(indexOfServer);
        this.status = status;
        this.busyTil = busyTil;
        this.restTime = restTime;
        this.parent = parent;
        this.position = position;
    }

    @Override
    public Pair<Boolean,BaseServer> canServeNow(double arrivalTime) {
        boolean result = false;
        if (this.getStatus() && this.getBusyTil() <= arrivalTime) {
            result = true;
        }

        return new Pair<Boolean,BaseServer>(result, this);
    }


    @Override
    public BaseServer updateArriveToServe(BaseServer attachedServer) {
        //we will never use this method
        return attachedServer;
    }

    @Override
    public Pair<Boolean,BaseServer> canJoinQ() {
        //we will never use this method!
        return new Pair<Boolean,BaseServer>(false,this);
    }

    @Override
    public BaseServer updateArriveToWait() {
        //we will never use this method!
        return this;
    }


    @Override
    public BaseServer updateServeToDone(BaseServer attachedServer, double newBusyTil) {
        //we will never use this method!
        return this;
    }

    @Override
    public BaseServer updateDoneToDone(BaseServer attachedServer,
            double actualRestingTime) {
        //we will never use this method!
        return this;
    }



    @Override
    public Pair<Pair<Boolean,Boolean>, Double> processWaitingCus(double currentTime) {
        //this method will never be used!
        Pair<Boolean,Boolean> innerPair = new Pair<Boolean,Boolean>(false,false);
        return new Pair<Pair<Boolean,Boolean>,Double>(innerPair, 0.00);
    }

    @Override
    public BaseServer getActualServer(double currentTime) {
        //this method will never be used!
        return this;
    }

    @Override
    public BaseServer updateWaitingToServe(double waitedFor, BaseServer actualServer) {
        //this method will never be used!
        return this;
    }


    @Override
    public int getParent() {
        return this.parent;
    }

    @Override
    public int getPosition() {
        return this.position;
    }

    @Override
    public double getBusyTil() {
        return this.busyTil;
    }

    @Override
    public double getRestTime() {
        return 0.00;
    }

    @Override
    public Supplier<Double> copyOverRestTime() {
        return () -> 0.00;
    }

    public boolean getStatus() {
        return this.status;
    }

    @Override
    public String toString() {
        return "self-check " + (this.getIndexOfServer() + 1);
    }


}
