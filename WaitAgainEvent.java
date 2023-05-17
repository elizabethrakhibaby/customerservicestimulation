import java.text.DecimalFormat;
import java.util.function.Supplier;

class WaitAgainEvent extends Event {

    //constructor
    WaitAgainEvent(double arrivalTime, Customer customer, BaseServer attachedServer) {
        super(arrivalTime, customer, attachedServer);
    }

    @Override
    public Pair<Event, ImList<BaseServer>> getNextEvent(ImList<BaseServer> tempServers) {
        BaseServer attachedServer = this.getAttachedServer();
        int parent = attachedServer.getParent();
        BaseServer originalParent = tempServers.get(parent);

        /* For human server
         * its parent is itself(human server)
         * 
         * For self-checkout server
         * its parent is megaserver
         */


        //this is time attached to Wait Event
        double currentTime = this.getTimeOfEvent(); 

        /* originalParent can be either human server or mega server!
         * 
         * FOR HUMAN SERVERS
         * check the status - if status is true, need to double check by looking at busyTil time!
         * 
         * if status is true, and busytil time <= currentTime,
         * then canRetrieve will be set to true, else it will be false
         * 
         * if status is true and canRetrive is false, we need to create WaitAgain Event
         * if status is false, we need to create WaitAgain Event
         * 
         * hence, we ONLY create serve event if status and canRetrieve are BOTH TRUE!
         * 
         * the timing that we attach to WaitAgain Event 
         * is the busyTil timing of the server it is attached to
         * --> why?
         * This is because, we can expect the customer tagged to this 
         * Wait Event to wait for at least(could be longer) the busyTil time of the oldServer
         * This is also to prevent an infinite loop during extraction of events from PQ
         * 
         * (proof by contradiction)
         * The created WaitAgain Event has to be added back to PQ!
         * If WaitAgain Event has same timing as this(extracted Wait Event),
         * the former event will be extracted again and again during pq.poll()
         * 
         * Furthermore, WaitAgain Event's toString() method will return an empty string 
         * so that the event of the tagged customer waiting is not printed more than once.
         * 
         * 
         * we need a method that returns:
         * current status(boolean), canRetrieve(true), timeToAttachToWaitAgain
         * 
         * FOR MEGASERVERS
         * we will loop through the entire list! 
         * - details in processWaitingCus(currentTime) of megasERVER 
         */

        Pair<Pair<Boolean,Boolean>, Double> checking = 
            originalParent.processWaitingCus(currentTime);

        boolean status = checking.first().first();
        boolean canRetrieve = checking.first().second();
        double timeToAttachToWaitAgain = checking.second();

        if (status && canRetrieve) {
            //actual server is either a human server or a sc server
            //getActualServer(currentTime) invoked on human server or megaserver
            BaseServer actualServer = originalParent.getActualServer(currentTime);
            //Create Serve Event
            Event serve = new ServeEvent(currentTime, this.getCustomer(), actualServer);

            double waitedFor = currentTime - this.getCusArrivalTime();

            /*For human server
             * change status to false
             * remove from queue
             * ADD onto current waitingTimeTracker, waitedFor
             */

            /* For megaServer
             * remove from queue
             * ADD onto current waitingTimeTracker, waitedFor
             * at CORRECT position, change the status to false!
             */



            int indexOfParent = actualServer.getParent();
            BaseServer newParent = 
                originalParent.updateWaitingToServe(waitedFor, actualServer);

            tempServers = tempServers.set(indexOfParent, newParent);

            return new Pair<Event, ImList<BaseServer>>(serve, tempServers);

        } else {
            //create WaitAgain Event!
            Event waitAgain = new WaitAgainEvent(timeToAttachToWaitAgain, 
                    this.getCustomer(), attachedServer);

            return new Pair<Event, ImList<BaseServer>>(waitAgain, tempServers);

        }

    }



    @Override
    public String toString() {
        return "";
    }
}

