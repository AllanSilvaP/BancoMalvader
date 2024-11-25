package model;

public class UsuarioInfo {
    private final String senhaHash;
    private final boolean isFuncionario;
    private final boolean isCliente;

    public UsuarioInfo(String senhaHash, boolean isFuncionario, boolean isCliente) {
        this.senhaHash = senhaHash;
        this.isFuncionario = isFuncionario;
        this.isCliente = isCliente;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public boolean isFuncionario() {
        return isFuncionario;
    }

    public boolean isCliente() {
        return isCliente;
    }
}