package com.korporate.wing.trocaProduto.util;

import com.korporate.spring.persistence.multitenancy.TenantContext;
import com.korporate.wing.trocaProduto.feign.FeignStorm;
import com.korporate.wing.trocaProduto.payload.storm.IntegracaoServicosEnum;
import com.korporate.wing.trocaProduto.payload.storm.PayloadIntegracaoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class IntegracaoServicosUtil
{
    @Autowired
    private FeignStorm feignStorm;

    private static final long MIN_TIME_TO_RENEW_TOKEN = 10;

    // O token será mantido em memória para diminuir o acesso ao banco
    private Map<IntegracaoKey, PayloadIntegracaoResponse> integracoesMap = new HashMap<>();

    public synchronized PayloadIntegracaoResponse carregarIntegracao(IntegracaoServicosEnum servico, boolean forcarNovoToken) {
        // cria key para o emissor.servico
        IntegracaoKey key = new IntegracaoKey(TenantContext.getTenant(), servico);

        // Verifica se existe integracao em memoria para a chave desejada
        PayloadIntegracaoResponse integracao = integracoesMap.get(key);

        if(integracao == null || (forcarNovoToken && integracao.getGenerationTime().isBefore(Instant.now().minus(MIN_TIME_TO_RENEW_TOKEN, ChronoUnit.SECONDS)))) {
            // Caso nao encontre integracao em memoria, busca a integracao no storm para emissor/servico
            integracao = feignStorm.obterIntegracaoEmissorServico(servico);
            integracao.setGenerationTime(Instant.now());
            integracoesMap.put(key, integracao);
        }

        return integracao;
    }

    public class IntegracaoKey {
        private String tenant;
        private IntegracaoServicosEnum servico;

        public IntegracaoKey(String tenant, IntegracaoServicosEnum servico) {
            this.tenant = tenant;
            this.servico = servico;
        }

        public String getTenant() {
            return tenant;
        }

        public void setTenant(String tenant) {
            this.tenant = tenant;
        }

        public IntegracaoServicosEnum getServico() {
            return servico;
        }

        public void setServico(IntegracaoServicosEnum servico) {
            this.servico = servico;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            IntegracaoKey that = (IntegracaoKey) o;
            return Objects.equals(getTenant(), that.getTenant()) && getServico() == that.getServico();
        }

        @Override
        public int hashCode() {
            return Objects.hash(getTenant(), getServico());
        }
    }
}
