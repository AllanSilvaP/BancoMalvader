package DAO;

import model.Funcionario;
import util.DataManager;
import model.Endereco;
import java.sql.*;
import java.util.List;

public class FuncionarioDAO {
    private static final System.Logger logger = System.getLogger(FuncionarioDAO.class.getName());
    private Connection connection;

    public FuncionarioDAO() {
        try {
            this.connection = ConnectionFactory.getConnection();
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao estabelecer conexão com o banco de dados.", e);
        }
    }

    private Connection getConnection() {
        return this.connection;
    }


    public Funcionario buscarFuncionarioPorId(int idFuncionario) {
        String sql = "SELECT * FROM funcionario f " +
                     "INNER JOIN endereco e ON e.id_usuario = f.id_usuario " +
                     "INNER JOIN usuario u ON u.id_usuario = f.id_usuario " + 
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

  //INSERIR O SQL AQUI
    public void salvarFuncionario(Funcionario funcionario) throws SQLException {
        String inserirUsuarioSQL = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, tipo_usuario, senha) VALUES (?, ?, ?, ?, 'FUNCIONARIO', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(inserirUsuarioSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, funcionario.getNome());
            stmt.setString(2, funcionario.getCpf());
            stmt.setDate(3, java.sql.Date.valueOf(funcionario.getDataNascimento()));
            stmt.setString(4, funcionario.getTelefone());
            stmt.setString(5, funcionario.getSenha());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                int idUsuario = rs.getInt(1);
                funcionario.setIdUsuario(idUsuario); //ACHO QUE E O ATT DO ID
            }
        }

        String inserirFuncionarioSQL = "INSERT INTO funcionario (codigo_funcionario, cargo, id_usuario) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(inserirFuncionarioSQL)) {
            stmt.setInt(1, funcionario.getCodigoFuncionario());
            stmt.setString(2, funcionario.getCargo());
            stmt.setInt(3, funcionario.getIdUsuario()); 
            stmt.executeUpdate();
        }
        

        String filename = "funcionarios.dat";
        List<Funcionario> funcionarios = DataManager.carregarFuncionarios(filename );
        funcionarios.add(funcionario);

        DataManager.salvarFuncionarios(funcionarios, filename);
    }
    


    private Funcionario mapearFuncionario(ResultSet rs) throws SQLException {
        Endereco endereco = new Endereco(
            rs.getInt("id_endereco"), 
            rs.getString("cep"),
            rs.getString("local"),
            rs.getInt("numero_casa"),
            rs.getString("bairro"),
            rs.getString("cidade"),
            rs.getString("estado")
        );

        Funcionario funcionario = new Funcionario(
            rs.getString("nome"), // Nome
            rs.getString("cpf"), // CPF
            rs.getDate("data_nascimento").toLocalDate(), // nascimento
            rs.getString("telefone"), // Telefone
            endereco, // Endereço
            rs.getInt("codigo_funcionario"),
            rs.getString("cargo"),
            rs.getString("senha")
        );


        return funcionario;
    }
    
    public void atualizarFuncionario(Funcionario funcionario) {
        String sqlUsuario = "UPDATE usuario SET nome = ?, cpf = ?, data_nascimento = ?, telefone = ?, senha = ? WHERE id_usuario = ?";
        String sqlFuncionario = "UPDATE funcionario SET codigo_funcionario = ?, cargo = ? WHERE id_funcionario = ?";

        try (Connection conn = getConnection()) {

            conn.setAutoCommit(false);

            // Atualizar os dados da tabela usuario
            try (PreparedStatement stmtUsuario = conn.prepareStatement(sqlUsuario)) {
                stmtUsuario.setString(1, funcionario.getNome());
                stmtUsuario.setString(2, funcionario.getCpf());
                stmtUsuario.setDate(3, Date.valueOf(funcionario.getDataNascimento()));
                stmtUsuario.setString(4, funcionario.getTelefone());
                stmtUsuario.setString(5, funcionario.getSenha()); 
                stmtUsuario.setInt(6, funcionario.getIdUsuario()); 
                stmtUsuario.executeUpdate();
            }

            try (PreparedStatement stmtFuncionario = conn.prepareStatement(sqlFuncionario)) {
                stmtFuncionario.setInt(1, funcionario.getCodigoFuncionario());
                stmtFuncionario.setString(2, funcionario.getCargo());
                stmtFuncionario.setInt(3, funcionario.getId()); 
                stmtFuncionario.executeUpdate();
            }

            conn.commit();
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao atualizar o funcionário no banco.", e);
            try (Connection conn = getConnection()) {
                conn.rollback();
            } catch (SQLException ex) {
                logger.log(System.Logger.Level.ERROR, "Erro ao fazer rollback da transação.", ex);
            }
        }
    }
}