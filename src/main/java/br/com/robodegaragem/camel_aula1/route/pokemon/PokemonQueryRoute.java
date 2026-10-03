package br.com.robodegaragem.camel_aula1.route.pokemon;

import br.com.robodegaragem.camel_aula1.model.Pokemon;
import br.com.robodegaragem.camel_aula1.renderer.PokemonHtmlRenderer;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class PokemonQueryRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
    //http://localhost:8080/pokemon/parametros?pokemons=bulbasaur,charmander,pikachu,squirtle
        from("platform-http:/pokemon/parametros?httpMethodRestrict=GET")
                .routeId("rota-pokemon-parametros")

                // Recupera o parâmetro: ?pokemons=bulbasaur,charmander,pikachu e guarda em uma Exchange Property.
                .setProperty("pokemonQuery",header("pokemons"))
                .log("Parâmetro recebido: ${exchangeProperty.pokemonQuery}")

                // Converte: "bulbasaur,charmander,pikachu" para: List<String>
                .process(exchange -> {
                    String parametro = exchange.getProperty("pokemonQuery",String.class );
                    List<String> nomes = Arrays.stream(parametro.split(","))
                                    .map(String::trim)
                                    .filter(nome -> !nome.isEmpty())
                                    .toList();
                    // A lista de nomes passa a ser o Body.
                    exchange.getMessage().setBody(nomes);
                    // Cria uma lista para armazenar os objetos Pokemon retornados durante o processamento.
                    exchange.setProperty("pokemonsProcessados",new ArrayList<Pokemon>());
                })
                .log("Lista de Pokémons: ${body}")

                // Divide a List<String>. Cada execução terá no Body apenas um nome:
                .split(body())
                    .log("------------------------------------------")
                    .log("Pokémon solicitado: ${body}")
                    // Nossa PokeApiRoute já foi criada para receber: <name>bulbasaur</name> Então adaptamos o nome recebido para esse formato antes de chamar a sub-rota.
                    .setBody( simple("<name>${body}</name>"))
                    .to("direct:rota-pokeapi")
                    .to("direct:rota-pokedex")

                    .process(exchange -> {
                        Pokemon pokemon = exchange.getMessage().getBody(Pokemon.class);
                        List<Pokemon> pokemons = exchange.getProperty("pokemonsProcessados",List.class);
                        pokemons.add(pokemon);
                    })
                .end()

                // Terminou o split. Colocamos a lista completa de Pokemon no Body.
                .setBody( exchangeProperty("pokemonsProcessados"))
                .log("Pokémons processados: ${body}")

                .bean( PokemonHtmlRenderer.class,"render")

                // Informa ao navegador que o Body deve ser interpretado como HTML.
                .setHeader("Content-Type",constant("text/html; charset=UTF-8")
                );
    }
}