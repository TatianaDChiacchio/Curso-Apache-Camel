package br.com.robodegaragem.camel_aula1.route.pokemon;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class PokeApiRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:rota-pokeapi")
                .routeId("rota-pokeapi")
                // salva na propriedade pokemonName o nome do pokemon que está em xpath("/name/text()"
                .setProperty("pokemonName", xpath("/name/text()"))
                // por exemplo, agora temos:
                // Body:
                // <name>bulbasaur</name>
                //
                // Property:
                // pokemonName = bulbasaur
                // aqui busca da propriedade que acabou de ser criada, o nome do pokemon
                .log("Buscando o Pokémon: ${exchangeProperty.pokemonName}")
                //importante porque estamos fazendo duas chamadas HTTP consecutivas: primeiro para a PokéAPI e depois para a Pokédex.
                //Não queremos que informações HTTP da primeira chamada "vazem" para a segunda.
                .removeHeaders("*")
                // "Na próxima chamada HTTP, use o método GET."
                .setHeader("CamelHttpMethod", constant("GET"))
                // "Quero a resposta sem compressão."
                .setHeader("Accept-Encoding", constant("identity")
                )
                // GET https://pokeapi.co/api/v2/pokemon/bulbasaur
                // A resposta da PokéAPI substitui o Body do Exchange.
                // antes o body era: <name>bulbasaur</name>
                // depois de consultar a api, o body é substituído e agora temos no body:
                // {
                //   "id": 1,
                //   "name": "bulbasaur",
                //   "height": 7,
                //   "weight": 69,
                //   ...
                //}
                .toD("https://pokeapi.co/api/v2/pokemon/" + "${exchangeProperty.pokemonName}")
                .log("Dados recebidos da PokéAPI para: " + "${exchangeProperty.pokemonName}");
    }
}