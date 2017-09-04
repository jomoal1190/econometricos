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
	@Column(name="mes")
	private Integer mes;
	@Column(name="anio")
	private Integer anio;
	@Column(name="monto")
	private Double monto;
	
	
	
	public PeriodoDeAfecto() {
	}
	public PeriodoDeAfecto(Long id, Integer mes, Integer anio, Double monto) {
		super();
		this.id = id;
		this.mes = mes;
		this.anio = anio;
		this.monto = monto;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Integer getMes() {
		return mes;
	}
	public void setMes(Integer mes) {
		this.mes = mes;
	}
	public Integer getAnio() {
		return anio;
	}
	public void setAnio(Integer anio) {
		this.anio = anio;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	
	
	
	
}
