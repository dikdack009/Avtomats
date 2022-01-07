import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Regex {
    private String regex;
    private DFA dfa;
    private boolean compile;

    public Regex(String regex) {
        this.regex = regex;
        dfa = null;
        compile = false;
    }

    public void compile(){
        dfa = new DFA(regex);
        dfa.makeDFA();
        dfa.getRoot().printTree(dfa.getRoot(), -1);
        System.out.println(dfa);
        compile = true;
    }

    public boolean match(String inputString){
        State state = dfa.getStartState();
        for (int i = 0; i < inputString.length(); ++i){
            if (dfa.getAlphabet().contains((inputString.charAt(i))))
                state = state.getNextState(String.valueOf(inputString.charAt(i)));
            else return false;
        }
        return state.getStatePositions().stream().anyMatch(lit -> lit.getValue().equals("$"));
    }
}
