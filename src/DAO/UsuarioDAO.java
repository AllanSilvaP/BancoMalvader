package DAO;

import model.Usuario;
import model.UsuarioInfo;
import model.Funcionario;
import model.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDAO {
    private static final System.Logger logger = System.getLogger(UsuarioDAO.class.getName());
    private Connection connection;

    public UsuarioDAO() {
        try {
            // Usando a ConnectionFactory para obter a conexão
            this.connection = ConnectionFactory.getConnection();
            this.connection.setAutoCommit(false); // Inicia a transação manualmente
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao conectar com o banco de dados", e);
            throw new RuntimeException("Erro ao conectar com o banco de dados", e);
        }
    }

    private Connection getConnection() {
        return this.connection;
    }
    
 // Buscar informações completas do usuário por ID
    public Usuario buscarUsuarioPorId(int idUsuario) {
        String sql = """
            SELECT u.id_usuario, u.nome, u.cpf, u.data_nascimento, u.telefone, u.tipo_usuario, u.senha,
                   e.local, e.numero_casa, e.bairro, e.cidade, e.estado, e.cep
            FROM usuario u
            LEFT JOIN endereco e ON e.id_usuario = u.id_usuario
            WHERE u.id_usuario = ?
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Usuario usuario = new Funcionario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNome(rs.getString("nome"));
                    usuario.setCpf(rs.getString("cpf"));
                    usuario.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                    usuario.setTelefone(rs.getString("telefone"));
                    usuario.setSenha(rs.getString("senha"));

                    // Verificar e preencher endereço, se disponível
                    Endereco endereco = new Endereco();
                    endereco.setLocal(rs.getString("local"));
                    endereco.setNumeroCasa(rs.getInt("numero_casa"));
                    endereco.setBairro(rs.getString("bairro"));
                    endereco.setCidade(rs.getString("cidade"));
                    endereco.setEstado(rs.getString("estado"));
                    endereco.setCep(rs.getString("cep"));
                    usuario.setEndereco(endereco);

                    return usuario;
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao buscar informações do usuário por ID", e);
        }
        return null;
    }

    // Listar todos os usuários
    public List<Usuario> listarUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT * FROM usuario";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario = new Funcionario();
                usuario.setId(rs.getInt("id_usuario"));
                usuario.setNome(rs.getString("nome"));
                usuario.setCpf(rs.getString("cpf"));
                usuario.setDataNascimento(rs.getDate("data_nascimento").toLocalDate());
                usuario.setTelefone(rs.getString("telefone"));
                usuario.setSenha(rs.getString("senha"));
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao listar todos os usuários", e);
        }

        return usuarios;
    }

    // Validar CPF e senha
    public boolean validarCpfESenha(String cpf, String senha) {
        String sql = "SELECT senha FROM usuario WHERE cpf = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHash = rs.getString("senha");
                    return BCrypt.checkpw(senha, senhaHash); // Verifica a senha com o hash armazenado
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao validar CPF e senha", e);
        }
        return false;
    }


    // Método para verificar se o CPF já existe no banco de dados
    public boolean cpfExiste(String cpf) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE cpf = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao verificar se o CPF existe", e);
        }
        return false;
    }

    // Buscar usuário por CPF
    public UsuarioInfo buscarUsuarioPorCpf(String cpf) {
        String sql = """
            SELECT u.senha, 
                   CASE WHEN f.id_funcionario IS NOT NULL THEN 1 ELSE 0 END AS is_funcionario,
                   CASE WHEN c.id_cliente IS NOT NULL THEN 1 ELSE 0 END AS is_cliente
            FROM usuario u
            LEFT JOIN funcionario f ON f.id_usuario = u.id_usuario
            LEFT JOIN cliente c ON c.id_usuario = u.id_usuario
            WHERE u.cpf = ? 
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaHash = rs.getString("senha");
                    boolean isFuncionario = rs.getInt("is_funcionario") == 1;
                    boolean isCliente = rs.getInt("is_cliente") == 1;

                    // Retorna o objeto UsuarioInfo com todos os dados necessários
                    return new UsuarioInfo(senhaHash, isFuncionario, isCliente);
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao buscar usuário por CPF", e);
        }
        return null; // Retorna null se o usuário não for encontrado
    }

    // Salvar um novo usuário e retornar o ID do usuário
 // Salvar um novo usuário e retornar o ID do usuário
    public int salvarUsuario(Usuario usuario) throws SQLException {
        if (cpfExiste(usuario.getCpf())) {
            System.out.println("Erro: CPF já cadastrado.");
            return -1; // Indica que o CPF já existe
        }

        String sql = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, senha, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?)";
        String senhaHash = usuario.getSenhaHash();

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setDate(3, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, senhaHash);
            stmt.setString(6, usuario instanceof Funcionario ? "FUNCIONARIO" : "CLIENTE");

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int idGerado = rs.getInt(1);

                        // Associe o ID do usuário ao endereço
                        if (usuario.getEndereco() != null) {
                            salvarEndereco(usuario.getEndereco(), idGerado); // Salva o endereço do usuário
                        }

                        // NEW: Additional handling based on user type
                        if (usuario instanceof Funcionario) {
                            salvarFuncionario((Funcionario) usuario, idGerado);
                        } else {
                            salvarCliente(idGerado);
                        }

                        getConnection().commit(); // Commit da transação
                        return idGerado; // Retorna o ID do usuário gerado
                    }
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao salvar o usuário: " + usuario.getCpf(), e);
            getConnection().rollback(); // Rollback em caso de erro
        }

        return -1; // Retorna -1 se algo deu errado
    }

    // Método para salvar o cliente
    private void salvarCliente(int idUsuario) throws SQLException {
        String sql = "INSERT INTO cliente (id_usuario) VALUES (?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            stmt.executeUpdate();
        }
    }

    // Método para salvar o funcionário
    private void salvarFuncionario(Funcionario funcionario, int idUsuario) throws SQLException {
        String sql = "INSERT INTO funcionario (codigo_funcionario, cargo, id_usuario) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setLong(1, funcionario.getCodigoFuncionario());
            stmt.setString(2, funcionario.getCargo());
            stmt.setInt(3, idUsuario);
            stmt.executeUpdate();
        }
    }

    // Salvar o endereço do usuário
    private void salvarEndereco(Endereco endereco, int idUsuario) throws SQLException {
        String sql = "INSERT INTO endereco (local, numero_casa, bairro, cidade, estado, cep, id_usuario) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, endereco.getLocal());
            stmt.setInt(2, endereco.getNumeroCasa());
            stmt.setString(3, endereco.getBairro());
            stmt.setString(4, endereco.getCidade());
            stmt.setString(5, endereco.getEstado());
            stmt.setString(6, endereco.getCep());
            stmt.setInt(7, idUsuario);

            stmt.executeUpdate();
        }
    }

    // Atualizar senha de um usuário
    public void salvarSenha(int idUsuario, String senhaHash) {
        String sql = "UPDATE usuario SET senha = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, senhaHash);
            stmt.setInt(2, idUsuario);
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected == 0) {
                logger.log(System.Logger.Level.WARNING, "Nenhum usuário encontrado para atualizar a senha.");
            }
            getConnection().commit(); // Commit da transação
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao salvar senha para o usuário: " + idUsuario, e);
            try {
                getConnection().rollback(); // Rollback em caso de erro
            } catch (SQLException rollbackEx) {
                logger.log(System.Logger.Level.ERROR, "Erro ao realizar rollback", rollbackEx);
            }
        }
    }

    // Atualizar um usuário
    public void atualizarUsuario(Usuario usuario) {
        String sql = "UPDATE usuario SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, senha = ?, tipo_usuario = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getSenhaHash());
            stmt.setString(6, usuario instanceof Funcionario ? "FUNCIONARIO" : "CLIENTE");
            stmt.setInt(7, usuario.getId());
            stmt.executeUpdate();

            if (usuario.getEndereco() != null) {
                // Atualizar o endereço
                atualizarEndereco(usuario.getEndereco(), usuario.getId());
            }

            getConnection().commit(); // Commit da transação
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao atualizar o usuário no banco.", e);
            try {
                getConnection().rollback(); // Rollback em caso de erro
            } catch (SQLException rollbackEx) {
                logger.log(System.Logger.Level.ERROR, "Erro ao realizar rollback", rollbackEx);
            }
        }
    }

    // Atualizar o endereço do usuário
    private void atualizarEndereco(Endereco endereco, int idUsuario) throws SQLException {
        String sql = "UPDATE endereco SET local = ?, numero_casa = ?, bairro = ?, cidade = ?, estado = ?, cep = ? WHERE id_usuario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, endereco.getLocal());
            stmt.setInt(2, endereco.getNumeroCasa());
            stmt.setString(3, endereco.getBairro());
            stmt.setString(4, endereco.getCidade());
            stmt.setString(5, endereco.getEstado());
            stmt.setString(6, endereco.getCep());
            stmt.setInt(7, idUsuario);

            stmt.executeUpdate();
        }
    }
}


