import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;

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
}
