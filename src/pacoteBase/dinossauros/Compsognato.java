package pacoteBase.dinossauros;
import pacoteBase.jogador.Jogador01;

public class Compsognato extends Dinossauros {
    public Compsognato(int x, int y) { super(x, y, 1); }

    @Override
    public void atacar(Jogador01 jogador) {
        // se falha na esquiva perde 1 de vida
        if (!jogador.tentarEsquivar()) {
            jogador.setSaude(jogador.getSaude() - 1);
        }
    }
    @Override
    public void mover(int novoX, int novoY) {
        this.x = novoX;
        this.y = novoY;
       // System.out.println("Compsognato se moveu para: " + x + ", " + y);
    }

    @Override
    public int[] decidirMovimento(Jogador01 jogador) {
        return sortearMovimento(); //lógica aleatória da classe pai
    }
}