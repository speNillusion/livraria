package org.livraria.connections;

import java.sql.*;

public class DbConnection extends ADbConnection {

    /**
     * Constructor that passes the connection details to the abstract parent class.
     * @param URL The full JDBC URL for the MySQL database.
     * @param USER The database username.
     * @param PASSWORD The database user password.
     */
    public DbConnection(String URL, String USER, String PASSWORD) {
        super(URL, USER, PASSWORD); // Chama o construtor da classe pai (ADbConnection)
    }

    @Override
    public Boolean connect() {
        if (isConnected()) {
            System.out.println("A conexão já está ativa.");
            return true;
        }

        try {
            System.out.println("Conectando ao banco de dados MySQL...");
            // As propriedades url, user e password são herdadas da classe pai
            this.connection = DriverManager.getConnection(this.url, this.user, this.password);
            System.out.println("Conexão bem-sucedida!");
            try {
                this.check();

            } catch (RuntimeException e) {
                throw new RuntimeException("Could not check to the database", e);
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Falha na conexão com o banco de dados.");
            throw new RuntimeException("Could not connect to the database", e);
        }
    }

    @Override
    public Boolean disconnect() {
        if (!isConnected()) {
            System.out.println("Nenhuma conexão ativa para fechar.");
            return true;
        }
        try {
            System.out.println("Fechando a conexão com o banco de dados...");
            this.connection.close();
            System.out.println("Conexão fechada com sucesso.");
            return true;
        } catch (SQLException e) {
            System.err.println("Erro ao fechar a conexão com o banco de dados.");
            e.notify();
            return false;
        }
    }
}