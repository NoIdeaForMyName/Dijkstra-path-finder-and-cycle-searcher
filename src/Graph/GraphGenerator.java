package Graph;

import DisjointSetDataStructure.DisjointSetForest;

import java.awt.*;
import java.util.*;
import java.util.List;

public class GraphGenerator {
    private final int vertices;
    private final int edges;
    private final int maxWeight;
    private int width;
    private int height;
    private final int borders;
    private final int vxRadius;

    public GraphGenerator(int vertices, int edges, int maxWeight, int width, int height, int borders, int vxRadius) {
        this.vertices = vertices;
        this.edges = edges;
        this.maxWeight = maxWeight;

        this.width = width;
        this.height = height;
        this.borders = borders;
        this.vxRadius = vxRadius;

    }

    public Graph generatePlanarGraph() {
        if (edges > (3 * vertices) - 6) {
            System.out.println("Cannot generate planar graph with the given number of edges and vertices.");
            return null;
        }

        List<List<Integer>> adjacencyList = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        Random random = new Random();
        //int maxEdges = (3 * vertices) - 6;
        int edgesLeft = this.edges;
        int notConnectedVertices = vertices;
        DisjointSetForest dsf = new DisjointSetForest(vertices);
        Map<List<Integer>, Integer> weights = new HashMap<>();


        while (edgesLeft > 0) {

            int weight = random.nextInt(1, maxWeight);
            int u = random.nextInt(vertices);
            int v = random.nextInt(vertices);

            if (u != v && !adjacencyList.get(u).contains(v) && !adjacencyList.get(v).contains(u) &&
                    adjacencyList.get(u).size() < 3 && adjacencyList.get(v).size() < 3) {

                if (dsf.getNbOfDisjointedSets()-1 < edgesLeft || dsf.find(u) != dsf.find(v)) {
                    adjacencyList.get(u).add(v);
                    adjacencyList.get(v).add(u);
                    List<Integer> tempList = new ArrayList<>();
                    tempList.add(Math.min(u, v));
                    tempList.add(Math.max(u, v));
                    weights.put(tempList, weight);
                    edgesLeft--;
                    dsf.union(u, v);
                }

                //if (dsf.find(u.getNb()) == u.getNb()) notConnectedVertices -= 1;
                //if (dsf.find(v.getNb()) == v.getNb()) notConnectedVertices -= 1;

            }
        }

        // setting vertices in space...
        List<Point> vertexCoordinates = new ArrayList<>();
        width -= 2 * borders;
        height -= 2 * borders;

        int centerX = width/2 + borders;
        int centerY = height/2 + borders;
        int r; //Radius
        if (height < width) r = height/2 - borders;
        else r = width/2 - borders;
        //in circle :)
        for (int i = 0; i < vertices; i++) {
            double angle = (double) i / vertices * 2*Math.PI;
            int dx = (int) Math.round(r * Math.sin(angle));
            int dy = (int) Math.round(r * Math.cos(angle));
            vertexCoordinates.add(new Point(centerX+dx, centerY+dy));
        }
        //end setting vertices in space

        return new Graph(adjacencyList, weights, vertexCoordinates, maxWeight);
    }

    public Graph generateRelativeNeighborhoodGraph() {
        List<Point> vertexCoordinates = generateVertexCoordinates();

        List<List<Integer>> adjacencyList = new LinkedList<>();
        Map<List<Integer>, Integer> weights = new HashMap<>();

        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }

        for (int i = 0; i < vertices - 1; i++) { // TODO POCZYTAJ CZY NIE DA SIE TEGO ZROBIC EFEKTYWNIEJ
            for (int j = i + 1; j < vertices; j++) {
                boolean closerVertexExists = false;
                for (int k = 0; k < vertices; k++) {
                    if (k != i && k != j) {
                        Point p = vertexCoordinates.get(i);
                        Point q = vertexCoordinates.get(j);
                        Point r = vertexCoordinates.get(k);

                        double distancePQ = calculateDistance(p, q);
                        double distancePR = calculateDistance(p, r);
                        double distanceQR = calculateDistance(q, r);

                        if (distancePR < distancePQ && distanceQR < distancePQ) {
                            closerVertexExists = true;
                            break;
                        }
                    }
                }

                if (!closerVertexExists) {
                    adjacencyList.get(i).add(j);
                    adjacencyList.get(j).add(i);

                    int weight = new Random().nextInt(1, maxWeight);
                    List<Integer> tempList = new ArrayList<>();
                    tempList.add(Math.min(i, j));
                    tempList.add(Math.max(i, j));
                    weights.put(tempList, weight);
                }
            }
        }

        return new Graph(adjacencyList, weights, vertexCoordinates, maxWeight);
    }

    public Graph generateGabrielGraph() {
        List<Point> vertexCoordinates = generateVertexCoordinates();
        List<List<Integer>> adjacencyList = new LinkedList<>();
        for (int i = 0; i < vertices; i++) {
            adjacencyList.add(new ArrayList<>());
        }
        Map<List<Integer>, Integer> weights = new HashMap<>();

        for (int i = 0; i < vertices - 1; i++) {
            for (int j = i + 1; j < vertices; j++) {
                boolean insideCircle = false;
                for (int k = 0; k < vertices; k++) {
                    if (k != i && k != j) {
                        Point p = vertexCoordinates.get(i);
                        Point q = vertexCoordinates.get(j);
                        Point r = vertexCoordinates.get(k);

                        Point cirCenter = new Point((p.x+q.x)/2, (p.y+q.y)/2);
                        double R = calculateDistance(p, cirCenter);
                        double distanceR = calculateDistance(r, cirCenter);

                        if (distanceR <= R) {
                            insideCircle = true;
                            break;
                        }
                    }
                }

                if (!insideCircle) {
                    adjacencyList.get(i).add(j);
                    adjacencyList.get(j).add(i);

                    int weight = new Random().nextInt(1, maxWeight);
                    List<Integer> tempList = new ArrayList<>();
                    tempList.add(Math.min(i, j));
                    tempList.add(Math.max(i, j));
                    weights.put(tempList, weight);
                }
            }
        }

        return new Graph(adjacencyList, weights, vertexCoordinates, maxWeight);
    }

    private List<Point> generateVertexCoordinates() {
        List<Point> vertexCoordinates = new ArrayList<>();
        Random random = new Random();
        width -= 2 * borders;
        height -= 2 * borders;
        int remainingVx = vertices;

        while (remainingVx != 0) {
            int x = random.nextInt(width) + borders;
            int y = random.nextInt(height) + borders;
            boolean add = true;
            Point newPoint = new Point(x, y);
            for (Point vx : vertexCoordinates) {
                double dist = calculateDistance(vx, newPoint);
                if (dist <= 4*vxRadius) {
                    add = false;
                    break;
                }
            }
            if (add) {
                vertexCoordinates.add(newPoint);
                remainingVx--;
            }
        }

        return vertexCoordinates;
    }

    private double calculateDistance(Point p1, Point p2) {
        int dx = p2.x - p1.x;
        int dy = p2.y - p1.y;
        return Math.sqrt(dx * dx + dy * dy);
    }


}
