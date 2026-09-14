package controle;

import pacoteBase.jogador.Jogador01;
import javax.swing.SwingUtilities;
import java.util.Scanner;
import java.util.Random;
import java.io.ObjectOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    private static final String[] MAPAS_DISPONIVEIS = {
            "mapa01.txt",
            "mapa02.txt",
            "mapa03.txt"
    };
    private static final Random random = new Random();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("  BEM-VINDO AO SOBREVIVÊNCIA JURÁSSICA! ");
        System.out.println("========================================");

        boolean rodandoMenu = true;
        while (rodandoMenu) {
            System.out.println("\n--- MENU PRINCIPAL ---");
            System.out.println("1 - Novo Jogo");
            System.out.println("2 - Carregar Jogo");
            System.out.println("3 - Sair");
            System.out.print("Escolha uma opção: ");

            String opcaoMenu = scanner.nextLine();

            switch (opcaoMenu) {
                case "1":
                    menuPartida(scanner, null);
                    break;
                case "2":
                    Tabuleiro tabSalvo = carregarJogo();
                    if (tabSalvo != null) {
                        menuPartida(scanner, tabSalvo);
                    }
                    break;
                case "3":
                    System.out.println("Saindo do complexo...");
                    rodandoMenu = false;
                    break;
                default:
                    System.out.println("Opção inválida!");
                    break;
            }
        }
        scanner.close();
    }

    private static void menuPartida(Scanner scanner, Tabuleiro tabuleiroCarregado) {
        executarPartida(tabuleiroCarregado);

        System.out.println("\n[SISTEMA] Modo gráfico carregado! Jogue na janela que se abriu.");
        System.out.println("Quando terminar ou fechar a janela, pressione ENTER aqui no terminal para voltar ao Menu...");
        scanner.nextLine();
    }

    private static void executarPartida(Tabuleiro tabuleiro) {
        System.out.println("Entrou em executarPartida");
        boolean jogoNovo = (tabuleiro == null);
        if (tabuleiro == null) {
            System.out.println("Criando tabuleiro");
            tabuleiro = criarTabuleiroComMapaAleatorio();
        }

        Tabuleiro tabuleiroFinal = tabuleiro;
        SwingUtilities.invokeLater(() -> {
            System.out.println("Entrou no invokeLater");

            if (jogoNovo) {
                escolherDificuldade(tabuleiroFinal.getJogador());
            }

            abrirJanelaJogo(tabuleiroFinal);
        });
    }

    private static Tabuleiro criarTabuleiroComMapaAleatorio() {
        Jogador01 jogador = new Jogador01(0, 0, 5, 2);
        Tabuleiro tabuleiro = new Tabuleiro(20, jogador);
        String mapaEscolhido = MAPAS_DISPONIVEIS[random.nextInt(MAPAS_DISPONIVEIS.length)];
        tabuleiro.carregarMapaDeRecurso(mapaEscolhido);
        return tabuleiro;
    }

    public static void abrirJanelaJogo(Tabuleiro tabuleiro) {
        JanelaJogo janela = new JanelaJogo(tabuleiro);
        janela.setDefaultCloseOperation(javax.swing.JFrame.HIDE_ON_CLOSE);
        janela.setVisible(true);
    }

    public static void novoJogo() {
        Tabuleiro tabuleiro = criarTabuleiroComMapaAleatorio();
        escolherDificuldade(tabuleiro.getJogador());
        abrirJanelaJogo(tabuleiro);
    }

    public static void reiniciarJogo(Tabuleiro tabuleiroAnterior) {
        Jogador01 jogadorAnterior = tabuleiroAnterior.getJogador();
        Jogador01 novoJogador = new Jogador01(0, 0, 5, jogadorAnterior.getPercepcao());

        Tabuleiro novoTabuleiro = new Tabuleiro(tabuleiroAnterior.getTamanho(), novoJogador);
        novoTabuleiro.carregarMapaDeRecurso(tabuleiroAnterior.getNomeMapa());

        abrirJanelaJogo(novoTabuleiro);
    }


    public static void escolherDificuldade(Jogador01 jogador) {
        String[] opcoes = {"Fácil", "Médio", "Difícil"};
        int escolha = javax.swing.JOptionPane.showOptionDialog(
                null,
                "Escolha a dificuldade da partida:",
                "Dificuldade",
                javax.swing.JOptionPane.DEFAULT_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE,
                null, opcoes, opcoes[1]); // "Médio" fica selecionado por padrão

        int percepcao;
        switch (escolha) {
            case 0: percepcao = 3; break; // Fácil: mais fácil desviar dos ataques
            case 2: percepcao = 1; break; // Difícil: mais difícil desviar dos ataques
            default: percepcao = 2; break; // Médio (ou se fechar o popup sem escolher)
        }

        jogador.setPercepcao(percepcao);
        System.out.println("Dificuldade escolhida: " + opcoes[escolha == -1 ? 1 : escolha]
                + " (percepção do jogador = " + percepcao + ")");
    }

    private static final String ARQUIVO_SAVE_PADRAO = "savegame.dat";

    public static void salvarJogo(Tabuleiro tabuleiro) {
        salvarJogo(tabuleiro, ARQUIVO_SAVE_PADRAO);
    }

    public static void salvarJogo(Tabuleiro tabuleiro, String nomeArquivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(nomeArquivo))) {
            oos.writeObject(tabuleiro);
            System.out.println("Jogo salvo com sucesso em '" + nomeArquivo + "'!");
        } catch (IOException e) {
            System.out.println("Erro ao salvar o jogo: " + e.getMessage());
        }
    }

    public static Tabuleiro carregarJogo() {
        return carregarJogo(ARQUIVO_SAVE_PADRAO);
    }

    public static Tabuleiro carregarJogo(String nomeArquivo) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(nomeArquivo))) {
            System.out.println("Jogo carregado com sucesso!");
            return (Tabuleiro) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro: Nenhum save ativo encontrado ou arquivo corrompido.");
            return null;
        }
    }
}