package View;

import javax.swing.*;

import DAO.ClienteDAO;
import DAO.UsuarioDAO;
import controller.BancoController;
import exception.ValorInvalidoException;
import model.Cliente;
import model.Conta;
import model.Transacao;
import service.Autenticacao;

import java.sql.SQLException;
import java.util.List;

public class MenuClienteView extends JFrame {

    private static final long serialVersionUID = 1L;
    private BancoController bancoController;
    private Cliente clienteAutenticado;  // Cliente autenticado
    private Conta contaAutenticada;      // Conta do cliente autenticado

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

    public void autenticarCliente(String cpf, String senha) {
        try {
            Autenticacao autenticacao = new Autenticacao(new UsuarioDAO(), new ClienteDAO());
            System.out.println("Tentando autenticar o cliente com CPF: " + cpf);  // Depuração
            clienteAutenticado = autenticacao.autenticarCliente(cpf, senha);

            if (clienteAutenticado != null) {
                // Verifica se o cliente tem contas associadas
                if (!clienteAutenticado.getContas().isEmpty()) {
                    contaAutenticada = clienteAutenticado.getContas().get(0); // Pega a primeira conta associada
                    JOptionPane.showMessageDialog(this, 
                        "Login bem-sucedido! Conta número: " + contaAutenticada.getNumeroConta());
                    exibirMenu();  // Exibe o menu após autenticar
                } else {
                    JOptionPane.showMessageDialog(this, "Nenhuma conta associada ao cliente.");
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage());
        }
    }

    public void exibirMenu() {
        setVisible(true);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        // Botão Saldo
        JButton saldoButton = new JButton("Saldo");
        saldoButton.setBounds(10, 30, 200, 25);
        panel.add(saldoButton);

        saldoButton.addActionListener(e -> {
            if (contaAutenticada != null) {
                double saldo = bancoController.consultarSaldo(contaAutenticada.getNumeroConta());
                JOptionPane.showMessageDialog(null, "Saldo: R$ " + saldo);
            } else {
                JOptionPane.showMessageDialog(null, "Conta não autenticada.");
            }
        });

        // Botão Depósito
        JButton depositoButton = new JButton("Depósito");
        depositoButton.setBounds(10, 70, 200, 25);
        panel.add(depositoButton);

        depositoButton.addActionListener(e -> {
            if (contaAutenticada != null) {
                String valorStr = JOptionPane.showInputDialog("Digite o valor do depósito:");

                try {
                    double valor = Double.parseDouble(valorStr);
                    bancoController.realizarDeposito(contaAutenticada.getNumeroConta(), valor);
                    JOptionPane.showMessageDialog(null, "Depósito realizado com sucesso.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Valor inválido.");
                } catch (ValorInvalidoException ex) {
                    JOptionPane.showMessageDialog(null, "Erro: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Conta não autenticada.");
            }
        });

        // Botão Saque
        JButton saqueButton = new JButton("Saque");
        saqueButton.setBounds(10, 110, 200, 25);
        panel.add(saqueButton);

        saqueButton.addActionListener(e -> {
            if (contaAutenticada != null) {
                String valorStr = JOptionPane.showInputDialog("Digite o valor do saque:");

                try {
                    double valor = Double.parseDouble(valorStr);
                    bancoController.realizarSaque(contaAutenticada.getNumeroConta(), valor);
                    JOptionPane.showMessageDialog(null, "Saque realizado com sucesso.");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Valor inválido.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Conta não autenticada.");
            }
        });

        // Botão Extrato
        JButton extratoButton = new JButton("Extrato");
        extratoButton.setBounds(10, 150, 200, 25);
        panel.add(extratoButton);

        extratoButton.addActionListener(e -> {
            if (contaAutenticada != null) {
                List<Transacao> transacoes = bancoController.getExtrato(contaAutenticada.getNumeroConta());

                if (transacoes.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Nenhuma transação encontrada para esta conta.");
                } else {
                    StringBuilder extrato = new StringBuilder("Extrato da Conta " + contaAutenticada.getNumeroConta() + ":\n\n");

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
            } else {
                JOptionPane.showMessageDialog(null, "Conta não autenticada.");
            }
        });

        // Botão Sair
        JButton sairButton = new JButton("Sair");
        sairButton.setBounds(10, 230, 200, 25);
        panel.add(sairButton);

        sairButton.addActionListener(e -> System.exit(0));
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuClienteView frame = new MenuClienteView();
            frame.setVisible(true);
        });
    }
}
