public abstract class Distribution{

    //Pair<String> pair;

    public abstract double pdf(double x);

    public abstract double cdf(double x);

    public abstract double[] getWeight();

    @Override
    public String toString(){
        String string = "";
        for(double d:this.getWeight())
            string += d + ",";
        return string;
    }

    /*@Override
    public int compareTo(Object o) {
        if(o instanceof Distribution) {
            Distribution distribution = (Distribution) o;
            return this.cdf(threshold) - distribution.cdf(threshold) < 0 ? 1 : -1;
        }
        return -1;
    }*/

}
