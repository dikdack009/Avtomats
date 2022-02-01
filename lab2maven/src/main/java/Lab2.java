import java.util.*;

public class Lab2 {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String scan = "";
        Regex regex = new Regex();
        System.out.print("1. addition 2. and 3. simple 4. exit ? ");
        String an = scanner.nextLine();
        while (!an.equals("4")){
            System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
            try {
                if (an.equals("1")) {
                    regex = new Regex(scan);
                    regex.compile();
                    DFA dfa = new DFA(scan);
                    regex = new Regex(dfa.addition());
                }
                else if (an.equals("2")){
                    regex = new Regex(scan);
                    regex.compile();
                    System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                    scan = scanner.nextLine();
                    DFA dfa = new DFA(scan);
                    DFA result = regex.getDfa().and(dfa);
                    regex = new Regex(result);
                }

            }catch (RuntimeException exception){
                System.out.println(exception.getMessage());
                exception.printStackTrace();
                System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                scan = scanner.nextLine();
                continue;
            }
            if (an.equals("3")) {
                try {
                    regex = new Regex(scan);
                    regex.compile();
                } catch (RuntimeException exception) {
                    System.out.println(exception.getMessage());
                    exception.printStackTrace();
                    System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                    scan = scanner.nextLine();
                    continue;
                }
            }

            System.out.print("Введите строку или \"exit\" для выхода -> ");
            String str = scanner.nextLine();
            while (!str.equals("exit")){
                System.out.println("\nСтрока <" + str + "> "  + (regex.match(str) ? "подходит!" : "не подходит!"));
                System.out.println(regex.findAll(str));
                System.out.print("\n\nВведите новую строку или \"exit\" для выхода -> ");
                str = scanner.nextLine();
            }

            System.out.print("1. addition 2. and 3. simple 4. exit ? ");
            an = scanner.nextLine();
        }
    }
}


//(a|bcd...){2}e{3}
