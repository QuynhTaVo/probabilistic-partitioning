import java.util.Comparator;

public class SuperCellPair<T extends Comparable> {

    Pair<String> pair;
    T ij, ii, jj;

    public SuperCellPair(String i,String j, T ij, T ii, T jj) {
        this.pair = new Pair<>(i,j);
        if(pair.id1.equals(i)) {
            this.ij = ij;
            this.ii = ii;
            this.jj = jj;
        }
        else {
            this.ij = ij;
            this.jj = ii;
            this.ii = jj;
        }
    }

    public SuperCellPair(SuperCellPair<T> superCellPair) {
        this.pair = new Pair<>(superCellPair.pair);
        this.ij = superCellPair.ij;
        this.ii = superCellPair.ii;
        this.jj = superCellPair.jj;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof SuperCellPair){
            SuperCellPair superCellPair = (SuperCellPair) o;
            return this.pair.equals(superCellPair.pair);
        }
        return false;
    }

    @Override
    public int hashCode(){
        return this.pair.hashCode();
    }
}

class SuperCellPairComparator0 implements Comparator<SuperCellPair> {

    @Override
    public int compare(SuperCellPair o1, SuperCellPair o2) {
        if (o1.ij != null && o2.ij != null && o1.ij.compareTo(o2.ij) != 0)
            return o1.ij.compareTo(o2.ij);
        else {
            if (o1.ii != null && o2.ii != null && o1.ii.compareTo(o2.ii) != 0)
                return o1.ii.compareTo(o2.ii);
            else {
                if (o1.jj != null && o2.jj != null && o1.jj.compareTo(o2.jj) != 0)
                    return o1.jj.compareTo(o2.jj);
                else
                    return Integer.compare(o1.pair.hashCode(),o2.pair.hashCode());
            }
        }
    }

}

class SuperCellPairComparator1 implements Comparator<SuperCellPair<DistributionLogNormalMuComparable>> {

    @Override
    public int compare(SuperCellPair<DistributionLogNormalMuComparable> o1, SuperCellPair<DistributionLogNormalMuComparable> o2) {
        return o1.ii.mu + o1.jj.mu != o2.ii.mu + o2.jj.mu ? Double.compare(o1.ii.mu + o1.jj.mu,o2.ii.mu + o2.jj.mu) : Integer.compare(o1.pair.hashCode(),o2.pair.hashCode());
    }

}

class SuperCellPairComparator2 implements Comparator<SuperCellPair<DistributionNormalMuComparable>> {

    @Override
    public int compare(SuperCellPair<DistributionNormalMuComparable> o1, SuperCellPair<DistributionNormalMuComparable> o2) {
        return o1.ii.mu + o1.jj.mu != o2.ii.mu + o2.jj.mu ? Double.compare(o1.ii.mu + o1.jj.mu,o2.ii.mu + o2.jj.mu) : Integer.compare(o1.pair.hashCode(),o2.pair.hashCode());
    }

}
