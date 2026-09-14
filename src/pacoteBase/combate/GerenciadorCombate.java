package pacoteBase.combate;

import pacoteBase.dinossauros.Dinossauros;
import pacoteBase.dinossauros.TiranossauroRex;
import pacoteBase.jogador.Jogador01;
import pacoteBase.item.Item;

public class GerenciadorCombate {

    public boolean executarAtaque(Jogador01 jogador, Dinossauros dino, Item arma) {
        boolean derrotado = arma.executarAtaque(jogador, dino);

        if (derrotado) {
            System.out.println(dino.getClass().getSimpleName() + " foi derrotado!");
            return true;
        } else {
            realizarContraAtaque(jogador, dino);
            return false;
        }
    }

    private void realizarContraAtaque(Jogador01 jogador, Dinossauros dino) {
        System.out.println("\n" + dino.getClass().getSimpleName() + " está revidando!");
        int dado3Lados = (int)(Math.random() * 3) + 1;

        if (dado3Lados <= jogador.getPercepcao()) {
            System.out.println("-> Você usou seus instintos e se esquivou do ataque!");
        } else {
            int danoSofrido = (dino instanceof TiranossauroRex) ? 2 : 1;

            int novaSaude = jogador.getSaude() - danoSofrido;
            jogador.setSaude(novaSaude);
            System.out.println("-> Você foi atingido! Perdeu " + danoSofrido + " de vida.");
        }
    }
}