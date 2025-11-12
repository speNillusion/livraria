CREATE DATABASE IF NOT EXISTS livraria;

USE livraria;
-- Tabela para armazenar os Autores
-- Separar os autores evita redundância de dados.
CREATE TABLE autores (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL UNIQUE,
    nacionalidade VARCHAR(100)
);

-- Tabela para armazenar os Gêneros
-- Facilita a categorização e a busca por gênero.
CREATE TABLE generos (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(100) NOT NULL UNIQUE
);

-- Tabela para armazenar as Editoras
-- Centraliza as informações das editoras.
CREATE TABLE editoras (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nome VARCHAR(255) NOT NULL UNIQUE,
    pais_origem VARCHAR(100)
);

-- Tabela principal para armazenar os Livros
-- Esta tabela conecta todas as outras através de chaves estrangeiras.
CREATE TABLE livros (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(255) NOT NULL,
    sinopse TEXT,
    ano_publicacao INT,
    numero_paginas INT,
    isbn VARCHAR(20) UNIQUE, -- ISBN é um identificador único para edições de livros
    idioma_origem VARCHAR(50),

    -- Chaves Estrangeiras para conectar com as outras tabelas
    autor_id INT,
    genero_id INT,
    editora_id INT,

    FOREIGN KEY (autor_id) REFERENCES autores(id),
    FOREIGN KEY (genero_id) REFERENCES generos(id),
    FOREIGN KEY (editora_id) REFERENCES editoras(id)
);
-- Índices para otimizar as buscas mais comuns
CREATE INDEX idx_livros_titulo ON livros(titulo);
CREATE INDEX idx_autores_nome ON autores(nome);