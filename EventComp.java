import java.util.Comparator;


class EventComp implements Comparator<Event> {
    public int compare(Event e1, Event e2) {
        double diff = e1.getTimeOfEvent() - e2.getTimeOfEvent();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            // same time
            double seconddiff = e1.getCusIndex() - e2.getCusIndex();
            if (seconddiff < 0) {
                return -1;
            } else if (seconddiff > 0) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}

/* Create an implementation of Comparator<Event>
 * comapre(x,y) should return <0 if x comes first, >0 if y comes first or 0 otherwise
 * */

