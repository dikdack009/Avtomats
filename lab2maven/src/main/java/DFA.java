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
    private IdentityHashMap <Node, Set<Node>> followPosDict;
    private State startState;
    private State currentState;
    private List<State> allStatesList;
    private Set<Character> alphabet;
    private Set<State> acceptStates;

    public DFA(Node root, Set<Character> alphabet) {
        this.alphabet = alphabet;
        this.root = root;
        followPosDict = new IdentityHashMap<>();
        configureFollowPos(root);
        allStatesList = new ArrayList<>();
        acceptStates = Collections.newSetFromMap(new IdentityHashMap<>());
    }

    public DFA(String root) {
        SyntaxTree syntaxTree = new SyntaxTree(root);
        syntaxTree.makeSyntaxTree();
        this.root = syntaxTree.getSyntaxTree();
        this.alphabet = syntaxTree.getAlphabet();
        followPosDict = new IdentityHashMap<>();
        configureFollowPos(this.root);
        allStatesList = new ArrayList<>();
        acceptStates = Collections.newSetFromMap(new IdentityHashMap<>());
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
            firstPosSet = Collections.newSetFromMap(new IdentityHashMap<>());
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
            lastPosSet = Collections.newSetFromMap(new IdentityHashMap<>());
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
                Set<Node> leftFollowSet = configureLastPos(expression.getLeftChild(), null);
                Set<Node> rightFollowSet = configureFirstPos(expression.getRightChild(), null);
                for (Node node : leftFollowSet) {
                    if (!followPosDict.containsKey(node)) {
                        followPosDict.put(node, Collections.newSetFromMap(new IdentityHashMap<>()));
                    }
                    followPosDict.get(node).addAll(rightFollowSet);
                }
                configureFollowPos(expression.getRightChild());
            }
            case "..." -> {
                Set<Node> lastPosSet = configureLastPos(expression.getLeftChild(), null);
                Set<Node> firstPosSet = configureFirstPos(expression.getLeftChild(), null);
                for (Node node : lastPosSet) {
                    if (followPosDict.containsKey(node)) {
                        followPosDict.get(node).addAll(firstPosSet);
                    }
                    else {
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
            int current = unmarkedID.iterator().next();
            unmarkedID.remove(current);
            alphabet.remove('$');
            alphabet.remove('^');

            System.out.println(alphabet);
            for(char symbol : alphabet){
                System.out.println(allStatesList);
                State newState = new State(lastID + 1, configureNodeUnion(current, symbol));
                if (!allStatesList.contains(newState)){
                    unmarkedID.add(++lastID);
                    allStatesList.add(newState);
                }
                else{
                    newState = allStatesList.get(allStatesList.indexOf(newState));
                }
                allStatesList.get(current).appendNewTransition(String.valueOf(symbol), newState);
            }
        }
        for (State st: allStatesList) {
            for (Node node: st.getStatePositions()) {
                if (node.getValue().equals("$")) {
                    acceptStates.add(st);
                }
            }
        }
        //minimizationDFA();
    }

    protected Set<Node> configureNodeUnion(int state, char symbol){
        Set<Node> resultSet = Collections.newSetFromMap(new IdentityHashMap<>());
        for (Node node : allStatesList.get(state).getStatePositions())
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
        splitting.add(Collections.newSetFromMap(new IdentityHashMap<>()));
        splitting.add(Collections.newSetFromMap(new IdentityHashMap<>()));

        for (State state : acceptStates)
            splitting.get(0).add(state);

        for(State curState : allStatesList)
            for (State st :acceptStates)
                if (!st.equals(curState))
                    splitting.get(1).add(curState);

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
        return prev;
    }

    private String getPath(int i, int j, int k, Map<TripleNumbers, String> cashedKPath) {
        return cashedKPath.containsKey(new TripleNumbers(i, j, k)) ?
                cashedKPath.get(new TripleNumbers(i, j, k)) :
                k_path(i, j, k);
    }

    public String k_path(int i, int j, int k) {
        if (k == -1) {
            // transChars.add(c.toString());
            StringJoiner transChars = new StringJoiner("|", "(", ")");
            for (Character c : alphabet)
                if (allStatesList.get(i).getNextState(c.toString()) == allStatesList.get(j))
                    transChars.add(c.toString());

            if (transChars.length() == 2)
                return i == j ? "E" : null;

            return transChars.toString();
        } else {
            Map<TripleNumbers, String> cashedKPath = new HashMap<>();
            String firstInF = getPath(i, j, k - 1, cashedKPath);
            String secondInF = getPath(i, k, k - 1, cashedKPath);
            String thirdInF = getPath(k, k, k - 1, cashedKPath);
            String fourthInF = getPath(k, j, k - 1, cashedKPath);

            String firstInFRes = firstInF == null ? "" : addBracket(firstInF);
            boolean checkRes = secondInF == null || thirdInF == null || fourthInF == null;
            String secondInFRes = checkRes ? "" : (firstInF == null ? "" : '|') + addBracket(secondInF);
            String thirdInFRes = checkRes ? "" : thirdInF.equals("E") ? "E" : "(" + thirdInF + "|E)...";
            String fourthInFRes = checkRes ? "" : addBracket(fourthInF);

            String res = firstInFRes + secondInFRes + thirdInFRes + fourthInFRes;
            cashedKPath.put(new TripleNumbers(i, j, k), res.equals("") ? null : res);
            return res.equals("") ? null : res;
        }
    }

    private String addBracket(String expr) {
        return expr.contains("|") ? "(" + expr + ")" : expr;
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