import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DFA {
    private Expression root;

    protected boolean configureNullable(Node expression){
        switch (expression.getValue()) {
            case ".":
                return configureNullable(expression.getLeftChild()) && configureNullable(expression.getRightChild());
            case "|":
                return configureNullable(expression.getLeftChild()) || configureNullable(expression.getRightChild());
            case "*":
                return configureNullable(expression.getLeftChild());
            default:
                return expression.getValue().equals("^");
        }
    }
}
