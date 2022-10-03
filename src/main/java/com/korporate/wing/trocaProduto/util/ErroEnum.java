package com.korporate.wing.trocaProduto.util;

import com.korporate.spring.web.exception.ErrorInterface;

import java.io.Serializable;

public enum ErroEnum implements ErrorInterface, Serializable
{
    PARAMETRO_NAO_ENCONTRADO(11, "Parametro não encontrado: %s"),

    ROTINA_NAO_ENCONTRADA(21, "Rotina não encontrada"),

    ERRO_AO_ESCREVER_ARQUIVO_EM_DISCO(31, "I/O: Erro ao escrever aquivo em disco"),
    ERRO_AO_ENVIAR_MENSAGEM_PRO_SQS(32, "I/O: Erro ao escrever aquivo em disco");;

    ErroEnum (int code, String message)
    {
        this.code = code;
        this.message = message;
    }
    private int code;
    private String message;

    @Override
    public int getCode()
    {
        return this.code;
    }

    @Override
    public java.lang.String getMessage()
    {
        return this.message;
    }
}
