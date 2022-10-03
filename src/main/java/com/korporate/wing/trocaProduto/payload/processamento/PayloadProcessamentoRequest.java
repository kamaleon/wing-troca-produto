package com.korporate.wing.trocaProduto.payload.processamento;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import org.springframework.beans.BeanUtils;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PayloadProcessamentoRequest {

    private UUID solicitacaoId;
    private long linha;
    private String payload;

    public static Processamento convert(PayloadProcessamentoRequest payload) {
        Processamento entity = null;
        if (payload != null) {
            entity = new Processamento();
            BeanUtils.copyProperties(payload, entity);
        }
        return entity;
    }

    public UUID getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(UUID solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public long getLinha() {
        return linha;
    }

    public void setLinha(long linha) {
        this.linha = linha;
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload) {
        this.payload = payload;
    }
}
