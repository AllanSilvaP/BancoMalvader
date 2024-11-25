package DAO;

import model.Funcionario;
import model.Endereco;
import java.sql.*;

public class FuncionarioDAO {
    private static final System.Logger logger = System.getLogger(FuncionarioDAO.class.getName());
    private Connection connection;

    // No construtor, cria a conexão utilizando a ConnectionFactory
    public FuncionarioDAO() {
        try {
            this.connection = ConnectionFactory.getConnection(); // Usando o ConnectionFactory para obter a conexão
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao estabelecer conexão com o banco de dados.", e);
        }
    }

    private Connection getConnection() {
        return this.connection;
    }

    public void salvarFuncionario(Funcionario funcionario) throws SQLException {
        // SQL para inserir o usuário
        String inserirUsuarioSQL = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha) VALUES (?, ?, ?, ?, 'FUNCIONARIO', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(inserirUsuarioSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(funcionario.getDataNascimento()));
            stmt.setString(4, funcionario.getTelefone());
            stmt.setString(5, funcionario.getSenhaHash());
            stmt.executeUpdate();

            // Obtendo o ID do usuário gerado
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                funcionario.setIdUsuario(idUsuario);  // Atualiza o ID do usuário no objeto funcionário
            }
        }

        // SQL para inserir o funcionário
        String inserirFuncionarioSQL = "INSERT INTO funcionario (codigo_funcionario, cargo, id_usuario) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(inserirFuncionarioSQL)) {
            stmt.setInt(1, funcionario.getCodigoFuncionario());
            stmt.setString(2, funcionario.getCargo());
            stmt.setInt(3, funcionario.getIdUsuario());  // Utiliza o ID do usuário inserido
            stmt.executeUpdate();
        }
    }

    public void deletarFuncionario(int idFuncionario) {
        String sql = "DELETE FROM funcionario WHERE id_funcionario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao deletar o funcionário: " + idFuncionario, e);
        }
    }

    public Funcionario buscarFuncionarioPorId(int idFuncionario) {
        String sql = "SELECT * FROM funcionario f " +
                     "INNER JOIN endereco e ON e.id_usuario = f.id_usuario " +
                     "WHERE f.id_funcionario = ?";
        Funcionario funcionario = null;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idFuncionario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    funcionario = mapearFuncionario(rs);
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao buscar o funcionário pelo ID: " + idFuncionario, e);
        }
        return funcionario;
    }
    
    public void atualizarFuncionario(Funcionario funcionario) {
        String sql = "UPDATE funcionario SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, senha = ?, tipo_usuario = ? WHERE id_funcionario = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCpf());
            stmt.setDate(3, Date.valueOf(funcionario.getDataNascimento()));
            stmt.setString(4, funcionario.getTelefone());
            stmt.setString(5, funcionario.getSenhaHash());
            stmt.setString(6, "FUNCIONARIO");
            stmt.setInt(7, funcionario.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao atualizar o funcionário no banco.", e);
        }
    }

    private Funcionario mapearFuncionario(ResultSet rs) throws SQLException {
        // Criação do objeto Endereco
        Endereco endereco = new Endereco(
            rs.getInt("id_endereco"),  // id_endereco, que agora está no banco
            rs.getString("cep"),
            rs.getString("local"),
            rs.getInt("numero_casa"),
            rs.getString("bairro"),
            rs.getString("cidade"),
            rs.getString("estado")
        );

        // Criação do objeto Funcionario
        Funcionario funcionario = new Funcionario(
            rs.getString("nome"), // Nome do funcionário
            rs.getString("cpf"), // CPF
            rs.getDate("data_nascimento").toLocalDate(), // Data de nascimento
            rs.getString("telefone"), // Telefone
            endereco, // Endereço
            rs.getInt("codigo_funcionario"), // Código do funcionário
            rs.getString("cargo"), // Cargo
            rs.getString("senha") // Hash da senha
        );


        return funcionario;
    }
}
