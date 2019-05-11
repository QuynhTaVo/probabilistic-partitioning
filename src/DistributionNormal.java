import java.util.Arrays;
import java.util.Collection;

public class DistributionNormal extends Distribution {

    double mu,sigmasquare;
    //set mode for ordering of compareTo
    //private static boolean muOrdering = true;
    //static double kappa = 0;

    //abstract protected void calculateMu();
    //abstract protected void calculateSigmaSquare();

    public DistributionNormal(double mu, double sigmasquare){
        this.mu = mu;
        this.sigmasquare = sigmasquare;
    }

    public DistributionNormal(Double[] array){
        this(Arrays.asList(array));
        /*this.mu = 0;
        for(double d:array)
            this.mu += d / array.length;
        this.sigmasquare = 0;
        for(double d:array)
            this.sigmasquare += sqr(d - this.mu) / (array.length - 1);*/
    }

    //when the dataset does not record 0
    public DistributionNormal(Double[] array, int size){
        this(Arrays.asList(array),size);
        /*if(array.length <= size){
            this.mu = 0;
            for (double d : array)
                this.mu += d / size;
            this.sigmasquare = 0;
            for (double d : array)
                this.sigmasquare += sqr(d - this.mu) / (size - 1);
            this.sigmasquare += this.mu * (size - array.length) / (size - 1);
        }*/
    }

    public DistributionNormal(Collection<Double> array){
        this.mu = 0;
        this.sigmasquare = 0;
        if(array != null) {
            for (double d : array)
                this.mu += d / (double) array.size();
            for (double d : array)
                this.sigmasquare += sqr(d - this.mu) / (double) (array.size() - 1);
        }
    }

    //when the dataset does not record 0, cast int to double
    public DistributionNormal(Collection<Double> array, int size){
        this(array,(double) size);
        /*this.mu = 0;
        this.sigmasquare = 0;
        if(array != null && array.size() <= size){
            for (double d : array)
                this.mu += d / size;
            for (double d : array)
                this.sigmasquare += sqr(d - this.mu) / (size - 1);
            this.sigmasquare += sqr(this.mu) * (size - array.size()) / (size - 1);
        }*/
    }

    //when the dataset does not record 0, cast int to double
    public DistributionNormal(Collection<Double> array, double size){
        this.mu = 0;
        this.sigmasquare = 0;
        if(array != null && ((double) array.size()) <= size){
            for (double d : array)
                this.mu += d / size;
            for (double d : array)
                this.sigmasquare += sqr(d - this.mu) / (size - 1);
            this.sigmasquare += sqr(this.mu) * (size - array.size()) / (size - 1);
        }
    }

    //for easy conversion between children
    public DistributionNormal(DistributionNormal distributionNormal){
        this.mu = distributionNormal.mu;
        this.sigmasquare = distributionNormal.sigmasquare;
    }

    private double sqr(double a) {
        return a * a;
    }

    /*public static void setMuOrdering(boolean mode){
        DistributionNormal.muOrdering = mode;
    }*/

    /*public static void setKappa(double kappa){
        DistributionNormal.kappa = kappa;
    }*/

    @Override
    public double pdf(double x) {
        return 1 / Math.sqrt(2 * Math.PI * this.sigmasquare) * Math.pow(Math.E,- sqr(x - mu) / (2 * this.sigmasquare));
    }

    @Override
    public double cdf(double x) {
        return (1 + org.apache.commons.math3.special.Erf.erf((x - this.mu) / Math.sqrt(this.sigmasquare * 2))) / 2;
    }

    @Override
    public double[] getWeight(){
        return new double[]{this.mu,this.sigmasquare};
    }

    /*@Override
    public String toString() {
        return this.mu + "," + this.sigmasquare;
    }*/

    /*@Override
    public int compareTo(Object o) {
        if(o instanceof DistributionNormal) {
            DistributionNormal normalDistribution = (DistributionNormal) o;
            if(muOrdering)
                return this.mu > normalDistribution.mu ? 1 : -1;
            else
                return this.cdf(kappa) > normalDistribution.cdf(kappa) ? 1 : -1;
        }
        return -1;
    }*/

    public static DistributionNormal merge(DistributionNormal normal1, DistributionNormal normal2) {
        if(normal1 == null)
            normal1 = new DistributionNormal(0,0);
        if(normal2 == null)
            normal2 = new DistributionNormal(0,0);
        return new DistributionNormal(normal1.mu + normal2.mu,normal1.sigmasquare + normal2.sigmasquare);
    }

}

/*class NormalDistributionMuComparator implements Comparator<DistributionNormal> {

    @Override
    public int compare(DistributionNormal o1, DistributionNormal o2) {
        return Double.compare(o1.mu,o2.mu);
    }

}

class NormalDistributionCDFComparator implements Comparator<DistributionNormal> {

    static double kappa = 0;

    public void setKappa(double kappa){
        NormalDistributionCDFComparator.kappa = kappa;
    }

    @Override
    public int compare(DistributionNormal o1, DistributionNormal o2) {
        return Double.compare(o1.cdf(kappa),o2.cdf(kappa));
    }

}*/
