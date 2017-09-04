package com.umg.econo.serviceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.umg.econo.Utileria;
import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaBDanio;
import com.umg.econo.dao.RespuestaGeneralDao;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.model.Empleado;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;
import com.umg.econo.repository.EmpleadoRepository;
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
	@Autowired private Utileria utileria;
	@Autowired private EmpleadoRepository empleadoRepository;
	
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
	@Override
	public List<Map> getConsulta(HttpServletRequest request, HttpServletResponse response) {
		logger.info("opcion "+request.getParameter("opcion"));

		String opcion = request.getParameter("opcion");
		Long producto = null;
		if (!request.getParameter("producto").equals(""))
		{
			producto = Long.parseLong(request.getParameter("producto"));
		}
		String anio = request.getParameter("anio");
		String mes = request.getParameter("mes");
		Integer metodo = Integer.getInteger(request.getParameter("metodo"));
		
		List<Map> respuesta= null;
		//VALIDACIONES SEGUN PARAMETROS
		//SI ENVIA PRODUCTO
		
		if(producto != null)
		{
			
			if(mes != null && mes.equals("on") )
			{
				respuesta = registroRepository.findByParametrosActuaProductol(producto);
			}else {
				respuesta = registroRepository.findByParametrosProducto(producto);
			}
			
			
		}
		else{
			
			if(mes != null && mes.equals("on")) {
				respuesta = registroRepository.findByParametrosActual();
				
			}
			else {
				respuesta = registroRepository.findByParametrosAll();
				
			}
			
		}
		
		logger.info("Recibio registros "+respuesta.size());
		
		return respuesta;
	}
	@Override
	public String insertEmpleado(HttpServletRequest request, HttpServletResponse response) {
		 String nombre = request.getParameter("nombre");
		 Float porcentaje=(float) 0;
		 if(!request.getParameter("porcentaje").isEmpty())
		 {
			 porcentaje = Float.parseFloat(request.getParameter("porcentaje"));
		 }
		 String respuesta ="";
		 if(request.getParameter("opcion").equals("1"))
		 {
			 porcentaje = porcentaje*1;
		 }
		 else {
			 porcentaje = porcentaje*-1;
		 }
		 
		 Empleado newEmpleado = new Empleado();
		 newEmpleado.setNombre(nombre);
		 newEmpleado.setPorcentaje(porcentaje);
		 try {
			 empleadoRepository.save(newEmpleado);
			 respuesta = CODIGO_CORRECTO;
		 }catch(Exception e)
		 {
			 logger.error("Error al insertar empleado",e);
			 respuesta = CODIGO_INCORRECTO;
		 }
		 
		return respuesta;
	}
	@Override
	public List<Empleado> getAllEmpleados() {
		List<Empleado> respuesta = new ArrayList<Empleado>();
				respuesta = (List<Empleado>) empleadoRepository.findAll();
		return respuesta;
	}
	@Override
	public String deleteEmpleado(HttpServletRequest request, HttpServletResponse response) {
		Long id= Long.parseLong(request.getParameter("id"));
		Empleado empleado = new Empleado();
		empleado.setId(id);
		String respuesta ="";
		try
		{
			respuesta =CODIGO_CORRECTO;
			empleadoRepository.delete(empleado);
		}catch(Exception e)
		{
			logger.error("Error al eliminar empleado",e);
			respuesta = CODIGO_INCORRECTO;
		}
		
		
		
		return respuesta;
	}
	@Override
	public String updateEmpleado(HttpServletRequest request, HttpServletResponse response) {
		Long id= Long.parseLong(request.getParameter("id"));
		String nombre = request.getParameter("nombre");
		Float monto = Float.parseFloat(request.getParameter("porcentaje"));
		String respuesta="";
		Empleado empleado = new Empleado();
		empleado.setId(id);
		empleado.setNombre(nombre);
		empleado.setPorcentaje(monto);
		
		try {
			empleadoRepository.save(empleado);
			respuesta = CODIGO_CORRECTO;
		} catch (Exception e) {
			logger.error("Error al actualizar empleado");
			respuesta = CODIGO_INCORRECTO;
			
		}
		
		return respuesta;
	}
	@Override
	public Empleado getEmpleado(HttpServletRequest request, HttpServletResponse response) {
		Long id= Long.parseLong(request.getParameter("id"));
		Empleado empleado = empleadoRepository.findOne(id);
		
		return empleado;
	}
	
	

}
