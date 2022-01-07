import java.util.*;

public class Lab2 {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите регулярное выражение или \"exit\" для выхода -> ");
        String scan = scanner.nextLine();
        Regex regex;
        while (!scan.equals("exit")){
            try {
                regex = new Regex(scan);
                regex.compile();
            }catch (RuntimeException exception){
                System.out.println(exception.getMessage());
                System.out.print("Введите новое регулярное выражение или \"exit\" для выхода -> ");
                scan = scanner.nextLine();
                continue;
            }

            System.out.print("Введите строку или \"exit\" для выхода -> ");
            String str = scanner.nextLine();
            while (!str.equals("exit")){
                System.out.println("Строка <" + str + "> "  + (regex.match(str) ? "подходит!" : "не подходит!"));
                System.out.print("\nВведите новую строку или \"exit\" для выхода -> ");
                str = scanner.nextLine();
            }

            System.out.print("\nВведите новое регулярное выражение или \"exit\" для выхода -> ");
            scan = scanner.nextLine();
        }
    }
}


//(a|bcd...){2}e{3}
