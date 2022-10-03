package com.korporate.wing.trocaProduto.util;

public enum ParametroEnum
{
    // Geral
    NOME_DO_EMISSOR(1);

    private Long id;

    ParametroEnum(int id)
    {
        this.id = Long.valueOf(id);
    }

    public Long getId()
    {
        return id;
    }
}
