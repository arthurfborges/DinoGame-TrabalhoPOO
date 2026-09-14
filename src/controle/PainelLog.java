package controle;

import javax.swing.*;
import java.awt.*;

public class PainelLog extends JScrollPane {
    private JTextArea areaTexto;

    public PainelLog() {
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font("Monospaced", Font.PLAIN, 12));
        areaTexto.setBackground(new Color(30, 30, 30)); // Estilo terminal
        areaTexto.setForeground(Color.GREEN);           // Estilo terminal

        this.setPreferredSize(new Dimension(0, 150));
        this.setViewportView(areaTexto);
        this.setBorder(BorderFactory.createTitledBorder("Log de Eventos"));
    }

    public void adicionarMensagem(String mensagem) {
        areaTexto.append("> " + mensagem + "\n");
        areaTexto.setCaretPosition(areaTexto.getDocument().getLength());
    }

    public void limpar() {
        areaTexto.setText("");
    }
}