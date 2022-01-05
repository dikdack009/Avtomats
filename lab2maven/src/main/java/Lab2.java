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
                System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                scan = scanner.nextLine();
                continue;
            }
            DFA dfa = new DFA(syntaxTree.getSyntaxTree(), syntaxTree.getAlphabet());
            dfa.makeDFA();

            System.out.println(dfa);
            System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}

//(a|bcd...){2}e{3}
