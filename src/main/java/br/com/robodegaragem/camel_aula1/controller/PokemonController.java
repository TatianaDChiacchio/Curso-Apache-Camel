package br.com.robodegaragem.camel_aula1.controller;

import br.com.robodegaragem.camel_aula1.model.Pokemon;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pokedex")
public class PokemonController {

    @PostMapping
    // "Pegue o JSON que chegou no corpo do POST e transforme em um objeto Pokemon."
    // o body que veio na exchange, porque temos @RequestBody, se encaixa na classe Pokemon e vira um json de pokemon
    public ResponseEntity<Pokemon> savePokemon(@RequestBody Pokemon pokemon) {

        System.out.println("[POST] - " + pokemon);

        return ResponseEntity.ok(pokemon);
    }
}