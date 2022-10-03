package com.korporate.wing.trocaProduto.model.processamento;

import com.korporate.spring.persistence.domain.KorporateEntity;
import org.hibernate.envers.NotAudited;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "T_TRANSACAO")
@SequenceGenerator(name = "sequence", sequenceName = "S_TRANSACAO", allocationSize = 1)
@AttributeOverride(name = "id", column = @Column(name = "ID_TRANSACAO"))
public class Transacao extends KorporateEntity
{
	public enum Operacao
	{
		CARREGADO_DA_FILA("Carregado da fila"),
		REQUEST_ENVIADO("Request enviado"),
		RESPONSE_RECEBIDO("Response recebido"),
		PROCESSO_CONCLUIDO("Processo concluído"),
		ERRO("Erro");

		private String descricao;

		Operacao(String str)
		{
			descricao = str;
		}

		public String getDescricao()
		{
			return descricao;
		}
	}

	@Enumerated(EnumType.STRING)
	private Operacao operacao;

	private ZonedDateTime data;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PROCESSAMENTO")
	@NotAudited
	private Processamento processamento;

	public Operacao getOperacao()
	{
		return operacao;
	}

	public void setOperacao(Operacao operacao)
	{
		this.operacao = operacao;
	}

	public ZonedDateTime getData()
	{
		return data;
	}

	public void setData(ZonedDateTime data)
	{
		this.data = data;
	}

	public Processamento getProcessamento()
	{
		return processamento;
	}

	public void setProcessamento(Processamento processamento)
	{
		this.processamento = processamento;
	}
}