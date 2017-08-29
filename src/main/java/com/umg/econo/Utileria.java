package com.umg.econo;


import org.springframework.stereotype.Service;

@Service
public class Utileria {

	public String cambioMesLetras(String mes) {
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
	
	public String cambioMesNumero(Integer mes) {
		String espa=null;
		if(mes==1)
		{
			espa="Enero";
		}
		else if(mes==2) {
			espa="Febrero";
		}
		else if(mes==3)
		{
			espa="Marzo";
		}
		else if(mes==4)
		{
			espa="Abril";
		}
		else if(mes==5)
		{
			espa="Mayo";
		}
		else if(mes==6)
		{
			espa="Junio";
		}
		else if(mes==7)
		{
			espa="Julio";
		}
		else if(mes==8)
		{
			espa="Agosto";
		}
		else if(mes==9)
		{
			espa="Septiembre";
		}
		else if(mes==10)
		{
			espa="Octubre";
		}
		else if(mes==11)
		{
			espa="Noviembre";
		}
		else if(mes==12)
		{
			espa="Diciembre";
		}

		return espa;
	}
}
