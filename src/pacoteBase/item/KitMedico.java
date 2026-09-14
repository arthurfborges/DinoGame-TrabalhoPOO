package pacoteBase.item;

import pacoteBase.jogador.Jogador01;
import pacoteBase.dinossauros.Dinossauros;

public class KitMedico extends Item {

    public KitMedico() {
        super("Kit Médico", "Recupera 2 pontos de vida.");
    }

    @Override
    public void aplicarEfeito(Jogador01 jogador) {
        int novaSaude = jogador.getSaude() + 2;
        jogador.setSaude(novaSaude);

        System.out.println("Você usou o Kit Médico e recuperou 2 de vida!");

        jogador.getInventario().remove(this);
    }

    @Override
    public boolean executarAtaque(Jogador01 jogador, Dinossauros dinossauro) {
        throw new UnsupportedOperationException("Erro: Você não pode atacar utilizando um Kit Médico!");
    }
}