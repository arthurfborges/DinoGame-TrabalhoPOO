package pacoteBase.item;

import pacoteBase.jogador.Jogador01;
import pacoteBase.dinossauros.Dinossauros;
import pacoteBase.dinossauros.Velociraptor;

public class Dardos extends Item {

    public Dardos() {
        super("Dardos", "Um dardo de uso único.");
    }

    @Override
    public void aplicarEfeito(Jogador01 jogador) { System.out.println("Dardo carregado."); }

    @Override
    public boolean executarAtaque(Jogador01 jogador, Dinossauros dinossauro) {
        if (dinossauro instanceof Velociraptor) {
            System.out.println("-> Dardos não funcionam em Velociraptors! Ataque completamente ineficaz.");
            return dinossauro.getSaude() <= 0;
        }

        System.out.println("Você dispara um dardo!");
        jogador.getInventario().remove(this);
        System.out.println("-> Dardo utilizado e removido do inventário!");

        dinossauro.receberDano(2);

        return dinossauro.getSaude() <= 0;
    }
}