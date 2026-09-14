package pacoteBase.item;

import pacoteBase.jogador.Jogador01;
import pacoteBase.dinossauros.Dinossauros;
import pacoteBase.dinossauros.TiranossauroRex;

public class Maos extends Item {

    public Maos() {
        super("Mãos", "Ataque corpo a corpo desarmado.");
    }

    @Override
    public void aplicarEfeito(Jogador01 jogador) { /* Não faz nada */ }

    @Override
    public boolean executarAtaque(Jogador01 jogador, Dinossauros dinossauro) {
        System.out.println("Você tenta golpear " + dinossauro.getClass().getSimpleName() + " com os punhos nus!");

        if (dinossauro instanceof TiranossauroRex) {
            System.out.println("-> O T-Rex é imune a ataques sem arma! Nenhum dano causado.");
            return dinossauro.getSaude() <= 0;
        }

        int dado = (int)(Math.random() * 6) + 1;
        System.out.println("-> Resultado do dado de ataque: " + dado);

        int dano = 0;
        if (dado == 6) dano = 2;
        else if (dado >= 3) dano = 1;

        if (dano > 0) {
            dinossauro.receberDano(dano);
        } else {
            System.out.println("-> Você errou o golpe!");
        }

        return dinossauro.getSaude() <= 0;
    }
}