package pacoteBase.item;
import pacoteBase.dinossauros.Dinossauros;
import pacoteBase.jogador.Jogador01;
import java.io.Serializable;
public abstract class Item implements Serializable {
    protected String nome;
    protected String descricao;

    public Item(String nome, String descricao) {
        this.nome = nome;
        this.descricao = descricao;
    }

    public abstract void aplicarEfeito(Jogador01 jogador);

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public abstract boolean executarAtaque(Jogador01 jogador, Dinossauros dinossauro);

}