package org.livraria.interfaces;

import java.sql.Connection;

/**
 * Interface that defines the contract for database connection classes.
 * @version 1.0.0
 */
public interface IDbConnection {

    /**
     * Establishes a connection to the database.
     * @return true if the connection is successful, false otherwise.
     */
    Boolean connect();

    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @param nome The user's name to be inserted.
     * @param email The user's email to be inserted.
     * @return true if the insert is successful, false otherwise.
     */
    Boolean insert(String table, String nome, String email);

    /**
     * Closes the database connection.
     * @return true if the disconnection is successful, false otherwise.
     */
    Boolean disconnect();

    /**
     * Create inserts in any table that has 'nome' and 'email' columns.
     * @param table The name of the table where data will be inserted (e.g., "usuarios").
     * @return true if the select is successful, false otherwise.
     */
    Boolean select(String table);

    /**
     * Checks if the required database structures (like tables) exist, and creates them if they don't.
     * @return true if the structures exist or were created successfully, false otherwise.
     */
    Boolean check();

    /**
     * Provides the active database connection object.
     * @return The active SQL Connection object.
     */
    Connection getConnection();
}