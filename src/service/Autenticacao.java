package service;

import org.mindrot.jbcrypt.BCrypt;
import DAO.UsuarioDAO;
import DAO.ClienteDAO;
import model.Cliente;
import model.Conta;
import model.UsuarioInfo;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Autenticacao {
    private final UsuarioDAO usuarioDAO;
    private final ClienteDAO clienteDAO;
    private static final Logger logger = Logger.getLogger(Autenticacao.class.getName());

    // Construtor com ambos os DAOs
    public Autenticacao(UsuarioDAO usuarioDAO, ClienteDAO clienteDAO) {
        this.usuarioDAO = usuarioDAO;
        this.clienteDAO = clienteDAO;
    }

    // Método para autenticar um usuário pelo CPF e senha
    public boolean autenticarUsuario(String cpf, String senhaFornecida) {
        try {
            // Recupera os dados do usuário a partir do CPF
            UsuarioInfo usuarioInfo = usuarioDAO.buscarUsuarioPorCpf(cpf);

            if (usuarioInfo != null) {
                // Valida a senha fornecida contra o hash armazenado
                if (BCrypt.checkpw(senhaFornecida, usuarioInfo.getSenhaHash())) {
                    if (usuarioInfo.isFuncionario()) {
                        System.out.println("Usuário autenticado como funcionário.");
                    } else if (usuarioInfo.isCliente()) {
                        System.out.println("Usuário autenticado como cliente.");
                    }
                    return true; // Sucesso na autenticação
                } else {
                    logger.log(Level.WARNING, "Senha incorreta para o CPF: " + cpf);
                }
            } else {
                logger.log(Level.WARNING, "Usuário não encontrado para CPF: " + cpf);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao autenticar o usuário com CPF: " + cpf, e);
        }
        return false; // Falha na autenticação
    }

    public Cliente autenticarCliente(String cpf, String senha) throws SQLException {
        // Primeiro, buscar o usuário pelo CPF
        UsuarioInfo usuarioInfo = usuarioDAO.buscarUsuarioPorCpf(cpf);

        // Verifica se o usuário foi encontrado
        if (usuarioInfo != null) {
            // Verifica a senha fornecida contra o hash armazenado
            if (BCrypt.checkpw(senha, usuarioInfo.getSenhaHash())) {
                // Agora, buscamos o cliente (relacionado ao usuário)
                Cliente cliente = clienteDAO.buscarClientePorIdUsuario(usuarioInfo.getIdUsuario());

                if (cliente != null) {
                    // Se o cliente for encontrado, buscamos as contas associadas ao cliente
                    List<Conta> contas = clienteDAO.buscarContasPorCliente(cliente.getId_cliente());

                    if (!contas.isEmpty()) {
                        cliente.setContas(contas); // Armazena as contas no cliente
                        System.out.println("Cliente autenticado com sucesso! Conta número: " + contas.get(0).getNumeroConta());
                        return cliente; // Retorna o cliente com suas contas
                    } else {
                        System.out.println("Cliente encontrado, mas sem contas associadas.");
                    }
                } else {
                    System.out.println("Cliente não encontrado para o usuário.");
                }
            } else {
                System.out.println("Senha incorreta para o CPF: " + cpf);
            }
        } else {
            System.out.println("Usuário não encontrado para o CPF: " + cpf);
        }

        throw new IllegalArgumentException("CPF ou senha inválidos!");
    }


}

