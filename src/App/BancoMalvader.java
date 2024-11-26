package App;

import javax.swing.*;
import controller.ClienteController;
import controller.ContaController;
import controller.FuncionarioController;
import controller.BancoController;
import service.Autenticacao;
import service.ContaService;
import DAO.FuncionarioDAO;
import DAO.ClienteDAO;
import DAO.ContaDAO;
import DAO.UsuarioDAO;
import View.LoginView;
import View.MenuClienteView;
import View.MenuFuncionarioView;

import java.sql.SQLException;

public class BancoMalvader {

    private Autenticacao autenticacaoService;
    private FuncionarioController funcionarioController;
    private ClienteController clienteController;
    private ContaController contaController;
    private ContaService contaService;

    // Construtor com injeção de dependência correta
    public BancoMalvader(Autenticacao autenticacaoService,
                         FuncionarioController funcionarioController,
                         ClienteController clienteController,
                         ContaController contaController,
                         ContaService contaService) {
        this.autenticacaoService = autenticacaoService;
        this.funcionarioController = funcionarioController;
        this.clienteController = clienteController;
        this.contaController = contaController;
        this.contaService = contaService;
    }

    public static void main(String[] args) throws SQLException {
        // Criando instâncias de DAOs
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ContaDAO contaDAO = new ContaDAO();
        ClienteDAO clienteDAO = new ClienteDAO();
        ContaService contaService = new ContaService();

        // Criando as instâncias dos controladores com os DAOs necessários
        FuncionarioController funcionarioController = new FuncionarioController(funcionarioDAO, clienteDAO, contaDAO);
        ClienteController clienteController = new ClienteController(clienteDAO);
        ContaController contaController = new ContaController(contaService, contaDAO);

        // Criando o BancoMalvader com injeção de dependências
        BancoMalvader banco = new BancoMalvader(
            new Autenticacao(usuarioDAO), // Passando o UsuarioDAO para a Autenticacao
            funcionarioController,
            clienteController,
            contaController,
            contaService
        );

        banco.iniciarSistema();
    }

    public void iniciarSistema() throws SQLException {
        LoginView loginView = new LoginView();
        loginView.setVisible(true);
    }

    public void autenticarUsuario(String tipoUsuario) throws SQLException {
        try {
            String cpf = JOptionPane.showInputDialog("Digite o CPF do " + tipoUsuario + ":");
            String senha = JOptionPane.showInputDialog("Digite a senha do " + tipoUsuario + ":");

            if (autenticacaoService.autenticarUsuario(cpf, senha)) {
                // Se o tipo for funcionário
                if (tipoUsuario.equals("funcionário")) {
                    BancoController bancoController = new BancoController();

                    new MenuFuncionarioView(bancoController, funcionarioController, contaService).setVisible(true);
                } else {
                    // Se for cliente
                    new MenuClienteView().setVisible(true);
                }
            } else {
                JOptionPane.showMessageDialog(null, tipoUsuario + " não encontrado ou senha incorreta.");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "CPF inválido. Verifique o formato.");
        }
    }
}
