package com.umg.econo.controller;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaBDanio;
import com.umg.econo.dao.RespuestaGeneralDao;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;
import com.umg.econo.objetosCalculos.CalculoMinimosCuadrados;
import com.umg.econo.objetosCalculos.RespuestaMinimos;
import com.umg.econo.repository.ProductosRepository;
import com.umg.econo.repository.RegistroRepository;
import com.umg.econo.service.ServiceWeb;

@Controller
public class FormarRegistros {
	private static Logger logger = LoggerFactory.getLogger(FormarRegistros.class);
	@Autowired  ServiceWeb servicioWeb;
	@Autowired  RegistroRepository repositoryRegistro;
	
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
		
//		if(request.getParameter("anio").equals("on"))
//		{
//			List<Registro> RegistroMaximo = repositoryRegistro.findByRegistrosMaximo();
//			anio = formato.format(RegistroMaximo.get(0).getFechaCreacion());
//			valorAnio = Integer.parseInt(anio.split("/")[0]);
//		}
		
		
		List<Map> respuesta=servicioWeb.getConsulta(request, response);
		List<RespuestaBDanio> respuestabdProyeccion = new ArrayList<RespuestaBDanio>();
		List<CalculoMinimosCuadrados> minimos = new ArrayList<CalculoMinimosCuadrados>();
		List<RespuestaBDanio> respuestabd = new ArrayList<RespuestaBDanio>();
		
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
		model.addAttribute("sinProyeccion",respuestabd);
		Integer tamanioArray = minimos.size();
		
		valorAnio=minimos.get(tamanioArray-1).getX().intValue();
		RespuestaMinimos valores=valoresMinimos(minimos);
		logger.info("Tama "+valorAnio);
		Integer sumaAnio=0;
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
		logger.info("Ultimo Registro anio "+valorAnio);
		logger.info("MAX "+max);
		Integer rango = max.intValue()-valorAnio;
		if(rango==0)
		{
			rango = 5;
		}
		logger.info("rango "+rango);
		for(int i=1; i<=rango; i++)
		{
			sumaAnio=valorAnio+i;
			RespuestaBDanio agregar = new RespuestaBDanio();
			agregar.setAnio(sumaAnio.toString());
			Float y = valores.getValorm()*sumaAnio+valores.getValorb();
			agregar.setTotal(y);
			respuestabdProyeccion.add(agregar);
		}
		model.addAttribute("objeto", respuestabdProyeccion);
		
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
	
}
