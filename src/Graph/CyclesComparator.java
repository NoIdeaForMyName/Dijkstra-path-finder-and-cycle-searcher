package Graph;

import java.util.Comparator;
import java.util.List;

public class CyclesComparator implements Comparator<List<Integer>> {

    private final Graph graph;

    public CyclesComparator(Graph graph) {
        this.graph = graph;
    }

    // from the biggest to the smallest (in terms of weight sum)
    @Override
    public int compare(List<Integer> o1, List<Integer> o2) {
        return Integer.compare(weightSum(o2), weightSum(o1));
    }

    public int weightSum(List<Integer> cycle) {
        int sum = 0;
        int n = cycle.size();
        for (int i = 0; i < n; i++) {
            if (i+1 < n) {
                sum += graph.getWeight(cycle.get(i), cycle.get(i+1));
            }
            else {
                sum += graph.getWeight(cycle.get(i), cycle.get(0));
            }
        }
        return sum;
    }
}
