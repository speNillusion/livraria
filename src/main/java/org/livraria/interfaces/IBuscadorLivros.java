package org.livraria.interfaces;

import org.livraria.types.Livro;

import java.util.List;

/**
 * ‘Interface’ (Contrato) para serviços de busca de livros.
 * Define a funcionalidade essencial que qualquer buscador de livros deve oferecer:
 * receber uma consulta em texto e retornar uma lista de objetos Livro.
 */
public interface IBuscadorLivros {

    /**
     * Busca e processa livros com base em uma consulta de texto.
     *
     * @param consulta A consulta do usuário.
     * @return Uma lista de objetos {@link Livro}.
     * @throws Exception Se ocorrer um erro durante a busca ou processamento.
     */
    List<Livro> buscarLivros(String consulta) throws Exception;
}

