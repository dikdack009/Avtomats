import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class State {
    private Map<String, State> transitions;
    private int stateID;
    private Set<Node> statePositions;

    public State(Integer lastID, Set<Node> statePositions) {
        this.statePositions = statePositions;
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
        return "State{" +
//                "transitions=" + transitions +
                ", stateID=" + stateID +
                '}';
    }

    public void printTransitions(){
        for (String key : transitions.keySet()) {
            System.out.println(transitions.get(key) + " by " + key);
        }
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
}
