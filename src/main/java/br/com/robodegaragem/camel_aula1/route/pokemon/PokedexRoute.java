package br.com.robodegaragem.camel_aula1.route.pokemon;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import br.com.robodegaragem.camel_aula1.model.Pokemon;
@Component
public class PokedexRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        from("direct:rota-pokedex")
                .routeId("rota-pokedex")
                // agora o body não é mais o xml e sim o json com todos os dados do pokemon
                .log("Salvando dados de Pokémon na Pokédex: ${exchangeProperty.pokemonName}")
                //.log("BODY ANTES DO POST: ${body}")
                .removeHeaders("CamelHttp*")
                .setHeader("CamelHttpMethod", constant("POST"))
                .setHeader("Content-Type", constant("application/json"))
                // aqui chamou para o controller
                .to("http://localhost:8080/pokedex")
                // transforma o retorno da api em um json de Pokemon
                .unmarshal()
                .json(Pokemon.class)
                .log("Resposta do Controller: ${body}");
    }
}