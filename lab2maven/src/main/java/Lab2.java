import java.util.*;

import static java.lang.Math.abs;

public class Lab2 {
    private static String regex;
    private static List<Node> syntaxTree;

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        while (!scan.equals("exit")){
            regex = scan.trim();
            syntaxTree = new ArrayList<>();
            makeSyntaxTree();
            try {

            }catch (RuntimeException exception){
                System.out.println(exception);
            }
            System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }

    private static IndexBrackets findBrackets() {
        boolean startBrackets = false;
        boolean endBrackets = false;
        int firstBracket = 0;
        int secondBracket = 0;
        for (int i = 0; i < regex.length(); ++i) {
            if (regex.charAt(i) == '(') {
                firstBracket = i;
                startBrackets = true;
            }
            else if (startBrackets && regex.charAt(i) == '('){
                firstBracket = i;
            }
            else if (regex.charAt(i) == ')' && startBrackets) {
                secondBracket = i;
                return new IndexBrackets(firstBracket, secondBracket);
            }
        }
        return new IndexBrackets(firstBracket, secondBracket);
    }

    private static void removeFigureBrackets(){
        boolean startBrackets = false;
        boolean endBrackets = false;
        int firstBracket = 0;
        int secondBracket;
        int figureNumber;
        String figureBefore;
        long firstFigureCount = Arrays.stream(regex.split("")).filter(s -> s.equals("{")).count();
        long secondFigureCount = Arrays.stream(regex.split("")).filter(s -> s.equals("}")).count();
        if (firstFigureCount != secondFigureCount) throw new FigureBracketsException();
        for (int i = 0; i < regex.length(); ++i){
            if (regex.charAt(i) == '{'){
                firstBracket = i;
                startBrackets = true;
                continue;
            }
            if (startBrackets && (regex.charAt(i) == '{' || firstBracket == 0)) throw new FigureBracketsException();
            else if (regex.charAt(i) == '}' && startBrackets){
                secondBracket = i;
                endBrackets = true;
                if (regex.charAt(firstBracket - 1) == '}') throw new FigureBracketsException();
                try {
                    figureNumber = Integer.parseInt(regex.substring(firstBracket + 1, secondBracket));
                }catch (NumberFormatException e) {
                    throw new FigureBracketsException();
                }
                if (figureNumber <= 0) throw new FigureBracketsException();
                regex = regex.replace(regex.substring(firstBracket, secondBracket + 1), "");
                if (regex.charAt(firstBracket - 1) == ')'){
                    int f2 = firstBracket - 1;
                    int f1 = 0;
                    for (int k = f2; k >= 0; --k){
                        if (regex.charAt(k) == '(') f1 = k;
                    }
                    figureBefore = regex.substring(f1, f2 + 1);
                }
                else figureBefore = String.valueOf(regex.charAt(firstBracket - 1));
                regex = new StringBuilder(regex).insert(firstBracket, figureBefore.repeat(figureNumber - 1)).toString();
            }
        }
        if (startBrackets != endBrackets) throw new FigureBracketsException();
    }

    private static long typicalCheckRegex(){
        long firstBracketCount = Arrays.stream(regex.split("")).filter(s -> s.equals("(")).count();
        long secondBracketCount = Arrays.stream(regex.split("")).filter(s -> s.equals(")")).count();
        if (firstBracketCount != secondBracketCount) throw new BracketsException();
        removeFigureBrackets();
        regex = regex.replaceAll("\\.\\.\\.", "*");
        return firstBracketCount;
    }

    private static boolean isMeta( char i){
        return String.valueOf(i).equals("*") ||
                String.valueOf(i).equals("?") ||
                String.valueOf(i).equals("|");
    }

    private static void makeSyntaxTree(){
        regex = "(" + regex + ")";
        long numberBrackets = typicalCheckRegex();
        System.out.println(regex);
        int firstBracket = findBrackets().getStart();
        int secondBracket = findBrackets().getEnd();
        String expression = regex.substring(firstBracket + 1, secondBracket);
        System.out.println(expression);
        List<Node> currentExpression = new ArrayList<>();

        // создаём листья
        for (int i = 0; i < expression.length(); ++i){
            if (!isMeta(expression.charAt(i))){
                currentExpression.add(new Node(String.valueOf(expression.charAt(i)), IsList.LIST, i));
            }
        }

        // ищем пару aNode и * и создаём starNode
        for (int i = 1; i < expression.length(); ++i) {
            if (expression.charAt(i) == '*'){
                System.out.println("WHAT");
                Node starNode = new Node("*", IsList.NODE, i - 1);
                for (Node node : currentExpression)
                    if (node.getId() == i - 1){
                        starNode.setLeftChild(node);
                        currentExpression.remove(node);
                        break;
                    }
                starNode.setId(starNode.getLeftChild().getId());
                currentExpression.add(starNode);
                Collections.sort(currentExpression);
                for (Node node : currentExpression){
                    if (node.getId() > starNode.getId())
                        node.setId(starNode.getRightChild().getId() - 1);
                }
            }
        }
        for (Node node : currentExpression)
            System.out.println(node);
        System.out.println();
        // ищем пару node и node и создаём catNode
        for (int i = 1; i < currentExpression.size(); ++i) {
            if (abs(currentExpression.get(i).getId() - currentExpression.get(i - 1).getId()) == 1){
                if (currentExpression.get(i - 1).getId() - currentExpression.get(i).getId() == 1)
                    swap(currentExpression.get(i - 1), currentExpression.get(i));
                Node catNode = new Node(".", IsList.NODE, i);
                catNode.setLeftChild(currentExpression.get(i - 1));
                catNode.setRightChild(currentExpression.get(i));
                currentExpression.remove(currentExpression.get(i - 1));
                currentExpression.remove(currentExpression.get(i - 1));
                catNode.setId(catNode.getLeftChild().getId());
                currentExpression.add(catNode);
                for (Node node : currentExpression)
                    System.out.println(node);
                System.out.println();
                Collections.sort(currentExpression);

                for (Node node : currentExpression){
                    if (node.getId() > catNode.getId())
                        node.setId(abs(node.getId() - 1));
                }
                i = 0;
                for (Node node : currentExpression)
                    System.out.println(node);
                System.out.println();
            }
        }

        // ищем триплет и создаём orNode
        for (int i = 1; i < currentExpression.size(); ++i) {
            //System.out.println(currentExpression);
            if(currentExpression.get(i).getId() - currentExpression.get(i - 1).getId() == 2 &&
                    expression.charAt(currentExpression.get(i - 1).getId() + 1) == '|'){
                Node orNode = new Node("|", IsList.NODE, i);
                orNode.setLeftChild(currentExpression.get(i - 1));
                orNode.setRightChild(currentExpression.get(i));
                currentExpression.remove(currentExpression.get(i - 1));
                currentExpression.remove(currentExpression.get(i - 1));
                orNode.setId(orNode.getLeftChild().getId());
                currentExpression.add(i - 1, orNode);
                for (Node node : currentExpression){
                    if (node.getId() > orNode.getId())
                        node.setId(abs(orNode.getLeftChild().getId() - orNode.getRightChild().getId()));
                }
                i = 0;
            }
        }

        for (Node node : currentExpression)
            System.out.println(node);
    }

    private static void swap(Node node, Node node1) {
        int tmp = node.getId();
        node.setId(node1.getId());
        node1.setId(tmp);
    }
}

//(a|bcd...){2}e{3}
