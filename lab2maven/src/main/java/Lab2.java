import java.util.*;

public class Lab2 {

    public static void main(String[] args){
        SyntaxTree syntaxTree = new SyntaxTree();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        String regex;
        while (!scan.equals("exit")){
            regex = scan;
            syntaxTree.setRegex(regex);
            try {
                syntaxTree.makeSyntaxTree();
            }catch (RuntimeException exception){
                System.out.println(exception.getMessage());
                System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                scan = scanner.nextLine();
                continue;
            }
            System.out.println("\nРегулярное выражение: " + syntaxTree.getRegex());

            System.out.println("\n" + "СД: ");
            syntaxTree.printTree(syntaxTree.getSyntaxTree(), -1);
            DFA dfa = new DFA(syntaxTree.getSyntaxTree(), syntaxTree.getAlphabet());
            dfa.makeDFA();
            System.out.println("\nДКА:\n" + dfa);

            System.out.println("Восстановление РВ:\n" +
                    dfa.k_path(dfa.getStartState().getStateID(),dfa.getAcceptStates().stream().toList().get(0).getStateID(),dfa.getAllStatesList().size() - 1));
            dfa.minimizationDFA();

            System.out.println("\nМинимизированный ДКА:\n" + dfa);

            System.out.print("\nВведите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}


//(a|bcd...){2}e{3}
