package org.livraria.types;

/**
 * Classe de modelo (POJO) que representa a entidade Livro.
 * Contém todos os atributos de um livro que serão recebidos da IA e salvos no banco de dados.
 */
public class Livro {
    String titulo;
    String autor;
    String genero;
    String sinopse;
    int anodepublicacao;
    String editora;
    String origem;
    int numerodepaginas;
    String ISBN;

    public String getIsbn() {
        return ISBN;
    }

    public void setIsbn(String isbn) {
        this.ISBN = isbn;
    }

    public int getNumeroPaginas() {
        return numerodepaginas;
    }

    public void setNumeroPaginas(int numeroPaginas) {
        this.numerodepaginas = numeroPaginas;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(String origem) {
        this.origem = origem;
    }

    public String getEditora() {
        return editora;
    }

    public void setEditora(String editora) {
        this.editora = editora;
    }

    public int getAnoPublicacao() {
        return anodepublicacao;
    }

    public void setAnoPublicacao(int anoPublicacao) {
        this.anodepublicacao = anoPublicacao;
    }

    public String getSinopse() {
        return sinopse;
    }

    public void setSinopse(String sinopse) {
        this.sinopse = sinopse;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Livro(String titulo, String autor, String genero, String sinopse,
                 int anoPublicacao, String editora, String origem,
                 int numeroPaginas, String isbn) {
        this.titulo = titulo;
        this.autor = autor;
        this.genero = genero;
        this.sinopse = sinopse;
        this.anodepublicacao = anoPublicacao;
        this.editora = editora;
        this.origem = origem;
        this.numerodepaginas = numeroPaginas;
        this.ISBN = isbn;
    }

    @Override
    public String toString() {
        return "Livro {\n" +
                "  Título: '" + titulo + "',\n" +
                "  Autor: '" + autor + "',\n" +
                "  Gênero: '" + genero + "',\n" +
                "  Ano de Publicação: " + anodepublicacao + ",\n" +
                "  Editora: '" + editora + "',\n" +
                "  Nº de Páginas: " + numerodepaginas + ",\n" +
                "  ISBN: '" + ISBN + "',\n" +
                "  Origem: '" + origem + "',\n" +
                "  Sinopse: '" + sinopse + "'\n" +
                "}";
    }
}
