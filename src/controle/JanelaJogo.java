package controle;

import pacoteBase.Entidade;
import pacoteBase.ambiente.Parede;
import pacoteBase.caixaDeSuprimentos.CaixaDeSuprimentos;
import pacoteBase.dinossauros.*;
import pacoteBase.jogador.Jogador01;
import pacoteBase.combate.GerenciadorCombate;
import pacoteBase.item.Item;
import pacoteBase.item.Maos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

public class JanelaJogo extends JFrame {
    private Tabuleiro tabuleiro;
    private JButton[][] botoesGrade;
    private GerenciadorCombate combate;

    private Map<String, ImageIcon> icones = new HashMap<>();

    private static final int TAMANHO_ICONE = 28;

    private JLabel lblSaude;
    private JLabel lblPosicao;
    private JLabel lblDardos;
    private JList<String> listaInventario;
    private DefaultListModel<String> modeloInventario;
    private PainelLog painelLog; // Painel que mostra as mensagens do jogo na tela

    private javax.swing.Timer timerDeRepintura;

    private boolean modoDebug = false;

    public JanelaJogo(Tabuleiro tabuleiro) {
        this.tabuleiro = tabuleiro;
        this.combate = new GerenciadorCombate();
        int tam = tabuleiro.getTamanho();
        this.botoesGrade = new JButton[tam][tam];

        this.painelLog = new PainelLog();

        redirecionarPrintsParaOPainel();

        setTitle("Sobrevivência Jurássica - UFPel");
        setSize(1050, 850); // Aumentado para caber o painel inferior
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE); // Esconde ao fechar para voltar ao terminal
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        JPanel painelGrade = new JPanel(new GridLayout(tam, tam));
        inicializarGrade(painelGrade, tam);
        add(painelGrade, BorderLayout.CENTER);

        JPanel painelLateral = criarPainelLateral();
        add(painelLateral, BorderLayout.EAST);

        JPanel painelInferior = criarPainelInstrucoes();
        JPanel painelBotoesUtilitarios = criarPainelBotoesUtilitarios();
        JPanel painelTopoSul = new JPanel(new BorderLayout());
        painelTopoSul.add(painelInferior, BorderLayout.CENTER);
        painelTopoSul.add(painelBotoesUtilitarios, BorderLayout.SOUTH);

        JPanel painelSul = new JPanel(new BorderLayout());
        painelSul.add(painelTopoSul, BorderLayout.NORTH);
        painelSul.add(painelLog, BorderLayout.CENTER);
        add(painelSul, BorderLayout.SOUTH);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                char tecla = Character.toUpperCase(e.getKeyChar());
                processarComandoTeclado(tecla);
            }
        });

        setFocusable(true);
        atualizarVisualizacao();

        painelLog.adicionarMensagem("Bem-vindo(a) à Sobrevivência Jurássica!");
        painelLog.adicionarMensagem("Controles: [W] Cima  [A] Esquerda  [S] Baixo  [D] Direita");
        painelLog.adicionarMensagem("Esbarre em caixas para abrir e em dinossauros para atacar.");
        painelLog.adicionarMensagem("Use o botão 'Debug: Revelar Tabuleiro' para ver o mapa inteiro.");
        painelLog.adicionarMensagem("Use o botão 'Sair do Jogo' para encerrar a qualquer momento.");

        iniciarThreadsDosDinossauros();

        timerDeRepintura = new javax.swing.Timer(100, e -> atualizarVisualizacao());
        timerDeRepintura.start();

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                pararTudo();
            }
        });
    }

    private void iniciarThreadsDosDinossauros() {
        for (Dinossauros d : tabuleiro.getListaDinossauros()) {
            if (d instanceof TiranossauroRex) continue;
            d.iniciarThread(tabuleiro);
        }
    }

    private void pararTudo() {
        if (timerDeRepintura != null) {
            timerDeRepintura.stop();
        }
        for (Dinossauros d : tabuleiro.getListaDinossauros()) {
            d.pararThread();
        }
    }

    private void redirecionarPrintsParaOPainel() {
        java.io.PrintStream streamOriginal = System.out;

        java.io.PrintStream streamComLog = new java.io.PrintStream(streamOriginal) {
            @Override
            public void println(String mensagem) {
                super.println(mensagem); // continua imprimindo no terminal, sempre

                if (ehMensagemDeMovimento(mensagem)) {
                    return; // não mostra na tela, só no terminal
                }

                painelLog.adicionarMensagem(mensagem); // as outras mensagens aparecem normalmente
            }
        };

        System.setOut(streamComLog);
    }


    private boolean ehMensagemDeMovimento(String mensagem) {
        return mensagem.contains("se moveu para")
                || mensagem.contains("avançou para")
                || mensagem.contains("correu para")
                || mensagem.contains("permanece imóvel");
    }


    private ImageIcon carregarIcone(String nomeArquivo) {
        if (icones.containsKey(nomeArquivo)) {
            return icones.get(nomeArquivo); // já carregada antes (pode ser null)
        }

        ImageIcon icone = null;
        java.net.URL caminho = getClass().getResource("/imagens/" + nomeArquivo);
        if (caminho != null) {
            ImageIcon original = new ImageIcon(caminho);
            Image redimensionada = original.getImage()
                    .getScaledInstance(TAMANHO_ICONE, TAMANHO_ICONE, Image.SCALE_SMOOTH);
            icone = new ImageIcon(redimensionada);
        }

        icones.put(nomeArquivo, icone); // guarda no cache, mesmo se for null
        return icone;
    }

    private String nomeArquivoDoDinossauro(Dinossauros d) {
        if (d instanceof Velociraptor) return "velociraptor.png";
        if (d instanceof Troodonte) return "troodonte.png";
        if (d instanceof Compsognato) return "compsognato.png";
        if (d instanceof TiranossauroRex) return "tiranossauro.png";
        return null;
    }

    private JPanel criarPainelInstrucoes() {
        JPanel painel = new JPanel();
        painel.setBackground(new Color(40, 40, 40)); // Fundo escuro para destacar
        painel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        painel.setLayout(new GridLayout(2, 1)); // 2 linhas de texto

        JLabel lblObjetivo = new JLabel("🎯 OBJETIVO: Explore o mapa, encontre suprimentos e elimine todos os dinossauros para vencer!");
        lblObjetivo.setForeground(Color.WHITE);
        lblObjetivo.setFont(new Font("Arial", Font.BOLD, 14));
        lblObjetivo.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel lblControles = new JLabel("⌨️ CONTROLES: Use as teclas [W, A, S, D] para andar. | Esbarre em caixas para abrir. | Esbarre em dinossauros para atacar.");
        lblControles.setForeground(new Color(200, 200, 200)); // Cinza claro
        lblControles.setFont(new Font("Arial", Font.PLAIN, 13));
        lblControles.setHorizontalAlignment(SwingConstants.CENTER);

        painel.add(lblObjetivo);
        painel.add(lblControles);

        return painel;
    }

    private JPanel criarPainelBotoesUtilitarios() {
        JPanel painel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        painel.setBackground(new Color(40, 40, 40));

        JButton btnDebug = new JButton("🐞 Debug: Revelar Tabuleiro");
        btnDebug.setFocusable(false);
        btnDebug.addActionListener(e -> alternarModoDebug());
        painel.add(btnDebug);

        JButton btnSalvar = new JButton("💾 Salvar Jogo");
        btnSalvar.setFocusable(false);
        btnSalvar.addActionListener(e -> salvarJogoAtual());
        painel.add(btnSalvar);

        JButton btnSair = new JButton("🚪 Sair do Jogo");
        btnSair.setFocusable(false);
        btnSair.addActionListener(e -> sairDoJogo());
        painel.add(btnSair);

        return painel;
    }

    private void salvarJogoAtual() {
        Main.salvarJogo(tabuleiro);
        painelLog.adicionarMensagem("Jogo salvo! Use 'Carregar Jogo' no menu principal para retomar depois.");
        requestFocusInWindow();
    }


    private void alternarModoDebug() {
        modoDebug = !modoDebug;

        if (modoDebug) {
            painelLog.adicionarMensagem("========== MODO DEBUG ATIVADO ==========");
            int tam = tabuleiro.getTamanho();
            for (int i = 0; i < tam; i++) {
                for (int j = 0; j < tam; j++) {
                    Entidade entidade = tabuleiro.getEntidade(i, j);
                    if (entidade != null) {
                        painelLog.adicionarMensagem(entidade.descreverParaDebug());
                    } else {
                        CaixaDeSuprimentos caixa = tabuleiro.buscarCaixaEm(i, j);
                        if (caixa != null) {
                            painelLog.adicionarMensagem(
                                    String.format("%-16s | Posição: [%2d,%2d]", "CaixaSuprimentos", i, j));
                        }
                    }
                }
            }
            painelLog.adicionarMensagem("=========================================");
        } else {
            painelLog.adicionarMensagem("Modo debug desativado. Névoa de guerra restaurada.");
        }

        atualizarVisualizacao();
        requestFocusInWindow(); // devolve o foco para o teclado (WASD)
    }

    private void sairDoJogo() {
        int resposta = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja sair do jogo?",
                "Sair do Jogo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (resposta == JOptionPane.YES_OPTION) {
            painelLog.adicionarMensagem("Encerrando o jogo. Até a próxima!");
            pararTudo();
            dispose();
            System.exit(0);
        }
    }

    private void inicializarGrade(JPanel painel, int tam) {
        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                JButton botao = new JButton();
                botao.setFocusable(false);
                botao.setFont(new Font("Arial", Font.BOLD, 12));
                botao.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
                botoesGrade[i][j] = botao;
                painel.add(botao);
            }
        }
    }

    private JPanel criarPainelLateral() {
        JPanel painel = new JPanel();
        painel.setPreferredSize(new Dimension(250, 800));
        painel.setBorder(BorderFactory.createEmptyBorder(20, 15, 20, 15));
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBackground(new Color(240, 240, 240));

        JLabel lblTituloStatus = new JLabel("📊 STATUS DO JOGADOR");
        lblTituloStatus.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloStatus.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTituloStatus);
        painel.add(Box.createRigidArea(new Dimension(0, 15)));

        lblSaude = new JLabel("❤️ Saúde: 5/5");
        lblSaude.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSaude.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblSaude);

        lblPosicao = new JLabel("📍 Posição: [0, 0]");
        lblPosicao.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPosicao.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblPosicao);

        lblDardos = new JLabel("🎯 Munição: 0");
        lblDardos.setFont(new Font("Arial", Font.PLAIN, 13));
        lblDardos.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblDardos);

        painel.add(Box.createRigidArea(new Dimension(0, 25)));
        painel.add(new JSeparator());
        painel.add(Box.createRigidArea(new Dimension(0, 20)));

        JLabel lblTituloInv = new JLabel("🎒 INVENTÁRIO (JList)");
        lblTituloInv.setFont(new Font("Arial", Font.BOLD, 14));
        lblTituloInv.setAlignmentX(Component.CENTER_ALIGNMENT);
        painel.add(lblTituloInv);
        painel.add(Box.createRigidArea(new Dimension(0, 10)));

        modeloInventario = new DefaultListModel<>();
        listaInventario = new JList<>(modeloInventario);
        listaInventario.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaInventario.setFont(new Font("Arial", Font.PLAIN, 13));

        JScrollPane scrollPane = new JScrollPane(listaInventario);
        scrollPane.setPreferredSize(new Dimension(220, 300));
        painel.add(scrollPane);
        painel.add(Box.createRigidArea(new Dimension(0, 15)));

        JButton btnUsar = new JButton("Usar Item Selecionado");
        btnUsar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnUsar.setFocusable(false);
        btnUsar.addActionListener(e -> usarItemSelecionado());
        painel.add(btnUsar);

        return painel;
    }

    private void desenharComIconeOuTexto(JButton botao, String nomeArquivo, String textoReserva, Color corReserva) {
        ImageIcon icone = (nomeArquivo != null) ? carregarIcone(nomeArquivo) : null;
        if (icone != null) {
            botao.setIcon(icone);
        } else {
            botao.setText(textoReserva);
            botao.setBackground(corReserva);
        }
    }

    public void atualizarVisualizacao() {
        int tam = tabuleiro.getTamanho();
        Jogador01 jog = tabuleiro.getJogador();

        lblSaude.setText("❤️ Saúde: " + jog.getSaude());
        lblPosicao.setText("📍 Posição: [" + jog.getX() + ", " + jog.getY() + "]");
        lblDardos.setText("🎯 Munição: " + jog.getQuantidadeDardos());

        modeloInventario.clear();
        for (Item item : jog.getInventario()) {
            modeloInventario.addElement(item.getNome());
        }

        if (jog.getSaude() <= 0) {
            finalizarJogo(false, "GAME OVER! Os dinossauros venceram.");
            return;
        }
        if (tabuleiro.todosDinossaurosDerrotados()) {
            finalizarJogo(true, "VITÓRIA! Você limpou o mapa.");
            return;
        }

        for (int i = 0; i < tam; i++) {
            for (int j = 0; j < tam; j++) {
                JButton botao = botoesGrade[i][j];
                botao.setText("");
                botao.setIcon(null);
                botao.setBackground(null);

                if (modoDebug || tabuleiro.podeVer(i, j) || (i == jog.getX() && j == jog.getY())) {
                    Entidade e = tabuleiro.getEntidade(i, j);
                    CaixaDeSuprimentos caixa = tabuleiro.buscarCaixaEm(i, j);

                    desenharComIconeOuTexto(botao, "chao_visivel.png", "", new Color(144, 238, 144));

                    if (e instanceof Jogador01) {
                        desenharComIconeOuTexto(botao, "jogador.png", "P", Color.BLUE);
                    } else if (e instanceof Parede) {
                        desenharComIconeOuTexto(botao, "parede.png", "###", Color.DARK_GRAY);
                    } else if (e instanceof Dinossauros) {
                        Dinossauros d = (Dinossauros) e;
                        String arquivo = nomeArquivoDoDinossauro(d);
                        desenharComIconeOuTexto(botao, arquivo, "D", Color.RED);
                    } else if (caixa != null) {
                        desenharComIconeOuTexto(botao, "caixa.png", "[?]", Color.ORANGE);
                    }
                } else {
                    desenharComIconeOuTexto(botao, "nevoa.png", "", Color.BLACK);
                }
            }
        }
    }

    private void finalizarJogo(boolean vitoria, String mensagem) {
        pararTudo();

        String[] opcoes = {"Reiniciar Jogo", "Novo Jogo", "Sair"};
        int escolha = JOptionPane.showOptionDialog(
                this,
                mensagem,
                vitoria ? "Vitória!" : "Fim de Jogo",
                JOptionPane.DEFAULT_OPTION,
                vitoria ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.WARNING_MESSAGE,
                null,
                opcoes,
                opcoes[0]);

        dispose();

        switch (escolha) {
            case 0: // Reiniciar Jogo: mesmo mapa e mesma dificuldade
                Main.reiniciarJogo(tabuleiro);
                break;
            case 1: // Novo Jogo: escolhe dificuldade e sorteia mapa novo
                Main.novoJogo();
                break;
            default: // Sair (ou fechou o diálogo sem escolher)
                System.exit(0);
                break;
        }
    }

    private void usarItemSelecionado() {
        String selecionado = listaInventario.getSelectedValue();
        if (selecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um item da lista!");
            return;
        }

        Jogador01 jog = tabuleiro.getJogador();
        Item itemAlvo = null;

        for (Item item : jog.getInventario()) {
            if (item.getNome().equals(selecionado)) {
                itemAlvo = item;
                break;
            }
        }

        if (itemAlvo != null) {
            itemAlvo.aplicarEfeito(jog);
            atualizarVisualizacao();
            requestFocusInWindow(); // Mantém o foco no teclado para continuar andando
        }
    }

    private void processarComandoTeclado(char comando) {
        int dx = 0, dy = 0;
        switch (comando) {
            case 'W': dx = -1; break;
            case 'S': dx = 1; break;
            case 'A': dy = -1; break;
            case 'D': dy = 1; break;
            default: return;
        }

        Jogador01 jog = tabuleiro.getJogador();
        int novoX = jog.getX() + dx;
        int novoY = jog.getY() + dy;

        if (!tabuleiro.ehPosicaoValida(novoX, novoY)) return;

        Entidade alvo = tabuleiro.getEntidade(novoX, novoY);
        CaixaDeSuprimentos caixa = tabuleiro.buscarCaixaEm(novoX, novoY);

        if (alvo instanceof Dinossauros) {
            Item arma = escolherArmaJanela(jog, (Dinossauros) alvo);
            combate.executarAtaque(jog, (Dinossauros) alvo, arma);
            if (alvo.getSaude() <= 0) {
                tabuleiro.removerEntidade(novoX, novoY);
            }
        } else if (caixa != null) {
            caixa.abrir(jog);
            tabuleiro.removerCaixa(caixa);
            tabuleiro.moverJogador(novoX, novoY);
        } else {
            tabuleiro.moverJogador(novoX, novoY);
        }

        atualizarVisualizacao();
    }

    private Item escolherArmaJanela(Jogador01 jogador, Dinossauros alvo) {
        String[] opcoes = {"Mãos", "Bastão Elétrico", "Dardos"};
        int escolha = JOptionPane.showOptionDialog(this,
                alvo.getClass().getSimpleName() + " apareceu! Com o que vai atacar?",
                "Combate",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
                null, opcoes, opcoes[0]);

        if (escolha == 1 && jogador.temBastaoEletrico()) {
            for (Item i : jogador.getInventario()) {
                if (i instanceof pacoteBase.item.BastaoEletrico) return i;
            }
        } else if (escolha == 2 && jogador.getQuantidadeDardos() > 0) {
            for (Item i : jogador.getInventario()) {
                if (i instanceof pacoteBase.item.Dardos) return i;
            }
        }
        return new Maos();
    }
}