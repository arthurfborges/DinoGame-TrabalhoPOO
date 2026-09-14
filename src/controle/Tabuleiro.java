package controle;

import pacoteBase.Entidade;
import pacoteBase.ambiente.Parede;
import pacoteBase.dinossauros.*;
import pacoteBase.caixaDeSuprimentos.CaixaDeSuprimentos;
import pacoteBase.jogador.Jogador01;
import pacoteBase.item.*;
import java.io.*;
import java.util.*;

public class Tabuleiro implements Serializable { // Adicionado o Serializable aqui
    private int tamanho;
    private Entidade[][] grade;
    private Jogador01 jogador;
    private Set<String> listaParedes = new HashSet<>();
    private List<CaixaDeSuprimentos> listaCaixas = new ArrayList<>();

    private String nomeMapa;

    public Tabuleiro(int tamanho, Jogador01 jogador) {
        this.tamanho = tamanho;
        this.grade = new Entidade[tamanho][tamanho];
        this.jogador = jogador;
    }

    public int getTamanho() { return tamanho; }
    public void setTamanho(int tamanho) { this.tamanho = tamanho; }
    public Entidade[][] getGrade() { return grade; }
    public void setGrade(Entidade[][] grade) { this.grade = grade; }
    public Jogador01 getJogador() { return jogador; }
    public void setJogador(Jogador01 jogador) { this.jogador = jogador; }
    public Set<String> getListaParedes() { return listaParedes; }
    public void setListaParedes(Set<String> listaParedes) { this.listaParedes = listaParedes; }
    public List<CaixaDeSuprimentos> getListaCaixas() { return listaCaixas; }
    public void setListaCaixas(List<CaixaDeSuprimentos> listaCaixas) { this.listaCaixas = listaCaixas; }
    public String getNomeMapa() { return nomeMapa; }
    public void setNomeMapa(String nomeMapa) { this.nomeMapa = nomeMapa; }

    public synchronized boolean ehPosicaoValida(int x, int y) {
        if (x < 0 || x >= tamanho || y < 0 || y >= tamanho) return false;
        return !listaParedes.contains(x + "," + y);
    }

    public synchronized Entidade getEntidade(int x, int y) {
        if (x < 0 || x >= tamanho || y < 0 || y >= tamanho) return null;
        return grade[x][y];
    }

    public synchronized CaixaDeSuprimentos buscarCaixaEm(int x, int y) {
        for (CaixaDeSuprimentos c : listaCaixas) {
            if (c.getX() == x && c.getY() == y) return c;
        }
        return null;
    }

    public synchronized boolean todosDinossaurosDerrotados() {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (grade[i][j] instanceof Dinossauros) return false;
            }
        }
        return true;
    }

    public boolean podeVer(int targetX, int targetY) {
        int px = jogador.getX();
        int py = jogador.getY();
        if (targetX != px && targetY != py) return false;

        int dx = Integer.signum(targetX - px);
        int dy = Integer.signum(targetY - py);

        int currX = px + dx;
        int currY = py + dy;

        while (currX != targetX || currY != targetY) {
            if (grade[currX][currY] instanceof Parede || grade[currX][currY] != null) return false;
            if (buscarCaixaEm(currX, currY) != null) return false;
            currX += dx;
            currY += dy;
        }
        return true;
    }

    public synchronized void moverJogador(int novoX, int novoY) {
        grade[jogador.getX()][jogador.getY()] = null;
        jogador.mover(novoX, novoY);
        grade[novoX][novoY] = jogador;
    }

    public synchronized void removerEntidade(int x, int y) {
        if (x >= 0 && x < tamanho && y >= 0 && y < tamanho) grade[x][y] = null;
    }

    public synchronized void adicionarEntidade(Entidade e) {
        if (e.getX() >= 0 && e.getX() < tamanho && e.getY() >= 0 && e.getY() < tamanho) grade[e.getX()][e.getY()] = e;
    }

    public synchronized void removerCaixa(CaixaDeSuprimentos c) { listaCaixas.remove(c); }

    public void executarTurnoDinossauros() {
        Set<Dinossauros> movidos = new HashSet<>();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                Entidade e = grade[i][j];
                if (e instanceof Dinossauros && !movidos.contains(e)) {
                    Dinossauros d = (Dinossauros) e;
                    if (d instanceof TiranossauroRex) continue;

                    int[] destino = d.decidirMovimento(this.jogador);
                    if (ehPosicaoValida(destino[0], destino[1]) && grade[destino[0]][destino[1]] == null) {
                        grade[i][j] = null;
                        d.mover(destino[0], destino[1]);
                        grade[d.getX()][d.getY()] = d;
                        movidos.add(d);
                    }
                }
            }
        }
    }

    public void moverUmDinossauro(Dinossauros d) {
        int x = d.getX();
        int y = d.getY();

        if (getEntidade(x, y) != d) return;

        int[] destino = d.decidirMovimento(this.jogador);
        if (ehPosicaoValida(destino[0], destino[1]) && grade[destino[0]][destino[1]] == null) {
            grade[x][y] = null;
            d.mover(destino[0], destino[1]);
            grade[d.getX()][d.getY()] = d;
        }
    }

    public synchronized List<Dinossauros> getListaDinossauros() {
        List<Dinossauros> lista = new ArrayList<>();
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (grade[i][j] instanceof Dinossauros) {
                    lista.add((Dinossauros) grade[i][j]);
                }
            }
        }
        return lista;
    }

    public void carregarMapaDeRecurso(String nomeArquivo) {
        this.nomeMapa = nomeArquivo;
        try (InputStream is = getClass().getResourceAsStream("/" + nomeArquivo)) {
            if (is == null) {
                throw new NullPointerException("O arquivo de recurso '" + nomeArquivo + "' não foi localizado.");
            }

            try (Scanner scanner = new Scanner(is)) {
                int linha = 0;
                while (scanner.hasNextLine() && linha < tamanho) {
                    String[] colunas = scanner.nextLine().split(" ");
                    for (int col = 0; col < colunas.length && col < tamanho; col++) {
                        char tipo = colunas[col].charAt(0);
                        switch (tipo) {
                            case '#': grade[linha][col] = new Parede(linha, col); listaParedes.add(linha + "," + col); break;
                            case 'K': listaCaixas.add(new CaixaDeSuprimentos(linha, col, new KitMedico(), false)); break;
                            case 'B': listaCaixas.add(new CaixaDeSuprimentos(linha, col, new BastaoEletrico(), false)); break;
                            case 'D': listaCaixas.add(new CaixaDeSuprimentos(linha, col, new Dardos(), false)); break;
                            case 'C': grade[linha][col] = new Compsognato(linha, col); break;
                            case 'T': grade[linha][col] = new Troodonte(linha, col); break;
                            case 'R': grade[linha][col] = new TiranossauroRex(linha, col); break;
                            case 'V': grade[linha][col] = new Velociraptor(linha, col); break;
                            case 'P': jogador.mover(linha, col); grade[linha][col] = jogador; break;
                        }
                    }
                    linha++;
                }
            }
        } catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
            System.out.println("[ERRO CRÍTICO NO MAPA] O arquivo está ausente ou corrompido: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("[ERRO I/O] Erro ao ler o arquivo de mapa: " + e.getMessage());
        }
    }

    public void imprimirMapaComVisao(boolean debug) {
        for (int i = 0; i < tamanho; i++) {
            for (int j = 0; j < tamanho; j++) {
                if (debug || podeVer(i, j) || (i == jogador.getX() && j == jogador.getY())) {
                    Entidade e = grade[i][j];
                    CaixaDeSuprimentos c = buscarCaixaEm(i, j);
                    if (e instanceof Jogador01) System.out.print("P ");
                    else if (e instanceof Parede) System.out.print("█ ");
                    else if (e instanceof TiranossauroRex) System.out.print("R ");
                    else if (e instanceof Velociraptor) System.out.print("V ");
                    else if (e instanceof Troodonte) System.out.print("T ");
                    else if (e instanceof Compsognato) System.out.print("C ");
                    else if (c != null) System.out.print("X ");
                    else System.out.print(". ");
                } else {
                    System.out.print("? ");
                }
            }
            System.out.println();
        }
    }
}