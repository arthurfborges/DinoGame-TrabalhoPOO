package controle;

public class ComandoInvalidoException extends Exception{
    public ComandoInvalidoException(String mensagem) {
        super(mensagem);
    }
}