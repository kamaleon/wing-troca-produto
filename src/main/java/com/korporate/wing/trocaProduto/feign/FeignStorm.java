package com.korporate.wing.trocaProduto.feign;

import com.korporate.wing.trocaProduto.payload.storm.IntegracaoServicosEnum;
import com.korporate.wing.trocaProduto.payload.storm.PayloadIntegracaoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "${feign.storm}")
public interface FeignStorm {

    @GetMapping("integracoes/{servico}")
    PayloadIntegracaoResponse obterIntegracaoEmissorServico(@PathVariable IntegracaoServicosEnum servico);
}
