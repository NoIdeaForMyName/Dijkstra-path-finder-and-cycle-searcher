package Dijkstra;

import Graph.Graph;

import java.util.*;

public class DijkstraShortestPathFinder {

//    public static List<Integer> shortestPath(Graph graph, int s, int end) {
//
//        List<List<Integer>> adjacencyList = graph.getAdjacencyList();
//        int infinity = graph.getInfinity();
//
//        DisjointSetForest dsf = new DisjointSetForest(adjacencyList.size());
//        List<Integer> V_t = new ArrayList<>();
//        V_t.add(s);
//        List<Integer> comp_V = new ArrayList<>();
//        List<Integer> L = new ArrayList<>();
//        List<Integer> P = new ArrayList<>(); //poprzednicy
//        for (int i = 0; i < adjacencyList.size(); i++) {
//            L.add(i);
//            P.add(null);
//            if (i != s)
//                comp_V.add(i);
//        }
//
//        for (int v : comp_V) {
//            if (adjacencyList.get(s).contains(v))
//                L.set(v, graph.getWeight(s, v));
//            else L.set(v, infinity);
//        }
//
//        while (!V_t.contains(end)) { // algorithm...
//            int u = getIndexOfMin(L, comp_V);
//            V_t.add(u);
//            comp_V.remove((Integer) u);
//            //TODO ustaw poprzednika w tablicy
//
//            for (int v : comp_V) {
//                Integer weight = graph.getWeight(u, v);
//                if (weight != null)
//                    L.set(v, Math.min(L.get(v), L.get(u)+graph.getWeight(u, v)));
//            }
//        }
//
//
//
//
//        return null;
//    }

    public static List<Integer> shortestPath(Graph graph, int s, int end) {

        List<List<Integer>> adjacencyList = graph.getAdjacencyList();
        int infinity = graph.getInfinity();

        boolean[] oldVertices = new boolean[adjacencyList.size()];
        int[] distance = new int[adjacencyList.size()];
        int[] P = new int[adjacencyList.size()];
        P[s] = s;

        for (int i = 0; i < adjacencyList.size(); i++) {
            //Integer dist = graph.getWeight(s, i);
            //if (dist != null) distance[i] = dist;
            //else
            distance[i] = infinity;
        }
        distance[s] = 0;

        while (true) {
            int v = getMinDistIdx(distance, oldVertices);
            if (v == end) break;
            oldVertices[v] = true;

            for (int u : adjacencyList.get(v)) {//przydalo by sie dac if i sprawdzac oldVertices...
                int weight = graph.getWeight(u, v);
                if (distance[v]+weight < distance[u]) {
                    distance[u] = distance[v] + weight;
                    //TODO poprzendik
                    P[u] = v;
                }
            }

        }

        List<Integer> shortestPath = new ArrayList<>();
        //System.out.println(Arrays.toString(P));
        int currVx = end;
        while (currVx != s) {
            shortestPath.add(currVx);
            currVx = P[currVx];
        }
        shortestPath.add(s);
        Collections.reverse(shortestPath);

        return shortestPath;
    }

    private static int getMinDistIdx(int[] distance, boolean[] oldVertices) {
        int index = 0;
        while (oldVertices[index]) index++;
        for (int i = 0; i < distance.length; i++) {
            if (!oldVertices[i] && distance[i] < distance[index])
                index = i;
        }
        return index;
    }

}
