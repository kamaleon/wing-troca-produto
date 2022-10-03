package com.korporate.wing.trocaProduto.model.parametro;

import com.korporate.spring.parametro.model.KorporateParametro;
import org.hibernate.envers.Audited;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Audited
@Entity
@Table(name = "T_PARAMETRO")
@AttributeOverride(name = "id", column = @Column(name = "ID_PARAMETRO"))
public class Parametro extends KorporateParametro
{

}