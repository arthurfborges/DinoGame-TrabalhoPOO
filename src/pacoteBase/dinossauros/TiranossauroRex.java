package pacoteBase.dinossauros;
import pacoteBase.jogador.Jogador01;

public class TiranossauroRex extends Dinossauros {
    public TiranossauroRex(int x, int y) { super(x, y, 3); }

    @Override
    public void atacar(Jogador01 jogador) {
        //golpe do inimigo especial diminui 2 pontos de saúde
        if (!jogador.tentarEsquivar()) {
            jogador.setSaude(jogador.getSaude() - 2);
        }
    }

    //não se move pelo cenário.
    @Override
    public int[] decidirMovimento(Jogador01 jogador) {
        return new int[]{this.x, this.y};
    }

    @Override
    public void mover(int novoX, int novoY) {
        System.out.println("O T-Rex permanece imóvel e colossal.");
    }

    // O T-Rex não se move, então nem precisa ter sua thread iniciada.
    // Quem decide isso é o JanelaJogo, que pula instâncias de TiranossauroRex
    // na hora de chamar iniciarThread() em cada dinossauro do mapa.
}