package org.livraria.connections;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.cdimascio.dotenv.Dotenv;
import org.livraria.types.Livro;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.List;

/**
 * Implementação de um buscador de livros que utiliza a API do Groq
 * com o modelo 'compound' para obter dados atualizados via busca na web.
 */
public class BuscadorLivros {
    public static final Dotenv dotenv = Dotenv.load( );
    private static final String URL_JDBC = dotenv.get("URL_JDBC");
    private static final String USER_JDBC = dotenv.get("USER_JDBC");
    private static final String PASSWORD_JDBC = dotenv.get("PASSWORD_JDBC");
    // Adicione esta variável ao seu arquivo .env
    private static final String GROQ_API_KEY = dotenv.get("GROQ_API_KEY");
    private static final String GROQ_API_URL = "https://api.groq.com/openai/v1/chat/completions";

    private static final Gson gson = new Gson( );

    public List<Livro> buscarLivros(String consulta) throws Exception {
        System.out.println("Enviando prompt para a IA (Groq) com busca na web...");

        String systemPrompt = """
                Você é um assistente de catalogação de livros extremamente rápido e eficiente que utiliza busca na web para obter dados precisos e atualizados.
                Sua resposta DEVE ser um objeto JSON válido e nada mais.
                O JSON deve ter uma única chave "livros", que contém uma lista de objetos de livros.
                Cada objeto de livro deve ter as seguintes chaves: "titulo", "autor", "genero", "sinopse", "anodepublicacao", "editora", "origem", "numerodepaginas", "ISBN".
                Para o ISBN, forneça o ISBN-13 sempre que possível. Para o número de páginas, use uma edição comum como referência.
                Exemplo de formato de saída:
                { "livros": [ { "titulo": "O Senhor dos Anéis", "autor": "J.R.R. Tolkien", "genero": "Fantasia", "sinopse": "Uma jornada para destruir um anel poderoso.", "anodepublicacao": 1954, "editora": "Allen & Unwin", "origem": "Reino Unido", "numerodepaginas": 423, "ISBN": "978-0618640157" } ] }
                Não adicione nenhum texto, explicação ou formatação fora do objeto JSON principal e NÃO DEIXE FALTANDO NENHUM PARÂMETRO!
                """;

        // --- CORREÇÃO: Construindo o corpo da requisição para a API do Groq ---
        JsonObject requestBodyJson = new JsonObject();
        requestBodyJson.addProperty("model", "openai/gpt-oss-120b");

        // Estrutura de 'messages' padrão
        JsonArray messages = new JsonArray();
        JsonObject systemMessage = new JsonObject();
        systemMessage.addProperty("role", "system");
        systemMessage.addProperty("content", systemPrompt);
        messages.add(systemMessage);

        JsonObject userMessage = new JsonObject();
        userMessage.addProperty("role", "user");
        userMessage.addProperty("content", consulta);
        messages.add(userMessage);

        requestBodyJson.add("messages", messages);

        // Parâmetros específicos do Groq para ativar ferramentas
        JsonObject compoundCustom = new JsonObject();
        JsonObject tools = new JsonObject();
        JsonArray enabledTools = new JsonArray();
        enabledTools.add("web_search"); // Ativando a busca na web
        tools.add("enabled_tools", enabledTools);
        compoundCustom.add("tools", tools);
        requestBodyJson.add("compound_custom", compoundCustom);

        // Outros parâmetros (opcionais, mas bons para controle)
        requestBodyJson.addProperty("temperature", 0.5);
        requestBodyJson.addProperty("max_tokens", 8192);
        requestBodyJson.addProperty("stream", false); // Não usaremos stream para obter a resposta completa de uma vez

        String requestBody = gson.toJson(requestBodyJson);
        System.out.println("Request Body Gerado para Groq: " + requestBody);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(GROQ_API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + GROQ_API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Falha na requisição à API Groq: " + response.statusCode() + " " + response.body());
        }

        String respostaJson = response.body();
        System.out.println("Resposta JSON da API Groq: " + respostaJson);

        return parsearRespostaComGson(respostaJson);
    }

    private List<Livro> parsearRespostaComGson(String respostaJson) {
        try {
            GroqResponse apiResponse = gson.fromJson(respostaJson, GroqResponse.class);
            if (apiResponse == null || apiResponse.choices == null || apiResponse.choices.isEmpty()) {
                System.err.println("Resposta da API Groq vazia ou em formato inesperado.");
                return Collections.emptyList();
            }
            String conteudo = apiResponse.choices.get(0).message.content;

            // Limpa o bloco de código markdown, se houver
            if (conteudo != null && conteudo.startsWith("```json")) {
                conteudo = conteudo.substring(7, conteudo.length() - 3).trim();
            }

            LivrosContainer container = gson.fromJson(conteudo, LivrosContainer.class);
            return container != null ? container.livros : Collections.emptyList();
        } catch (Exception e) {
            System.err.println("Erro ao fazer o parse do JSON com Gson: " + e.getMessage());
            return Collections.emptyList();

        }
    }

    // --- Classes auxiliares para o parsing do JSON com Gson ---
    private static class GroqResponse { List<Choice> choices; }
    private static class Choice { Message message; }
    private static class Message { String content; }
    private static class LivrosContainer { List<Livro> livros; }

    public static void main(String[] args) {

        BuscadorLivros buscador = new BuscadorLivros();
        DbConnection client = new DbConnection(URL_JDBC,USER_JDBC,PASSWORD_JDBC);
        Boolean clientConnect = client.connect();

        try {
            List<Livro> livros = buscador.buscarLivros("cadastre todos os livros do autor Jorge Amado");

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
    }
}
