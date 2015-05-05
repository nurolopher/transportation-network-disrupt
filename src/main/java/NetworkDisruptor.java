import org.graphstream.algorithm.flow.FordFulkersonAlgorithm;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import utilities.MaxFlowMinCut;
import utilities.Pair;

import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

public class NetworkDisruptor {

    public static Graph graph;
    public static int[][] capacityArray;
    public static FordFulkersonAlgorithm fordFulkersonAlgorithm = new FordFulkersonAlgorithm();
    protected static String styleSheet =
            "node {" +
                    "   fill-color: blue;" +
                    " size:20px; " +
                    "text-background-mode:none;" +
                    "}" +
                    "node.marked {" +
                    "   fill-color: red;" +
                    "}" +
                    "node:clicked {\n" +
                    "        fill-color: red;\n" +
                    "    }" +
                    "edge {" +
                    "text-color:red;" +
                    "text-alignment:above;" +
                    "fill-color: brown;" +
                    "}";


    public static void main(String args[]) {
        /*Global Configuration*/
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        int sourceT;
        int sink;
        double maxFlow;

        Scanner scanner = new Scanner(System.in);
        System.out.println("Source ID: ");
        sourceT = scanner.nextInt();

        System.out.println("Sink ID");
        sink = scanner.nextInt();

        initializeGraph();
        loadFromDgsFile();

        fordFulkersonAlgorithm.init(graph, Integer.toString(sourceT), Integer.toString(sink));

        setCapacityFromEdges();
        fordFulkersonAlgorithm.compute();
        maxFlow = fordFulkersonAlgorithm.getMaximumFlow();


        initializeCapacityArray();

        for (Node node : graph) {
            node.addAttribute("ui.label", node.getId());
        }

        setLabels();

        /* Print Max flow*/
        System.out.println("Max flow is : " + maxFlow);

        printCapacityArray();
        MaxFlowMinCut maxFlowMinCut = new MaxFlowMinCut(graph.getNodeCount());
        maxFlowMinCut.maxFlowMinCut(capacityArray, sourceT, sink);

        maxFlowMinCut.printCutSet();

        /*Color min cut edges*/
        Set<Pair> cutSet = maxFlowMinCut.getCutSet();
        colorEdges(cutSet);
        graph.display();
    }

    private static void initializeGraph() {
        graph = new MultiGraph("Transportation Network Disruption");
        graph.addAttribute("ui.stylesheet", styleSheet);
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
    }

    private static void printCapacityArray() {
        for (int i = 1; i <= graph.getNodeCount(); i++) {
            for (int j = 1; j <= graph.getNodeCount(); j++) {
                System.out.printf("%10d", capacityArray[i][j]);
            }
            System.out.println();
        }
    }

    private static void setLabels() {
        for (Edge edge : graph.getEachEdge()) {
            Node nodeFirst = graph.getNode(Character.toString(edge.getId().charAt(0)));
            Node nodeSecond = graph.getNode(Character.toString(edge.getId().charAt(1)));
            double flow = fordFulkersonAlgorithm.getFlow(nodeFirst, nodeSecond);
            double capacity = fordFulkersonAlgorithm.getCapacity(nodeFirst, nodeSecond);
            edge.setAttribute("label", capacity + ", " + flow);

            //fill array!
            capacityArray[Integer.parseInt(nodeFirst.getId())][Integer.parseInt(nodeSecond.getId())] = (int) capacity;
        }
    }

    private static void loadFromDgsFile() {
        try {
            FileSource source = FileSourceFactory.sourceFor(
                    "data/tutorialNumerical.dgs");
            source.addSink(graph);
            source.begin("data/tutorialNumerical.dgs");
            while (source.nextEvents()) ;
            source.end();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void setCapacityFromEdges() {
        for (Edge edge : graph.getEachEdge()) {
            String nodeFirst = Character.toString(edge.getId().charAt(0));
            String nodeSecond = Character.toString(edge.getId().charAt(1));
            double weight = Double.parseDouble(edge.getAttribute("weight").toString());
            fordFulkersonAlgorithm.setCapacity(nodeFirst, nodeSecond, weight);
        }
    }

    private static void initializeCapacityArray() {
        capacityArray = new int[graph.getNodeCount() + 1][graph.getNodeCount() + 1];
        for (int i = 1; i < graph.getNodeCount(); i++) {
            for (int j = 1; j < graph.getNodeCount(); j++) {
                capacityArray[i][j] = 0;
            }
        }
    }

    public static void colorEdges(Set<Pair> cutSet) {
        Iterator<Pair> iterator = cutSet.iterator();
        while (iterator.hasNext()) {
            Pair pair = iterator.next();
            graph.getEdge(pair.source + "" + pair.destination).addAttribute("ui.style", "fill-color: rgb(0,255,0);");
        }
    }
}
