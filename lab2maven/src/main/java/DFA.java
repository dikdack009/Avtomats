import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DFA {
    private Node root;
    private Map <Node, Set<Node>> followPosDict;
    private State startState;
    private State currentState;
    private List<State> allStatesList;
    private Set<Character> alphabet;

    public DFA(Node root, Set<Character> alphabet) {
        this.alphabet = alphabet;
        this.root = root;
        followPosDict = new HashMap<>();
        configureFollowPos(root);
        allStatesList = new ArrayList<>();
        makeDFA();
        startState = allStatesList.get(0);
    }

    public void goNode(Node node){
        System.out.println("Value - " + node.getValue());
        System.out.println("Nullable - " + configureNullable(node));
        Set<Node> firstPosSet = new HashSet<>();
        System.out.println("First - " + configureFirstPos(node, firstPosSet));
        Set<Node> lastPosSet = new HashSet<>();
        System.out.println("Last - " + configureLastPos(node, lastPosSet));
        System.out.println();
        if (node.getRightChild() != null) goNode((node.getRightChild()));
        if (node.getLeftChild() != null) goNode((node.getLeftChild()));
    }
    protected boolean configureNullable(Node expression){
        return switch (expression.getValue()) {
            case "." -> configureNullable(expression.getLeftChild()) && configureNullable(expression.getRightChild());
            case "|" -> configureNullable(expression.getLeftChild()) || configureNullable(expression.getRightChild());
            case "*" -> configureNullable(expression.getLeftChild());
            default -> expression.getValue().equals("^");
        };
    }

    protected Set<Node> configureFirstPos(Node expression, Set<Node> firstPosSet){
        if (firstPosSet == null){
            firstPosSet = new HashSet<>();
        }
        switch (expression.getValue()) {
            case ".":
                if (!configureNullable(expression.getLeftChild())){
                    configureFirstPos(expression.getLeftChild(), firstPosSet);
                }
                else {
                    configureFirstPos(expression.getLeftChild(), firstPosSet);
                    configureFirstPos(expression.getRightChild(), firstPosSet);
                }
                break;
            case "|":
                configureFirstPos(expression.getLeftChild(), firstPosSet);
                configureFirstPos(expression.getRightChild(), firstPosSet);
                break;
            case "*":
                configureFirstPos(expression.getLeftChild(), firstPosSet);
                break;
            default:
                if (!expression.getValue().equals("^")){
                    firstPosSet.add(expression);
                }
        }
        return firstPosSet;
    }

    protected Set<Node> configureLastPos(Node expression, Set<Node> lastPosSet){
        if (lastPosSet == null){
            lastPosSet = new HashSet<>();
        }
        switch (expression.getValue()) {
            case ".":
                if (!configureNullable(expression.getRightChild())){
                    configureLastPos(expression.getRightChild(), lastPosSet);
                }
                else {
                    configureLastPos(expression.getLeftChild(), lastPosSet);
                    configureLastPos(expression.getRightChild(), lastPosSet);
                }
                break;
            case "|":
                configureLastPos(expression.getLeftChild(), lastPosSet);
                configureLastPos(expression.getRightChild(), lastPosSet);
                break;
            case "*":
                configureLastPos(expression.getLeftChild(), lastPosSet);
                break;
            default:
                if (!expression.getValue().equals("^")){
                    lastPosSet.add(expression);
                }
        }
        return lastPosSet;
    }

    protected void configureFollowPos(Node expression){

        switch (expression.getValue()) {
            case "." -> {
                configureFollowPos(expression.getLeftChild());
                Set<Node> leftFollowSet = new HashSet<>(configureLastPos(expression.getLeftChild(), null));
                Set<Node> rightFollowSet = new HashSet<>(configureFirstPos(expression.getRightChild(), null));
                for (Node node : leftFollowSet) {
                    if (followPosDict.containsKey(node)) {
                        followPosDict.get(node).addAll(rightFollowSet);
                    } else {
                        followPosDict.put(node, rightFollowSet);
                    }
                }
                configureFollowPos(expression.getRightChild());
            }
            case "*" -> {
                Set<Node> lastPosSet = new HashSet<>(configureLastPos(expression.getLeftChild(), null));
                Set<Node> firstPosSet = new HashSet<>(configureFirstPos(expression.getLeftChild(), null));
                for (Node node : lastPosSet) {
                    if (followPosDict.containsKey(node)) {
                        followPosDict.get(node).addAll(firstPosSet);
                    } else {
                        followPosDict.put(node, firstPosSet);
                    }
                }
            }
        }

    }

    protected void makeDFA(){
        Set<Integer> unmarkedID = new HashSet<>();
        Integer lastID = 0;

        unmarkedID.add(lastID);
        State state = new State(lastID, configureFirstPos(root, null));
        allStatesList.add(state);
        while (!unmarkedID.isEmpty()){
            List<Integer> list = new ArrayList<>(unmarkedID);
            Integer currentUnmarked = list.get(0);
            unmarkedID.remove(currentUnmarked);
            System.out.println(alphabet);
            for(char symbol : alphabet){
                State newState = new State(lastID + 1, configureNodeUnion(state, symbol));
                if (!allStatesList.contains(newState)){
                    ++lastID;
                    unmarkedID.add(lastID);
                    allStatesList.add(newState);
                }
                else{
                    newState = allStatesList.get(allStatesList.indexOf(newState));
                }
                allStatesList.get(currentUnmarked).appendNewTransition(String.valueOf(symbol), newState);
            }
        }
    }

    protected Set<Node> configureNodeUnion(State state, char symbol){
        Set<Node> resultSet = new HashSet<>();
        for (Node node : state.getStatePositions())
            if (node.getValue().equals(String.valueOf(symbol)) && followPosDict.containsKey(node))
                resultSet.addAll(followPosDict.get(node));
        return resultSet;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Start: ");
        builder.append(startState.getStateID()).append('\n');
        for (State st : allStatesList) {
            builder.append(st.getStateID()).append(" -> ");
            for (String tr : st.getTransitions().keySet()) {
                builder.append(st.getNextState(tr).getStateID()).append("{").append(tr).append("} ");
            }
            builder.append('\n');
        }
        return builder.toString();
    }

}
