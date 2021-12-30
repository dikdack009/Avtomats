import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Expression {
    private Node list;
    private String string;
    private Screening isScreening;
}

enum Screening{
    Yes,
    No
}
