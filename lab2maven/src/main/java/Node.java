import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
class Node implements Comparable<Node> {
    private String value;
    private Node leftChild;
    private Node rightChild;
    private IsList isList = IsList.NODE;
    private int id;

    public Node (String value, IsList isList, int id){
        this.value = value;
        leftChild = null;
        rightChild = null;
        this.isList = isList;
        this.id = id;
    }

    @Override
    public String toString() {
        return "Node value = { " + value +
                ", leftChild = " + leftChild +
                ", rightChild = " + rightChild +
                ", id = " + id +
                " }";
    }

    @Override
    public int compareTo(Node o) {
        return this.id == o.id ? -1 : 1;
    }
}