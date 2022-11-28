package com.korporate.wing.trocaProduto.service.processamento;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import com.korporate.wing.trocaProduto.model.auditoria.AuditoriaHTTP;
import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.wing.trocaProduto.payload.comunicacaoCards.CardsAlterarProdutoDTO;
import com.korporate.wing.trocaProduto.payload.storm.IntegracaoServicosEnum;
import com.korporate.wing.trocaProduto.payload.storm.PayloadIntegracaoResponse;
import com.korporate.wing.trocaProduto.service.auditoria.AuditoriaHTTPService;
import com.korporate.wing.trocaProduto.service.aws.S3Service;
import com.korporate.wing.trocaProduto.service.aws.SQSService;
import com.korporate.wing.trocaProduto.util.IntegracaoServicosUtil;
import com.korporate.wing.trocaProduto.util.LogProcessamentoUtil;
import com.korporate.wing.trocaProduto.util.WingDateUtil;
import com.opencsv.CSVWriter;
import kong.unirest.HttpMethod;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NonTransactionalService
{
    @Autowired
    private ProcessamentoService processamentoService;
    @Autowired
    private AuditoriaHTTPService auditoriaHTTPService;
    @Autowired
    private IntegracaoServicosUtil integracaoServicosUtil;
    @Autowired
    private SQSService sqsService;
    @Autowired
    private S3Service s3Service;


    public void realizarProcessamento(Processamento processamento, Long qtdItensSolicitacao, String receiptHandle)
    {
        // verifica se o Processamento já existe
        Optional<Processamento> optionalProcessamento = processamentoService.find(processamento.getSolicitacaoId(), processamento.getLinha());

        // se não existir realiza o cadastro de um novo Processamento
        if (optionalProcessamento.isEmpty())
        {
            // (new Transactional) cria uma Transacao de item lido da fila, salva o Processamento e remove o item da fila
            processamentoService.salvarProcessamentoERemoverItemDaFila(processamento, receiptHandle);

            // inicia o MDC e gera o log de item lido da fila
            LogProcessamentoUtil.iniciarMDC(processamento);
            LogProcessamentoUtil.logTransacao(Transacao.Operacao.CARREGADO_DA_FILA);
        }
        // caso contrário, pega o Processamento existente e dá continuidade
        else
        {
            processamento = optionalProcessamento.get();
            LogProcessamentoUtil.iniciarMDC(processamento);
        }

        // se o Processamento já estiver concluído, vamos rejeitar
        if (processamento.getStatus() == Processamento.Status.CONCLUIDO)
        {
            String mensagemErro = String.format("O processamento %s linha %d foi rejeitado porque já foi processado em %s", processamento.getSolicitacaoId(), processamento.getLinha(), WingDateUtil.toStringddMMyyyyHHmmss(processamento.getUpdateDate()));
            LogProcessamentoUtil.erro(mensagemErro);
            return;
        }
        // faz a requisição na plataforma da Conductor
        else
        {
            acessarAPIConductor(processamento);
        }

        if (processamentoService.numeroLinhasProcessadas(processamento.getSolicitacaoId()).compareTo(qtdItensSolicitacao) >= 0) {
            sqsService.enviarSolicitacaoParaFilaDeConclusao(processamento.getSolicitacaoId());
        }
    }

    private void acessarAPIConductor(Processamento processamento)
    {
        // se não tem response, vamos realizar o request + response novamente
        if (!processamento.existeTransacao(Transacao.Operacao.RESPONSE_RECEBIDO))
        {
            PayloadIntegracaoResponse integracao = integracaoServicosUtil.carregarIntegracao(IntegracaoServicosEnum.CARDS, false);

            CardsAlterarProdutoDTO dto = CardsAlterarProdutoDTO.fill(processamento.getPayload());

            // (new Transactional) cria um AuditoriaHTTP com os dados do request e salva no banco
            AuditoriaHTTP auditoriaHTTP = auditoriaHTTPService.saveRequest(processamento, "[PLACEHOLDER]requestBody", "[PLACEHOLDER]url", HttpMethod.POST);
            LogProcessamentoUtil.logTransacao(Transacao.Operacao.REQUEST_ENVIADO);

            // faz a requisicao
            HttpResponse<JsonNode> jsonNodeHttpResponse = null;
            var i = 0;
            while(i < 10){
                jsonNodeHttpResponse = Unirest.patch(integracao.getUrlBase() + "/api/api/v3/portadores/alterar-produto-portador")
                        .header("accept", "application/json")
                        .header("Authorization", integracao.getLastToken())
                        .header("Content-type","application/json")
                        .queryString("idPortador", dto.getIdPortador())
                        .queryString("idProduto", dto.getIdProduto())
                        .asJson();
                if(jsonNodeHttpResponse.getStatus() == 401){
                    integracao = integracaoServicosUtil.carregarIntegracao(IntegracaoServicosEnum.CARDS, true);
                } else {
                    break;
                }
                i++;
            }
            // (new Transactional) atualiza o AuditoriaHTTP com os dados do response
            auditoriaHTTPService.updateResponse(auditoriaHTTP, jsonNodeHttpResponse);

            LogProcessamentoUtil.logTransacao(Transacao.Operacao.RESPONSE_RECEBIDO);

            if(!jsonNodeHttpResponse.isSuccess() ){
                processamento.setStatus(Processamento.Status.ERRO);
                if(jsonNodeHttpResponse.getBody() != null){
                    processamento.setResponse(jsonNodeHttpResponse.getBody().getObject().get("status").toString() +
                            " - " + jsonNodeHttpResponse.getBody().getObject().get("title").toString() +
                            " - " + jsonNodeHttpResponse.getBody().getObject().get("detail").toString());
                }else{
                    processamento.setResponse(jsonNodeHttpResponse.getStatus() + " - " + jsonNodeHttpResponse.getStatusText());
                }
                processamentoService.concluirProcesso(processamento, Processamento.Status.ERRO);
            } else {
                processamento.setResponse(jsonNodeHttpResponse.getStatus() + " - " + jsonNodeHttpResponse.getStatusText());
                processamentoService.concluirProcesso(processamento, Processamento.Status.CONCLUIDO);
            }

            LogProcessamentoUtil.logTransacao(Transacao.Operacao.PROCESSO_CONCLUIDO);
        }

    }

    public void concluirProcessamentoSolicitacao(UUID solicitacaoId) {
        List<Processamento> processamentos = processamentoService.getLinhasProcessadas(solicitacaoId);

        File arquivoResultado = new File(System.getProperty("java.io.tmpdir") + File.separator + TenantContext.getTenant() + "_RESULT_" + solicitacaoId + ".csv");

        CSVWriter writer;
        try {
            int count = 1;
            for (Processamento proc : processamentos) {

                String[] payloadArray = proc.getPayload().split(",");

                String[] resultLine = new String[]{
                        payloadArray[0],
                        payloadArray[1],
                        "#####",
                        String.valueOf(proc.getStatus() == Processamento.Status.CONCLUIDO),
                        proc.getStatus() == Processamento.Status.CONCLUIDO ? proc.getResponse() : "",
                        proc.getStatus() == Processamento.Status.CONCLUIDO ? "" : proc.getResponse()
                };

                writer = new CSVWriter(new FileWriter(arquivoResultado, count > 1));
                writer.writeNext(resultLine);
                writer.close();

                count++;
            }
        } catch (IOException e){
            e.printStackTrace();
        }

        s3Service.upload(arquivoResultado);
    }
}
