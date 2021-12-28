import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

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
}