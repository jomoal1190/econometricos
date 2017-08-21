package com.umg.econo.repository;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.umg.econo.model.Producto;


public interface ProductosRepository extends CrudRepository<Producto, Long>{
	
	Producto findById(Long id);
	
	

}
