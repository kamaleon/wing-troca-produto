package com.korporate.wing.trocaProduto.util;

import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.spring.persistence.multitenancy.TenantContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogProcessamentoUtil
{
    private static final Logger logger = LoggerFactory.getLogger(LogProcessamentoUtil.class);
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("ddMMyyyyHHmmss");
    private static final int operacaoCount = 4;

    // ThreadLocal com os checkpoints de cada thread
    private static ThreadLocal<Instant> checkpoint = new ThreadLocal();

    public static void iniciarMDC(Processamento processamento)
    {
        // limpa o MDC do processamento anterior
        MDC.clear();

        String dataHora = LocalDateTime.now().format(dateFormat);
        String threadId = dataHora + "_" + Thread.currentThread().getId();
        MDC.put("trackId", threadId);
        MDC.put("tenant", TenantContext.getTenant());
        MDC.put("processamento", String.valueOf(processamento.getId()));
        MDC.put("solicitacaoId", String.valueOf(processamento.getSolicitacaoId()));
        MDC.put("linha", String.valueOf(processamento.getLinha()));
        MDC.put("rotina", "TROCA_DE_PRODUTO"); // TODO

        // seta o checkpoint
        checkpoint.set(Instant.now());
    }

    public static void logTransacao(Transacao.Operacao operacao)
    {
        // calcula a diferença do último checkpoint
        Duration tempoExecucao = Duration.between(checkpoint.get(), Instant.now());
        double duration = tempoExecucao.toMillis()/1000d;
        MDC.put("duration", String.valueOf(duration));

        // calcula a etapa desta Operacao
        int etapa = operacao.ordinal() + 1;

        // gera o log
        String message = String.format("Processamento - Transação %s/%s: %s", etapa, operacaoCount, operacao.getDescricao());
        logger.info(message);

        // atualiza o checkpoint
        checkpoint.set(Instant.now());
    }

    public static void erro(String erro)
    {
        // calcula a diferença do último checkpoint
        Duration tempoExecucao = Duration.between(checkpoint.get(), Instant.now());
        double duration = tempoExecucao.toMillis()/1000d;
        MDC.put("duration", String.valueOf(duration));

        // gera o log
        logger.error(erro);

        // atualiza o checkpoint
        checkpoint.set(Instant.now());
    }

}
