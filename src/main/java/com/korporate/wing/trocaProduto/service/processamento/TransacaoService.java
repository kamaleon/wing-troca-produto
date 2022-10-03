package com.korporate.wing.trocaProduto.service.processamento;

import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.wing.trocaProduto.repository.processamento.TransacaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;


@Service
@Transactional
public class TransacaoService
{
    @Autowired
    private TransacaoRepository repository;

    public Transacao save(Transacao objeto)
    {
        return repository.save(objeto);
    }

}