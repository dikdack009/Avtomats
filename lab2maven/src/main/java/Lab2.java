import java.util.*;

public class Lab2 {

    public static void main(String[] args){
        SyntaxTree syntaxTree = new SyntaxTree();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        String regex;
        while (!scan.equals("exit")){
            regex = scan.trim();
            syntaxTree.setRegex(regex);
            try {
                syntaxTree.makeSyntaxTree();
            }catch (RuntimeException exception){
                System.out.println(exception.getMessage());
            }
            DFA dfa = new DFA(syntaxTree.getSyntaxTree(), syntaxTree.getAlphabet());
            dfa.makeDFA();
            dfa.goNode(syntaxTree.getSyntaxTree());


            System.out.println(dfa);

            for (State state: dfa.getAllStatesList()) {
                System.out.println(state.getStateID());
                state.printTransitions();
                System.out.println();
            }

            System.out.println();
            System.out.println(dfa.getAcceptStates());
            System.out.println();
            for (Node key: dfa.getFollowPosDict().keySet()) {
                System.out.println("Key " + key.getValue() + " " + dfa.getFollowPosDict().get(key));
            }

            System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}

//(a|bcd...){2}e{3}
