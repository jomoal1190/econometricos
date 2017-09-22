package com.umg.econo.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.umg.econo.Utileria;
import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaBDanio;
import com.umg.econo.dao.RespuestaGeneralDao;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.dao.ResumirSumas;
import com.umg.econo.model.Categoria;
import com.umg.econo.model.PeriodoDeAfecto;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Proveedor;
import com.umg.econo.model.Registro;
import com.umg.econo.model.RegistrosAlterados;
import com.umg.econo.objetosCalculos.CalculoFuncionCuadratica;
import com.umg.econo.objetosCalculos.CalculoMinimosCuadrados;
import com.umg.econo.objetosCalculos.CalculoRegresionLineal;
import com.umg.econo.objetosCalculos.RespuestaFuncionCuadratica;
import com.umg.econo.objetosCalculos.RespuestaMinimos;
import com.umg.econo.objetosCalculos.RespuestaRegresionLineal;
import com.umg.econo.repository.CategoriaRepository;
import com.umg.econo.repository.ProductosRepository;
import com.umg.econo.repository.ProveedoresRepository;
import com.umg.econo.repository.RegistroRepository;
import com.umg.econo.service.ServiceWeb;

import Jama.Matrix;

@Controller
public class FormarRegistros {
	private static Logger logger = LoggerFactory.getLogger(FormarRegistros.class);
	@Autowired  ServiceWeb servicioWeb;
	@Autowired  RegistroRepository repositoryRegistro;
	@Autowired Utileria utileria;
	@Autowired CategoriaRepository categoriaRepository;
	@Autowired ProductosRepository productoRepository;
	@Autowired ProveedoresRepository proveedorRepository;
	
	@RequestMapping(value="/getRegistros", method=RequestMethod.GET)
    public String customerForm(Model model) {
		ObtenerParametroGenerico<Producto> peticion = new ObtenerParametroGenerico<Producto>();
		RespuestaParametroDao<Producto>  respuesta = servicioWeb.getAllProdcutos(peticion);
		model.addAttribute("producto", respuesta.getLista());
		List<Registro> RegistroMinimo = repositoryRegistro.findByRegistrosMinimo();
		String anio = null;
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		List<Categoria> categoria = new ArrayList<Categoria>();
		categoria = categoriaRepository.findAll();
		model.addAttribute("categoria", categoria);
		anio = formato.format(RegistroMinimo.get(0).getFechaCreacion());
		System.out.println("Fecha "+anio.split("/")[0]);
		model.addAttribute("fechaMinima", anio.split("/")[0]);
		
		List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaximo();
		anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
		System.out.println("Fecha "+anio.split("/")[0]);
		model.addAttribute("fechaMaxima", Integer.parseInt(anio.split("/")[0])+5);
		
        return "getRegistros";
    }
	
	@RequestMapping(value="/getLastRegistreProduct", method=RequestMethod.GET)
	public @ResponseBody String ultimoRegistroProducto(HttpServletRequest request, HttpServletResponse response)
	{
		String  anio = null;
		Long producto = Long.parseLong(request.getParameter("producto"));
		logger.info("producto "+producto);
		List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaxProducto(producto);
		logger.info("Tama "+RegistroMaximo.size());
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
		System.out.println("Fecha "+anio.split("/")[0]);
		anio =anio.split("/")[0];
		return anio;
	}
	
	@RequestMapping(value="/getLastRegistro", method=RequestMethod.GET)
	public @ResponseBody Integer getLastRegisterA(HttpServletRequest request, HttpServletResponse response)
	{
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaximo();
		String anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
		Integer valorAnio = Integer.parseInt(anio.split("/")[0]);
		return valorAnio;
	}
	
	@RequestMapping(value="/getProductos", method=RequestMethod.GET)
	public @ResponseBody List<Producto> getProductos(HttpServletRequest request, HttpServletResponse response)
	{
		Long idProveedor = Long.parseLong(request.getParameter("proveedor"));
		logger.info("Proveedor "+idProveedor);
		List<Producto> productos = productoRepository.findByProveedorId(idProveedor);
		
		return productos;
	}
	@RequestMapping(value="/getProveedores", method=RequestMethod.GET)
	public @ResponseBody List<Proveedor> getProveedores(HttpServletRequest request, HttpServletResponse response)
	{
		Long idCategoria = Long.parseLong(request.getParameter("categoria"));
		logger.info("Categoria id "+idCategoria);
		List<Proveedor> proveedor = proveedorRepository.findByCategoria(idCategoria);
		logger.info("Cantidad de proveedores "+proveedor.size());
		return proveedor;
	}
	
	
	
	@RequestMapping(value = "/consultarInfo", method = RequestMethod.POST)
	 public String consultarInfo(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		Float error=(float) 0.0;
		if(!request.getParameter("error").isEmpty())
		{
			error = Float.parseFloat(request.getParameter("error"));
			
		}
		
		logger.info("Valor del error "+error);
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		String anio = null;
		Integer valorAnio = null;
		List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaximo();
		anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
		valorAnio = Integer.parseInt(anio.split("/")[0]);
		List<PeriodoDeAfecto> periodo = servicioWeb.getAllPeriodos();
		List<Map> respuesta = new ArrayList<Map>();
		Map<Integer,Float> respuestaAgrupada = new HashMap<>();
		if(periodo.isEmpty())
		{
			respuesta=servicioWeb.getConsulta(request, response);
		}
		else {
			respuesta=servicioWeb.getConsultaPerido(request, response);
			if(request.getParameter("anio")!=null)
			{
				for(Map mapa: respuesta)
				{
					for(PeriodoDeAfecto per: periodo)
					{
						Date fechaS =  (Date) mapa.get("fecha");
						if(!fechaS.before(per.getInicio()) && !fechaS.after(per.getFin()))
						{
							
							Float total = Float.parseFloat(mapa.get("total").toString()) ;
							total=total-((total)-(total*per.getMonto()));
							mapa.replace("total", total);
						}
						
					}		
				}
			}
			else
			{
				for(Map mapa: respuesta)
				{
					for(PeriodoDeAfecto per: periodo)
					{
						Date fechaS =  (Date) mapa.get("fecha");
						if(!fechaS.before(per.getInicio()) && !fechaS.after(per.getFin()))
						{
							
							Float total = Float.parseFloat(mapa.get("total").toString()) ;
							total=total-((total)-(total*per.getMonto()));
							mapa.replace("total", total);
						}
						
					}		
				}
			}
			
			
		}
		
		if(request.getParameter("anio")!=null)
		{
			respuestaAgrupada =unificarAnios(respuesta);
			respuesta.clear();
			for(Entry<Integer, Float> sum: respuestaAgrupada.entrySet())
			{
				Map<String, String> agregar = new HashMap<String, String>();
				agregar.put("anio", sum.getKey().toString());
				agregar.put("total", sum.getValue().toString());
				respuesta.add(agregar);				
			}
		
		}
		else {
			respuestaAgrupada = unificarMes(respuesta);
			respuesta.clear();
			for(Entry<Integer,Float> sum: respuestaAgrupada.entrySet())
			{
				Map<String, String> agregar = new HashMap<String, String>();
				agregar.put("mes", sum.getKey().toString());
				agregar.put("total", sum.getValue().toString());
				respuesta.add(agregar);
			}
		}
		
		
		
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		//-------------------------------- METODO DE FUNCION CUADRATICA --------------------------------
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		if(request.getParameter("metodo").equals("1"))
		{
			logger.info("Funcion Cuadratica ");
			model.addAttribute("grafica", 1);
			if(respuesta.isEmpty())
			{
				model.addAttribute("Registro0",1);
				return "graficasMostrar";
			}
			else{
				model.addAttribute("Registro0",0);
				List<RespuestaBDanio> respuestabdProyeccion = new ArrayList<RespuestaBDanio>();
				List<RespuestaBDanio> respuestabd = new ArrayList<RespuestaBDanio>();
				List<CalculoFuncionCuadratica> funcionCuadratica = new ArrayList<CalculoFuncionCuadratica>();
				Integer rango=1;
				
				if(request.getParameter("anio") !=null)
				{
					model.addAttribute("valorEncabezado", "Años");
					model.addAttribute("textoEncabezado", "Ventas por año");
					
					for(Map mapa: respuesta)
					{
						Integer anioR = Integer.parseInt(mapa.get("anio").toString());
						Float total = Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(anioR.toString());
						agregar.setTotal((float)Math.round(total));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						
						
						//AGREGANDO CAMPOS PARA FUNCION CUADRATICA PRUEBA 1
						CalculoFuncionCuadratica cuadratica = new CalculoFuncionCuadratica();
						cuadratica.setX(Float.parseFloat(anioR.toString()));
						cuadratica.setY(total);
						cuadratica.setX2(Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString()));
						cuadratica.setX3(Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString()));
						cuadratica.setX4(Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString()));
						cuadratica.setXy(anioR*total);
						cuadratica.setX2y(anioR*anioR*total);
						funcionCuadratica.add(cuadratica);						
						
					}
					
					RespuestaFuncionCuadratica resultado = valoresFuncionCuadratica(funcionCuadratica);
					
					Integer tamanioArray = funcionCuadratica.size();
					valorAnio=funcionCuadratica.get(tamanioArray-1).getX().intValue();
					Float max = null;
					Float min = null;
					if(!request.getParameter("max").equals(""))
					{
						max =Float.parseFloat(request.getParameter("max"));
					}
					if(!request.getParameter("min").equals(""))
					{
						min =Float.parseFloat(request.getParameter("min"));
					}
					rango = max.intValue()-valorAnio;
					
					if(rango==0)
					{
						rango = 5;
					}
					logger.info("Rango "+rango);
					Integer sumaAnio=0;
//					  ------------ LLAMADA A METODO FUNCION CUADRATICA
				
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(sumaAnio.toString());
						Float y = (float) (Math.round(resultado.getA()+(resultado.getB()*sumaAnio)+(resultado.getC()*sumaAnio))) ;				
						agregar.setTotal(y);
						respuestabdProyeccion.add(agregar);
					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta el "+(max.intValue()+rango));
					model.addAttribute("objeto", respuestabdProyeccion);
					
					
				}
				else if (request.getParameter("mes").equals("on"))
				{
					model.addAttribute("valorEncabezado", "Años");
					model.addAttribute("textoEncabezado", "Ventas por año");
					for(Map mapa: respuesta)
					{
						Float mes = Float.parseFloat(mapa.get("mes").toString());
						Float total = Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(mes.intValue()));
						agregar.setTotal((float)Math.round(total));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						
						
						//AGREGANDO CAMPOS PARA FUNCION CUADRATICA PRUEBA 1
						CalculoFuncionCuadratica cuadratica = new CalculoFuncionCuadratica();
						cuadratica.setX(mes);
						cuadratica.setY(total);
						cuadratica.setX2(mes*mes);
						cuadratica.setX3(mes*mes*mes);
						cuadratica.setX4(mes*mes*mes*mes);
						cuadratica.setXy(mes*total);
						cuadratica.setX2y(mes*mes*total);
						funcionCuadratica.add(cuadratica);						
						
					}
					
					RespuestaFuncionCuadratica resultado = valoresFuncionCuadratica(funcionCuadratica);
					
					Integer tamanioArray = funcionCuadratica.size();
					valorAnio=funcionCuadratica.get(tamanioArray-1).getX().intValue();
					rango = 12-valorAnio;
					Integer sumaAnio=0;
					
//					  ------------ LLAMADA A METODO FUNCION CUADRATICA
				
					for(int i=1; i<=rango; i++)
					{
						
						sumaAnio=valorAnio+i;
						
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(sumaAnio));
						Float y = (float) (Math.round(resultado.getA()+(resultado.getB()*sumaAnio)+(resultado.getC()*sumaAnio*sumaAnio)));
						logger.info("Valor a "+resultado.getA());
						logger.info("Valor b "+resultado.getB());
						logger.info("Valor c "+resultado.getC());
						logger.info("Valor mes "+sumaAnio);
						logger.info("Valor y "+y);
						agregar.setTotal(y);
						respuestabdProyeccion.add(agregar);
					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta diciembre del presente año");
					model.addAttribute("objeto", respuestabdProyeccion);
				}
				model.addAttribute("sinProyeccion",respuestabd);
			}
			
		}
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		//-------------------------------- METODO DE MINIMOS CUADRADOS --------------------------------
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		else if (request.getParameter("metodo").equals("2"))
		{
			
			
			
			//VALIDACION DE METODO PARA MINIMOS CUADRADOS
			logger.info("Minimos Cuadrados metodo");
			model.addAttribute("grafica", 2);
			if(respuesta.isEmpty())
			{
				model.addAttribute("Registro0",1);
				return "graficasMostrar";
			}
			else{
				model.addAttribute("Registro0",0);
				List<RespuestaBDanio> respuestabdProyeccion = new ArrayList<RespuestaBDanio>();
				List<CalculoMinimosCuadrados> minimos = new ArrayList<CalculoMinimosCuadrados>();
				List<RespuestaBDanio> respuestabd = new ArrayList<RespuestaBDanio>();
				Integer rango=1;
			
				
				if(request.getParameter("anio") != null)
				{
					model.addAttribute("valorEncabezado", "Años");
					model.addAttribute("textoEncabezado", "Ventas por año");
					for(Map mapa: respuesta)
					{
						Integer anioM=Integer.parseInt(mapa.get("anio").toString());
						logger.info("anio "+anioM);
						Float total =Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(anioM.toString());
						agregar.setTotal((float)Math.round(total));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						//AGREAGANDO VALORES A MINIMOS CUADRADOS
						CalculoMinimosCuadrados nuevoMinimo = new CalculoMinimosCuadrados();
						nuevoMinimo.setX(Float.parseFloat(anioM.toString()));
						nuevoMinimo.setY(total);
						nuevoMinimo.setXy(anioM*total);
						Float potencia = (float) (anioM*anioM);
						nuevoMinimo.setX2(potencia);
						minimos.add(nuevoMinimo);
					}
					Integer tamanioArray = minimos.size();
					valorAnio=minimos.get(tamanioArray-1).getX().intValue();
					
				
					
					//VALIDACION DE ANIOS DE PROYECCION
					Float max = null;
					Float min = null;
					if(!request.getParameter("max").equals(""))
					{
						max =Float.parseFloat(request.getParameter("max"));
					}
					if(!request.getParameter("min").equals(""))
					{
						min =Float.parseFloat(request.getParameter("min"));
					}
					
					rango = max.intValue()-valorAnio;
					
					if(rango==0)
					{
						rango = 5;
					}
					
					RespuestaMinimos valores=valoresMinimos(minimos);
				
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(sumaAnio.toString());
						Float y = valores.getValorm()*sumaAnio+valores.getValorb();
						agregar.setTotal((float) Math.round(y));
						respuestabdProyeccion.add(agregar);
					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta el "+(max.intValue()+rango));
					model.addAttribute("objeto", respuestabdProyeccion);
				}
				else if (request.getParameter("mes").equals("on"))
				{
					model.addAttribute("valorEncabezado", "Meses");
					model.addAttribute("textoEncabezado", "Ventas por mes");
					for(Map mapa: respuesta)
					{
						
						Float mes=Float.parseFloat(mapa.get("mes").toString());
						Float total =Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						logger.info("Mes "+mapa.get("mes"));
						agregar.setAnio(utileria.cambioMesNumero(mes.intValue()));
						agregar.setTotal((float) Math.round(total));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						//AGREAGANDO VALORES A MINIMOS CUADRADOS
						CalculoMinimosCuadrados nuevoMinimo = new CalculoMinimosCuadrados();
						nuevoMinimo.setX(mes);
						nuevoMinimo.setY(total);
						nuevoMinimo.setXy(mes*total);
						Float potencia = (float) (mes*mes);
						nuevoMinimo.setX2(potencia);
						minimos.add(nuevoMinimo);
					}
					
					Integer tamanioArray = minimos.size();
				
					
					valorAnio=minimos.get(tamanioArray-1).getX().intValue();
					rango = 12-valorAnio;
					RespuestaMinimos valores=valoresMinimos(minimos);
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(sumaAnio));
						Float y = valores.getValorm()*sumaAnio+valores.getValorb();
						agregar.setTotal((float)Math.round(y));
						respuestabdProyeccion.add(agregar);
					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta diciembre del presente año");
					model.addAttribute("objeto", respuestabdProyeccion);
							
				}
				
				model.addAttribute("sinProyeccion",respuestabd);
		}
		
			
		}
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		//-------------------------------- METODO DE COVARIANZA LINEAL --------------------------------
		//---------------------------------------------------------------------------------------------
		//---------------------------------------------------------------------------------------------
		else if(request.getParameter("metodo").equals("3"))
		{
			logger.info("Regresion con covarianza lineal");
			model.addAttribute("grafica", 1);
			if(respuesta.isEmpty())
			{
				model.addAttribute("Registro0",1);
				return "graficasMostrar";
			}
			else{
				model.addAttribute("Registro0",0);
				List<RespuestaBDanio> respuestabdProyeccion = new ArrayList<RespuestaBDanio>();
				List<CalculoRegresionLineal> regresion = new ArrayList<CalculoRegresionLineal>();
				List<RespuestaBDanio> respuestabd = new ArrayList<RespuestaBDanio>();
				Integer rango=1;
				
				if(request.getParameter("anio") !=null)
				{
					model.addAttribute("valorEncabezado", "Años");
					model.addAttribute("textoEncabezado", "Ventas por año");
					
					for(Map mapa: respuesta)
					{
						Integer anioR = Integer.parseInt(mapa.get("anio").toString());
						Float total = Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(anioR.toString());
						agregar.setTotal(total);
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						CalculoRegresionLineal regresionCalcular = new CalculoRegresionLineal();
						regresionCalcular.setX(Float.parseFloat(anioR.toString()));
						regresionCalcular.setY(total);
						regresionCalcular.setX2(Float.parseFloat(anioR.toString())*Float.parseFloat(anioR.toString()));
						regresionCalcular.setY2(total*total);
						regresionCalcular.setXy(anioR*total);
						regresion.add(regresionCalcular);
						
											
						
					}
					
					
					Integer tamanioArray = regresion.size();
					valorAnio=regresion.get(tamanioArray-1).getX().intValue();
					Float max = null;
					Float min = null;
					if(!request.getParameter("max").equals(""))
					{
						max =Float.parseFloat(request.getParameter("max"));
					}
					if(!request.getParameter("min").equals(""))
					{
						min =Float.parseFloat(request.getParameter("min"));
					}
					rango = max.intValue()-valorAnio;
					
					if(rango==0)
					{
						rango = 5;
					}
					
					RespuestaRegresionLineal valores = valoresRegresion(regresion);
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(sumaAnio.toString());
						
						Float y = (float) (valores.getValorB1()*sumaAnio+valores.getValorB2());
						agregar.setTotal((float) Math.round(y*100)/100);
						respuestabdProyeccion.add(agregar);
					}
					
		
					
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta el "+(max.intValue()+rango));
					model.addAttribute("objeto", respuestabdProyeccion);
					
				
					
				}
				else if (request.getParameter("mes").equals("on"))
				{
					model.addAttribute("valorEncabezado", "Meses");
					model.addAttribute("textoEncabezado", "Ventas por mes");
					logger.info("tamanio mapa "+respuesta.size());
					for(Map mapa: respuesta)
					{
						Float mes = Float.parseFloat(mapa.get("mes").toString());
						Float total = Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(mes.intValue()));
						agregar.setTotal(total);
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						CalculoRegresionLineal regresionCalcular = new CalculoRegresionLineal();
						regresionCalcular.setX(mes);
						regresionCalcular.setY(total);
						regresionCalcular.setX2(mes*mes);
						regresionCalcular.setY2(total*total);
						regresionCalcular.setXy(mes*total);
						regresion.add(regresionCalcular);
						
										
						
					}
					
					
					Integer tamanioArray = regresion.size();
				
					
					valorAnio=regresion.get(tamanioArray-1).getX().intValue();
					rango = 12-valorAnio;
					
					RespuestaRegresionLineal valores = valoresRegresion(regresion);
					Integer sumaAnio=0;
					logger.info("Rango "+rango);
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(sumaAnio));
						
						Float y = (float) (valores.getValorB1()*sumaAnio+valores.getValorB2());
						agregar.setTotal(y);
						respuestabdProyeccion.add(agregar);
					}
					
		
					
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion del año "+new Date().getYear());
					model.addAttribute("objeto", respuestabdProyeccion);
						
				}
				model.addAttribute("sinProyeccion",respuestabd);
			}
			
		}
		
		
		return "graficasMostrar";
		
	}

	public RespuestaMinimos valoresMinimos(List<CalculoMinimosCuadrados> calculos)
	{
		RespuestaMinimos respuesta = new RespuestaMinimos();
		Float sumax= (float) 0.00;
		Float sumay=(float) 0.00;
		Float sumaxy=(float) 0.00;
		int sumax2=0;
		
		for(CalculoMinimosCuadrados cal: calculos)
		{
			
			sumax+=cal.getX();
			sumay+=cal.getY();
			sumaxy+=cal.getXy();
			sumax2+=cal.getX2();
			
		}
		
		Integer n = calculos.size();
		Float m = (float) (((n*sumaxy)-(sumax*sumay))/((n*sumax2)-(sumax*sumax)));
		Float b = (float) (((sumay*sumax2)-(sumax*sumaxy))/((n*sumax2)-(sumax*sumax)));
		respuesta.setValorm(m);
		respuesta.setValorb(b);
		return respuesta;
	}
	
	public RespuestaRegresionLineal valoresRegresion(List<CalculoRegresionLineal> calculos)
	{
		RespuestaRegresionLineal respuesta = new RespuestaRegresionLineal();
		Float sumax = (float) 0.00;
		Float sumay = (float) 0.00;
		Float sumay2 = (float) 0.00;
		Float sumax2 = (float) 0.00;
		Float sumaxy = (float) 0.00;
		
		for(CalculoRegresionLineal cal: calculos)
		{
			sumax +=cal.getX();
			sumay += cal.getY();
			sumax2 += cal.getX2();
			sumay2 += cal.getY2();
			sumaxy += cal.getXy();
		}
		
		Integer n = calculos.size();
	
		//Calculos de medias
		Float xmedia = sumax/n;
		Float ymedia = sumay/n;
		//Varianzas de desviaciones tipicas cuadradas
		Float varianzax2 = (sumax2/n)-(xmedia*xmedia);
		Float varianzay2 = (sumay2/n)-(ymedia*ymedia);
		//Varianzas de desviaciones tipicas
		Float varianzax = (float) Math.sqrt(varianzax2);
		Float varianzay = (float) Math.sqrt(varianzay2);
		//Covarianza xy
		Float covarianzaxy = (sumaxy/n)-(xmedia*ymedia);
		//Correlacion lineal de Pearson
		Float rpearson = (covarianzaxy)/(varianzax*varianzay);
		//Recta de regresion 
		Float b1 = (covarianzaxy/varianzax2);
		Float b2 = ymedia +((covarianzaxy/varianzax2)*(-xmedia));
		respuesta.setValorB1(b1);
		respuesta.setValorB2(b2);
		
		return respuesta;
	}
	// METODO DE VARIANZA MEDIA SIN USUAR
	public RespuestaRegresionLineal valoresRegresionCalculob(List<CalculoRegresionLineal> calculos)
	{
		RespuestaRegresionLineal respuesta = new RespuestaRegresionLineal();
		Float sumax = (float) 0.00;
		Float sumay = (float) 0.00;
		Float sumay2 = (float) 0.00;
		Float sumax2 = (float) 0.00;
		Float sumaxy = (float) 0.00;
		
		for(CalculoRegresionLineal cal: calculos)
		{
			sumax +=cal.getX();
			sumay += cal.getY();
			sumax2 += cal.getX2();
			sumay2 += cal.getY2();
			sumaxy += cal.getXy();
		}
		Integer n = calculos.size();
		//Calculos de medias
		Float xmedia = sumax/n;
		Float ymedia = sumay/n;
		//Varianzas de desviaciones tipicas cuadradas
		Float b = ((sumaxy)-(n*xmedia*ymedia))/((sumax2)-(n*xmedia*xmedia));
		Float a = ymedia - (b*xmedia);
		respuesta.setValorB1(b);
		respuesta.setValorB2(a);
		
		return respuesta;
	}
	
	
	public RespuestaFuncionCuadratica valoresFuncionCuadratica(List<CalculoFuncionCuadratica> calculos){
		RespuestaFuncionCuadratica respuesta = new RespuestaFuncionCuadratica();
		Float sumax = (float) 0.00;
		Float sumay = (float) 0.00;
		Float sumax2 = (float) 0.00;
		Float sumax3 = (float) 0.00;
		Float sumax4 = (float) 0.00;
		Float sumaxy = (float) 0.00;
		Float sumax2y = (float) 0.00;
		
		
		for(CalculoFuncionCuadratica cal: calculos)
		{
			sumax +=cal.getX();
			sumay += cal.getY();
			sumax2 += cal.getX2();
			sumax3 += cal.getX3();
			sumax4 += cal.getX4();
			sumaxy += cal.getXy();
			sumax2y += cal.getX2y();
		}
		Integer n = calculos.size();
		
		
        
		double[][] lhsArray = {{n, sumax, sumax2*n}, {sumax, sumax2, sumax3}, {sumax2, sumax3, sumax4}};
        double[] rhsArray = {sumay, sumaxy, sumax2y};
 
        Matrix lhs = new Matrix(lhsArray);
        Matrix rhs = new Matrix(rhsArray, 3);
 
        Matrix ans = lhs.solve(rhs);

        respuesta.setA((float) (ans.get(0, 0)));
        respuesta.setB( (float) (ans.get(1, 0)));
        respuesta.setC((float)  (ans.get(2, 0)));
        System.out.println("a = " + (ans.get(0, 0)));
        System.out.println("b = " + (ans.get(1, 0)));
        System.out.println("c = " + (ans.get(2, 0)));
		
		return respuesta;
	}
	public TreeMap<Integer,Float> unificarAnios(List<Map> mapa)
	{
		TreeMap<Integer,Float> sumas = new TreeMap<Integer,Float>();
		for(Map map: mapa)
		{
			if(sumas.containsKey(map.get("anio")))
			{	
				Float total = sumas.get(map.get("anio"));
				logger.info("total "+total);
				total= total+Float.parseFloat(map.get("total").toString());
				sumas.replace(Integer.parseInt(map.get("anio").toString()), total);
			}
			else {
				sumas.put(Integer.parseInt(map.get("anio").toString()),Float.parseFloat(map.get("total").toString()));
				logger.info("Valor "+sumas.size());
			}	
		}
		
		return sumas;
	}
	public TreeMap<Integer,Float> unificarMes(List<Map> mapa)
	{
		
		TreeMap<Integer,Float> sumas = new TreeMap<Integer,Float>();
		for(Map map: mapa)
		{
			if(sumas.containsKey(map.get("mes")))
			{	
				Float total = sumas.get(map.get("mes"));
				logger.info("total "+total);
				total= total+Float.parseFloat(map.get("total").toString());
				sumas.replace(Integer.parseInt(map.get("mes").toString()), total);
			}
			else {
				sumas.put(Integer.parseInt(map.get("mes").toString()),Float.parseFloat(map.get("total").toString()));
				logger.info("Valor "+sumas.size());
			}	
		}
		
		return sumas;
	}
	
	
}
