package com.korporate.wing.trocaProduto.payload.processamento;

public class DTOContagem {
    private Long quantidadeProcessada;

    public DTOContagem(Long quantidadeProcessada) {
        this.quantidadeProcessada = quantidadeProcessada;
    }

    public Long getQuantidadeProcessada() {
        return quantidadeProcessada;
    }

    public void setQuantidadeProcessada(Long quantidadeProcessada) {
        this.quantidadeProcessada = quantidadeProcessada;
    }
}
