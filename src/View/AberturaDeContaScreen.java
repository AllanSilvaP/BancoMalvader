package View;

import javax.swing.*;
import controller.BancoController;
import model.Conta;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AberturaDeContaScreen extends JFrame {

    private static final long serialVersionUID = 1L;

    public AberturaDeContaScreen(BancoController bancoController) {
        setTitle("Abertura de Conta");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));
        add(panel);

        // Campos do cliente
        JLabel nomeLabel = new JLabel("Nome do Cliente:");
        JTextField nomeField = new JTextField();
        panel.add(nomeLabel);
        panel.add(nomeField);

        JLabel cpfLabel = new JLabel("CPF do Cliente:");
        JTextField cpfField = new JTextField();
        panel.add(cpfLabel);
        panel.add(cpfField);

        JLabel enderecoLabel = new JLabel("Endereço (Local, Nº, Bairro, Cidade, Estado - CEP):");
        JTextField enderecoField = new JTextField();
        panel.add(enderecoLabel);
        panel.add(enderecoField);

        // Campos da conta
        JLabel numeroContaLabel = new JLabel("Número da Conta:");
        JTextField numeroContaField = new JTextField();  // Mudando para JTextField (String)
        panel.add(numeroContaLabel);
        panel.add(numeroContaField);

        JLabel saldoLabel = new JLabel("Saldo Inicial:");
        JTextField saldoField = new JTextField();
        panel.add(saldoLabel);
        panel.add(saldoField);

        JLabel tipoContaLabel = new JLabel("Tipo de Conta:");
        JComboBox<String> tipoContaCombo = new JComboBox<>(new String[]{"Corrente", "Poupança"});
        panel.add(tipoContaLabel);
        panel.add(tipoContaCombo);

        JButton confirmarButton = new JButton("Confirmar");
        panel.add(new JLabel());
        panel.add(confirmarButton);

        confirmarButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    // Captura os dados do formulário
                    String cpfCliente = cpfField.getText();
                    String numeroContaText = numeroContaField.getText(); // Alterando para String
                    String saldoInicialText = saldoField.getText();
                    String tipoConta = (String) tipoContaCombo.getSelectedItem();

                    // Validações
                    if (cpfCliente.isEmpty() || numeroContaText.isEmpty() || saldoInicialText.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "Todos os campos devem ser preenchidos!");
                        return;
                    }

                    if (!isValidCPF(cpfCliente)) {
                        JOptionPane.showMessageDialog(null, "CPF inválido!");
                        return;
                    }

                    if (!bancoController.isCpfCadastrado(cpfCliente)) {
                        JOptionPane.showMessageDialog(null, "Cliente não encontrado!");
                        return;
                    }

                    double saldoInicial = Double.parseDouble(saldoInicialText);

                    // Cria a nova conta com número de conta como String
                    Conta novaConta = new Conta(numeroContaText, "1234", saldoInicial, tipoConta, 0);

                    // Chama o controlador para cadastrar a conta
                    bancoController.cadastrarConta(novaConta, cpfCliente);

                    JOptionPane.showMessageDialog(null, "Conta criada com sucesso!");
                    dispose();

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Erro ao criar conta: " + ex.getMessage());
                }
            }
        });
    }

    // Validação simples de CPF
    private boolean isValidCPF(String cpf) {
        return cpf != null && cpf.length() == 11 && cpf.matches("[0-9]+");
    }
}
