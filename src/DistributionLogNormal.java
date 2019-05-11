import java.util.Collection;

//use sqrt instead of log for the approximation
public class DistributionLogNormal extends Distribution {

    //only keep 1 value, even if after multiple estimations the number is off
    double mu;
    double sigmasquare;

    public DistributionLogNormal(double mu, double sigmasquare){
        this.mu = mu;
        this.sigmasquare = sigmasquare;
    }

    //beware of division by 0
    public DistributionLogNormal(Collection<Double> array){
        this.mu = 0;
        this.sigmasquare = 0;
        if(array != null) {
            for (double d : array)
                this.mu += Math.sqrt(d) / (double) array.size();
            for (double d : array)
                this.sigmasquare += sqr(Math.sqrt(d) - this.mu) / (double) (array.size() - 1);
        }
    }

    //when the dataset does not record 0, cast int to double
    public DistributionLogNormal(Collection<Double> array, double size){
        this.mu = 0;
        this.sigmasquare = 0;
        if(array != null && ((double) array.size()) <= size){
            for (double d : array)
                this.mu += Math.sqrt(d) / size;
            for (double d : array)
                this.sigmasquare += sqr(Math.sqrt(d) - this.mu) / (size - 1);
            this.sigmasquare += sqr(this.mu) * (size - array.size()) / (size - 1);
        }
    }

    public DistributionLogNormal(DistributionLogNormal distributionLogNormal){
        this.mu = distributionLogNormal.mu;
        this.sigmasquare = distributionLogNormal.sigmasquare;
    }

    private static double sqr(double a) {
        return a * a;
    }

    @Override
    public double pdf(double x) {
        return Math.exp(-sqr(Math.log(x) - this.mu) / 2 / this.sigmasquare) / (x * Math.sqrt(this.sigmasquare * 2 * Math.PI));
    }

    @Override
    public double cdf(double x) {
        return 0.5 + org.apache.commons.math3.special.Erf.erf((Math.log(x) - this.mu) / Math.sqrt(2 * this.sigmasquare)) / 2;
    }

    @Override
    public double[] getWeight(){
        return new double[]{this.mu,this.sigmasquare};
    }

    public static DistributionLogNormal merge(DistributionLogNormal logNormal1, DistributionLogNormal logNormal2) {
        double sigmasquare = Math.log((Math.exp(2 * logNormal1.mu + logNormal1.sigmasquare) * (Math.exp(logNormal1.sigmasquare) - 1) + Math.exp(2 * logNormal2.mu + logNormal2.sigmasquare) * (Math.exp(logNormal2.sigmasquare) - 1)) / sqr(Math.exp(logNormal1.mu + logNormal1.sigmasquare / 2) + Math.exp(logNormal2.mu + logNormal2.sigmasquare / 2)) + 1);
        double mu = Math.log(Math.exp(logNormal1.mu + logNormal1.sigmasquare / 2) + Math.exp(logNormal2.mu + logNormal2.sigmasquare / 2)) - sigmasquare / 2;
        return new DistributionLogNormal(mu,sigmasquare);
    }

    /*@Override
    public String toString(){
        return this.mu + "," + this.sigmasquare;
    }*/

}
