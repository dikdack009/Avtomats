import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class TripleNumbers implements Comparable<TripleNumbers> {
    int i, j, k;
    TripleNumbers(int i, int j, int k) {
        this.i = i;
        this.j = j;
        this.k = k;
    }

    @Override
    public int hashCode() {
        return Objects.hash(i, j, k);
    }

    @Override
    public int compareTo(@NotNull TripleNumbers o) {
        return i == o.i && j == o.j && k == o.k ? 1 : -1;
    }
}
