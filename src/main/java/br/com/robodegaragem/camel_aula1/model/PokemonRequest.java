package br.com.robodegaragem.camel_aula1.model;

import java.util.List;

public class PokemonRequest {

    private List<String> pokemons;

    public PokemonRequest() {
    }

    public List<String> getPokemons() {
        return pokemons;
    }

    public void setPokemons(List<String> pokemons) {
        this.pokemons = pokemons;
    }
}