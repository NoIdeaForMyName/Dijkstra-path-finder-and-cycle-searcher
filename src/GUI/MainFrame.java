package GUI;

import Dijkstra.DijkstraShortestPathFinder;
import Graph.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.util.List;

public class MainFrame extends JFrame {

    private JPanel mainPanel;
    private GraphPanel graphPanel;
    private JPanel optionPanel;
    private final int GRAPH_PANEL_WIDTH = 900;
    private final int GRAPH_PANEL_HEIGHT = 550;
    private final int RADIUS = 40;

    private JComboBox<String> graphSelectionBox;
    private JButton newGraphButton;
    private JButton newRandomPathButton;
    private JButton drawPathButton;
    private JButton findCyclesButton;

    String[] graphs;
    private Graph graph;
    private GraphGenerator graphGenerator;
    private List<Point> vertexCoordinates;
    private final int MAX_VERTICES = 10;
    private final int MAX_EDGES = 20;
    private final int MAX_WEIGHT = 20;
    private int vertices;
    private int edges;
    private int source;
    private int end;
    private List<Integer> shortestPath;
    private List<List<Integer>> cycles;

    public MainFrame() {

        mainPanel = new JPanel();
        graphPanel = new GraphPanel();

        graphPanel.setPreferredSize(new Dimension(GRAPH_PANEL_WIDTH, GRAPH_PANEL_HEIGHT));
        graphPanel.setBorder(BorderFactory.createLineBorder(Color.RED));
        graphPanel.setLayout(new BorderLayout());

        optionPanel = new JPanel();

        graphs = new String[]{"Graph In Circle", "Relative Neighborhood Graph", "Gabriel Graph"};
        graphSelectionBox = new JComboBox<>(graphs);
        DefaultListCellRenderer dlcr = new DefaultListCellRenderer();
        dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
        graphSelectionBox.setRenderer(dlcr);

        newGraphButton = new JButton("draw new graph");
        newGraphButton.addActionListener(new NewGraphButtonListener());

        newRandomPathButton = new JButton("loss new path");
        newRandomPathButton.addActionListener(new NewPathButtonListener());
        newRandomPathButton.setEnabled(false);

        drawPathButton = new JButton("draw path");
        drawPathButton.addActionListener(new DrawPathButtonListener());
        drawPathButton.setEnabled(false);

        findCyclesButton = new JButton("find cycles");
        findCyclesButton.addActionListener(new FindCyclesButtonListener());
        findCyclesButton.setEnabled(false);

        optionPanel.setLayout(new GridLayout(2, 3, 100, 10));

        optionPanel.add(newGraphButton);
        optionPanel.add(newRandomPathButton);
        optionPanel.add(drawPathButton);
        optionPanel.add(graphSelectionBox);
        optionPanel.add(new Component() {});
        optionPanel.add(findCyclesButton);

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.add(optionPanel);
        mainPanel.add(graphPanel);

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
        setTitle("Dijkstra Shortest Path Finder");
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private class GraphPanel extends JPanel {

        private int whatToRepaint; //1 - graph; 2 - source, end; 3 - path

        public void repaint(int whatToRepaint) {
            this.whatToRepaint = whatToRepaint;
            this.repaint();
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            eraseAll(g);
            switch (whatToRepaint) {
                case 1 -> paintGraph(g);

                case 2 -> {
                    paintGraph(g);
                    paintSourceEnd(g);
                }

                case 3 -> {
                    paintPath(g);
                    paintGraph(g);
                    paintSourceEnd(g);
                }
            }
        }

        private void paintGraph(Graphics g) {
            g.setColor(new Color(73, 73, 73));
            Set<List<Integer>> alreadyDrawn = new HashSet<>();
            List<List<Integer>> adjacencyList;
            if (graph != null) adjacencyList = graph.getAdjacencyList();
            else adjacencyList = new ArrayList<>();
            for (int i = 0; i < adjacencyList.size(); i++) { // i - mainVx
                for (Integer vx : adjacencyList.get(i)) {
                    List<Integer> edge = new ArrayList<>();
                    edge.add(Math.min(i, vx));
                    edge.add(Math.max(i, vx));
                    if (alreadyDrawn.add(edge)) { //not drawn yet...
                        Point p1 = vertexCoordinates.get(i);
                        Point p2 = vertexCoordinates.get(vx);
                        g.drawLine(p1.x, p1.y, p2.x, p2.y);

                        int weight = graph.getWeight(i, vx);

                        int w_posX_1;
                        int w_posY_1;
                        int w_posX_2;
                        int w_posY_2;
                        int pX_max = Math.max(p1.x, p2.x);
                        int pX_min = Math.min(p1.x, p2.x);
                        int pY_max = Math.max(p1.y, p2.y);
                        int pY_min = Math.min(p1.y, p2.y);

                        //double ratio = ((double)(pY_max-pY_min)) / ((double)(pX_max-pX_min));
                        //int w_dx = RADIUS;
                        //int w_dy = (int) (w_dx*ratio);

                        int w_dx = (pX_max-pX_min)/4;
                        int w_dy = (pY_max-pY_min)/4;

                        w_posX_1 = pX_min + w_dx;
                        w_posX_2 = pX_max - w_dx;
                        if ((pX_min == p1.x && pY_max == p1.y) || (pX_min == p2.x && pY_max == p2.y)) {
                            w_posY_1 = pY_max - w_dy;
                            w_posY_2 = pY_min + w_dy;
                        }
                        else {
                            w_posY_1 = pY_min + w_dy;
                            w_posY_2 = pY_max - w_dy;
                        }

                        //int w_posX = (p1.x + p2.x)/2; OLD
                        //int w_posY = (p1.y + p2.y)/2; OLD
                        //if (Math.abs(p1.x-p2.x) > Math.abs(p1.y-p2.y)) w_posY += 3; OLD
                        //else w_posX += 3; OLD

                        g.setColor(Color.BLACK);
                        g.drawString(Integer.toString(weight), w_posX_1, w_posY_1);
                        g.drawString(Integer.toString(weight), w_posX_2, w_posY_2);
                        g.setColor(new Color(73, 73, 73));
                    }
                }
            }

            g.setColor(new Color(175, 167, 167));
            if (vertexCoordinates == null) vertexCoordinates = new ArrayList<>();
            for (int i = 0; i < vertexCoordinates.size(); i++) {
                Point vertexPos = vertexCoordinates.get(i);
                int x = vertexPos.x - RADIUS/2;
                int y = vertexPos.y - RADIUS/2;
                g.fillOval(x, y, RADIUS, RADIUS);
                g.setColor(new Color(0, 0, 0));
                g.drawString(Integer.toString(i), vertexPos.x-2, vertexPos.y+5);
                g.setColor(new Color(175, 167, 167));
            }
        }

        private void paintSourceEnd(Graphics g) {
            Point sourcePos = vertexCoordinates.get(source);
            Point endPos = vertexCoordinates.get(end);

            g.setColor(Color.GREEN);
            int x = sourcePos.x - RADIUS/2;
            int y = sourcePos.y - RADIUS/2;
            g.fillOval(x, y, RADIUS, RADIUS);

            g.setColor(Color.RED);
            x = endPos.x - RADIUS/2;
            y = endPos.y - RADIUS/2;
            g.fillOval(x, y, RADIUS, RADIUS);

            g.setColor(Color.BLACK);
            g.drawString(Integer.toString(source), sourcePos.x-2, sourcePos.y+5);
            g.drawString(Integer.toString(end), endPos.x-2, endPos.y+5);

        }

        private void paintPath(Graphics g) {

            Graphics2D g2 = (Graphics2D) g;
            float thickness = 3f; // line thickness
            Stroke oldStroke = g2.getStroke(); // remember old stroke
            g2.setStroke(new BasicStroke(thickness)); // set new thickness
            g2.setColor(Color.ORANGE);

            for (int i = 0; i < shortestPath.size(); i++) {
                Point onPathPos_1 = vertexCoordinates.get(shortestPath.get(i));
                if (i+1 == shortestPath.size()) break;
                Point onPathPos_2 = vertexCoordinates.get(shortestPath.get(i+1));

                g2.drawLine(onPathPos_1.x, onPathPos_1.y, onPathPos_2.x, onPathPos_2.y);
            }
            g2.setStroke(oldStroke); //restore old thickness
        }

        private void eraseAll(Graphics g) {
            g.setColor(Color.WHITE);
            g.drawRect(0, 0, getWidth(), getHeight());
        }

    }

    private class NewGraphButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {

            String selectedGraph = (String) (graphSelectionBox.getSelectedItem());

            Random random = new Random();
            vertices = random.nextInt(MAX_VERTICES);
            edges = random.nextInt(MAX_EDGES);

            if (graphs[0].equals(selectedGraph)) {
                while (edges > 3 * vertices - 6 || edges < vertices - 1 || Math.floor(vertices * 1.5) <= edges) {
                    vertices = random.nextInt(MAX_VERTICES);
                    edges = random.nextInt(MAX_EDGES);
                }
            }

            else {
                vertices *= 1.4;
                while (vertices < 5) vertices = random.nextInt((int) (MAX_VERTICES*1.4));
            }

            graphGenerator = new GraphGenerator(vertices, edges, MAX_WEIGHT, GRAPH_PANEL_WIDTH, GRAPH_PANEL_HEIGHT, RADIUS, RADIUS); //TODO
            //graph = graphGenerator.generateRelativeNeighborhoodGraph();
            if (graphs[0].equals(selectedGraph)) graph = graphGenerator.generatePlanarGraph();
            if (graphs[1].equals(selectedGraph)) graph = graphGenerator.generateRelativeNeighborhoodGraph();
            if (graphs[2].equals(selectedGraph)) graph = graphGenerator.generateGabrielGraph();
            vertexCoordinates = graph.getVertexCoordinates();
            //System.out.println(vertexCoordinates);

            newRandomPathButton.setEnabled(true);
            drawPathButton.setEnabled(false);
            findCyclesButton.setEnabled(true);

            graphPanel.repaint(1);
        }
    }

    private class NewPathButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            Random random = new Random();
            source = random.nextInt(vertices);
            end = random.nextInt(vertices);
            while (source == end) end = random.nextInt(vertices);

            drawPathButton.setEnabled(true);
            graphPanel.repaint(2);
        }
    }

    private class DrawPathButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            shortestPath = DijkstraShortestPathFinder.shortestPath(graph, source, end);
            graphPanel.repaint(3);
            ((JButton) e.getSource()).setEnabled(false);
        }
    }

    private class FindCyclesButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            cycles = graph.findAllCycles();
            CyclesComparator cyclesComparator = new CyclesComparator(graph);

            ((JButton) e.getSource()).setEnabled(false);

            JDialog popupWindow = new JDialog();
            popupWindow.setTitle("Found cycles");
            popupWindow.setModal(true);
            popupWindow.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

            JPanel contentPane = new JPanel();
            contentPane.setLayout(new BorderLayout());

            JPanel cyclePanel = new JPanel();
            cyclePanel.setLayout(new GridLayout(cycles.size(), 2));

            for (List<Integer> cycle : cycles) {
                JLabel cycleLabel = new JLabel(cycle.toString());
                JPanel listPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                listPanel.add(cycleLabel);
                cyclePanel.add(listPanel);

                int sum = cyclesComparator.weightSum(cycle);
                JLabel sumLabel = new JLabel("Sum: " + sum);
                JPanel sumPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                sumPanel.add(sumLabel);
                cyclePanel.add(sumPanel);
            }

            JScrollPane cycleScrollPane = new JScrollPane(cyclePanel);
            cycleScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            JLabel tempLabel = new JLabel("TEXT");
            int screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
            int textHeight = cycles.size() * (tempLabel.getFontMetrics(tempLabel.getFont()).getHeight() + 15);
            int windowHeight = Math.min(screenHeight/2, textHeight);
            cycleScrollPane.setPreferredSize(new Dimension(cycleScrollPane.getPreferredSize().width, windowHeight));


            JButton okButton = new JButton("OK");
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(okButton);
            okButton.addActionListener(e1 -> {
                ((JButton) e.getSource()).setEnabled(true);
                popupWindow.dispose(); // Close the popup window
            });

            popupWindow.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e1) {
                    super.windowClosing(e1);
                    ((JButton) e.getSource()).setEnabled(true);
                }
            });

            if (!cycles.isEmpty()) contentPane.add(cycleScrollPane, BorderLayout.CENTER);
            else {
                JPanel innerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                innerPanel.add(new JLabel("No cycles found!"));
                contentPane.add(innerPanel, BorderLayout.CENTER);
            }
            contentPane.add(buttonPanel, BorderLayout.SOUTH);
            popupWindow.setContentPane(contentPane);

            popupWindow.setMinimumSize(new Dimension(200, 0));

            popupWindow.pack();

            popupWindow.setLocationRelativeTo(null);
            popupWindow.setVisible(true);
        }

    }

}
