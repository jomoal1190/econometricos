package com.umg.econo.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

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
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;
import com.umg.econo.objetosCalculos.CalculoFuncionCuadratica;
import com.umg.econo.objetosCalculos.CalculoMinimosCuadrados;
import com.umg.econo.objetosCalculos.CalculoRegresionLineal;
import com.umg.econo.objetosCalculos.RespuestaFuncionCuadratica;
import com.umg.econo.objetosCalculos.RespuestaMinimos;
import com.umg.econo.objetosCalculos.RespuestaRegresionLineal;
import com.umg.econo.repository.ProductosRepository;
import com.umg.econo.repository.RegistroRepository;
import com.umg.econo.service.ServiceWeb;

import Jama.Matrix;

@Controller
public class FormarRegistros {
	private static Logger logger = LoggerFactory.getLogger(FormarRegistros.class);
	@Autowired  ServiceWeb servicioWeb;
	@Autowired  RegistroRepository repositoryRegistro;
	@Autowired Utileria utileria;
	
	@RequestMapping(value="/getRegistros", method=RequestMethod.GET)
    public String customerForm(Model model) {
		ObtenerParametroGenerico<Producto> peticion = new ObtenerParametroGenerico<Producto>();
		RespuestaParametroDao<Producto>  respuesta = servicioWeb.getAllProdcutos(peticion);
		model.addAttribute("producto", respuesta.getLista());
		List<Registro> RegistroMinimo = repositoryRegistro.findByRegistrosMinimo();
		String anio = null;
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		
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
	
	@RequestMapping(value = "/consultarInfo", method = RequestMethod.POST)
	 public String consultarInfo(HttpServletRequest request, HttpServletResponse response, Model model, RedirectAttributes redirectAttributes) {
		
		logger.info("PARAMETROS "+request.getParameter("opcion"));
		SimpleDateFormat formato = new SimpleDateFormat("yyyy/MM/dd");
		String anio = null;
		Integer valorAnio = null;
		List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaximo();
		anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
		valorAnio = Integer.parseInt(anio.split("/")[0]);
		
		
		logger.info("anioReguest "+request.getParameter("mes"));
		List<Map> respuesta=servicioWeb.getConsulta(request, response);
		if(request.getParameter("metodo").equals("1"))
		{
			logger.info("Regresion ");
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
				List<CalculoFuncionCuadratica> funcionCuadratica = new ArrayList<CalculoFuncionCuadratica>();
				Integer rango=1;
				
				if(request.getParameter("anio") !=null)
				{
					model.addAttribute("valorEncabezado", "Años");
					model.addAttribute("textoEncabezado", "Ventas por año");
					
					for(Map mapa: respuesta)
					{
						Float anioR = Float.parseFloat(mapa.get("anio").toString());
						Float total = Float.parseFloat(mapa.get("total").toString());
						Integer suma = Integer.parseInt(mapa.get("suma").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(anioR.toString());
						agregar.setSuma(suma);
						agregar.setTotal(total);
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						CalculoRegresionLineal regresionCalcular = new CalculoRegresionLineal();
						regresionCalcular.setX(anioR);
						regresionCalcular.setY(total);
						regresionCalcular.setX2(anioR*anioR);
						regresionCalcular.setY2(total*total);
						regresionCalcular.setXy(anioR*total);
						regresion.add(regresionCalcular);
						
						//AGREGANDO CAMPOS PARA FUNCION CUADRATICA PRUEBA 1
						CalculoFuncionCuadratica cuadratica = new CalculoFuncionCuadratica();
						cuadratica.setX(anioR);
						cuadratica.setY(total);
						cuadratica.setX2(anioR*anioR);
						cuadratica.setX3(anioR*anioR*anioR);
						cuadratica.setX4(anioR*anioR*anioR*anioR);
						cuadratica.setXy(anioR*total);
						cuadratica.setX2y(anioR*anioR*total);
						funcionCuadratica.add(cuadratica);						
						
					}
					
					RespuestaFuncionCuadratica resultado = valoresFuncionCuadratica(funcionCuadratica);
					
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
					logger.info("Tama "+valorAnio);
					
					RespuestaRegresionLineal valores = valoresRegresion(regresion);
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(sumaAnio.toString());
						
						Float y = (float) (valores.getValorB1()*sumaAnio+valores.getValorB2());
//						Float y = (float) Math.pow(sumaAnio, valores.getValorB1());
						agregar.setTotal(y);
						respuestabdProyeccion.add(agregar);
					}
					//  ------------ LLAMADA A METODO FUNCION CUADRATICA
//					for(int i=1; i<=rango; i++)
//					{
//						sumaAnio=valorAnio+i;
//						RespuestaBDanio agregar = new RespuestaBDanio();
//						agregar.setAnio(sumaAnio.toString());
//						Float y = (float) (sumaAnio*sumaAnio*resultado.getC())-(sumaAnio*resultado.getB())+(resultado.getA()) ;
////						Float y = (float) Math.pow(sumaAnio, valores.getValorB1());
//						
//						agregar.setTotal(y);
//						respuestabdProyeccion.add(agregar);
//					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta el "+(max.intValue()+rango));
					model.addAttribute("objeto", respuestabdProyeccion);
					logger.info("Valor b1 "+valores.getValorB1());
					logger.info("Valor b2 "+valores.getValorB2());
					valores = valoresRegresionCalculob(regresion);
					logger.info("Valor b opcion 2 "+valores.getValorB1());
					logger.info("Valor a opcion 2 "+valores.getValorB2());
					
				}
				model.addAttribute("sinProyeccion",respuestabd);
			}
			
		}
		else if (request.getParameter("metodo").equals("2"))
		{
			
			
			
			//VALIDACION DE METODO PARA MINIMOS CUADRADOS
			logger.info("Minimos ");
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
						Float anioM=Float.parseFloat(mapa.get("anio").toString());
						Float total =Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(mapa.get("anio").toString());
						agregar.setSuma(Integer.parseInt(mapa.get("suma").toString()));
						agregar.setTotal(total);
						logger.info("Total "+mapa.get("total"));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						//AGREAGANDO VALORES A MINIMOS CUADRADOS
						CalculoMinimosCuadrados nuevoMinimo = new CalculoMinimosCuadrados();
						nuevoMinimo.setX(anioM);
						nuevoMinimo.setY(total);
						nuevoMinimo.setXy(anioM*total);
						Float potencia = (float) (anioM*anioM);
						nuevoMinimo.setX2(potencia);
						minimos.add(nuevoMinimo);
					}
					Integer tamanioArray = minimos.size();
					valorAnio=minimos.get(tamanioArray-1).getX().intValue();
					
					logger.info("Tama "+valorAnio);
					
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
					logger.info("Valor b1 "+valores.getValorb());
					logger.info("Valor b2 "+valores.getValorm());
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(sumaAnio.toString());
						Float y = valores.getValorm()*sumaAnio+valores.getValorb();
						agregar.setTotal(y);
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
						logger.info("Mes "+mapa.get("mes"));
						logger.info("Suma "+mapa.get("suma"));
						logger.info("Total "+mapa.get("total"));
						Float anioM=Float.parseFloat(mapa.get("mes").toString());
						Float total =Float.parseFloat(mapa.get("total").toString());
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(mapa.get("mes").hashCode()));
						agregar.setSuma(Integer.parseInt(mapa.get("suma").toString()));
						agregar.setTotal(total);
						logger.info("Total "+mapa.get("total"));
						respuestabdProyeccion.add(agregar);
						respuestabd.add(agregar);
						//AGREAGANDO VALORES A MINIMOS CUADRADOS
						CalculoMinimosCuadrados nuevoMinimo = new CalculoMinimosCuadrados();
						nuevoMinimo.setX(anioM);
						nuevoMinimo.setY(total);
						nuevoMinimo.setXy(anioM*total);
						Float potencia = (float) (anioM*anioM);
						nuevoMinimo.setX2(potencia);
						minimos.add(nuevoMinimo);
					}
					
					Integer tamanioArray = minimos.size();
					rango = 12-tamanioArray;
					valorAnio=minimos.get(tamanioArray-1).getX().intValue();
					RespuestaMinimos valores=valoresMinimos(minimos);
					Integer sumaAnio=0;
					for(int i=1; i<=rango; i++)
					{
						sumaAnio=valorAnio+i;
						RespuestaBDanio agregar = new RespuestaBDanio();
						agregar.setAnio(utileria.cambioMesNumero(sumaAnio));
						Float y = valores.getValorm()*sumaAnio+valores.getValorb();
						agregar.setTotal(y);
						respuestabdProyeccion.add(agregar);
					}
					model.addAttribute("rangoProyeccion", "Resultados con proyeccion hasta diciembre del presente año");
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
		logger.info("M "+m);
		Float b = (float) (((sumay*sumax2)-(sumax*sumaxy))/((n*sumax2)-(sumax*sumax)));
		logger.info("b "+b);
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
		double[][] lhsArray = {{n, sumax, sumax2}, {sumax, sumax2, sumax3}, {sumax2, sumax3, sumax4}};
        double[] rhsArray = {sumay, sumaxy, sumax2y};
 
        //Creating Matrix Objects with arrays
        Matrix lhs = new Matrix(lhsArray);
        Matrix rhs = new Matrix(rhsArray, 3);
 
        //Calculate Solved Matrix
        Matrix ans = lhs.solve(rhs);
 
        //Printing Answers
       
        respuesta.setA((float) Math.round(ans.get(0, 0)));
        respuesta.setB( (float) Math.round(ans.get(1, 0)));
        respuesta.setC((float) Math.round(ans.get(2, 0)));
        System.out.println("x = " + Math.round(ans.get(0, 0)));
        System.out.println("y = " + Math.round(ans.get(1, 0)));
        System.out.println("z = " + Math.round(ans.get(2, 0)));
		
		return respuesta;
	}
	
}
