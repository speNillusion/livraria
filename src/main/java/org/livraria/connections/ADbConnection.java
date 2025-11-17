package org.livraria.connections;

import org.livraria.interfaces.IDbConnection;
import org.livraria.types.Livro;
import java.sql.*;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for database connections.
 * It manages connection details, the connection object, and defines the core contract for connection handling.
 * @version 1.1.0
 */
public abstract class ADbConnection implements IDbConnection {

    protected final String url;
    protected final String user;
    protected final String password;
    protected Connection connection = null;

    /**
     * Constructor to initialize the connection details.
     * @param url The full JDBC URL for the database.
     * @param user The database username.
     *param password The database user password.
     */
    public ADbConnection(String url, String user, String password) {
        if (url == null || user == null || password == null) {
            throw new IllegalArgumentException("URL, USER, and PASSWORD cannot be null.");
        }
        this.url = url;
        this.user = user;
        this.password = password;
    }

    /**
     * Provides the active database connection object.
     * Throws a RuntimeException if the connection is not active or has been closed.
     * @return The active SQL Connection object.
     */
    @Override
    public Connection getConnection() {
        if (!isConnected()) {
            throw new RuntimeException("Connection is not active. Please call connect() before getting the connection.");
        }
        return this.connection;
    }

    /**
     * Checks if the connection is currently active and valid.
     * @return true if the connection is not null and not closed, false otherwise.
     */
    public boolean isConnected() {
        try {
            return this.connection != null && !this.connection.isClosed();
        } catch (SQLException e) {
            System.err.println("Error checking connection status: " + e.getMessage());
            return false;
        }
    }

    /**
     * Selects and displays all records from a table.
     * Assumes the table has at least 'id', 'nome', and 'email' columns.
     * @param table The name of the table to query (e.g., "usuarios").
     * @return true if the select is successful and prints results, false if an error occurs.
     */
    @Override
    public Boolean select(String table) {
        if (!isConnected()) {
            System.err.println("Não é possível buscar dados. A conexão com o banco de dados não está ativa.");
            return false;
        }

        String selectSQL = String.format("SELECT * FROM %s", table);

        System.out.println("Executando busca de dados na tabela: " + table);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(selectSQL);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            // Armazenar os nomes das colunas e as larguras máximas
            String[] columnNames = new String[columnCount];
            int[] columnWidths = new int[columnCount];

            // Lista para armazenar todas as linhas de dados
            List<String[]> dataRows = new ArrayList<>();

            // 1. Primeira Passagem: Coletar dados e calcular a largura máxima de cada coluna
            for (int i = 0; i < columnCount; i++) {
                columnNames[i] = metaData.getColumnName(i + 1);
                // Inicializa a largura com o tamanho do nome da coluna
                columnWidths[i] = columnNames[i].length();
            }

            while (resultSet.next()) {
                String[] row = new String[columnCount];
                for (int i = 0; i < columnCount; i++) {
                    // Obtém o valor como String (seguro para todos os tipos)
                    String value = resultSet.getString(i + 1);
                    if (value == null) {
                        value = "NULL"; // Trata valores nulos
                    }
                    row[i] = value;

                    // Atualiza a largura máxima da coluna
                    columnWidths[i] = Math.max(columnWidths[i], value.length());
                }
                dataRows.add(row);
            }

            if (dataRows.isEmpty()) {
                System.out.println("--- Resultados da Tabela: " + table + " ---");
                System.out.println("Nenhum registro encontrado na tabela.");
                System.out.println("----------------------------------------");
                return true;
            }

            // 2. Segunda Passagem: Exibir a tabela formatada

            System.out.println("--- Resultados da Tabela: " + table + " ---");

            // Função auxiliar para formatar a string com preenchimento
            java.util.function.BiFunction<String, Integer, String> padRight = (s, n) ->
                    String.format("%-" + n + "s", s);

            // Exibir Cabeçalho
            StringBuilder headerBuilder = new StringBuilder();
            for (int i = 0; i < columnCount; i++) {
                headerBuilder.append(padRight.apply(columnNames[i], columnWidths[i]));
                if (i < columnCount - 1) {
                    headerBuilder.append(" | ");
                }
            }
            System.out.println(headerBuilder.toString());

            // Exibir Linha Separadora
            StringBuilder separatorBuilder = new StringBuilder();
            for (int i = 0; i < columnCount; i++) {
                separatorBuilder.append("-".repeat(columnWidths[i]));
                if (i < columnCount - 1) {
                    separatorBuilder.append("-+-");
                }
            }
            System.out.println(separatorBuilder.toString());

            // Exibir Dados
            for (String[] row : dataRows) {
                StringBuilder rowBuilder = new StringBuilder();
                for (int i = 0; i < columnCount; i++) {
                    rowBuilder.append(padRight.apply(row[i], columnWidths[i]));
                    if (i < columnCount - 1) {
                        rowBuilder.append(" | ");
                    }
                }
                System.out.println(rowBuilder.toString());
            }

            System.out.println("----------------------------------------");
            return true;

        } catch (SQLException e) {
            System.err.println("Falha ao executar o comando SELECT na tabela '" + table + "'.");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            return false;
        }
    }



    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @param nome The user's name to be inserted.
     * @param email The user's email to be inserted.
     * @return true if the insert is successful, false otherwise.
     */
    @Override
    public Boolean insert(String table, String nome, String email) {
        if (!isConnected()) {
            System.err.println("Não é possível inserir dados. A conexão com o banco de dados não está ativa.");
            return false;
        }

        String insertSQL = String.format("INSERT INTO %s (nome, email) VALUES (?, ?)", table);

        System.out.println("Preparando a inserção de dados na tabela: " + table);

        try (PreparedStatement preparedStatement = this.connection.prepareStatement(insertSQL)) {

            preparedStatement.setString(1, nome);
            preparedStatement.setString(2, email);

            int rowsAffected = preparedStatement.executeUpdate();

            if (rowsAffected > 0) {
                System.out.println("Dados inseridos com sucesso! Linhas afetadas: " + rowsAffected);
                return true;
            } else {
                System.err.println("A inserção falhou, nenhuma linha foi alterada.");
                return false;
            }

        } catch (SQLException e) {
            System.err.println("Falha ao executar o comando de inserção na tabela '" + table + "'.");

            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            e.notify();
            return false;
        }
    }

    /**
     * Insere um objeto Livro completo no banco de dados, gerenciando as tabelas
     * relacionadas (autores, generos, editoras) para evitar duplicatas.
     *
     * @param livro O objeto Livro a ser inserido.
     * @return true se o livro foi inserido com sucesso, false caso contrário.
     */
    public boolean inserirLivro(Livro livro) {
        if (!isConnected()) {
            System.err.println("Não é possível inserir o livro. A conexão com o banco de dados não está ativa.");
            return false;
        }

        String sqlInsertLivro = "INSERT INTO livros (titulo, sinopse, ano_publicacao, numero_paginas, isbn, idioma_origem, autor_id, genero_id, editora_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            // Desativa o auto-commit para tratar a inserção como uma transação única.
            // Se algo der errado, podemos reverter tudo.
            connection.setAutoCommit(false);

            // 1. Obter ou inserir o ID do Autor
            // O segundo parâmetro 'nacionalidade' é nulo porque não temos essa info no objeto Livro.
            int autorId = obterOuInserirId("autores", livro.getAutor(), null);

            // 2. Obter ou inserir o ID do Gênero
            // O gênero pode ser composto ("Ficção, Aventura"). Vamos pegar apenas o primeiro.
            String primeiroGenero = livro.getGenero().split(",")[0].trim();
            int generoId = obterOuInserirId("generos", primeiroGenero, null);

            // 3. Obter ou inserir o ID da Editora
            // O segundo parâmetro é o país de origem da editora, que podemos extrair do livro.
            int editoraId = obterOuInserirId("editoras", livro.getEditora(), livro.getOrigem());

            // 4. Inserir o Livro na tabela principal com os IDs obtidos
            try (PreparedStatement pstmtLivro = connection.prepareStatement(sqlInsertLivro)) {
                pstmtLivro.setString(1, livro.getTitulo());
                pstmtLivro.setString(2, livro.getSinopse());
                pstmtLivro.setInt(3, livro.getAnoPublicacao());
                pstmtLivro.setInt(4, livro.getNumeroPaginas());
                pstmtLivro.setString(5, livro.getIsbn());
                pstmtLivro.setString(6, livro.getOrigem()); // Usando 'origem' como 'idioma_origem'
                pstmtLivro.setInt(7, autorId);
                pstmtLivro.setInt(8, generoId);
                pstmtLivro.setInt(9, editoraId);

                int rowsAffected = pstmtLivro.executeUpdate();

                if (rowsAffected > 0) {
                    // Se tudo deu certo, confirma a transação.
                    connection.commit();
                    System.out.println("Livro '" + livro.getTitulo() + "' inserido com sucesso!");
                    return true;
                } else {
                    // Se a inserção do livro falhou, reverte tudo.
                    connection.rollback();
                    System.err.println("A inserção do livro '" + livro.getTitulo() + "' falhou, nenhuma linha foi alterada.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.err.println("Falha crítica ao inserir o livro '" + livro.getTitulo() + "'. A transação será revertida.");
            System.err.println("SQLState: " + e.getSQLState());
            System.err.println("Error Code: " + e.getErrorCode());
            System.err.println("Message: " + e.getMessage());
            try {
                // Tenta reverter a transação em caso de erro.
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Erro ao tentar reverter a transação: " + ex.getMessage());
            }
            return false;
        } finally {
            try {
                // Reativa o auto-commit para as próximas operações.
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Erro ao reativar o auto-commit: " + e.getMessage());
            }
        }
    }

    /**
     * Método auxiliar para obter o ID de um item em uma tabela (autor, genero, editora).
     * Se o item não existir, ele é inserido e o novo ID é retornado.
     *
     * @param tabela O nome da tabela (autores, generos, editoras).
     * @param nome O valor a ser procurado/inserido na coluna 'nome'.
     * @param colunaExtraValor O valor para a segunda coluna (nacionalidade ou pais_origem), pode ser nulo.
     * @return O ID do item.
     * @throws SQLException Se ocorrer um erro no banco de dados.
     */
    private int obterOuInserirId(String tabela, String nome, String colunaExtraValor) throws SQLException {
        String sqlSelect = "SELECT id FROM " + tabela + " WHERE nome = ?";
        String sqlInsert = "";

        // Define a query de inserção com base na tabela
        boolean temColunaExtra = false;
        if (tabela.equals("autores")) {
            sqlInsert = "INSERT INTO autores (nome, nacionalidade) VALUES (?, ?)";
            temColunaExtra = true;
        } else if (tabela.equals("editoras")) {
            sqlInsert = "INSERT INTO editoras (nome, pais_origem) VALUES (?, ?)";
            temColunaExtra = true;
        } else { // generos
            sqlInsert = "INSERT INTO generos (nome) VALUES (?)";
        }

        // 1. Tenta encontrar o item
        try (PreparedStatement pstmtSelect = connection.prepareStatement(sqlSelect)) {
            pstmtSelect.setString(1, nome);
            try (ResultSet rs = pstmtSelect.executeQuery()) {
                if (rs.next()) {
                    // Se encontrou, retorna o ID existente
                    return rs.getInt("id");
                }
            }
        }

        // 2. Se não encontrou, insere o novo item
        System.out.println("Item '" + nome + "' não encontrado na tabela '" + tabela + "'. Inserindo...");
        try (PreparedStatement pstmtInsert = connection.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
            pstmtInsert.setString(1, nome);

            // --- CORREÇÃO APLICADA AQUI ---
            if (temColunaExtra) {
                if (colunaExtraValor != null) {
                    // Se o valor extra existe, define-o como String.
                    pstmtInsert.setString(2, colunaExtraValor);
                } else {
                    // Se o valor extra é nulo, informa isso explicitamente ao JDBC.
                    // Usamos Types.VARCHAR porque as colunas 'nacionalidade' e 'pais_origem' são VARCHAR.
                    pstmtInsert.setNull(2, Types.VARCHAR);
                }
            }
            // --- FIM DA CORREÇÃO ---

            int affectedRows = pstmtInsert.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmtInsert.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        // Retorna o novo ID gerado
                        int novoId = generatedKeys.getInt(1);
                        System.out.println("Item '" + nome + "' inserido com sucesso com o ID: " + novoId);
                        return novoId;
                    }
                }
            }
        }

        // Se a inserção falhar, lança uma exceção para que a transação principal seja revertida.
        throw new SQLException("Não foi possível obter ou inserir o ID para '" + nome + "' na tabela '" + tabela + "'.");
    }

    @Override
    public Boolean check() {
        if (!isConnected()) {
            System.err.println("Não é possível verificar as tabelas. A conexão não está ativa.");
            return false;
        }

        try (Statement statement = this.connection.createStatement()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS usuarios (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nome VARCHAR(100) NOT NULL, " +
                    "email VARCHAR(100) NOT NULL UNIQUE, " +
                    "data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";

            System.out.println("Verificando/Criando a tabela 'usuarios'...");
            statement.execute(createTableSQL);
            System.out.println("Tabela 'usuarios' verificada/criada com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Falha ao verificar/criar a tabela 'usuarios'.");
            e.notify();
            return false;
        }
    }

    // --- Métodos Abstratos a serem implementados pelas classes filhas ---

    /**
     * {@inheritDoc}
     * This method must be implemented by subclasses to establish a connection
     * to a specific database (e.g., MySQL, PostgreSQL).
     */
    @Override
    public abstract Boolean connect();

    /**
     * {@inheritDoc}
     * This method must be implemented by subclasses to properly close the
     * database connection and release resources.
     */
    @Override
    public abstract Boolean disconnect();
}
