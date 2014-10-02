package net.krisg.riseabove.jeepneys;

import net.krisg.riseabove.jeepneys.data.DEdge;
import net.krisg.riseabove.jeepneys.data.DVertex;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by KrisEmmanuel on 9/30/2014.
 */
public class Dijkstra {
    public static void computePaths(DVertex source)
    {
        source.minDistance = 0.;
        PriorityQueue<DVertex> vertexQueue = new PriorityQueue<DVertex>();
        vertexQueue.add(source);

        while (!vertexQueue.isEmpty()) {
            DVertex u = vertexQueue.poll();

            // Visit each edge exiting u
            for (DEdge e : u.adjacencies)
            {
                DVertex v = e.target;

                double weight = e.weight;


                double distanceThroughU = u.minDistance + weight;

                if (distanceThroughU < v.minDistance) {
                    vertexQueue.remove(v);

                    v.minDistance = distanceThroughU ;
                    v.previous = u;
                    vertexQueue.add(v);
                }
            }
        }
    }

    public static List<DVertex> getShortestPathTo(DVertex target)
    {
        List<DVertex> path = new ArrayList<DVertex>();
        for (DVertex vertex = target; vertex != null; vertex = vertex.previous)
            path.add(vertex);

        Collections.reverse(path);
        return path;
    }
}
