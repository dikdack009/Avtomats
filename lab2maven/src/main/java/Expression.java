import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.beans.Transient;
import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expression implements Comparator<Expression> {
    private Node list;
    private String string;
    private IndexBrackets indexBrackets;


    @Override
    public int compare(Expression o1, Expression o2) {
        return o1.getIndexBrackets().compare(o1.getIndexBrackets(), o2.getIndexBrackets());
    }
}
