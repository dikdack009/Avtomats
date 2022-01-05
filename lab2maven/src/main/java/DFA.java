import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.stream.Collectors;

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
    private Set<State> acceptStates;

    public DFA(Node root, Set<Character> alphabet) {
        this.alphabet = alphabet;
        this.root = root;
        followPosDict = new HashMap<>();
        configureFollowPos(root);
        allStatesList = new ArrayList<>();
        acceptStates = new HashSet<>();
    }


    public void setAcceptStates(Set<State> acceptStates) {
        this.acceptStates = acceptStates;
    }


    protected boolean configureNullable(Node expression){
        return switch (expression.getValue()) {
            case "." -> configureNullable(expression.getLeftChild()) && configureNullable(expression.getRightChild());
            case "|" -> configureNullable(expression.getLeftChild()) || configureNullable(expression.getRightChild());
            case "..." -> true;
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
            case "...":
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
            case "...":
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
            case "..." -> {
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
            case "|" -> {
                configureFollowPos(expression.getLeftChild());
                configureFollowPos(expression.getRightChild());
            }
        }

    }

    protected void makeDFA(){
        Set<Integer> unmarkedID = new HashSet<>();
        Integer lastID = 0;
        unmarkedID.add(lastID);
        State state = new State(lastID, configureFirstPos(root, null));
        allStatesList.add(state);
        startState = state;
        currentState = state;
        while (!unmarkedID.isEmpty()){
            List<Integer> list = new ArrayList<>(unmarkedID);
            Integer currentUnmarked = list.get(0);
            unmarkedID.remove(currentUnmarked);
            alphabet.remove('$');
            alphabet.remove('^');

            for(char symbol : alphabet){
                State newState = new State(lastID + 1, configureNodeUnion(allStatesList.get(currentUnmarked), symbol));
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
        for (State st: allStatesList) {
            for (Node node: st.getStatePositions()) {
                if (node.getValue().equals("$")) {
                    acceptStates.add(st);
                }
            }
        }
        System.out.println(this);
        minimizationDFA();
    }

    protected Set<Node> configureNodeUnion(State state, char symbol){
        Set<Node> resultSet = new HashSet<>();
        for (Node node : state.getStatePositions())
            if (node.getValue().equals(String.valueOf(symbol)))
                resultSet.addAll(followPosDict.get(node));
        return resultSet;
    }

    private Set<State> getGroupById(State st, List<Set<State>> split) {
        for (Set<State> set : split) {
            if (set.stream().anyMatch(state -> state.equals(st)))
                return set;
        }
        return null;
    }

    protected void minimizationDFA() {
        List<Set<State>> splitting = new ArrayList<>();
        splitting.add(acceptStates);
        Set<State> tmp = new HashSet<>();
        for(State curState : allStatesList)
            for (State st :acceptStates)
                if (!st.equals(curState))
                    tmp.add(curState);

        splitting.add(tmp);
        System.out.println("Split: " + splitting);
        List<Set<State>> buf = splitting;
        List<Set<State>> bufSplitting = makeNewSplit(buf);
        while (!bufSplitting.equals(splitting)) {
            splitting = bufSplitting;
            bufSplitting = makeNewSplit(splitting);
        }

        makeStates(splitting);
    }

    private void makeStates(List<Set<State>> split) {
        ArrayList<State> result = new ArrayList<>();
        int lastId = 0;
        for (Set<State> set : split) {
            result.add(new State(lastId++, set.stream().flatMap(st -> st.getStatePositions().stream()).collect(Collectors.toSet())));
            if (set.stream().anyMatch(state -> state.getStateID() == 0))
                startState = result.get(lastId - 1);
        }
        for (int i = 0; i < result.size(); ++i) {
            Map<String, State> currentTransitions = split.get(i).iterator().next().getTransitions();
            int finalI = i;
            currentTransitions.keySet().forEach(c -> result.get(finalI)
                    .appendNewTransition(c, result.get(split.indexOf(getGroupById(currentTransitions.get(c), split)))));
        }
        allStatesList = result;
        acceptStates = new HashSet<>();
        for (State st: allStatesList) {
            for (Node node: st.getStatePositions()) {
                if (node.getValue().equals("$")) {
                    acceptStates.add(st);
                }
            }
        }
    }


    private List<Set<State>> makeNewSplit(List<Set<State>> previous){
        List<Set<State>> prev = new ArrayList<>();

        for (int c = 0; c < previous.size(); ++c) {
            Set<State> group = previous.get(c);
            Set<State> newGroup = new HashSet<>();
            if (group.size() == 1) {
                prev.add(group);
                continue;
            }
            List<List<Set<State>>> setSetGroup = new ArrayList<>();
            Set<State> gr;
            for (int i = 0; i < group.size(); ++i) {
                State currentGroupMember = group.stream().toList().get(i);
                List<Set<State>> transGroup = new ArrayList<>();
                for (Character ch : alphabet) {
                    transGroup.add(getGroupById(currentGroupMember.getNextState(ch.toString()),previous));
                }
                setSetGroup.add(transGroup);
            }
            gr = group;
            for (int i = 1; i < group.size(); ++i) {
                State currentGroupMember = group.stream().toList().get(i);
                if (!setSetGroup.get(0).equals(setSetGroup.get(i))) {
                    newGroup.add(currentGroupMember);
                    previous.get(c).remove(currentGroupMember);
                    gr.remove(currentGroupMember);
                    if (i != group.size() - 1) --i;
                }
            }
            if (!newGroup.isEmpty()) {
                prev.add(newGroup);
            }
            prev.add(gr);
        }
        System.out.println("New split: " + prev);
        return prev;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Start state: ");
        builder.append(startState.getStateID()).append('\n');
        for (State st : allStatesList) {
            builder.append(st.getStateID()).append(" -> ");
            for (String tr : st.getTransitions().keySet()) {
                builder.append(st.getNextState(tr).getStateID()).append("{").append(tr).append("} ");
            }
            builder.append('\n');
        }
        builder.append("Acceptable state(s): ").append(acceptStates).append("\n");
        return builder.toString();
    }

}