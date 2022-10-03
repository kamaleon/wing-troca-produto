package com.korporate.wing.trocaProduto.model.processamento;

import com.korporate.wing.trocaProduto.model.auditoria.AuditoriaHTTP;
import com.korporate.spring.persistence.domain.KorporateEntity;
import org.hibernate.envers.NotAudited;

import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "T_PROCESSAMENTO")
@AttributeOverride(name = "id", column = @Column(name = "ID_PROCESSAMENTO"))
public class Processamento extends KorporateEntity
{
    public enum Status
    {
        INICIADO("Iniciado"),
        CONCLUIDO("Concluído"),
        ERRO("Concluído com erro");

        Status(String descricao) {
            this.descricao = descricao;
        }

        private String descricao;

        public String getDescricao() {
            return descricao;
        }
    }

    @Enumerated(EnumType.STRING)
    private Status status;
    private UUID solicitacaoId;
    private long linha;
    private String payload;
    private String response;

    // Transacao
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST, mappedBy = "processamento")
    @NotAudited
    private List<Transacao> transacaoList;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "processamento")
    @NotAudited
    private List<AuditoriaHTTP> auditoriaHTTPList;

    public Transacao criarTransacao(Transacao.Operacao operacao) {
        if (transacaoList == null)
            transacaoList = new LinkedList();

        // monta o objeto Transacao
        Transacao transacao = new Transacao();
        transacao.setOperacao(operacao);
        transacao.setProcessamento(this);
        transacao.setData(ZonedDateTime.now());

        // faz o relacionamento 1:N
        transacao.setProcessamento(this);
        transacaoList.add(transacao);

        return transacao;
    }

    public void adicionarAuditoria(AuditoriaHTTP auditoriaHTTP) {
        if (auditoriaHTTPList == null)
            auditoriaHTTPList = new LinkedList();

        auditoriaHTTP.setProcessamento(this);
        auditoriaHTTPList.add(auditoriaHTTP);
    }

    public boolean existeTransacao(Transacao.Operacao operacao)
    {
        return transacaoList.stream()
                .anyMatch(transacao -> transacao.getOperacao() == operacao);
    }

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public UUID getSolicitacaoId() {
        return solicitacaoId;
    }

    public void setSolicitacaoId(UUID solicitacaoId) {
        this.solicitacaoId = solicitacaoId;
    }

    public long getLinha()
    {
        return linha;
    }

    public void setLinha(long linha)
    {
        this.linha = linha;
    }

    public String getPayload()
    {
        return payload;
    }

    public void setPayload(String payload)
    {
        this.payload = payload;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public List<Transacao> getTransacaoList()
    {
        return transacaoList;
    }

    public void setTransacaoList(List<Transacao> transacaoList)
    {
        this.transacaoList = transacaoList;
    }

    public List<AuditoriaHTTP> getAuditoriaHTTPList()
    {
        return auditoriaHTTPList;
    }

    public void setAuditoriaHTTPList(List<AuditoriaHTTP> auditoriaHTTPList)
    {
        this.auditoriaHTTPList = auditoriaHTTPList;
    }
}
