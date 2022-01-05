import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;

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
        String expectedRegex = "(a|b)".repeat(3);
        syntaxTree = new SyntaxTree(regex);
        syntaxTree.removeFigureBrackets();
        String actualRegex = syntaxTree.getRegex();
        assertEquals(expectedRegex, actualRegex, "Неверно раскрытые фигурные скобки.");

        regex ="";
        //assertThrows(Throwable.class, () -> storage.addCustomer(input),
        //        "Не выброшено исключение при количестве элементов в строке больше 4");
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
        System.out.println("all - " + dfa.getAllStatesList());
        Set <State> newSet = new TreeSet<>();
        newSet.add(state1);
        newSet.add(state4);
        newSet.add(state3);
        dfa.setAcceptStates(newSet);
        System.out.println(dfa.getAcceptStates());
        System.out.println(dfa);
        dfa.minimizationDFA();
        System.out.println(dfa);
    }
}
