import java.util.*;

import static java.lang.Math.abs;

public class Lab2 {
    private static String regex;
    private static List<Node> syntaxTreeList;

    public static void main(String[] args){
        SyntaxTree syntaxTree = new SyntaxTree();
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        while (!scan.equals("exit")){
            regex = scan.trim();
            syntaxTree.setRegex(regex);
            syntaxTreeList = new ArrayList<>();
            syntaxTree.makeSyntaxTree();
            try {
//                syntaxTree.makeSyntaxTree();
            }catch (RuntimeException exception){
                System.out.println(exception.getMessage());
            }
            System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}

//(a|bcd...){2}e{3}
