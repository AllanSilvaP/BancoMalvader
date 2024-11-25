package View;

import javax.swing.*;
import controller.BancoController;
import exception.ValorInvalidoException;
import model.Transacao;  // Certifique-se de importar a classe Transacao
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class MenuClienteView extends JFrame {

    private static final long serialVersionUID = 1L;
    private BancoController bancoController;

    public MenuClienteView() {
        bancoController = new BancoController();

        setTitle("Menu Cliente");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    public void exibirMenu() {
        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JButton saldoButton = new JButton("Saldo");
        saldoButton.setBounds(10, 30, 200, 25);
        panel.add(saldoButton);

        saldoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
                String numeroConta = contaStr;  
                double saldo = bancoController.consultarSaldo(numeroConta);
                if (saldo >= 0) {
                    JOptionPane.showMessageDialog(null, "Saldo: " + saldo);
                } else {
                    JOptionPane.showMessageDialog(null, "Conta não encontrada.");
                }
            }
        });

        JButton depositoButton = new JButton("Depósito");
        depositoButton.setBounds(10, 70, 200, 25);
        panel.add(depositoButton);

        depositoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
                String numeroConta = contaStr; 
                String valorStr = JOptionPane.showInputDialog("Digite o valor do depósito:");
                double valor = Double.parseDouble(valorStr);
                try {
                    bancoController.realizarDeposito(numeroConta, valor);
                } catch (ValorInvalidoException e1) {
                    e1.printStackTrace();
                } 
            }
        });

        JButton saqueButton = new JButton("Saque");
        saqueButton.setBounds(10, 110, 200, 25);
        panel.add(saqueButton);

        saqueButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
                String numeroConta = contaStr;  
                String valorStr = JOptionPane.showInputDialog("Digite o valor do saque:");
                double valor = Double.parseDouble(valorStr);
                bancoController.realizarSaque(numeroConta, valor);  
            }
        });

        JButton extratoButton = new JButton("Extrato");
        extratoButton.setBounds(10, 150, 200, 25);
        panel.add(extratoButton);

        extratoButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String contaStr = JOptionPane.showInputDialog("Digite o número da conta:");
                String numeroConta = contaStr;

                if (numeroConta != null && !numeroConta.isEmpty()) {
                    try {
                        List<Transacao> transacoes = bancoController.getExtrato(numeroConta);

                        if (transacoes.isEmpty()) {
                            JOptionPane.showMessageDialog(null, "Nenhuma transação encontrada para esta conta.");
                        } else {
                            StringBuilder extrato = new StringBuilder("Extrato da Conta " + numeroConta + ":\n\n");

                            for (Transacao transacao : transacoes) {
                                extrato.append("Data: ").append(transacao.getDataHora()).append("\n");
                                extrato.append("Tipo: ").append(transacao.getTipoTransacao()).append("\n");
                                extrato.append("Valor: R$ ").append(transacao.getValor()).append("\n\n");
                            }

                            JTextArea textArea = new JTextArea(extrato.toString());
                            textArea.setEditable(false); 
                            JScrollPane scrollPane = new JScrollPane(textArea);
                            JOptionPane.showMessageDialog(null, scrollPane, "Extrato", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(null, "Erro ao carregar o extrato: " + ex.getMessage());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Número da conta inválido.");
                }
            }
        });

        JButton sairButton = new JButton("Sair");
        sairButton.setBounds(10, 230, 200, 25);
        panel.add(sairButton);

        sairButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0); 
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuClienteView frame = new MenuClienteView();
            frame.setVisible(true);
        });
    }
}
