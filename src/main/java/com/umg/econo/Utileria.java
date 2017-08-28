package com.umg.econo;


import org.springframework.stereotype.Service;

@Service
public class Utileria {

	public String cambioMes(String mes) {
		String espa=null;
		if(mes.equals("January"))
		{
			espa="Enero";
		}
		else if(mes.equals("February")) {
			espa="Febrero";
		}
		else if(mes.equals("March"))
		{
			espa="Marzo";
		}
		else if(mes.equals("April"))
		{
			espa="Abril";
		}
		else if(mes.equals("May"))
		{
			espa="Mayo";
		}
		else if(mes.equals("June"))
		{
			espa="Junio";
		}
		else if(mes.equals("July"))
		{
			espa="Julio";
		}
		else if(mes.equals("August"))
		{
			espa="Agosto";
		}
		else if(mes.equals("September"))
		{
			espa="Septiembre";
		}
		else if(mes.equals("October"))
		{
			espa="Ocutubre";
		}
		else if(mes.equals("November"))
		{
			espa="Noviembre";
		}
		else if(mes.equals("December"))
		{
			espa="Diciembre";
		}
		
			
		    
			
		return espa;
	}
}
