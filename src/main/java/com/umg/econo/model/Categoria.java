package com.umg.econo.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;



@Entity
@Table(name="categoria")
public class Categoria {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Long id;
	@Column(name="Descripcion")
	private String descripcion;
	@OneToMany(fetch = FetchType.EAGER, mappedBy="categoria")
	@JsonManagedReference
	Set<Producto> producto = new HashSet<Producto>();
	
	public Categoria() {
	}
	
	public Categoria(Long id, String descripcion, Set<Producto> producto) {
		super();
		this.id = id;
		this.descripcion = descripcion;
		this.producto = producto;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Set<Producto> getProducto() {
		return producto;
	}
	public void setProducto(Set<Producto> producto) {
		this.producto = producto;
	}
	
	

}
