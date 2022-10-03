package com.korporate.wing.trocaProduto.listener;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import com.korporate.wing.trocaProduto.service.processamento.NonTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class SQSConclusaoListener {
    @Autowired
    private NonTransactionalService nonTransactionalService;

    @SqsListener(value = "${aws.sqs.trocaProdutoConclusaoUrl}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onMessage(String solicitacaoId, @Headers Map<String, Object> headers) {

        TenantContext.setTenant((String) headers.get("tenant"));

        nonTransactionalService.concluirProcessamentoSolicitacao(UUID.fromString(solicitacaoId));
    }
}
