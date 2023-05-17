import java.util.Scanner;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.function.Supplier;

class Simulator {

    private final ImList<Customer> cusList;
    private final ImList<BaseServer> serverList;
    private final int numOfHumanServers;
    private final int numOfSCServers;
    private final int totalNumOfAllServers;
    private final int qMax;
    private final int numOfCustomers;

    //constructor

    Simulator(int numOfServers, int numOfSelfChecks, 
            int qmax, ImList<Double> arrivalTimes, 
            Supplier<Double> serviceTimes, 
            Supplier<Double> restTimes) {

        //create list of customer objects
        ImList<Customer> listOfCus = new ImList<Customer>();
        for (int w = 0; w < arrivalTimes.size(); w++) {
            Customer c = new Customer(w, arrivalTimes.get(w), serviceTimes);
            listOfCus = listOfCus.add(c);
        }

        this.numOfHumanServers = numOfServers;
        this.numOfSCServers = numOfSelfChecks;
        this.totalNumOfAllServers = numOfServers + numOfSelfChecks;
        this.qMax = qmax;
        this.cusList = listOfCus;
        this.numOfCustomers = arrivalTimes.size();

        /* Create list of servers : if there are k human servers
         * tempServerList index 0 to index k-1 is populated by human servers
         * tempServerList index k is populated by one mega server
         */

        ImList<BaseServer> tempServerList = new ImList<BaseServer>();
        //this list can store HumanServer objects and ONE MegaServer object

        for (int a = 0; a < numOfHumanServers; a++) {
            BaseServer humanServer = new HumanServer(a, qmax, restTimes);
            tempServerList = tempServerList.add(humanServer);
        }

        //attribute of MegaServer
        ImList<SCServer> listOfSCServers = new ImList<SCServer>();


        for (int b = 0; b < numOfSCServers; b++) {
            int indexOfServer = numOfHumanServers + b;
            int parent = numOfHumanServers;
            SCServer selfCheckServer = new SCServer(indexOfServer, parent, b);
            listOfSCServers = listOfSCServers.add(selfCheckServer);
        }

        if (numOfSCServers > 0) {
            int overallIndex = numOfHumanServers;
            MegaServer ms = new MegaServer(overallIndex, listOfSCServers, qmax);
            tempServerList = tempServerList.add(ms);
        }

        this.serverList = tempServerList;
    }

    public StringBuilder simulate() {

        ImList<BaseServer> tempServers = this.serverList;

        PQ<Event> pq = new PQ<Event>(new EventComp());
        //Create arrival events and store them inside PQ

        for (int i = 0; i < this.cusList.size(); i++) {

            Event arrive = new ArrivalEvent(this.cusList.get(i).getArrivalTime(), 
                    this.cusList.get(i));
            pq = pq.add(arrive);
        }

        StringBuilder sb = new StringBuilder();


        // Process highest priority event
        while (pq.isEmpty() == false) {

            // poll PQ to extract highest priority event
            Pair<Event, PQ<Event>> pair = pq.poll();

            Event highestPri = pair.first();

            pq = pair.second();

            String result = highestPri.toString();

            sb.append(result);

            Pair<Event, ImList<BaseServer>> resultPair = highestPri.getNextEvent(tempServers);

            Event nextEvent = resultPair.first();
            ImList<BaseServer> temp = resultPair.second();

            tempServers = temp; 

            if (nextEvent != highestPri) {
                pq = pq.add(nextEvent);
            }

        } //PQ is empty

        int cusServed = 0;
        double totalWaitingTime = 0.00;

        //Retrieve sum
        for (int a = 0; a < tempServers.size(); a++) {
            cusServed = cusServed + tempServers.get(a).getCusTracker();
            totalWaitingTime = totalWaitingTime + tempServers.get(a).getWaitingTimeTracker();
        }

        int cusLeft = numOfCustomers - cusServed;

        double averageWaitingTime = 0.000;
        if (cusServed == 0) {
            averageWaitingTime = 0.000;
        } else {
            averageWaitingTime = totalWaitingTime / cusServed;
        }   


        DecimalFormat df = new DecimalFormat("0.000");
        sb.append("[" + df.format(averageWaitingTime) + " " + cusServed + " " + cusLeft + "]");

        return sb;

    } 

}


