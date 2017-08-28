package com.umg.econo.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.umg.econo.dao.RespuestaBDanio;
import com.umg.econo.model.Registro;

public interface RegistroRepository extends CrudRepository<Registro, Long>{
	
	
	List<Registro> findByProducto(Long id);
	List<Registro> findAll();
	@Query("from Registro r where r.fechaCreacion=(select min(reg.fechaCreacion) from Registro reg)")
	List<Registro> findByRegistrosMinimo();
	
	@Query("from Registro r where r.fechaCreacion=(select max(reg.fechaCreacion) from Registro reg where reg.producto.id =?1)")
	List<Registro> findByRegistrosMaxProducto(Long producto);
	@Query("from Registro r where r.fechaCreacion=(select max(reg.fechaCreacion) from Registro reg)")
	List<Registro> findByRegistrosMaximo();
	
	@Query("select new map(year(r.fechaCreacion) as anio, sum(r.cantidad) as suma, sum(r.cantidad*p.precio) as total) from Registro r  inner join r.producto as p "
			+ "where year(r.fechaCreacion)=year(r.fechaCreacion) group by year(r.fechaCreacion) order by year(r.fechaCreacion) asc") 
	List<Map> findByParametrosAll();
	
	@Query("select new map(year(r.fechaCreacion) as anio, sum(r.cantidad) as suma, sum(r.cantidad*p.precio) as total) from Registro r  inner join r.producto as p "
			+ "where year(r.fechaCreacion)=year(r.fechaCreacion) and p.id=?1 group by year(r.fechaCreacion) order by year(r.fechaCreacion) asc") 
	List<Map> findByParametrosProducto(Long producto);
}
