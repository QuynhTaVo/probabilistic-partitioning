import java.util.*;

public abstract class Partition<T extends Comparable> {

    //this treeset is ordered by value and is never to be used to look for objects by hash or key
    TreeSet<SuperCellPair<T>> interactions;
    //HashSet<SuperCellPair<T>> originalInteractions;

    int countPartitions() {
        int count = 0;
        for(SuperCellPair<T> entry:this.interactions)
            if(entry.pair.id1.equals(entry.pair.id2))
                count += 1;
        return count;
    }

    public abstract HashSet<String>[] partition(double capacity,int serverNumber);

    //partition with different server capacities, unused at this time
    //public abstract HashSet<String>[] partition(double[] capacities);

    /*@Override
    public String toString() {
        String string = "";
        for(SuperCellPair<T> entry:this.interactions)
            string += entry.getKey().toString() + "\t" + entry.getValue() + "\n";
        return string;
    }*/

}
