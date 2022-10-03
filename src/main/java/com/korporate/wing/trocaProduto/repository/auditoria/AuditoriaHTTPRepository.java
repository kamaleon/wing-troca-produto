package com.korporate.wing.trocaProduto.repository.auditoria;

import com.korporate.wing.trocaProduto.model.auditoria.AuditoriaHTTP;
import com.korporate.spring.persistence.repository.KorporateRepository;
import org.springframework.stereotype.Repository;

@Repository
public class AuditoriaHTTPRepository extends KorporateRepository<AuditoriaHTTP>
{
    public AuditoriaHTTPRepository()
    {
        super(AuditoriaHTTP.class);
    }
}
