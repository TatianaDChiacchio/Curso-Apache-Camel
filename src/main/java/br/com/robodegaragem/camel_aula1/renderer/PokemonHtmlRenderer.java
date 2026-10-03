package br.com.robodegaragem.camel_aula1.renderer;

import br.com.robodegaragem.camel_aula1.model.Pokemon;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PokemonHtmlRenderer {

    public String render(List<Pokemon> pokemons) {

        StringBuilder html = new StringBuilder();

        html.append("""
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                    <meta charset="UTF-8">
                    <title>Pokémons</title>

                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            background: #f4f4f4;
                            padding: 30px;
                        }

                        h1 {
                            text-align: center;
                        }

                        .container {
                            display: flex;
                            flex-wrap: wrap;
                            justify-content: center;
                            gap: 20px;
                        }

                        .pokemon {
                            background: white;
                            width: 220px;
                            padding: 20px;
                            border-radius: 12px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                            text-align: center;
                        }

                        .pokemon img {
                            width: 150px;
                            height: 150px;
                            image-rendering: pixelated;
                        }

                        .pokemon h2 {
                            text-transform: capitalize;
                        }

                        .pokemon p {
                            margin: 5px;
                        }
                    </style>
                </head>

                <body>

                <h1>Pokémons encontrados</h1>

                <div class="container">
                """);

        for (Pokemon pokemon : pokemons) {

            String imagem = "";

            if (pokemon.getSprites() != null
                    && pokemon.getSprites().getFront_default() != null) {

                imagem = pokemon.getSprites().getFront_default();
            }

            html.append("<div class=\"pokemon\">");

            html.append("<img src=\"")
                    .append(imagem)
                    .append("\" alt=\"")
                    .append(pokemon.getName())
                    .append("\">");

            html.append("<h2>")
                    .append(pokemon.getName())
                    .append("</h2>");

            html.append("<p><strong>ID:</strong> ")
                    .append(pokemon.getId())
                    .append("</p>");

            html.append("<p><strong>Altura:</strong> ")
                    .append(pokemon.getHeight())
                    .append("</p>");

            html.append("<p><strong>Peso:</strong> ")
                    .append(pokemon.getWeight())
                    .append("</p>");

            html.append("</div>");
        }

        html.append("""
                </div>
                </body>
                </html>
                """);

        return html.toString();
    }
}