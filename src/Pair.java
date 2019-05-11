import java.util.Objects;

/**
 * Created by final on 11/15/2017.
 */
//Because this project uses String as keys, which extends Comparable, T can be extension of Comparable here, for future projects, for the sake of generality, remove extends Comparable
public class Pair<T extends Comparable> {

    //this class will attempt to keep an order on id1 id2
    public T id1,id2;

    public Pair(T id1,T id2) {
        if(id1.compareTo(id2) < 0) {
            this.id1 = id1;
            this.id2 = id2;
        }
        else {
            this.id1 = id2;
            this.id2 = id1;
        }
    }

    //fail-safe on keeping id1 id2 order
    public Pair(Pair<T> pair) {
        if(pair.id1.compareTo(pair.id2) < 0) {
            this.id1 = pair.id1;
            this.id2 = pair.id2;
        }
        else {
            this.id1 = pair.id2;
            this.id2 = pair.id1;
        }
    }

    public boolean contains(T id) {
        return this.id1.equals(id) || this.id2.equals(id);
    }

    //always override equals and hashCode
    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair) {
            Pair o = (Pair) obj;
            //fail-safe on id1 id2 order
            return (this.id1.equals(o.id1) && this.id2.equals(o.id2)) || (this.id1.equals(o.id2) && this.id2.equals(o.id1));
        }
        return false;
    }

    //always override hashCode and equals
    @Override
    public int hashCode(){
        //fail-safe on id1 id2 order
        return this.id1.compareTo(this.id2) <= 0 ? Objects.hash(this.id1.hashCode(),this.id2.hashCode()) : Objects.hash(this.id2.hashCode(),this.id1.hashCode());
    }

    @Override
    public String toString() {
        return this.id1.toString() + ":" + this.id2.toString();
    }
}
