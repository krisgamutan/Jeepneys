package net.krisg.riseabove.jeepneys.data;

import java.util.ArrayList;

/**
 * Created by KrisEmmanuel on 9/30/2014.
 */
public class DVertex implements Comparable<DVertex>{

    public final long vertexId;
    //public DEdge[] adjacencies;
    public ArrayList<DEdge> adjacencies = new ArrayList<DEdge>();
    public double minDistance = Double.POSITIVE_INFINITY;
    public DVertex previous;



    public DVertex(long vertexId)
    {
        this.vertexId = vertexId;
    }


    @Override
    public String toString() {
        return Long.toString(vertexId);
    }

    public int compareTo(DVertex other)
    {
        return Double.compare(minDistance, other.minDistance);
    }



    /* equals compare only vertexId */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DVertex)) return false;

        DVertex dVertex = (DVertex) o;

        if (vertexId != dVertex.vertexId) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return (int) (vertexId ^ (vertexId >>> 32));
    }
}
