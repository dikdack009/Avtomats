import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Node{
    private String value;
    private Node leftChild;
    private Node rightChild;

    public Node (String value){
        this.value = value;
        leftChild = null;
        rightChild = null;
    }

    @Override
    public String toString() {
        return "Node value = { " + value +
                ", leftChild = " + leftChild +
                ", rightChild = " + rightChild +
                " }";
    }

    protected void printTree(Node tree, int lines){
        if (tree == null) return;
        ++lines;
        printTree(tree.getRightChild(), lines);
        System.out.print(lines + "" +"\t".repeat(lines + 1) + "<" + tree.getValue() + ">\n");
        printTree(tree.getLeftChild(), lines);
    }
}