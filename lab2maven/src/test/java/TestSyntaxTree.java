import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тестирование создания Синтаксического дерева")
public class TestSyntaxTree {
    SyntaxTree syntaxTree;

    @BeforeEach
    public void setUp(){
       syntaxTree = new SyntaxTree();
    }

    @Test
    @DisplayName("Проверка замены фигурных скобок")
    public void testRemoveFigureBrackets(){
        String regex = "(a|b){3}";
        String expectedRegex = "(" + "(a|b)".repeat(3) + ")";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeFigureBrackets();
        String actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно раскрытые фигурные скобки.");

        regex = "a{3}";
        expectedRegex = "(" + "a".repeat(3) + ")";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeFigureBrackets();
        actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно раскрытые фигурные скобки.");
    }

    @Test
    public void testRemoveQuestion(){
        String regex = "a?";
        String expectedRegex = "(a|^)";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeQuestion();
        String actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно убранный знак вопроса.");

        regex = "a?b";
        expectedRegex = "(a|^)b";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeQuestion();
        actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно убранный знак вопроса.");

        regex = "(a|c)?b";
        expectedRegex = "((a|c)|^)b";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeQuestion();
        actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно убранный знак вопроса.");
    }

    @Test
    @DisplayName("Проверка построения ДКА и минимального ДКА")
    public void testMakeDFA(){
        DFA dfa = new DFA();
        Set<Character> set = new HashSet<>();
        set.add('L');
        set.add('D');
        dfa.setAlphabet(set);
        State state0 = new State(0, Set.of(new Node("a")));
        State state1 = new State(1, Set.of(new Node("$")));
        State state2 = new State(2, Set.of(new Node("b")));
        State state3 = new State(3, Set.of(new Node("$")));
        State state4 = new State(4, Set.of(new Node("$")));
        state0.appendNewTransition("L", state1);
        state0.appendNewTransition("D", state2);
        state1.appendNewTransition("L", state3);
        state1.appendNewTransition("D", state4);
        state2.appendNewTransition("L", state2);
        state2.appendNewTransition("D", state2);
        state3.appendNewTransition("L", state3);
        state3.appendNewTransition("D", state4);
        state4.appendNewTransition("L", state3);
        state4.appendNewTransition("D", state4);
        dfa.setStartState(state0);
        dfa.setAllStatesList(List.of(state0, state1, state2, state3, state4));
        Set <State> newSet = new TreeSet<>();
        newSet.add(state1);
        newSet.add(state4);
        newSet.add(state3);
        dfa.setAcceptStates(newSet);
        dfa.minimizationDFA();
    }

    @Test
    public void testTypicalCheckRegex(){
        String regex = "a{e}";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(FigureBracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = "a{}";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(FigureBracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = "a{";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(FigureBracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = "}";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(FigureBracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = "a(";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(BracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = ")";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(BracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = ">";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(BracketsException.class, () -> syntaxTree.typicalCheckRegex());

        regex = "|?";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(QuestionException.class, () -> syntaxTree.typicalCheckRegex());
    }

    @Test
    public void testOr(){
        String regex = "(|a)";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(OrException.class, () -> syntaxTree.makeSyntaxTree());

        regex = "|a";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(OrException.class, () -> syntaxTree.makeSyntaxTree());

        regex = "(b|)a";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(OrException.class, () -> syntaxTree.makeSyntaxTree());

        regex = "b|a";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.makeSyntaxTree();
        Node expected = new Node("|", new Node("b"), new Node("a"));
        Node expectedRegex = new Node(".");
        expectedRegex.setLeftChild(expected);
        expectedRegex.setRightChild(new Node("$"));
        assertEquals(expectedRegex, syntaxTree.getSyntaxTree());
    }

    @Test
    public void testStar(){
        String regex = "|...";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(StarException.class, () -> syntaxTree.makeSyntaxTree());

        regex = "...";
        syntaxTree = new SyntaxTree(regex);
        assertThrows(StarException.class, () -> syntaxTree.makeSyntaxTree());

        regex = "a...";
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.makeSyntaxTree();
        Node expected = new Node("...", new Node("a"), null);
        Node expectedRegex = new Node(".");
        expectedRegex.setLeftChild(expected);
        expectedRegex.setRightChild(new Node("$"));
        assertEquals(expectedRegex, syntaxTree.getSyntaxTree());
    }

}
