package com.umg.econo.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.Type;


@Entity
@Table(name="periodo")
public class PeriodoDeAfecto {

	@Id 
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="descripcion")
	private String descripcion;
	@Column(name="inicio")
	@Type(type="date")
	private Date inicio;
	@Column(name="fin")
	@Type(type="date")
	private Date fin;
	@Column(name="monto")
	private Float monto;
	
	public PeriodoDeAfecto() {
	}
	public PeriodoDeAfecto(Long id, String descripcion, Date inicio, Date fin, Float monto) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.inicio = inicio;
		this.fin = fin;
		this.monto = monto;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFin() {
		return fin;
	}

	public void setFin(Date fin) {
		this.fin = fin;
	}

	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Float getMonto() {
		return monto;
	}
	public void setMonto(Float monto) {
		this.monto = monto;
	}
	
	
	
	
}
