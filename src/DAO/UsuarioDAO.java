package DAO;

import model.Usuario;
import model.UsuarioInfo;
import model.Funcionario;
import model.Cliente;
import model.Endereco;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                   e.local, e.numero_casa, e.bairro, e.cidade, e.estado, e.cep,
                   f.codigo_funcionario, f.cargo
            FROM usuario u
            LEFT JOIN endereco e ON e.id_usuario = u.id_usuario
            LEFT JOIN funcionario f ON f.id_usuario = u.id_usuario
            WHERE u.id_usuario = ?
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setInt(1, idUsuario);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Verifica o tipo de usuário antes de instanciar
                    String tipoUsuario = rs.getString("tipo_usuario");
                    Usuario usuario;

                    if ("FUNCIONARIO".equals(tipoUsuario)) {
                        // Preencher dados do Funcionario a partir do banco
                        int codigoFuncionario = rs.getInt("codigo_funcionario");
                        String cargo = rs.getString("cargo");

                        // Usando o construtor do Funcionario
                        usuario = new Funcionario(
                            rs.getString("nome"), 
                            rs.getString("cpf"), 
                            rs.getDate("data_nascimento").toLocalDate(), 
                            rs.getString("telefone"), 
                            new Endereco(
                                rs.getString("cep"), 
                                rs.getString("local"), 
                                rs.getInt("numero_casa"), 
                                rs.getString("bairro"), 
                                rs.getString("cidade"), 
                                rs.getString("estado")
                            ),
                            codigoFuncionario,  // Código do funcionário vindo do banco
                            cargo,  // Cargo do funcionário vindo do banco
                            rs.getString("senha") // Senha em texto simples
                        );
                    } else if ("CLIENTE".equals(tipoUsuario)) {
                        // Usando o construtor do Cliente
                        usuario = new Cliente(
                            rs.getString("nome"), 
                            rs.getString("cpf"), 
                            rs.getDate("data_nascimento").toLocalDate(), 
                            rs.getString("telefone"), 
                            new Endereco(
                                rs.getString("cep"), 
                                rs.getString("local"), 
                                rs.getInt("numero_casa"), 
                                rs.getString("bairro"), 
                                rs.getString("cidade"), 
                                rs.getString("estado")
                            ),
                            rs.getString("senha"),  // Senha em texto simples
                            null  // Exemplo de conta null, substitua com a conta real, se necessário
                        );
                    } else {
                        // Tipo de usuário inválido ou desconhecido
                        return null;
                    }

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
        String sql = """
            SELECT u.id_usuario, u.nome, u.cpf, u.data_nascimento, u.telefone, u.tipo_usuario, u.senha,
                   e.local, e.numero_casa, e.bairro, e.cidade, e.estado, e.cep,
                   f.codigo_funcionario, f.cargo
            FROM usuario u
            LEFT JOIN endereco e ON e.id_usuario = u.id_usuario
            LEFT JOIN funcionario f ON f.id_usuario = u.id_usuario
        """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Usuario usuario;

                // Verificar o tipo de usuário
                String tipoUsuario = rs.getString("tipo_usuario");

                if ("FUNCIONARIO".equals(tipoUsuario)) {
                    // Se for FUNCIONARIO, instanciamos um Funcionario
                    usuario = new Funcionario(
                        rs.getString("nome"), 
                        rs.getString("cpf"), 
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("telefone"),
                        new Endereco(  // Usando o construtor sem id_endereco
                            rs.getString("cep"), 
                            rs.getString("local"), 
                            rs.getInt("numero_casa"), 
                            rs.getString("bairro"), 
                            rs.getString("cidade"), 
                            rs.getString("estado")
                        ),
                        rs.getInt("codigo_funcionario"),  // Código do funcionário
                        rs.getString("cargo"),  // Cargo do funcionário
                        rs.getString("senha")   // Senha em texto simples
                    );
                } else if ("CLIENTE".equals(tipoUsuario)) {
                    // Se for CLIENTE, instanciamos um Cliente
                    usuario = new Cliente(
                        rs.getString("nome"), 
                        rs.getString("cpf"), 
                        rs.getDate("data_nascimento").toLocalDate(),
                        rs.getString("telefone"),
                        new Endereco(  // Usando o construtor sem id_endereco
                            rs.getString("cep"), 
                            rs.getString("local"), 
                            rs.getInt("numero_casa"), 
                            rs.getString("bairro"), 
                            rs.getString("cidade"), 
                            rs.getString("estado")
                        ),
                        rs.getString("senha"),  // Senha em texto simples
                        null  // Exemplo de conta null, substitua com a conta real
                    );
                }else {
                    // Caso o tipo de usuário seja inválido, continue para o próximo
                    continue;
                }

                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao listar todos os usuários", e);
        }

        return usuarios;
    }

    public boolean validarCpfESenha(String cpf, String senha) {
        String sql = "SELECT senha FROM usuario WHERE cpf = ?";
        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String senhaArmazenada = rs.getString("senha");

                    // Comparando diretamente as senhas
                    return senha.equals(senhaArmazenada); // Comparação direta
                }
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao validar CPF e senha", e);
        }
        return false;
    }

    //PROCURE GENTE PELO CPF
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

    // Salvar um novo usuário e retornar o ID do usuário
    public int salvarUsuario(Usuario usuario) throws SQLException {
        if (cpfExiste(usuario.getCpf())) {
            System.out.println("Erro: CPF já cadastrado.");
            return -1; // Indica que o CPF já existe
        }

        String sql = "INSERT INTO usuario (nome, cpf, data_nascimento, telefone, senha, tipo_usuario) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getCpf());
            stmt.setDate(3, Date.valueOf(usuario.getDataNascimento()));
            stmt.setString(4, usuario.getTelefone());
            stmt.setString(5, usuario.getSenha()); // Usando a senha em texto simples
            stmt.setString(6, usuario instanceof Funcionario ? "FUNCIONARIO" : "CLIENTE");

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);  // Retorna o ID gerado
                    }
                }
            }
        }
        return -1; // Caso falhe ao salvar o usuário
    }
    
    public UsuarioInfo buscarUsuarioPorCpf(String cpf) {
        String sql = "SELECT id_usuario, nome, senha, tipo_usuario FROM usuario WHERE cpf = ?";

        try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
            stmt.setString(1, cpf);  // Define o CPF no parâmetro da consulta

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Obtém os dados da consulta
                    int idUsuario = rs.getInt("id_usuario");
                    String nome = rs.getString("nome");
                    String senha = rs.getString("senha");
                    String tipoUsuario = rs.getString("tipo_usuario");

                    // Cria um objeto UsuarioInfo com os dados encontrados
                    boolean isFuncionario = tipoUsuario.equals("FUNCIONARIO");

                    // Retorna o usuário
                    return new UsuarioInfo(idUsuario, nome, senha, isFuncionario);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();  // Para fins de depuração, pode ser substituído por um log
        }
        return null;  // Caso não encontre o usuário ou ocorra erro
    }


    // Fechar a conexão
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            logger.log(System.Logger.Level.ERROR, "Erro ao fechar a conexão", e);
        }
    }
}