public class Node {
    int row, col, g;
    double h, f;
    Node previous;

    public Node(int row, int col, int g, double h, Node previous) {
        this.row = row;
        this.col = col;
        this.g = g;
        this.h = h;
        this.f = g + h;
        this.previous = previous;
    }
}
