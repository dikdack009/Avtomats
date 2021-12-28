import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SyntaxTree {

    private String regex;
    private List<Node> syntaxTree;

    public SyntaxTree(String regex) {
        this.regex = regex;
    }

    private IndexBrackets findBrackets(Map<Integer, Integer> mapCheckedBrackets, String regex) {
        boolean startBrackets = false;
        int firstBracket = 0;
        int secondBracket = 0;
        for (int i = 0; i < regex.length(); ++i) {
            if (regex.charAt(i) == '(' && !startBrackets && !mapCheckedBrackets.containsKey(i)) {
                firstBracket = i;
                startBrackets = true;
            }
            else if (startBrackets && regex.charAt(i) == '(' && !mapCheckedBrackets.containsKey(i)){
                firstBracket = i;
            }
            else if (regex.charAt(i) == ')' && startBrackets) {
                secondBracket = i;
                if (!mapCheckedBrackets.containsKey(firstBracket) && !mapCheckedBrackets.containsValue(secondBracket)) {
                    mapCheckedBrackets.put(firstBracket, secondBracket);
                    break;
                }
            }
        }
        return new IndexBrackets(firstBracket, secondBracket);
    }

    protected void removeFigureBrackets(){
        boolean startBrackets = false;
        int firstBracket = 0;
        int secondBracket;
        int figureNumber;
        String figureBefore;
        List<Character> charList =  regex.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        long firstFigureCount = charList.stream().filter(s -> s.equals('{')).count();
        long secondFigureCount = charList.stream().filter(s -> s.equals('}')).count();
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
                if (regex.charAt(firstBracket - 1) == '}') throw new FigureBracketsException();
                try {
                    figureNumber = Integer.parseInt(regex.substring(firstBracket + 1, secondBracket));
                }catch (NumberFormatException e) {
                    throw new FigureBracketsException();
                }
                if (figureNumber <= 0) throw new FigureBracketsException();
                regex = regex.substring(0, firstBracket) + regex.substring(secondBracket + 1);
                if (regex.charAt(firstBracket - 1) == ')'){
                    int f2 = firstBracket - 1;
                    int f1 = 0;
                    for (int k = f2; k >= 0; --k){
                        if (regex.charAt(k) == '(') f1 = k;
                    }
                    figureBefore = regex.substring(f1, f2 + 1);
                    regex = regex.substring(0, f1) + regex.substring(f2 + 1);
                }
                else {
                    figureBefore = String.valueOf(regex.charAt(firstBracket - 1));
                    regex = regex.substring(0, firstBracket - 1) + regex.substring(firstBracket);
                }
                regex = new StringBuilder(regex).insert(firstBracket - figureBefore.length(),"(" + figureBefore.repeat(figureNumber) + ")").toString();
                startBrackets = false;
            }
        }
        //if (startBrackets != endBrackets) throw new FigureBracketsException();
    }

    protected long typicalCheckRegex(){
        regex = regex.replaceAll("\\.\\.\\.", "*");
        try{
            removeFigureBrackets();
        }
        catch (Exception ex) {
            throw new FigureBracketsException();
        }
        List<Character> charList =  regex.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        long firstBracketCount = charList.stream().filter(s -> s.equals('(')).count();
        long secondBracketCount = charList.stream().filter(s -> s.equals(')')).count();
        if (firstBracketCount != secondBracketCount) throw new BracketsException();

        return firstBracketCount;
    }

    private boolean isMeta(char i){
        return String.valueOf(i).equals("*") ||
                String.valueOf(i).equals("?") ||
                String.valueOf(i).equals("|") ||
                String.valueOf(i).equals("(") ||
                String.valueOf(i).equals(")");
    }

    protected List<Expression> configureHardLists(String expression, Map<String, Node> wholeExpression){
        List <Expression> currentExpression = configureSimpleLists(expression);
        boolean openBracket = false;
        int startBracket = 0;
        int endBracket = 0;
        int numberOpenBracket = 0;
        System.out.println("Зашли");
        for (int i = 0; i < expression.length(); ++i){
            if (expression.charAt(i) == '(' && !openBracket){
                startBracket = i;
                openBracket = true;
                System.out.println("Нашли скобку (" + startBracket);
            }
            else if (expression.charAt(i) == '(' && openBracket){
                numberOpenBracket++;
            }
            else if (expression.charAt(i) == ')' && numberOpenBracket == 0){
                endBracket = i;
                System.out.println("Нашли скобку )" + endBracket);
                openBracket = false;
                String microExpression = expression.substring(startBracket + 1, endBracket);
                System.out.println(microExpression + " micro");
                Node microCurrentExpression = wholeExpression.get(microExpression);
                int number = endBracket - startBracket - 1;
                while (number-- > 0){
                    currentExpression.remove(startBracket);
                }
                expression = expression.substring(0, startBracket) + expression.substring(endBracket);
                System.out.println(expression + " ex");
                currentExpression.get(startBracket).setString(microCurrentExpression.getValue());
                currentExpression.get(startBracket).setList(microCurrentExpression);
            }
            else if (expression.charAt(i) == ')' && numberOpenBracket != 0){
                numberOpenBracket--;
            }
        }
        return currentExpression;
    }

    protected List<Expression> configureSimpleLists(String expression){
        List<Expression> currentExpression = new ArrayList<>();
        for (int i = 0; i < expression.length(); ++i){
            String subExpression = expression.substring(i, i + 1);
            if (!isMeta(expression.charAt(i))){
                Node list = new Node(String.valueOf(expression.charAt(i)));
                currentExpression.add(new Expression(list, subExpression));
            }
            else {
                currentExpression.add(new Expression(null, subExpression));
            }
        }
        return currentExpression;
    }

    protected void replaceToStarNode(List<Expression> currentExpression){
        int result = hasStarList(currentExpression);
        while (result >= 0){
            //if (result == 0)
            Node leftChild = currentExpression.get(result - 1).getList();
            currentExpression.get(result - 1).setString("*");
            Node starNode = new Node("*");
            starNode.setLeftChild(leftChild);
            currentExpression.get(result - 1).setList(starNode);
            currentExpression.remove(result);
            result = hasStarList(currentExpression);
        }
    }

    protected int hasStarList(List<Expression> currentExpression){
        for (int i = 0; i < currentExpression.size(); ++i)
            if (currentExpression.get(i).getString().equals("*") && currentExpression.get(i).getList() == null)
                return i;
        return -1;
    }

    protected void replaceToCatNode(List<Expression> currentExpression){
        int result = hasCatList(currentExpression);
        while (result >= 0){
            Node catNode = new Node(".");
            Node leftChild = currentExpression.get(result - 1).getList();
            Node rightChild = currentExpression.get(result).getList();
            catNode.setLeftChild(leftChild);
            catNode.setRightChild(rightChild);
            currentExpression.remove(result);
            currentExpression.get(result - 1).setString(".");
            currentExpression.get(result - 1).setList(catNode);
            result = hasCatList(currentExpression);
        }
    }

    protected int hasCatList(List<Expression> currentExpression){
        for (int i = 1; i < currentExpression.size(); ++i){
            boolean leftHasList = currentExpression.get(i-1).getList() != null;
            boolean rightHasList = currentExpression.get(i).getList() != null;
            if (leftHasList && rightHasList)
                return i;
        }
        return -1;
    }

    protected void replaceToOrNode(List<Expression> currentExpression) {
        int result = hasOrList(currentExpression);
        while (result >= 0){
            Node orNode = new Node("|");
            Node leftChild = currentExpression.get(result - 1).getList();
            Node rightChild = currentExpression.get(result + 1).getList();
            orNode.setLeftChild(leftChild);
            orNode.setRightChild(rightChild);
            currentExpression.get(result - 1).setString("|");
            currentExpression.get(result - 1).setList(orNode);
            currentExpression.remove(result);
            currentExpression.remove(result);
            result = hasOrList(currentExpression);
        }
    }

    // TODO: написать экспешены для всех методов обработки реджекса
    protected int hasOrList(List<Expression> currentExpression){
        for (int i = 1; i < currentExpression.size(); ++i){
            if (currentExpression.get(i).getString().equals("|")) {
                boolean leftHasList = currentExpression.get(i - 1).getList() != null;
                boolean rightHasList = currentExpression.get(i + 1).getList() != null;
                if (leftHasList && rightHasList)
                    return i;
            }
        }
        return -1;
    }

    protected void makeSyntaxTree(){
        long numberBrackets = typicalCheckRegex() + 2;      // TODO: проверить что прибавить
        regex = "(" + regex + ")";
        System.out.println(regex);

        List<Expression> currentExpression;                             // текущее утверждение
        Map<String, Node> wholeExpression = new TreeMap<>() {};           // весь regex
        Map<Integer, Integer> mapCheckedBrackets = new TreeMap<>();    // Мара для проверки скобок, проверили мы их ли нет
        List<String> expressions = new ArrayList<>();                   // Все утверждения в виде String

        for (int count = 1; count <= numberBrackets; ++count) {
            currentExpression = new ArrayList<>();
            IndexBrackets indexBrackets = findBrackets(mapCheckedBrackets, regex);
            if (indexBrackets.getStart() == indexBrackets.getEnd())
                break;
            int firstBracket = indexBrackets.getStart();
            int secondBracket = indexBrackets.getEnd();
            String expression = regex.substring(firstBracket + 1, secondBracket);
            expressions.add(expression);
            System.out.println(count + ".) " + expression + "\n");

            // Для начала разбиваем текущее утверждение на листья
            //String bufferString = expression;
            if(expression.contains("(")){
                currentExpression = configureHardLists(expression, wholeExpression);
            }
            else currentExpression = configureSimpleLists(expression);
            replaceToStarNode(currentExpression);
            replaceToCatNode(currentExpression);
            replaceToOrNode(currentExpression);


            wholeExpression.put(expression,currentExpression.get(0).getList());

            printTree(currentExpression.get(0).getList(), -1);
        }
    }

    protected void printTree(Node tree, int lines){
        if (tree == null) return;
        ++lines;
        printTree(tree.getRightChild(), lines);
        System.out.print(lines + "" +"\t".repeat(lines + 1) + "<" + tree.getValue() + ">\n");
        printTree(tree.getLeftChild(), lines);
    }
}
