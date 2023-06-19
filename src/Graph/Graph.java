package Graph;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Graph {

    private final List<List<Integer>> adjacencyList;
    private final Map<List<Integer>, Integer> weights;
    private final List<Point> vertexCoordinates;
    private final int infinity;


    public Graph(List<List<Integer>> adjacencyList, Map<List<Integer>, Integer> weights, List<Point> vertexCoordinates, int MAX_WEIGHT) {
        this.adjacencyList = adjacencyList;
        this.weights = weights;
        this.vertexCoordinates = vertexCoordinates;
        infinity = adjacencyList.size() * MAX_WEIGHT + 1;
    }

    public List<List<Integer>> getAdjacencyList() {
        return adjacencyList;
    }

    public int getInfinity() {
        return infinity;
    }

    public Integer getWeight(int vert_1, int vert_2) {
        if (vert_1 == vert_2) return 0;
        List<Integer> tempList = new ArrayList<>();
        tempList.add(Math.min(vert_1, vert_2));
        tempList.add(Math.max(vert_1, vert_2));
        return weights.get(tempList);
    }

    public void setWeight(int vert_1, int vert_2, int weight) {
        List<Integer> tempList = new ArrayList<>();
        tempList.add(Math.min(vert_1, vert_2));
        tempList.add(Math.max(vert_1, vert_2));
        weights.put(tempList, weight);
    }

    public List<Point> getVertexCoordinates() {
        return vertexCoordinates;
    }

    private void DFSfindCycle(int v, int w, List<List<Integer>> cycles, boolean[] visited) {
        visited[w] = true;
        List<Integer> cycle;
        for (int u : adjacencyList.get(w)) {
            cycle = cycles.get(cycles.size()-1);
            if (cycle.isEmpty() || u != cycle.get(cycle.size()-1)) {
                cycle.add(w);
                if (u == v) {
                    cycles.add(new ArrayList<>(cycle)); // this cycle has ended, place for new one
                }
                else {
                    if (!visited[u]) DFSfindCycle(v, u, cycles, visited);
                }
                cycle = cycles.get(cycles.size() - 1);
                cycle.remove(cycle.size() - 1); //removes from cycle list
            }
        }

        visited[w] = false;
    }

    public List<List<Integer>> findAllCycles() {
        int n = adjacencyList.size();
        boolean[] visited = new boolean[n];
        List<List<Integer>> cycles = new ArrayList<>();
        //cycles.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            Arrays.fill(visited, false);
            cycles.add(new ArrayList<>());
            DFSfindCycle(i, i, cycles, visited);
        }

        // deleting repetitions...
        for (int i = 0; i < cycles.size(); i++) {
            if (cycles.get(i).isEmpty()) {
                cycles.remove(i);
                i--;
            }
            else {
                for (int j = 0; j < cycles.size(); j++) {
                    if (i != j) {
                        if (cycles.get(i).size() == cycles.get(j).size()) {
                            List<Integer> temp_1 = new ArrayList<>(cycles.get(i));
                            List<Integer> temp_2 = new ArrayList<>(cycles.get(j));
                            temp_1.sort(null);
                            temp_2.sort(null);
                            if (temp_1.equals(temp_2)) {
                                cycles.remove(j);
                                j--;
                            }
                        }
                    }
                }
            }
        }

        // sorting cycles...
        CyclesComparator comparator = new CyclesComparator(this);
        cycles.sort(comparator);

        return cycles;
    }


}
