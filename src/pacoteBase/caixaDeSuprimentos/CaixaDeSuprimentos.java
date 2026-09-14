package pacoteBase.caixaDeSuprimentos;

import pacoteBase.Entidade;
import pacoteBase.item.*;
import pacoteBase.jogador.Jogador01;
import java.io.Serializable;

public class CaixaDeSuprimentos extends Entidade implements Serializable {
    private Item itemContido;
    private boolean ehArmadilha;
    private int x, y;

    public CaixaDeSuprimentos(int x, int y, Item item, boolean ehArmadilha) {
        super(x, y, 10);
        this.x = x;
        this.y = y;
        this.itemContido = item;
        this.ehArmadilha = ehArmadilha;
    }

    public String abrir(Jogador01 jogador) {
        if (this.ehArmadilha) {
            this.ehArmadilha = false; //desarma
            return "ARMADILHA";
        }

        if (itemContido != null) {
            if (itemContido instanceof Dardos) {
                jogador.coletarItem(new Dardos());
                System.out.println("Você encontrou mais dardos!");
            } else {
                jogador.coletarItem(itemContido);
                System.out.println("Você coletou: " + itemContido.getNome());
            }
            this.itemContido = null;
            return "ITEM";
        }

        return "VAZIO";
    }

    public int getX() { return x; }
    public int getY() { return y; }

    @Override
    public void mover(int novoX, int novoY) {
        // caixas não se movem
    }

    public Item getItemContido() { return itemContido; }
    public void setItemContido(Item itemContido) { this.itemContido = itemContido; }
    public boolean isEhArmadilha() { return ehArmadilha; }
    public void setEhArmadilha(boolean ehArmadilha) { this.ehArmadilha = ehArmadilha; }
}