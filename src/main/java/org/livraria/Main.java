package org.livraria;

import io.github.cdimascio.dotenv.Dotenv;
import org.livraria.connections.BuscadorLivros;
import org.livraria.connections.DbConnection;
import java.util.Scanner;
import org.livraria.types.Livro;

import java.util.List;

public class Main {
    public static final Dotenv dotenv = Dotenv.load( );
    private static final String URL_JDBC = dotenv.get("URL_JDBC");
    private static final String USER_JDBC = dotenv.get("USER_JDBC");
    private static final String PASSWORD_JDBC = dotenv.get("PASSWORD_JDBC");

    public static void printMenu() {
        System.out.println("-------------------------------");
        System.out.println("1) Cadastrar livros");
        System.out.println("2) Ver todos os livros cadastrados");
        System.out.println("3) Sair");
        System.out.println("-------------------------------");
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        BuscadorLivros buscador = new BuscadorLivros();
        DbConnection client = new DbConnection(URL_JDBC,USER_JDBC,PASSWORD_JDBC);
        Boolean clientConnect = client.connect();
        Integer option = 0;

        while (!option.equals(3)) {
            printMenu();

            if (sc.hasNextInt()) {
                option = sc.nextInt();
                if (option.equals(3)) { break; }
            } else {
                printMenu();
            }

            switch (option) {
                case 1:
                    System.out.print("Digite o nome do Autor: ");
                    sc.nextLine();
                    String autor = sc.nextLine();
                    try {
                        List<Livro> livros = buscador.buscarLivros(String.format("cadastre todos os livros do autor %s", autor));

                        if (livros.isEmpty() && !clientConnect) {
                            System.out.println("Nenhum livro foi processado.");
                        } else {
                            for (Livro livro : livros) {
                                client.inserirLivro(livro);
                            }
                        }
                    } catch (Exception e) {
                        System.err.println("Ocorreu um erro fatal durante a busca de livros: " + e.getMessage());
                    }
                    break;
                case 2:
                    System.out.println();
                    client.select("livros");
                    System.out.println();
                default:
                    System.out.println("Número inválido");
            }
        }
    }
}