package service;

import DAO.UsuarioDAO;
import model.UsuarioInfo;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Autenticacao {
    private final UsuarioDAO usuarioDAO;
    private static final Logger logger = Logger.getLogger(Autenticacao.class.getName());

    public Autenticacao() {
        this(new UsuarioDAO());
    }

    public Autenticacao(UsuarioDAO usuarioDAO) {
        this.usuarioDAO = usuarioDAO;
    }

    public boolean autenticarUsuario(String cpf, String senhaFornecida) {
        try {
            UsuarioInfo usuarioInfo = usuarioDAO.buscarUsuarioPorCpf(cpf);
            
            if (usuarioInfo != null) {
                if (senhaFornecida.equals(usuarioInfo.getSenha())) {  // Comparação simples de senha em texto claro
                    if (usuarioInfo.isFuncionario()) {
                        System.out.println("Usuário autenticado como funcionário.");
                    } else if (usuarioInfo.isCliente()) {
                        System.out.println("Usuário autenticado como cliente.");
                    }
                    return true;
                } else {
                    logger.log(Level.WARNING, "Senha incorreta para o CPF: " + cpf);
                }
            } else {
                logger.log(Level.WARNING, "Usuário não encontrado para CPF: " + cpf);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erro ao autenticar o usuário com CPF: " + cpf, e);
        }
        return false;
    }
}
