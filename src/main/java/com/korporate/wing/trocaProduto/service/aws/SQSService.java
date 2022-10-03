package com.korporate.wing.trocaProduto.service.aws;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.korporate.spring.persistence.multitenancy.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class SQSService
{
    @Value("${aws.sqs.trocaProdutoConclusaoUrl}")
    private String conclusaoQueue;

    @Autowired
    AmazonSQS amazonSQS;

    public void enviarSolicitacaoParaFilaDeConclusao(UUID solicitacaoId) {

        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put("tenant", new MessageAttributeValue()
                .withDataType("String")
                .withStringValue(TenantContext.getTenant()));

        // cria o entry pro batch de mensagens
        SendMessageRequest entry = new SendMessageRequest()
                .withQueueUrl(conclusaoQueue)
                .withMessageBody(String.valueOf(solicitacaoId))
                .withMessageGroupId("storm-api")
                .withMessageAttributes(messageAttributes);

        amazonSQS.sendMessage(entry);
    }
}
