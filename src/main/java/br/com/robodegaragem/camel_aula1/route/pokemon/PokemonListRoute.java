package br.com.robodegaragem.camel_aula1.route.pokemon;

import br.com.robodegaragem.camel_aula1.model.Pokemon;
import br.com.robodegaragem.camel_aula1.model.PokemonRequest;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PokemonListRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // http://localhost:8080/pokemon/lista
        from("platform-http:/pokemon/lista?httpMethodRestrict=POST")
                .routeId("rota-pokemon-lista")
                .log("Lista recebida: ${body}")
                // JSON -> PokemonRequest
                .unmarshal()
                .json(PokemonRequest.class)
                // Lista que armazenará os resultados
                .process(exchange -> {
                    exchange.setProperty("pokemonsProcessados",new ArrayList<Pokemon>());
                })
                // Divide PokemonRequest.pokemons
                .split(simple("${body.pokemons}"))
                // Aqui o Body será, por exemplo:
                // "bulbasaur"
                .log("Pokemon solicitado: ${body}")
                // Adapta para o formato esperado pela PokeApiRoute
                .setBody(simple("<name>${body}</name>"))
                // Reutiliza as sub-rotas existentes
                .to("direct:rota-pokeapi")
                .to("direct:rota-pokedex")

                // Neste momento o Body é Pokemon
                .process(exchange -> {
                    Pokemon pokemon = exchange.getMessage().getBody(Pokemon.class);

                    List<Pokemon> pokemons = exchange.getProperty("pokemonsProcessados",List.class);
                    pokemons.add(pokemon);
                })
                .end()

                // Define a lista como resposta
                .setBody(exchangeProperty("pokemonsProcessados"))
                .log("Pokemons processados: ${body}")
                // List<Pokemon> -> JSON
                .marshal()
                    .json()
                .setHeader("Content-Type",constant("application/json"));
    }
}