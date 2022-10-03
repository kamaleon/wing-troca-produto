package com.korporate.wing.trocaProduto.service.processamento;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.korporate.spring.persistence.filter.Order;
import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.wing.trocaProduto.repository.processamento.ProcessamentoRepository;
import com.korporate.spring.persistence.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class ProcessamentoService {

    @Autowired
    private ProcessamentoRepository repository;
    @Autowired
    private TransacaoService transacaoService;
    @Autowired
    private AmazonSQSAsync amazonSQS;

    @Value("${aws.sqs.trocaProdutoProcessamentoUrl}")
    private String sqsBaseURL;

    public Optional<Processamento> find(UUID solicitacaoId, long linha)
    {
        Filter filter = new Filter()
               .equals("solicitacaoId", solicitacaoId)
                .equals("linha", linha);

        Optional<Processamento> optional = repository.find(filter);

        // carrega as agregações porque este método é chamado num processo não Transactional
        if (optional.isPresent())
        {
            optional.get().getTransacaoList().forEach(transacao -> transacao.getId());
            optional.get().getAuditoriaHTTPList().forEach(auditoriaHTTP -> auditoriaHTTP.getId());
        }

        return optional;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void salvarProcessamentoERemoverItemDaFila(Processamento processamento, String receiptHandle)
    {
        // adiciona a Transacao de item lido da fila
        processamento.criarTransacao(Transacao.Operacao.CARREGADO_DA_FILA);

        // salva o Processamento
        processamento.setStatus(Processamento.Status.INICIADO);
        repository.save(processamento);

        // remove o item da fila
        // amazonSQS.deleteMessage(sqsBaseURL, receiptHandle);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void concluirProcesso(Processamento processamento, Processamento.Status statusProcessamento)
    {
        // adiciona a Transacao de item lido da fila
        Transacao.Operacao statusOperacao = Transacao.Operacao.PROCESSO_CONCLUIDO;
        if (statusProcessamento == Processamento.Status.ERRO)
            statusOperacao = Transacao.Operacao.ERRO;

        Transacao transacao = processamento.criarTransacao(statusOperacao);
        transacaoService.save(transacao);

        // muda o status do Processamento
        processamento.setStatus(statusProcessamento);

        // salva o Processamento
        repository.update(processamento);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Long numeroLinhasProcessadas(UUID solicitacaoId){
        Filter filter = new Filter();
        filter.equals("solicitacaoId", solicitacaoId);
        filter.in("status", new Processamento.Status[]{Processamento.Status.CONCLUIDO, Processamento.Status.ERRO});
        return repository.count(filter);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<Processamento> getLinhasProcessadas(UUID solicitacaoId){
        Filter filter = new Filter();
        filter.equals("solicitacaoId", solicitacaoId);
        filter.in("status", new Processamento.Status[]{Processamento.Status.CONCLUIDO, Processamento.Status.ERRO});
        filter.addOrder("linha", Order.ASC);
        return repository.list(filter);
    }

}