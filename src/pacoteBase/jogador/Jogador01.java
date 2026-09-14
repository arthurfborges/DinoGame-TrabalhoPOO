package pacoteBase.jogador;

import pacoteBase.Entidade;
import pacoteBase.item.Item;
import pacoteBase.item.Dardos;
import pacoteBase.item.BastaoEletrico;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Jogador01 extends Entidade {

    private int percepcao;
    private List<Item> inventario;

    public Jogador01(int x, int y, int saude, int percepcao) {
        super(x, y, saude);
        this.percepcao = percepcao;
        this.inventario = new ArrayList<>();
    }

    @Override
    public void mover(int novoX, int novoY) {
        this.x = novoX;
        this.y = novoY;
        System.out.println("Jogador se moveu para: " + x + ", " + y);
    }

    public int getPercepcao() {
        return percepcao;
    }

    public void setPercepcao(int percepcao) {
        this.percepcao = percepcao;
    }

    public List<Item> getInventario() {
        return inventario;
    }

    public void coletarItem(Item item) {
        this.inventario.add(item);
    }

    // --- GESTÃO DE ITENS ---

    public int getQuantidadeDardos() {
        int count = 0;
        for (Item i : this.inventario) {
            if (i instanceof Dardos) count++;
        }
        return count;
    }

    public boolean temBastaoEletrico() {
        for (Item i : this.inventario) {
            if (i instanceof BastaoEletrico) return true;
        }
        return false;
    }

    public boolean tentarEsquivar() {
        Random dado = new Random();
        int resultadoDado = dado.nextInt(3) + 1;
        return resultadoDado <= this.percepcao;
    }

    public void usarItem(Item item) {
        if (this.inventario.contains(item)) {
            item.aplicarEfeito(this);
        } else {
            System.out.println("Item não encontrado no inventário!");
        }
    }
}