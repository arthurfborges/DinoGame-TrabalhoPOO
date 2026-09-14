package pacoteBase;
import java.io.Serializable;

public abstract class Entidade implements Serializable, Descritivel {
    protected int x;
    protected int y;
    protected int saude;

    public Entidade(int x, int y, int saude) {
        this.x = x;
        this.y = y;
        this.saude = saude;
    }

    public abstract void mover(int novoX, int novoY);

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSaude() {
        return saude;
    }

    public void setSaude(int saude) {
        this.saude = Math.max(0, saude);
    }

    public boolean estaViva() {
        return this.saude > 0;
    }
}