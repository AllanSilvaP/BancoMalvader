package App;

import javax.swing.*;
import controller.ClienteController;
import controller.ContaController;
import controller.FuncionarioController;
import service.Autenticacao;
import DAO.FuncionarioDAO;
import DAO.ContaDAO; 
import View.LoginView;
import View.MenuClienteView;
import View.MenuFuncionarioView;

import java.sql.SQLException;

public class BancoMalvader {

    private Autenticacao autenticacaoService;

    public BancoMalvader(ContaController contaController, ClienteController clienteController, FuncionarioController funcionarioController, Autenticacao autenticacaoService) {
        this.autenticacaoService = autenticacaoService;
    }

    public static void main(String[] args) throws SQLException {
        // Criando instâncias de DAOs
        FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
        ContaDAO contaDAO = new ContaDAO();

        // Criando o BancoMalvader com injeção de dependências correta
        BancoMalvader banco = new BancoMalvader(
            new ContaController(),
            new ClienteController(),
            new FuncionarioController(funcionarioDAO, contaDAO), // Passando instâncias corretas
            new Autenticacao(null, null)
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
                if (tipoUsuario.equals("funcionário")) {
                    FuncionarioDAO funcionarioDAO = new FuncionarioDAO();
                    ContaDAO contaDAO = new ContaDAO();
                    FuncionarioController funcionarioController = new FuncionarioController(funcionarioDAO, contaDAO);

                    new MenuFuncionarioView(null, funcionarioController).setVisible(true);
                } else {
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
