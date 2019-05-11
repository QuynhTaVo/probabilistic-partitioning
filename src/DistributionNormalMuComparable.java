import java.util.Collection;

public class DistributionNormalMuComparable extends DistributionNormal implements Comparable {

    public DistributionNormalMuComparable(double mu, double sigmasquare) {
        super(mu, sigmasquare);
    }

    public DistributionNormalMuComparable(Double[] array) {
        super(array);
    }

    public DistributionNormalMuComparable(Double[] array, int size) {
        super(array, size);
    }

    public DistributionNormalMuComparable(Collection<Double> array) {
        super(array);
    }

    public DistributionNormalMuComparable(Collection<Double> array, int size) {
        super(array, size);
    }

    public DistributionNormalMuComparable(Collection<Double> array, double size) {
        super(array, size);
    }

    public DistributionNormalMuComparable(DistributionNormal distributionNormal){
        super(distributionNormal);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof DistributionNormal)
            return Double.compare(this.mu,((DistributionNormal) o).mu);
        return -1;
    }

}