package node;

public class Node {
    public final int row, col, g;
    public final double h, f;
    public Node previous;

    public Node(int row, int col, int g, double h, Node previous) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.previous = previous;
    }
}
