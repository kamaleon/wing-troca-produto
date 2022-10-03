package com.korporate.wing.trocaProduto.repository.processamento;

import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.spring.persistence.repository.KorporateRepository;
import org.springframework.stereotype.Repository;

@Repository
public class ProcessamentoRepository extends KorporateRepository<Processamento>
{

    public ProcessamentoRepository() {
        super(Processamento.class);
    }
}