package com.korporate.wing.trocaProduto.service.parametro;

import com.korporate.wing.trocaProduto.model.parametro.Parametro;
import com.korporate.spring.parametro.service.KorporateParametroService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class ParametroService extends KorporateParametroService<Parametro>
{

}
