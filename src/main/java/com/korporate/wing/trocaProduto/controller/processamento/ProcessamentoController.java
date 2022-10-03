package com.korporate.wing.trocaProduto.controller.processamento;

import com.korporate.wing.trocaProduto.payload.processamento.DTOContagem;
import com.korporate.wing.trocaProduto.service.processamento.ProcessamentoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("${api.path}/processamento")
public class ProcessamentoController {

    @Autowired
    private ProcessamentoService processamentoService;

    @GetMapping("contagem/{id}")
    public ResponseEntity count(@PathVariable UUID id){

        DTOContagem dtoContagem = new DTOContagem(processamentoService.numeroLinhasProcessadas(id));
        return ResponseEntity.ok().body(dtoContagem);
    }
}
