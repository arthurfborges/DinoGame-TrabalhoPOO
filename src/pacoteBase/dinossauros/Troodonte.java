package pacoteBase.dinossauros;
import pacoteBase.dinossauros.Dinossauros;
import pacoteBase.jogador.Jogador01;

public class Troodonte extends Dinossauros {
    public Troodonte(int x, int y) { super(x, y, 2); }

    @Override
    public void atacar(Jogador01 jogador) {
        if (!jogador.tentarEsquivar()) {
            jogador.setSaude(jogador.getSaude() - 1);
        }
    }

    @Override
    public int[] decidirMovimento(Jogador01 jogador) {
        int novoX = (jogador.getX() > this.x) ? this.x + 1 : (jogador.getX() < this.x ? this.x - 1 : this.x);
        int novoY = (jogador.getY() > this.y) ? this.y + 1 : (jogador.getY() < this.y ? this.y - 1 : this.y);
        return new int[]{novoX, novoY};
    }

    @Override
    public void mover(int novoX, int novoY) {
        this.x = novoX;
        this.y = novoY;
       // System.out.println("Troodonte caçador avançou para: " + x + ", " + y);
    }
}