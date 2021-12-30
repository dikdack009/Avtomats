import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class SyntaxTree {

    private String regex;
    private Node syntaxTree;
    private Map <String, String> captureDictionary;

    public SyntaxTree(String regex) {
        this.regex = regex;
    }

    private IndexBrackets findBracketsForArray(Map<Integer, Integer> mapCheckedBrackets, List<Expression> currentExpression) {
        boolean startBrackets = false;
        int firstBracket = 0;
        int secondBracket = 0;
        for (int i = 0; i < currentExpression.size(); ++i) {
            if (currentExpression.get(i).getString().equals("(") && !startBrackets && !mapCheckedBrackets.containsKey(i)) {
                firstBracket = i;
                startBrackets = true;
            }
            else if (startBrackets && currentExpression.get(i).getString().equals("(") && !mapCheckedBrackets.containsKey(i)){
                firstBracket = i;
            }
            else if (currentExpression.get(i).getString().equals(")") && startBrackets) {
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
                if (figureNumber < 0) throw new FigureBracketsException();
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
        removeQuestion();
    }

    protected void removeQuestion(){
        if (regex.contains("?")){
            String figureBefore;
            for (int i = 0; i < regex.length(); ++i){
                if (regex.charAt(i) == '?'){
                    char charBefore = regex.charAt(i - 1);
                    System.out.println(charBefore);
                    //if (isMeta(charBefore)) throw new QuestionException();
                    if (charBefore == ')'){
                        int f2 = i - 1;
                        int f1 = 0;
                        for (int k = f2; k >= 0; --k){
                            if (regex.charAt(k) == '(') f1 = k;
                        }
                        figureBefore = regex.substring(f1, f2 + 1);
                        regex = regex.substring(0, f1) + regex.substring(f2 + 2);
                    }
                    else {
                        figureBefore = String.valueOf(regex.charAt(i - 1));
                        regex = regex.substring(0, i - 1) + regex.substring(i + 1);
                    }
                    //System.out.println(regex);

                    regex = new StringBuilder(regex).insert(i - figureBefore.length(),"(" + figureBefore + "|^)").toString();
                    break;
                }
            }
        }
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

        long firstAngleBracketCount = charList.stream().filter(s -> s.equals('<')).count();
        long secondAngleBracketCount = charList.stream().filter(s -> s.equals('>')).count();
        if (firstAngleBracketCount != secondAngleBracketCount) throw new BracketsException();

        System.out.println(regex);
        removeCaptureNames();
        System.out.println(captureDictionary);
        return firstBracketCount;
    }

    //TODO: проверить если после угольной скобки ничего нет
    protected void removeCaptureNames(){
        captureDictionary = new TreeMap<>();
        int firstAngleBracket = regex.indexOf("<");
        while (firstAngleBracket > 0) {
            int secondAngleBracket = regex.indexOf(">");
            int firstBracket = firstAngleBracket - 1;
            int secondBracket = 0;
            String capture = regex.substring(firstAngleBracket, secondAngleBracket + 1);
            capture = capture.substring(1, capture.length() - 1);

            int countOpenBrackets = 0;
            for (int count = firstBracket; count < regex.length(); ++count) {
                if (regex.charAt(count) == ')' && countOpenBrackets == 1) {
                    secondBracket = count;
                    break;
                } else if (regex.charAt(count) == '(') countOpenBrackets++;
                else if (regex.charAt(count) == ')' && countOpenBrackets != 1) countOpenBrackets--;
            }
            String capturedRegex = regex.substring(secondAngleBracket + 1, secondBracket);
            regex = regex.replaceFirst("<" + capture + ">", "");
            captureDictionary.put(capture, capturedRegex);
            firstAngleBracket = regex.indexOf("<");
        }

    }

    //TODO: try для вопроса
    protected void typicalCheckRegexForArray(List<Expression> currentExpression){
        long firstBracketCount = currentExpression.stream().filter(s -> s.getString().equals("(")).count();
        long secondBracketCount = currentExpression.stream().filter(s -> s.getString().equals(")")).count();
        if (firstBracketCount != secondBracketCount) throw new BracketsException();
    }

    private boolean isMeta(char i){
        return String.valueOf(i).equals("*") ||
                String.valueOf(i).equals("?") ||
                String.valueOf(i).equals("|") ||
                String.valueOf(i).equals("(") ||
                String.valueOf(i).equals(")");
    }

    protected List<Expression> configureSimpleLists(String expression){
        List<Expression> currentExpression = new ArrayList<>();
        for (int i = 0; i < expression.length(); ++i){
            String subExpression = expression.substring(i, i + 1);
            boolean screeningMeta = i >= 1 && expression.charAt(i) == '%' && expression.charAt(i + 2) == '%';
            if (screeningMeta){
                Node list = new Node(String.valueOf(expression.charAt(i + 1)));
                currentExpression.add(new Expression(list, subExpression, Screening.Yes));
                expression = expression.substring(0, i) + expression.charAt(i + 1) + expression.substring(i + 3);
            }
            else if (!isMeta(expression.charAt(i))){
                Node list = new Node(String.valueOf(expression.charAt(i)));
                currentExpression.add(new Expression(list, subExpression, Screening.Yes));
            }
            else {
                currentExpression.add(new Expression(null, subExpression, Screening.No));
            }
        }
        return currentExpression;
    }

    protected void replaceToStarNode(List<Expression> currentExpression){
        int result = hasStarList(currentExpression);
        while (result >= 0){
            Node leftChild = currentExpression.get(result - 1).getList();
            currentExpression.get(result - 1).setString("...");
            Node starNode = new Node("...");
            starNode.setLeftChild(leftChild);
            currentExpression.get(result - 1).setList(starNode);
            currentExpression.remove(result);
            result = hasStarList(currentExpression);
        }
    }

    protected int hasStarList(List<Expression> currentExpression){
        for (int i = 0; i < currentExpression.size(); ++i)
            if (currentExpression.get(i).getString().equals("*") && currentExpression.get(i).getList() == null) {
                if (i == 0) throw new StarException();
                return i;
            }
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
        //if (currentExpression.get(0).getString().equals("|")) throw new OrException(); // TODO: do normal exception
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
        long numberBrackets = typicalCheckRegex() + 2;
        regex = "((" + regex + ")$)";
        System.out.println(regex);

        List<Expression> currentExpression;                             // текущее утверждение
        Map<Integer, Integer> mapCheckedBrackets;                       // Мара для проверки скобок, проверили мы их ли нет

        currentExpression = configureSimpleLists(regex);
        typicalCheckRegexForArray(currentExpression);
        for (int count = 1; count <= numberBrackets; ++count) {
            mapCheckedBrackets = new TreeMap<>();
            IndexBrackets indexBrackets = findBracketsForArray(mapCheckedBrackets, currentExpression);
            if (indexBrackets.getStart() == indexBrackets.getEnd())
                break;
            int firstBracket = indexBrackets.getStart();
            int secondBracket = indexBrackets.getEnd();
            List<Expression> microExpression = currentExpression.subList(firstBracket + 1, secondBracket);
            List<Expression> tmp = new ArrayList<>(microExpression);

            replaceToStarNode(tmp);
            replaceToCatNode(tmp);
            replaceToOrNode(tmp);

            int result = secondBracket - firstBracket;
            while (result-- > 0)
                currentExpression.remove(firstBracket);

            currentExpression.get(firstBracket).setList(tmp.get(0).getList());
            currentExpression.get(firstBracket).setString(tmp.get(0).getString());
            syntaxTree = currentExpression.get(0).getList();
        }
        printTree(syntaxTree, -1);
    }

    protected void printTree(Node tree, int lines){
        if (tree == null) return;
        ++lines;
        printTree(tree.getRightChild(), lines);
        System.out.print(lines + "" +"\t".repeat(lines + 1) + "<" + tree.getValue() + ">\n");
        printTree(tree.getLeftChild(), lines);
    }
}
