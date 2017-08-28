package com.umg.econo.service;

import java.util.List;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaBDanio;
import com.umg.econo.dao.RespuestaGeneralDao;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;


public interface ServiceWeb {
	
	public RespuestaParametroDao<Registro> getAllRegistros(ObtenerParametroGenerico<Registro> registro);
	public RespuestaParametroDao<Producto> getAllProdcutos(ObtenerParametroGenerico<Producto> producto);
	public RespuestaGeneralDao insertExcel(ObtenerParametroGenerico<Producto> producto);
	public RespuestaGeneralDao insertExcelRegistro(ObtenerParametroGenerico<Registro> producto);
	public RespuestaGeneralDao insertRegistro(ObtenerParametroGenerico<Registro> registro);
	public List<Map> getConsulta(HttpServletRequest request, HttpServletResponse response);

}
