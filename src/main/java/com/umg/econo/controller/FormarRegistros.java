package com.umg.econo.controller;

import java.text.SimpleDateFormat;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.umg.econo.dao.ObtenerParametroGenerico;
import com.umg.econo.dao.RespuestaParametroDao;
import com.umg.econo.model.Producto;
import com.umg.econo.model.Registro;
import com.umg.econo.repository.ProductosRepository;
import com.umg.econo.repository.RegistroRepository;
import com.umg.econo.service.ServiceWeb;

@Controller
public class FormarRegistros {

	@Autowired  ServiceWeb servicioWeb;
	@Autowired  RegistroRepository repositoryRegistro;
	
	@RequestMapping(value="/minimosCuarados", method=RequestMethod.GET)
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
		model.addAttribute("fechaMaxima", anio.split("/")[0]);
		
        return "getRegistros";
    }
}
