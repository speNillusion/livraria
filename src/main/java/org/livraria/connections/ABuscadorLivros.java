package org.livraria.connections;

import org.livraria.interfaces.IBuscadorLivros;

import java.util.ArrayList;
import java.util.List;
import org.livraria.types.Livro;


/**
 * Classe abstrata que fornece uma implementação base para buscadores de livros.
 *
 * Implementa a interface {@link IBuscadorLivros} e cuida da lógica comum
 * de processar (parse) a string de dados dos livros.
 *
 * Deixa a responsabilidade de obter os dados brutos para as subclasses,
 * através do método abstrato {@code obterDadosBrutos}.
 */
public abstract class ABuscadorLivros implements IBuscadorLivros {

    /**
     * Método abstrato que as subclasses devem implementar para obter os dados brutos.
     */
    protected abstract String obterDadosBrutos(String consulta) throws Exception;

    @Override
    public List<Livro> buscarLivros(String consulta) throws Exception {
        String dadosBrutos = obterDadosBrutos(consulta);

        if (dadosBrutos == null || dadosBrutos.trim().isEmpty()) {
            System.err.println("Nenhum dado bruto foi retornado pela fonte.");
            return new ArrayList<>();
        }

        return parseConteudoDosLivros(dadosBrutos);
    }

    /**
     * Método concreto e compartilhado para analisar a string de dados e convertê-la em objetos Livro.
     */
    private List<Livro> parseConteudoDosLivros(String conteudo) {
        List<Livro> livros = new ArrayList<>();

        // Remove as chaves de abertura e fechamento e divide por "},{" para separar os livros
        String[] livrosStr = conteudo.replaceFirst("^\\{", "").replaceFirst("\\}$", "").split("\\},\\s*\\{");

        for (String livroData : livrosStr) {
            try {
                // Limita a 9 partes para evitar que vírgulas na sinopse quebrem o parse
                String[] campos = livroData.split(",", 9);
                if (campos.length < 9) {
                    System.err.println("AVISO: Ignorando linha mal formatada (campos insuficientes): " + livroData);
                    continue;
                }

                livros.add(new Livro(
                        campos[0].trim(), // titulo
                        campos[1].trim(), // autor
                        campos[2].trim(), // genero
                        campos[3].trim(), // sinopse
                        Integer.parseInt(campos[4].trim()), // anoPublicacao
                        campos[5].trim(), // editora
                        campos[6].trim(), // origem
                        Integer.parseInt(campos[7].trim()), // numeroPaginas
                        campos[8].trim()  // isbn
                ));

            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter número em: " + livroData + ". Livro ignorado.");
            } catch (Exception e) {
                System.err.println("Erro inesperado ao processar dados do livro: " + livroData + ". Livro ignorado.");
                e.printStackTrace();
            }
        }
        return livros;
    }
}

