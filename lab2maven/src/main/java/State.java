import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State{
    private HashMap<String, State> transitions;
    private int stateID;
    private Set<Node> statePositions;

    public State(Integer lastID, Set<Node> statePositions) {
        this.statePositions = new HashSet<>(statePositions);
        stateID = lastID;
        transitions = new HashMap<>();
    }

    public State(Integer lastID) {
        stateID = lastID;
        transitions = new HashMap<>();
        statePositions = new HashSet<>();
    }

    protected State getNextState(String symbol){
        return transitions.get(symbol);
    }

    protected void appendNewTransition(String symbol, State state){
        transitions.put(symbol, state);
    }

    @Override
    public String toString() {
        return stateID + String.valueOf(statePositions);
    }

    @Override
    public int hashCode(){
        return Objects.hash(statePositions);
    }

    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if(!(o instanceof State state)) return false;
        if (statePositions.equals(state.statePositions) && statePositions.size() == 1) return false;
        return statePositions.equals(state.statePositions);
    }

    public boolean isEnd() {
        for (Node node: this.getStatePositions()) {
            if (node.getValue().equals("$")) {
                return true;
            }
        }
        return false;
    }

}
