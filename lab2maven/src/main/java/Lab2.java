import java.util.*;

public class Lab2 {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        Regex regex;
        while (!scan.equals("exit")){
            regex = new Regex(scan);
            regex.compile();
            System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
            DFA dfa = new DFA(scan);
//            -------------------------------
//            DFA result = regex.getDfa().and(dfa);
//            regex = new Regex(result);
//            -------------------------------
            regex = new Regex(dfa.addition());
//            -------------------------------
//            try {
//                regex = new Regex(scan);
//                regex.compile();
//            }catch (RuntimeException exception){
//                System.out.println(exception.getMessage());
//                System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
//                scan = scanner.nextLine();
//                continue;
//            }


            System.out.print("Введите строку или \"exit\" для выхода -> ");
            String str = scanner.nextLine();
            while (!str.equals("exit")){
                System.out.println("\nСтрока <" + str + "> "  + (regex.match(str) ? "подходит!" : "не подходит!"));
                regex.findAll(str);
                System.out.print("\n\nВведите новую строку или \"exit\" для выхода -> ");
                str = scanner.nextLine();
            }

            System.out.print("\nВведите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}


//(a|bcd...){2}e{3}
