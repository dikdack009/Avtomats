public class Exceptions {
    @Override
    public String toString() {
        return "Неверное регулярное выражение!\t";
    }
}
class FigureBracketsException extends RuntimeException{
    public FigureBracketsException(){
        super(new Exceptions() +
            "Ошибка в использовании операции ‘повтор выражения’: r{x}.");
    }
}
class BracketsException extends RuntimeException{
    public BracketsException() {
        super(new Exceptions() +
            "Неверное количество скобок.");
    }
}
class StarException extends RuntimeException{
    public StarException() {
        super(new Exceptions() +
            "Ошибка в использовании операции ‘замыкание Клини’: r…");
    }
}
class OrException extends RuntimeException {
    public OrException() {
        super(new Exceptions() +
                "Ошибка в использовании операции ‘или’: r1|r2");
    }
}
class QuestionException extends RuntimeException {
    public QuestionException() {
        super(new Exceptions() +
            "Ошибка в использовании операции ‘опциональная часть’: r?");
    }
}
