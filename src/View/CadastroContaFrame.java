package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CadastroContaFrame extends JFrame {
    private static final long serialVersionUID = 1L;

    private JTextField nomeField;
    private JTextField cpfField;
    private JTextField telefoneField;
    private JComboBox<String> tipoUsuarioCombo;
    private JPasswordField senhaField;
    private JButton cadastrarButton;

    public CadastroContaFrame() {
        setTitle("Cadastro de Conta");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        nomeField = new JTextField(20);
        cpfField = new JTextField(11);
        telefoneField = new JTextField(15);
        tipoUsuarioCombo = new JComboBox<>(new String[]{"CLIENTE", "FUNCIONARIO"});
        senhaField = new JPasswordField(20);
        cadastrarButton = new JButton("Cadastrar");

        // Configurando layout
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Nome:"));
        panel.add(nomeField);

        panel.add(new JLabel("CPF:"));
        panel.add(cpfField);

        panel.add(new JLabel("Telefone:"));
        panel.add(telefoneField);

        panel.add(new JLabel("Tipo de Usuário:"));
        panel.add(tipoUsuarioCombo);

        panel.add(new JLabel("Senha:"));
        panel.add(senhaField);

        panel.add(new JLabel());
        panel.add(cadastrarButton);

        add(panel);

        // Ação do botão cadastrar
        cadastrarButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cadastrarUsuario();
            }
        });
    }

    private void cadastrarUsuario() {
        // Obter dados preenchidos
        String nome = nomeField.getText().trim();
        String cpf = cpfField.getText().trim();
        String telefone = telefoneField.getText().trim();
        String senha = new String(senhaField.getPassword());
        String tipoUsuario = (String) tipoUsuarioCombo.getSelectedItem();

        if (nome.isEmpty() || cpf.isEmpty() || telefone.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Preencha todos os campos!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Lógica para cadastro do cliente ou funcionário sem conta bancária
        if ("FUNCIONARIO".equals(tipoUsuario)) {
            new CadastroFuncionarioFrame(null, nome, cpf, telefone).setVisible(true);
        } else if ("CLIENTE".equals(tipoUsuario)) {
            new CadastroClienteFrame().setVisible(true);
        }

        // Fechar a tela de cadastro de conta
        this.dispose();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CadastroContaFrame().setVisible(true));
    }
}