import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.awt.*;
import java.util.*;
import java.util.List;
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
        //this.root.printTree(this.root, -1);
        this.alphabet = syntaxTree.getAlphabet();
        followPosDict = new IdentityHashMap<>();
        configureFollowPos(this.root);
        allStatesList = new ArrayList<>();
        acceptStates = Collections.newSetFromMap(new IdentityHashMap<>());
        makeDFA();
        startState = allStatesList.get(0);
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
        int lastID = 0;
        unmarkedID.add(lastID);
        allStatesList.add(new State(lastID, configureFirstPos(root, null)));
        while (!unmarkedID.isEmpty()){
            int current = unmarkedID.iterator().next();
            unmarkedID.remove(current);
            alphabet.remove('$');
            alphabet.remove('^');

            for(char symbol : alphabet){
                State newState = new State(lastID + 1, configureNodeUnion(current, symbol));
                currentState = newState;
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

    public DFA and(DFA other) {
        DFA result = new DFA("^");
        result.allStatesList.clear();
        for (int i = 0; i < this.allStatesList.size() * other.allStatesList.size(); ++i)
            result.allStatesList.add(new State(i));
        result.startState = result.allStatesList.get(this.startState.getStateID() * other.allStatesList.size() + other.startState.getStateID());
        Set<Character> buf = new HashSet<>();
        buf.addAll(this.alphabet);
        buf.addAll(other.alphabet);
        result.setAlphabet(buf);
        for (State st1 : this.allStatesList) {
            for (State st2 : other.allStatesList) {
                for (char c : result.alphabet) {
                    if (st1.isEnd() && st2.isEnd()) {
                        Set<Node> tmp = result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()).getStatePositions();
                        tmp.add(new Node("$"));
                        result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()).setStatePositions(tmp);
                        result.acceptStates.add(result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()));
                    }
                    if (this.startState == st1 && other.startState == st2){
                        result.startState = result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID());
                    }
                    State first = st1.getNextState(String.valueOf(c));
                    State second = st2.getNextState(String.valueOf(c));
                    if (second != null && first != null)
                    result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID())
                            .appendNewTransition(String.valueOf(c), result.allStatesList.get(first.getStateID() * other.allStatesList.size() + second.getStateID()));
                }
            }
        }
        return result;
    }

    public DFA and(String other) {
        DFA another = new DFA(other);
        return this.and(another);
    }

    public DFA addition(){
        System.out.print("Хотите ввести иной алфавит ? ");
        Scanner alphabet = new Scanner(System.in);
        Set<Character> buf = new HashSet<>();

        if (alphabet.nextLine().equals("y")){
            System.out.println("Вводите алфавит(или пустую строчку для завершения ввода):");
            String letter = alphabet.nextLine();
            while (!letter.equals("")){
                buf.add(letter.charAt(0));
                letter = alphabet.nextLine();
            }
        }
        else
            buf.addAll(this.alphabet);
//
//        if (this.alphabet.stream().noneMatch(buf::contains)){
//            StringJoiner tm = new StringJoiner("|", "(", ")");
//            for(Character ch : buf)
//                tm.add(ch.toString());
//            return new DFA(tm + "...");
//        }

        DFA result = new DFA("^");
        StringJoiner tm = new StringJoiner("|", "(", ")");
        for(Character ch : buf)
            tm.add(ch.toString());
        DFA other = new DFA(tm + "...");
        result.allStatesList.clear();
        for (int i = 0; i < this.allStatesList.size() * other.allStatesList.size(); ++i)
            result.allStatesList.add(new State(i));
        result.startState = result.allStatesList.get(this.startState.getStateID() * other.allStatesList.size() + other.startState.getStateID());
        result.setAlphabet(buf);
        System.out.println("This\n" + this);
        System.out.println("Other\n" + other);
        //result.setAcceptStates(new HashSet<>());
//        List <State> a = this.getAllStatesList();
//        a.add(new State(allStatesList.size()));
//        this.setAllStatesList(a);
        for (State st1 : this.allStatesList) {
            for (State st2 : other.allStatesList) {
                for (char c : result.alphabet)
                    if (!st1.isEnd() && st2.isEnd()) {
                        Set<Node> tmp = result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()).getStatePositions();
                        tmp.add(new Node("$"));
                        result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()).setStatePositions(tmp);
                        result.acceptStates.add(result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID()));
                    }
            }
        }
        System.out.println("Ацепты - " + result.acceptStates);
        for (State st1 : this.allStatesList) {
            for (State st2 : other.allStatesList) {
                for (char c : result.alphabet) {
                    if (!this.alphabet.contains(c) && !result.acceptStates.isEmpty()) {
                        st1.appendNewTransition(String.valueOf(c), result.acceptStates.stream().toList().get(0));
                        result.acceptStates.stream().toList().get(0).getStatePositions().add(new Node("$"));
                    }
                    if (this.startState == st1 && other.startState == st2){
                        result.startState = result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID());
                    }
                    State first = st1.getNextState(String.valueOf(c));
                    State second = st2.getNextState(String.valueOf(c));
                    if (second != null && first != null)
                        result.allStatesList.get(st1.getStateID() * other.allStatesList.size() + st2.getStateID())
                                .appendNewTransition(String.valueOf(c), result.allStatesList.get(first.getStateID() * other.allStatesList.size() + second.getStateID()));
                }
            }
        }
        System.out.println("Res\n" + result);
        return result;
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