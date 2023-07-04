import java.util.*;

public class ZombieBridge {

    private static Map<String, Integer> traversalTimes;
    static {
        traversalTimes = new HashMap<>();
        traversalTimes.put("A", 1);
        traversalTimes.put("B", 2);
        traversalTimes.put("C", 5);
        traversalTimes.put("D", 10);
    }

    private static class Node {
        private Set<String> walkers;
        private int flashlightPosition;

        public Node(Set<String> walkers, int flashlightPosition) {
            this.walkers = walkers;
            this.flashlightPosition = flashlightPosition;
        }

        @Override
        public int hashCode() {
            return Objects.hash(walkers, flashlightPosition);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Node other = (Node) obj;
            return flashlightPosition == other.flashlightPosition && Objects.equals(walkers, other.walkers);
        }

        public Set<String> getWalkers() {
            return walkers;
        }
    }

    private static class Edge {
        private Node destination;
        private int weight;

        public Edge(Node destination, int weight) {
            this.destination = destination;
            this.weight = weight;
        }

        public Node getDestination() {
            return destination;
        }

        public int getWeight() {
            return weight;
        }
    }

    public static boolean verifyNeighbourNode(Map<Node, List<Edge>> graph, Node n1, Node n2) {
        List<Edge> arestasDoNo = graph.getOrDefault(n1, new ArrayList<>());

        for (Edge aresta : arestasDoNo) {
            if (aresta.getDestination().equals(n2)) {
                return true;
            }
        }

        return false;
    }

    public static Map<Node, List<Edge>> transformInGraph(Set<String> allWalkers) {

        Node initialNode = new Node(allWalkers, 1);
        Queue<Node> queue = new LinkedList<>();
        Map<Node, List<Edge>> graph = new HashMap<>();

        queue.offer(initialNode);
        graph.put(initialNode, new ArrayList<>());

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            if (currentNode.walkers.isEmpty()) {
                return graph;
            }

            if (currentNode.flashlightPosition == 1) {
                for (String walker1 : currentNode.walkers) {
                    for (String walker2 : currentNode.walkers) {
                        if (!walker1.equals(walker2)) {
                            Set<String> newWalkers = new HashSet<>(currentNode.walkers);
                            newWalkers.remove(walker1);
                            newWalkers.remove(walker2);

                            int crossingTime = Math.max(traversalTimes.get(walker1), traversalTimes.get(walker2));
                            Node newNode = new Node(newWalkers, 1 - currentNode.flashlightPosition);

                            if (!graph.containsKey(newNode)) {
                                queue.offer(newNode);
                                graph.put(newNode, new ArrayList<>());
                            }

                            if (!verifyNeighbourNode(graph, currentNode, newNode)) {
                                Edge aresta = new Edge(newNode, crossingTime);
                                graph.get(currentNode).add(aresta);
                            }
                        }
                    }
                }
            } else {
                Set<String> newWalkers = new HashSet<>(currentNode.walkers);

                for (String walker : allWalkers) {
                    if (!newWalkers.contains(walker)) {

                        Set<String> updatedWalkers = new HashSet<>(newWalkers);
                        updatedWalkers.add(walker);
                        Node returnNode = new Node(updatedWalkers, 1 - currentNode.flashlightPosition);

                        if (!graph.containsKey(returnNode)) {
                            queue.offer(returnNode);
                            graph.put(returnNode, new ArrayList<>());
                        }
                        if (!verifyNeighbourNode(graph, currentNode, returnNode)) {
                            Edge aresta = new Edge(returnNode, traversalTimes.get(walker));
                            graph.get(currentNode).add(aresta);
                        }
                    }
                }
            }
        }

        return null; // Caso nenhum caminho seja encontrado
    }

    public static void buscaLargura(Map<Node, List<Edge>> graph, Node noInicial, Node noFinal) {

        Map<Node, Node> previousNodes = new HashMap<>();
        Map<Node, Integer> distances = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

        distances.put(noInicial, 0);
        queue.offer(noInicial);

        while (!queue.isEmpty()) {
            Node currentNode = queue.poll();

            if (currentNode.equals(noFinal)) {
                break;
            }

            int currentDistance = distances.get(currentNode);

            List<Edge> edges = graph.get(currentNode);

            for (Edge edge : edges) {
                Node destination = edge.getDestination();
                int weight = edge.getWeight();
                int newDistance = currentDistance + weight;

                if (!distances.containsKey(destination) || newDistance < distances.get(destination)) {
                    distances.put(destination, newDistance);
                    previousNodes.put(destination, currentNode);
                    queue.offer(destination);
                }
            }
        }

        if (distances.containsKey(noFinal)) {
            List<Node> path = new ArrayList<>();
            Node current = noFinal;
            path.add(current);

            while (!current.equals(noInicial)) {
                current = previousNodes.get(current);
                path.add(current);
            }

            System.out.println("Caminho percorrido:");
            for (int i = path.size() - 1; i >= 0; i--) {
                Node node = path.get(i);
                System.out.println("Nó " + node.getWalkers() + ", Torch: " + node.flashlightPosition);
            }
        } else {
            System.out.println("Caminho não encontrado.");
        }
    }

    public static void main(String[] args) {

        Set<String> allWalkers = new HashSet<>(Arrays.asList("A", "B", "C", "D"));
        Map<Node, List<Edge>> graph = transformInGraph(allWalkers);

        Node noInicial = new Node(allWalkers, 1);
        Node noFinal = new Node(Collections.emptySet(), 0);

        buscaLargura(graph, noInicial, noFinal);
    }
}
