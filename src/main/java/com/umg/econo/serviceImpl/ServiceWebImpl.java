package com.umg.econo.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaGeneralDao;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;
import com.umg.econo.repository.ProductosRepository;
import com.umg.econo.repository.RegistroRepository;
import com.umg.econo.service.ServiceWeb;
@Service
public class ServiceWebImpl implements ServiceWeb{

	private static Logger logger = LoggerFactory.getLogger(ServiceWebImpl.class);	
	private static final String CODIGO_CORRECTO = "001";
	private static final String MENSAJE_CORRECTO = "Obtencion con exitot";
	private static final String CODIGO_INCORRECTO = "000";
	private static final String MENSAJE_INCORRECTO = "Error en metodo de obtener";
	
	@Autowired private RegistroRepository registroRepository;
	@Autowired private ProductosRepository productosRepository;
	@Override
	public RespuestaParametroDao<Registro> getAllRegistros(ObtenerParametroGenerico<Registro> registro) {
		RespuestaParametroDao<Registro> respuesta = new RespuestaParametroDao<Registro>();
		List<Registro> registros;
		try {
			if(registro.getParametro()!= null && registro.getParametro().getId()!=null) {
				registros = registroRepository.findByProducto(registro.getParametro().getId());
				respuesta.setLista(registros);
				
			}
			else
			{
				registros = registroRepository.findAll();
				respuesta.setLista(registros);
				
			}
			respuesta.setCodigo(CODIGO_CORRECTO);
			respuesta.setMensaje(MENSAJE_CORRECTO);
			
		}catch(Exception e)
		{
			respuesta.setCodigo(CODIGO_INCORRECTO);
			respuesta.setMensaje(MENSAJE_INCORRECTO);
			logger.info("Registros no obtenidos");
		}
		
		
		return respuesta;
	}
	@Override
	public RespuestaParametroDao<Producto> getAllProdcutos(ObtenerParametroGenerico<Producto> producto) {
		RespuestaParametroDao<Producto> respuesta = new RespuestaParametroDao<Producto>();
		List<Producto> productos;
		try {
			productos= (List<Producto>) productosRepository.findAll();
			respuesta.setLista(productos);
			respuesta.setCodigo(CODIGO_CORRECTO);
			respuesta.setMensaje(MENSAJE_CORRECTO);
			
		}catch(Exception e) {
			
			respuesta.setCodigo(CODIGO_INCORRECTO);
			respuesta.setMensaje(MENSAJE_INCORRECTO);
			logger.info("Registros no obtenidos");
		}
		return respuesta;
	}
	@Override
	public RespuestaGeneralDao insertExcel(ObtenerParametroGenerico<Producto> producto) {
		RespuestaGeneralDao respuesta = new RespuestaGeneralDao();
		Producto insert = producto.getParametro();
		
		try {
		if(producto!= null && producto.getParametro().getNombre() != null && producto.getParametro().getPrecio() != null) {
			productosRepository.save(producto.getParametro());
			respuesta.setCodigo(CODIGO_CORRECTO);
			respuesta.setMensaje(MENSAJE_CORRECTO);
			}
		else {
			respuesta.setCodigo(CODIGO_INCORRECTO);
			respuesta.setMensaje(MENSAJE_INCORRECTO);
			logger.info("Registros no obtenidos");
		}
		}catch(Exception e) {
			
			respuesta.setCodigo(CODIGO_INCORRECTO);
			respuesta.setMensaje(MENSAJE_INCORRECTO);
			logger.info("Registros no obtenidos");
		}
		
		return respuesta;
	}
	@Override
	public RespuestaGeneralDao insertExcelRegistro(ObtenerParametroGenerico<Registro> registro) {
		RespuestaGeneralDao respuesta = new RespuestaGeneralDao();
		try {
			if(registro!= null && registro.getParametro() !=null) {
				registroRepository.save(registro.getParametro());
				respuesta.setCodigo(CODIGO_CORRECTO);
				respuesta.setMensaje(MENSAJE_CORRECTO);
				}
			else {
				respuesta.setCodigo(CODIGO_INCORRECTO);
				respuesta.setMensaje(MENSAJE_INCORRECTO);
				logger.info("Registros no obtenidos");
			}
			}catch(Exception e) {
				
				respuesta.setCodigo(CODIGO_INCORRECTO);
				respuesta.setMensaje(MENSAJE_INCORRECTO);
				logger.info("Registros no obtenidos");
			}
			
			return respuesta;
	}
	@Override
	public RespuestaGeneralDao insertRegistro(ObtenerParametroGenerico<Registro> registro) {
		RespuestaGeneralDao respuesta = new RespuestaGeneralDao();
		try {
			if(registro != null && registro.getParametro() != null)
			{
				registroRepository.save(registro.getParametro());
				respuesta.setCodigo(CODIGO_CORRECTO);
				respuesta.setMensaje(MENSAJE_CORRECTO);
				
			}
			
		}catch(Exception e)
		{
			respuesta.setCodigo(CODIGO_INCORRECTO);
			respuesta.setMensaje(MENSAJE_INCORRECTO);
			logger.info("Registros no obtenidos");
			
		}
		
		return respuesta;
	}

}
