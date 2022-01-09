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

    public Regex(DFA dfa) {
        this.regex = "";
        this.dfa = dfa;
        compile = false;
    }

    public void compile(){
        dfa = new DFA(regex);
        compile = true;
    }

    public boolean match(String inputString){
        State state = dfa.getStartState();
        for (int i = 0; i < inputString.length(); ++i){
            if (dfa.getAlphabet().contains((inputString.charAt(i))))
                state = state.getNextState(String.valueOf(inputString.charAt(i)));
            else return false;
        }
        //return state.getStatePositions().stream().anyMatch(lit -> lit.getValue().equals("$"));
        return dfa.getAcceptStates().contains(state);
    }

    public void findAll(String inputString) {
        System.out.print("Все подходящие подстроки: ");
        if (match(inputString)) System.out.print(inputString);
        else {
            for (int i = 0; i < inputString.length(); ++i) {
                int rememberAccept = -1;
                if (dfa.getAlphabet().contains((inputString.charAt(i))))
                    for (int j = i; j < inputString.length(); ++j) {
                        String currentStr = inputString.substring(i, j + 1);
                        if (match(currentStr)) {
                            rememberAccept = j;
                        }
                    }
                if (rememberAccept != -1) {
                    System.out.print(inputString.substring(i, rememberAccept + 1) + " ");
                    i = rememberAccept;
                }
            }
        }
    }
}
