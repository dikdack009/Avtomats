public class Exceptions {
    @Override
    public String toString() {
        return "Неверное регулярное выражение!\t";
    }
}
class FigureBracketsException extends RuntimeException{
    public FigureBracketsException(){super(new Exceptions() +
            "Ошибка в использовании операции ‘повтор выражения’: r{x}.");}
}
class BracketsException extends RuntimeException{
    public BracketsException() {super(new Exceptions() +
            "Неверное количество скобок.");}
}
