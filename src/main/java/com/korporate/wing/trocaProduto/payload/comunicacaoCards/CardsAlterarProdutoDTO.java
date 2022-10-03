package com.korporate.wing.trocaProduto.payload.comunicacaoCards;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.korporate.spring.util.KorporateBeanUtil;

import java.io.Serializable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class CardsAlterarProdutoDTO implements Serializable {
    private Long idPortador;
    private Integer idProduto;

    public CardsAlterarProdutoDTO(Long idPortador, Integer idProduto) {
        this.idPortador = idPortador;
        this.idProduto = idProduto;
    }

    public CardsAlterarProdutoDTO() {
    }

    public static CardsAlterarProdutoDTO fill(String alterarProdutoJson)
    {
        CardsAlterarProdutoDTO dto = new CardsAlterarProdutoDTO();

        String[] properties = alterarProdutoJson.split(",");
        dto.setIdPortador(Long.parseLong(properties[0]));
        dto.setIdProduto(Integer.parseInt(properties[1]));

        return dto;
    }

    public static String toJson(CardsAlterarProdutoDTO dto){
        return KorporateBeanUtil.getObjectAsJson(dto);
    }

    public Long getIdPortador() {
        return idPortador;
    }

    public void setIdPortador(Long idPortador) {
        this.idPortador = idPortador;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(Integer idProduto) {
        this.idProduto = idProduto;
    }
}
