import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Comparator;

@Data
@AllArgsConstructor
public class IndexBrackets implements Comparator<IndexBrackets> {
    private final int start;
    private final int end;

    @Override
    public int compare(IndexBrackets o1, IndexBrackets o2) {
        return o1.getStart() - o2.getEnd();
    }

}
