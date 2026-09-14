package pacoteBase.item;

import pacoteBase.jogador.Jogador01;
import pacoteBase.dinossauros.Dinossauros;

public class BastaoEletrico extends Item {

    public BastaoEletrico() {
        super("Bastão Elétrico", "Causa dano elétrico. Crítico em 6, falha em 1.");
    }

    @Override
    public void aplicarEfeito(Jogador01 jogador) { System.out.println("Bastão elétrico pronto."); }

    @Override
    public boolean executarAtaque(Jogador01 jogador, Dinossauros dinossauro) {
        System.out.println("Você ataca com o Bastão Elétrico!");

        int dado = (int)(Math.random() * 6) + 1;
        System.out.println("-> Resultado do dado: " + dado);

        int dano = 1; // Padrão para dados de 2 a 5
        if (dado == 6) {
            System.out.println("-> Acerto crítico!");
            dano = 2;
        } else if (dado == 1) {
            System.out.println("-> Falha no ataque!");
            dano = 0;
        }

        if (dano > 0) {
            dinossauro.receberDano(dano);
        }
        return dinossauro.getSaude() <= 0;
    }
}