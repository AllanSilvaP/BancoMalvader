package model;

public class UsuarioInfo {
    private int idUsuario;
    private String nome;
    private String senha;
    private boolean isFuncionario;

    // Construtor
    public UsuarioInfo(int idUsuario, String nome, String senha, boolean isFuncionario) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.senha = senha;
        this.isFuncionario = isFuncionario;
    }

    // Getters
    public int getIdUsuario() {
        return idUsuario;
    }

    public String getNome() {
        return nome;
    }

    public String getSenha() {
        return senha;
    }

    public boolean isFuncionario() {
        return isFuncionario;
    }

    public boolean isCliente() {
        return !isFuncionario;
    }
}
