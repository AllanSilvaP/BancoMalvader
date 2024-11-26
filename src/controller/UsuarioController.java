package controller;

import model.Usuario;

import java.sql.SQLException;

import DAO.UsuarioDAO;

public class UsuarioController {

    public int cadastrarUsuario(Usuario usuario) throws SQLException {
        UsuarioDAO usuarioDAO = new UsuarioDAO();
        return usuarioDAO.salvarUsuario(usuario); // Retorna o resultado da operação
    }
}