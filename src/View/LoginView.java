package View;

import javax.swing.*;
import service.Autenticacao; // Certifique-se de que o caminho está correto
import DAO.FuncionarioDAO;
import DAO.UsuarioDAO;
import DAO.ClienteDAO;
import DAO.ContaDAO;
import controller.BancoController;
import controller.FuncionarioController;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginView extends JFrame {

    private static final long serialVersionUID = 1L;
    private JTextField cpfText;
    private JPasswordField passwordText;
    private Autenticacao autenticacao;
    private static final Logger logger = Logger.getLogger(LoginView.class.getName());
    private final BancoController bancoController;
    private final FuncionarioController funcionarioController;

    public LoginView() {
    	
    	FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ContaDAO contaDAO = new ContaDAO();
        // Inicializa os controladores
        bancoController = new BancoController();
        funcionarioController = new FuncionarioController(funcionarioDAO, contaDAO);

        // Instância da classe de autenticação
        autenticacao = new Autenticacao(new UsuarioDAO(), new ClienteDAO());

        setTitle("Banco Malvader - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        add(panel);
        placeComponents(panel);
    }

    private void placeComponents(JPanel panel) {
        panel.setLayout(null);

        JLabel cpfLabel = new JLabel("CPF:");
        cpfLabel.setBounds(10, 20, 80, 25);
        panel.add(cpfLabel);

        cpfText = new JTextField(20);
        cpfText.setBounds(100, 20, 165, 25);
        panel.add(cpfText);

        JLabel passwordLabel = new JLabel("Senha:");
        passwordLabel.setBounds(10, 50, 80, 25);
        panel.add(passwordLabel);

        passwordText = new JPasswordField(20);
        passwordText.setBounds(100, 50, 165, 25);
        panel.add(passwordText);

        JButton loginFuncionarioButton = new JButton("Login Funcionário");
        loginFuncionarioButton.setBounds(10, 80, 150, 25);
        panel.add(loginFuncionarioButton);

        loginFuncionarioButton.addActionListener(e -> realizarLogin(true)); // true para funcionário

        JButton loginClienteButton = new JButton("Login Cliente");
        loginClienteButton.setBounds(170, 80, 150, 25);
        panel.add(loginClienteButton);

        loginClienteButton.addActionListener(e -> realizarLogin(false)); // false para cliente

        JButton cadastrarButton = new JButton("Cadastrar");
        cadastrarButton.setBounds(100, 120, 150, 25);
        panel.add(cadastrarButton);

        cadastrarButton.addActionListener(e -> abrirTelaCadastro());
    }

    private void realizarLogin(boolean isFuncionario) {
        try {
            String cpf = cpfText.getText();
            String senhaFornecida = new String(passwordText.getPassword());

            if (cpf.isEmpty() || senhaFornecida.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.");
                return;
            }

            // Autenticar usuário e determinar se é funcionário ou cliente
            boolean autenticado = autenticacao.autenticarUsuario(cpf, senhaFornecida);

            if (autenticado) {
                JOptionPane.showMessageDialog(this, isFuncionario
                        ? "Login de Funcionário bem-sucedido!"
                        : "Login de Cliente bem-sucedido!");

                // Abrir menu correspondente
                if (isFuncionario) {
                    new MenuFuncionarioView(bancoController, funcionarioController).setVisible(true);
                } else {
                    new MenuClienteView().setVisible(true);
                }

                this.dispose(); // Fecha a tela de login
            } else {
                JOptionPane.showMessageDialog(this, "CPF ou senha incorretos.");
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Erro inesperado durante o login", ex);
            JOptionPane.showMessageDialog(this, "Erro inesperado. Tente novamente mais tarde.");
        }
    }

    private void abrirTelaCadastro() {
        // Abre a tela de cadastro de usuário
        new CadastroContaFrame().setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginView frame = new LoginView();
            frame.setVisible(true);
        });
    }
}