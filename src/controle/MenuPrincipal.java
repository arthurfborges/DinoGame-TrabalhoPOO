package controle;

import pacoteBase.jogador.Jogador01;

import javax.swing.*;
import java.awt.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Random;

public class MenuPrincipal extends JFrame {
    private static final String[] MAPAS_DISPONIVEIS = {
            "mapa01.txt", "mapa02.txt", "mapa03.txt"
    };
    private static final Random random = new Random();

    public MenuPrincipal() {
        setTitle("Sobrevivência Jurássica - Menu");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centraliza a janela na tela

        setLayout(new GridLayout(4, 1, 10, 10));

        JLabel titulo = new JLabel("SOBREVIVÊNCIA JURÁSSICA", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 20));

        JButton btnNovoJogo = new JButton("Novo Jogo");
        JButton btnCarregar = new JButton("Carregar Jogo");
        JButton btnSair = new JButton("Sair");


        btnNovoJogo.addActionListener(e -> iniciarNovoJogo());

        btnCarregar.addActionListener(e -> carregarJogo());

        btnSair.addActionListener(e -> System.exit(0)); // Mata o programa inteiro

        add(titulo);
        add(btnNovoJogo);
        add(btnCarregar);
        add(btnSair);
    }

    private void iniciarNovoJogo() {
        Jogador01 jogador = new Jogador01(0, 0, 5, 2);
        Tabuleiro tabuleiro = new Tabuleiro(20, jogador);
        String mapaEscolhido = MAPAS_DISPONIVEIS[random.nextInt(MAPAS_DISPONIVEIS.length)];
        tabuleiro.carregarMapaDeRecurso(mapaEscolhido);

        Main.escolherDificuldade(jogador);

        abrirJanelaJogo(tabuleiro);
    }

    private void carregarJogo() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("savegame.dat"))) {
            Tabuleiro tabuleiroSalvo = (Tabuleiro) ois.readObject();
            abrirJanelaJogo(tabuleiroSalvo);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Nenhum save ativo encontrado ou arquivo corrompido.",
                    "Erro ao Carregar",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirJanelaJogo(Tabuleiro tabuleiro) {
        JanelaJogo janela = new JanelaJogo(tabuleiro);
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setVisible(true); // Mostra o jogo

        this.dispose(); // Destrói a janela de Menu (libera a memória)
    }
}