package com.korporate.wing.trocaProduto.repository.parametro;

import com.korporate.wing.trocaProduto.model.parametro.Parametro;
import com.korporate.spring.parametro.repository.KorporateParametroRepository;
import com.korporate.spring.persistence.filter.Filter;
import com.korporate.spring.persistence.filter.Order;
import com.korporate.spring.persistence.repository.KorporateRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ParametroRepository extends KorporateRepository<Parametro> implements KorporateParametroRepository<Parametro>
{
    public ParametroRepository()
    {
        super(Parametro.class);
    }

    @Override
    public Optional<Parametro> findById(UUID id)
    {
        return find(id);
    }

    @Override
    public Optional<Parametro> findByNumero(int numero)
    {
        Filter filter = new Filter();
        filter.equals("numero", numero);

        return find(filter);
    }

    public List<Parametro> findAllOrderByNumero()
    {
        Filter filter = new Filter();
        filter.addOrder("numero", Order.ASC);

        return list(filter);
    }
}