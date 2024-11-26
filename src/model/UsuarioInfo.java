package model;
public class UsuarioInfo {
	
    private final int idUsuario; // Add the idUsuario field
    private final String senhaHash;
    private final boolean isFuncionario;
    private final boolean isCliente;

    public UsuarioInfo(int idUsuario, String senhaHash, boolean isFuncionario, boolean isCliente) {
        this.idUsuario = idUsuario;
        this.senhaHash = senhaHash;
        this.isFuncionario = isFuncionario;
        this.isCliente = isCliente;
    }

    public int getIdUsuario() {
        return idUsuario;
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
