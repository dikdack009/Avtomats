import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

import static java.lang.Math.abs;

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
        boolean endBrackets = false;
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
                    System.out.println("первая скобка - " + firstBracket +  " вторая - " + secondBracket);
                    mapCheckedBrackets.put(firstBracket, secondBracket);
                    break;
                }
            }
        }
        return new IndexBrackets(firstBracket, secondBracket);
    }

    protected void removeFigureBrackets(){
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
                regex = regex.replace(figureBefore, "");
                regex = new StringBuilder(regex).insert(firstBracket - figureBefore.length(),"(" + figureBefore.repeat(figureNumber) + ")").toString();
            }
        }
        if (startBrackets != endBrackets) throw new FigureBracketsException();
    }

    protected long typicalCheckRegex(){
        long firstBracketCount = Arrays.stream(regex.split("")).filter(s -> s.equals("(")).count();
        long secondBracketCount = Arrays.stream(regex.split("")).filter(s -> s.equals(")")).count();
        if (firstBracketCount != secondBracketCount) throw new BracketsException();
        try{
            removeFigureBrackets();
        }
        catch (Exception ex) {
            throw new FigureBracketsException();
        }
        regex = regex.replaceAll("\\.\\.\\.", "*");
        return firstBracketCount;
    }

    private boolean isMeta( char i){
        return String.valueOf(i).equals("*") ||
                String.valueOf(i).equals("?") ||
                String.valueOf(i).equals("|") ||
                String.valueOf(i).equals("(") ||
                String.valueOf(i).equals(")");
    }

    private int isContainsExpression (List<Expression> expressionList, String str, IndexBrackets indexBrackets){
        for (Expression ex : expressionList){
            if (ex.getString().equals(str) && ex.getIndexBrackets().equals(indexBrackets)){
                return expressionList.indexOf(ex);
            }
        }
        return -1;
    }

    protected void makeSyntaxTree(){
        long numberBrackets = typicalCheckRegex() + 2;
        regex = "(" + regex + ")";
        System.out.println(regex);

        List<Node> currentExpression;
        List<Expression> wholeExpression = new ArrayList<>();
        Map <Integer, Integer> mapCheckedBrackets = new TreeMap<>();
        Map <Integer, Integer> mapCheckedBracketsEx = new TreeMap<>();
        List<String> expressions = new ArrayList<>();

        for (int count = 1; count <= numberBrackets; ++count) {
            currentExpression = new ArrayList<>();
            IndexBrackets indexBrackets = findBrackets(mapCheckedBrackets, regex);
            if (indexBrackets.getStart() == indexBrackets.getEnd() && indexBrackets.getStart() == 0)
                break;
            int firstBracket = indexBrackets.getStart();
            int secondBracket = indexBrackets.getEnd();
            //System.out.println("Нашли скобки");
//            System.out.println(mapCheckedBrackets);
            String expression = regex.substring(firstBracket + 1, secondBracket);
            expressions.add(expression);
            System.out.println(count + ".) " + expression + "\n");

            // создаём листья
            for (int i = 0; i < expression.length(); ++i) {
                if (!isMeta(expression.charAt(i))) {
                    currentExpression.add(new Node(String.valueOf(expression.charAt(i)), IsList.LIST, i));
                }
            }

            // сделать метод проверки на принадлежность к экс потому что если скобки то всё ломается
            // ты должен понять а если не понял то вернись в прошлое и спроси
            //System.out.println("index - " + expression.indexOf('('));
            if (expression.indexOf('(') >= 0){
                    long numberBracketsEx = Arrays.stream(expression.split("")).filter(s -> s.equals("(")).count();
                    System.out.println(numberBracketsEx +"AAAAAAAAAA");
                    for (int c = 1; c <= numberBracketsEx; ++c) {
                        IndexBrackets indexBracketsEx = findBrackets(mapCheckedBracketsEx, expression);
                        int firstBracketEx = indexBracketsEx.getStart();
                        int secondBracketEx = indexBracketsEx.getEnd();
                        System.out.println("первая " + firstBracketEx + " вторая " + secondBracketEx);
                        String tmp = expression.substring(firstBracketEx + 1, secondBracketEx);
                        int search = isContainsExpression(wholeExpression, tmp, indexBrackets);
                        if (search > 0){
                            currentExpression.add(wholeExpression.get(search).getList());
                        }
                    }
                currentExpression.forEach(System.out::println);
                }

            // ищем пару aNode и * и создаём starNode
            for (int i = 0; i < expression.length(); ++i) {
                if (expression.charAt(i) == '*') {
                    if (i == 0) throw new StarException();
                    Node starNode = new Node("*", IsList.NODE, i - 1);
                    for (Node node : currentExpression)
                        if (node.getId() == i - 1) {
                            starNode.setLeftChild(node);
                            currentExpression.remove(node);
                            break;
                        }
                    starNode.setId(starNode.getLeftChild().getId());
                    currentExpression.add(starNode);
                    Collections.sort(currentExpression);
                    for (Node node : currentExpression) {
                        if (node.getId() > starNode.getId())
                            node.setId(node.getId() - 1);
                    }
                }
            }

            // ищем пару node и node и создаём catNode
            for (int i = 1; i < currentExpression.size(); ++i) {
                if (abs(currentExpression.get(i).getId() - currentExpression.get(i - 1).getId()) == 1) {
                    if (currentExpression.get(i - 1).getId() - currentExpression.get(i).getId() == 1)
                        swap(currentExpression.get(i - 1), currentExpression.get(i));
                    Node catNode = new Node(".", IsList.NODE, i);
                    catNode.setLeftChild(currentExpression.get(i - 1));
                    catNode.setRightChild(currentExpression.get(i));
                    currentExpression.remove(currentExpression.get(i - 1));
                    currentExpression.remove(currentExpression.get(i - 1));
                    catNode.setId(catNode.getLeftChild().getId());
                    currentExpression.add(catNode);
                    Collections.sort(currentExpression);
                    for (Node node : currentExpression) {
                        if (node.getId() > catNode.getId())
                            node.setId(abs(node.getId() - 1));
                    }
                    i = 0;
                }
            }

            // ищем триплет и создаём orNode
            for (int i = 1; i < currentExpression.size(); ++i) {
                if (currentExpression.get(i).getId() - currentExpression.get(i - 1).getId() == 2 &&
                        expression.charAt(currentExpression.get(i - 1).getId() + 1) == '|') {
                    System.out.println("Нашли или!");
                    Node orNode = new Node("|", IsList.NODE, i);
                    orNode.setLeftChild(currentExpression.get(i - 1));
                    orNode.setRightChild(currentExpression.get(i));
                    currentExpression.remove(currentExpression.get(i - 1));
                    currentExpression.remove(currentExpression.get(i - 1));
                    orNode.setId(orNode.getLeftChild().getId());
                    currentExpression.add(i - 1, orNode);
                    for (Node node : currentExpression) {
                        if (node.getId() > orNode.getId())
                            node.setId(abs(orNode.getLeftChild().getId() - orNode.getRightChild().getId()));
                    }
                    i = 0;
                }
            }

            //printTree(currentExpression.get(0), -1);
            wholeExpression.add(new Expression(currentExpression.get(0), expression, indexBrackets));
            //printTree(wholeExpression.get(0), -1);
            wholeExpression.forEach(node ->  printTree(node.getList(), -1));
        }
        expressions.forEach(System.out::println);
        System.out.println("Regex - " + regex);
    }

    private void swap(Node node, Node node1) {
        int tmp = node.getId();
        node.setId(node1.getId());
        node1.setId(tmp);
    }

    protected void printTree(Node tree, int lines){
        if (tree == null) return;
        ++lines;
        printTree(tree.getRightChild(), lines);
        System.out.print(lines + "" +"\t".repeat(lines + 1) + "<" + tree.getValue() + ">\n");
        printTree(tree.getLeftChild(), lines);
    }
}
