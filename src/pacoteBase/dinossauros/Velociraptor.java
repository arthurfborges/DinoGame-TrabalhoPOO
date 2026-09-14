package pacoteBase.dinossauros;
import pacoteBase.jogador.Jogador01;

public class Velociraptor extends Dinossauros {
    public Velociraptor(int x, int y) { super(x, y, 2); }

    @Override
    public void atacar(Jogador01 jogador) {
        if (!jogador.tentarEsquivar()) {
            jogador.setSaude(jogador.getSaude() - 1);
        }
    }

    @Override
    public int[] decidirMovimento(Jogador01 jogador) {
        int[] passo1 = sortearMovimento();
        int tempX = this.x;
        int tempY = this.y;

        this.x = passo1[0];
        this.y = passo1[1];
        int[] passo2 = sortearMovimento();

        this.x = tempX;
        this.y = tempY;

        return passo2;
    }

    @Override
    public void mover(int novoX, int novoY) {
        this.x = novoX;
        this.y = novoY;
        System.out.println("Velociraptor correu para: " + x + ", " + y);
    }

    // 2x mais rápido que os outros dinossauros = metade do tempo de espera
    @Override
    protected int getIntervaloMovimentoMs() {
        return super.getIntervaloMovimentoMs() / 2;
    }
}