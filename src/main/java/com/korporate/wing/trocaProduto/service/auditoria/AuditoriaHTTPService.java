package com.korporate.wing.trocaProduto.service.auditoria;

import com.korporate.wing.trocaProduto.model.auditoria.AuditoriaHTTP;
import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.wing.trocaProduto.repository.auditoria.AuditoriaHTTPRepository;
import com.korporate.wing.trocaProduto.service.processamento.TransacaoService;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AuditoriaHTTPService
{
    @Autowired
    private AuditoriaHTTPRepository repository;
    @Autowired
    private TransacaoService transacaoService;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public AuditoriaHTTP saveRequest(Processamento processamento, String requestBody, String url, HttpMethod httpMethod)
    {
        // cria uma Transacao no Processamento pra informar que o request será feito
        Transacao transacao = processamento.criarTransacao(Transacao.Operacao.REQUEST_ENVIADO);
        transacaoService.save(transacao);

        // monta o objeto apenas com os dados do request
        AuditoriaHTTP auditoriaHTTP = new AuditoriaHTTP();
        auditoriaHTTP.setHttpMethod(httpMethod.name());
        auditoriaHTTP.setUrl(url);
        auditoriaHTTP.setRequestBody(requestBody);
        auditoriaHTTP.setRequestTimestamp(ZonedDateTime.now());

        processamento.adicionarAuditoria(auditoriaHTTP);

        // salva no banco
        return repository.save(auditoriaHTTP);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateResponse(AuditoriaHTTP auditoriaHTTP, HttpResponse<JsonNode> response)
    {
        // cria uma Transacao no Processamento pra informar que o response chegou
        Processamento processamento = auditoriaHTTP.getProcessamento();
        Transacao transacao = processamento.criarTransacao(Transacao.Operacao.RESPONSE_RECEBIDO);
        transacaoService.save(transacao);

        // pega o HttpStatus e body do Response
        String responseBody = null;
        int httpStatus = 0;
        try
        {
            httpStatus = response.getStatus();
            responseBody = response.getBody().toString();
        }
        catch (Exception e)
        {
        }

        // seta os valores no objeto
        auditoriaHTTP.setResponseTimestamp(ZonedDateTime.now());
        auditoriaHTTP.setHttpStatus(httpStatus);
        auditoriaHTTP.setResponseBody(responseBody);

        // atualiza no banco
        repository.update(auditoriaHTTP);
    }

}
