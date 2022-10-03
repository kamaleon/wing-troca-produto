package com.korporate.wing.trocaProduto.listener;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.wing.trocaProduto.payload.processamento.PayloadProcessamentoRequest;
import com.korporate.wing.trocaProduto.service.processamento.NonTransactionalService;
import com.korporate.spring.util.KorporateBeanUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.listener.SqsMessageDeletionPolicy;
import org.springframework.cloud.aws.messaging.listener.annotation.SqsListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class SQSProcessamentoListener
{
    @Autowired
    private NonTransactionalService nonTransactionalService;

    @SqsListener(value = "${aws.sqs.trocaProdutoProcessamentoUrl}", deletionPolicy = SqsMessageDeletionPolicy.ON_SUCCESS)
    public void onMessage(String data, @Headers Map<String, Object> headers)
    {
        PayloadProcessamentoRequest payload = KorporateBeanUtil.getJsonAsObject(data, PayloadProcessamentoRequest.class);
        Processamento processamento = PayloadProcessamentoRequest.convert(payload);

        processamento.setStatus(Processamento.Status.INICIADO);

        TenantContext.setTenant((String) headers.get("tenant"));

        nonTransactionalService.realizarProcessamento(processamento, (Long) headers.get("SolicitacaoItemCount"), (String) headers.get("ReceiptHandle"));
    }
}
