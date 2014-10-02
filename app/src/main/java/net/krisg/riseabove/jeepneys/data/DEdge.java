package net.krisg.riseabove.jeepneys.data;

/**
 * Created by KrisEmmanuel on 9/30/2014.
 */
public class DEdge {

    public final DVertex target;
    public final double weight;


    public DEdge(DVertex argTarget, double argWeight)
    {
        target = argTarget;
        weight = argWeight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DEdge)) return false;

        DEdge dEdge = (DEdge) o;

        if (Double.compare(dEdge.weight, weight) != 0) return false;
        if (!target.equals(dEdge.target)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = target.hashCode();
        temp = Double.doubleToLongBits(weight);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
