package com.korporate.wing.trocaProduto.model.auditoria;

import com.korporate.wing.trocaProduto.model.processamento.Processamento;
import com.korporate.spring.persistence.domain.KorporateEntity;
import org.hibernate.envers.NotAudited;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "T_AUDITORIA_HTTP")
@AttributeOverride(name = "id", column = @Column(name = "ID_AUDITORIA_HTTP"))
public class AuditoriaHTTP extends KorporateEntity
{
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROCESSAMENTO")
    @NotAudited
    private Processamento processamento;

    // Request
    private ZonedDateTime requestTimestamp;
    private String httpMethod;
    private String url;
    @Column(columnDefinition="text")
    private String requestBody;

    // Response
    private ZonedDateTime responseTimestamp;
    private int httpStatus;
    @Column(columnDefinition="text")
    private String responseBody;


    public Processamento getProcessamento()
    {
        return processamento;
    }

    public void setProcessamento(Processamento processamento)
    {
        this.processamento = processamento;
    }

    public ZonedDateTime getRequestTimestamp()
    {
        return requestTimestamp;
    }

    public void setRequestTimestamp(ZonedDateTime requestTimestamp)
    {
        this.requestTimestamp = requestTimestamp;
    }

    public String getHttpMethod()
    {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod)
    {
        this.httpMethod = httpMethod;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getRequestBody()
    {
        return requestBody;
    }

    public void setRequestBody(String requestBody)
    {
        this.requestBody = requestBody;
    }

    public ZonedDateTime getResponseTimestamp()
    {
        return responseTimestamp;
    }

    public void setResponseTimestamp(ZonedDateTime responseTimestamp)
    {
        this.responseTimestamp = responseTimestamp;
    }

    public int getHttpStatus()
    {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus)
    {
        this.httpStatus = httpStatus;
    }

    public String getResponseBody()
    {
        return responseBody;
    }

    public void setResponseBody(String responseBody)
    {
        this.responseBody = responseBody;
    }
}
