package com.korporate.wing.trocaProduto.repository.processamento;

import com.korporate.wing.trocaProduto.model.processamento.Transacao;
import com.korporate.spring.persistence.repository.KorporateRepository;
import org.springframework.stereotype.Repository;

@Repository
public class TransacaoRepository extends KorporateRepository<Transacao>
{

    public TransacaoRepository() {
        super(Transacao.class);
    }
}