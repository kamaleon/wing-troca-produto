package com.korporate.wing.trocaProduto.controller.parametro;

import com.fasterxml.jackson.databind.JsonNode;
import com.korporate.wing.trocaProduto.service.parametro.ParametroService;
import com.korporate.wing.trocaProduto.util.Permissao;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Hidden // Swagger
@RestController
@RequestMapping("${api.path}/parametro")
public class ParametroController
{
    @Autowired
    private ParametroService parametroService;

    @Secured(Permissao.PARAMETRO_LISTAR)
    @GetMapping
    public ResponseEntity listar()
    {
        return ResponseEntity.ok(parametroService.listAll());
    }

    @Secured(Permissao.PARAMETRO_ALTERAR)
    @PutMapping("{id}")
    public ResponseEntity alterar(@PathVariable UUID id, @RequestBody JsonNode json)
    {
        String valor = json.get("valor").asText();
        return ResponseEntity.ok(parametroService.update(id, valor));
    }
}
