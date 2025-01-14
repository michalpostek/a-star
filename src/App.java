import java.io.File;
import java.util.*;

class Node {
    int row, col, g, order;
    double h, f;
    Node previous;

    public Node(int row, int col, int g, double h, Node previous, int order) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.previous = previous;
        this.order = order;
    }
}

public class App {
    static final int MAP_SIZE = 20;
    static final int START_ROW = MAP_SIZE - 1;
    static final int START_COL = 0;
    static final int FINISH_ROW = 0;
    static final int FINISH_COL = MAP_SIZE - 1;
    static final int[][] MOVES = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
    static final int OBSTACLE = 5;
    static final int MOVE_COST = 1;

    public static void main(String[] args) throws Exception {
        int[][] map = parseGridTextFile("./src/grid.txt");

        printMap(map);
        System.out.println("\n");
        Optional<List<Node>> path = findPath(map);

        if (path.isPresent()) {
            fillPath(map, path.get());
            printMap(map);
        } else {
            System.out.println("Nie ma ścieżki");
        }
    }

    private static void fillPath(int[][] map, List<Node> path) {
        for (Node node : path) {
            map[node.row][node.col] = 3;
        }
    }

    private static Optional<Node> getTargetNode(PriorityQueue<Node> openList, int row, int col) {
        Optional<Node> targetNode = Optional.empty();

        for (Node node : openList) {
            if (row == node.row && col == node.col) {
                targetNode = Optional.of(node);
            }
        }

        return targetNode;
    }

    private static Optional<List<Node>> findPath(int[][] map) {
        int counter = 0;

        PriorityQueue<Node> openList = new PriorityQueue<Node>(Comparator
            .comparingDouble((Node node) -> node.f)
            .thenComparingInt((Node node) -> -node.order)
        );
        boolean[][] closedList = new boolean[MAP_SIZE][MAP_SIZE];

        Node startNode = new Node(START_ROW, START_COL, 0, getEuclideanDistance(START_ROW, START_COL), null, counter++);
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (isFinish(current)) {
                return Optional.of(reconstructPath(current));
            }

            closedList[current.row][current.col] = true;

            for (int i = 0; i < 4; i++) {
                int newRow = current.row + MOVES[i][0];
                int newCol = current.col + MOVES[i][1];

                if (!isMapElement(newRow, newCol) || map[newRow][newCol] == OBSTACLE || closedList[newRow][newCol]) {
                    continue;
                }

                Optional<Node> targetNode = getTargetNode(openList, newRow, newCol);

                int g = current.g + MOVE_COST;
                double heuristic = getEuclideanDistance(newRow, newCol);
                Node node = new Node(newRow, newCol, g, heuristic, current, counter++);

                if (targetNode.isEmpty()) {
                    openList.add(node);
                } else if (targetNode.get().f > node.f) {
                    openList.remove(targetNode.get());
                    openList.add(node);
                }
            }
        }

        return Optional.empty();
    }

    private static boolean isMapElement(int x, int y) {
        return x >= 0 && x < MAP_SIZE && y >= 0 && y < MAP_SIZE;
    }

    private static ArrayList<Node> reconstructPath(Node node) {
        ArrayList<Node> path = new ArrayList<Node>();
        Node current = node;

        while (current != null) {
            path.add(current);
            current = current.previous;
        }

        return path;
    }

    private static boolean isFinish(Node node) {
        return node.row == FINISH_ROW && node.col == FINISH_COL;
    }

    private static double getEuclideanDistance(int currentRow, int currentCol) {
        return Math.sqrt(Math.pow(FINISH_ROW - currentRow, 2) + Math.pow(FINISH_COL - currentCol, 2));
    }

    private static void printMap(int[][] map) {
        for (int[] rows : map) {
            for (int num : rows) {
                System.out.print(num + " ");
            }
            System.out.println();
        }
    }

    private static int[][] parseGridTextFile(String path) throws Exception {
        File file = new File(path);
        Scanner scanner = new Scanner(file);

        int[][] map = new int[MAP_SIZE][MAP_SIZE];
        int row = 0;

        while (scanner.hasNextLine() && row < MAP_SIZE) {
            String line = scanner.nextLine();
            String[] lineItems = line.split(" ");

            for (int col = 0; col < MAP_SIZE; col++) {
                map[row][col] = Integer.parseInt(lineItems[col]);
            }

            row++;
        }

        scanner.close();

        return map;
    }
}
