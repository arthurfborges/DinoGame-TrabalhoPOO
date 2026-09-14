package pacoteBase.dinossauros;

import pacoteBase.Entidade;
import pacoteBase.jogador.Jogador01;
import controle.Tabuleiro;
import java.util.Random;

public abstract class Dinossauros extends Entidade implements Runnable {
    protected Random random = new Random();

    private Tabuleiro tabuleiro;

    private volatile boolean rodando = false;

    public Dinossauros(int x, int y, int saude) {
        super(x, y, saude);
    }
    public void iniciarThread(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
        this.rodando = true;
        Thread thread = new Thread(this);
        thread.start();
    }

    public void pararThread() {
        this.rodando = false;
    }

    protected int getIntervaloMovimentoMs() {
        return 1000;
    }

    @Override
    public void run() {
        while (rodando && estaViva()) {
            try {
                Thread.sleep(getIntervaloMovimentoMs());
            } catch (InterruptedException e) {
                break; // se a thread for interrompida, ela simplesmente para
            }

            synchronized (tabuleiro) {
                if (!estaViva()) break;
                tabuleiro.moverUmDinossauro(this);
            }
        }
    }

    public void receberDano(int dano) {
        this.saude -= dano; // Reduz a saúde herdada de Entidade
        if (this.saude < 0) {
            this.saude = 0; // Evita que a vida fique negativa
        }
        System.out.println("-> " + this.getClass().getSimpleName() + " recebeu " + dano + " de dano! (Saúde restante: " + this.saude + ")");
    }

    public abstract void atacar(Jogador01 jogador);

    public abstract int[] decidirMovimento(Jogador01 jogador);

    protected int[] sortearMovimento() {
        int[] direcoes = {-1, 0, 1};
        int dx = direcoes[random.nextInt(3)];
        int dy = direcoes[random.nextInt(3)];

        if (dx != 0 && dy != 0) dx = 0;
        if (dx == 0 && dy == 0) dx = 1;

        return new int[]{this.x + dx, this.y + dy};
    }
}