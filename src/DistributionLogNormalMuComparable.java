import java.util.Collection;

public class DistributionLogNormalMuComparable extends DistributionLogNormal implements Comparable{

    public DistributionLogNormalMuComparable(double mu, double sigmasquare) {
        super(mu, sigmasquare);
    }

    public DistributionLogNormalMuComparable(Collection<Double> array) {
        super(array);
    }

    public DistributionLogNormalMuComparable(Collection<Double> array, double size) {
        super(array, size);
    }

    public DistributionLogNormalMuComparable(DistributionLogNormal distributionLogNormal) {
        super(distributionLogNormal);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof DistributionLogNormalMuComparable)
            return Double.compare(this.mu,((DistributionLogNormalMuComparable)o).mu);
        return -1;
    }

    public static DistributionLogNormalMuComparable merge(DistributionLogNormalMuComparable logNormalDistributionMuComparable1, DistributionLogNormalMuComparable logNormalDistributionMuComparable2){
        return new DistributionLogNormalMuComparable(DistributionLogNormal.merge(logNormalDistributionMuComparable1,logNormalDistributionMuComparable2));
    }

}
