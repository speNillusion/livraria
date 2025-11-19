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

    public abstract List<Livro> buscarLivros(String consulta) throws Exception;

    protected abstract List<Livro> parsearRespostaComGson(String respostaJson) throws Exception;
}

