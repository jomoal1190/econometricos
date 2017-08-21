package com.umg.econo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;


import com.umg.econo.model.Registro;

public interface RegistroRepository extends CrudRepository<Registro, Long>{
	
	List<Registro> findByProducto(Long id);
	List<Registro> findAll();
	@Query("from Registro r where r.fechaCreacion=(select min(reg.fechaCreacion) from Registro reg)")
	List<Registro> findByRegistrosMinimo();
	@Query("from Registro r where r.fechaCreacion=(select max(reg.fechaCreacion) from Registro reg)")
	List<Registro> findByRegistrosMaximo();
}
