import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State implements Comparable<State>{
    private Map<String, State> transitions;
    private int stateID;
    private Set<Node> statePositions;

    public State(Integer lastID, Set<Node> statePositions) {
        this.statePositions = statePositions;
        stateID = lastID;
        transitions = new HashMap<>();
    }

    public State(Integer lastID) {
        stateID = lastID;
        transitions = new HashMap<>();
    }

    protected State getNextState(String symbol){
        return transitions.get(symbol);
    }

    protected void appendNewTransition(String symbol, State state){
        transitions.put(symbol, state);
    }

    @Override
    public String toString() {
        return String.valueOf(stateID);
    }

    @Override
    public int hashCode(){
        return Objects.hash(statePositions);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof State state)) return false;
        return statePositions.equals(state.statePositions);
    }

    @Override
    public int compareTo(@NotNull State o) {
        return this.stateID == o.stateID ? 1 : -1;
    }
}
