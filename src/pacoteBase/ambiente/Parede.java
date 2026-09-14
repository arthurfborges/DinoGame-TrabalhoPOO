package pacoteBase.ambiente;

import pacoteBase.Entidade;

public class Parede extends Entidade {

    public Parede(int x, int y) {
        // indestrutível
        super(x, y, 999);
    }

    @Override
    public void mover(int novoX, int novoY) {
        // paredes não se movem
    }
}