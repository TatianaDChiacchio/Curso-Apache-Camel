package br.com.robodegaragem.camel_aula1.route.pokemon;

import br.com.robodegaragem.camel_aula1.model.Pokemon;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PokemonIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("platform-http:/pokemon?httpMethodRestrict=GET")
                .routeId("rota-principal-pokemon")
                .log("***************************************************")
                .log("Arquivo recebido: ${header.CamelFileName}")
                // carrega o arquixo xml
                .pollEnrich("file:input-pokemon" + "?fileName=pokemon.xml" + "&noop=true", 5000)
                // cria uma lista de Pokemon
                .process(exchange -> {
                    exchange.setProperty("pokemonsProcessados", new ArrayList<Pokemon>() );
                })
                .split()
                    .xpath("/pokemons/name")
                    // o split criar uma exchange para cada linha. Cada exchange vai passar por cada uma das rotas
                    // e dentro de cada exchange temos, por exemplo: Body = <name>bulbasaur</name>
                    .log("---------------------------------------------------")
                    .log("Pokemon encontrado no XML: ${body}")
                    .to("direct:rota-pokeapi")
                    .to("direct:rota-pokedex")
                    // Neste momento o Body é Pokemon
                    .process(exchange -> {
                        Pokemon pokemon = exchange.getMessage().getBody(Pokemon.class);
                        List<Pokemon> pokemons = exchange.getProperty("pokemonsProcessados", List.class);
                        pokemons.add(pokemon);
                    })
                .end()
                // Agora substituímos o XML pela lista
                .setBody( exchangeProperty("pokemonsProcessados"))
                .log("Resultado agregado: ${body}")
                // transforma a lista de Pokemon em json
                .marshal()
                    .json()
                .setHeader("Content-Type", constant("application/json"));
    }
}