jQuery(function($){
	$.getScript("/js/jquery.validate.min.js",function(){
				$(document).ready(function(){
					
					var msgRequiredGeneric = "Este campo es requerido";
					var msgMaxlength = "Cantidad de caracteres no permitidos";
					var msgRequiredEmail = "Debe ingresar un correo";
					var msgRequiredNumber = "Deben ser numeros";
					var msgAccept = "Formato de archivo no válido";
					var msgPhone = "Número de teléfono no válido";
					var msgMaxlengthNumber = $.validator.format("Ingrese un máximo de {0} números");
					var msgMinlengthNumber = $.validator.format("Ingrese al menos {0} números");
					var msgNumber = "Número no válido";
					
					$("input[name], select[name], span.fancyFiled", "#formulario").tooltipster({ 
						trigger: 'custom', // default is 'hover' which is no good here
						onlyOne: false,    // allow multiple tips to be open at a time
						position: 'right'  // display the tips to the right of the element
				    });
					
					
					$.validator.addMethod("phoneNumber", function(value, element){
						return this.optional(element) || /^[0-9]{4,4}-[0-9]{4,4}$/.test(value);
					});
					
					$.validator.addMethod("greaterStart", function (value, element, params) {
					    return this.optional(element) || $(element).datepicker("getDate") >= $(params).datepicker("getDate");
					});
					
					$.validator.addMethod("dateBCR", function ( value, element ) {
						var check = false,
						re = /^\d{1,2}\/\d{1,2}\/\d{4}$/,
						adata, gg, mm, aaaa, xdata;
						if ( re.test( value ) ) {
							adata = value.split( "/" );
							gg = parseInt( adata[ 0 ], 10 );
							mm = parseInt( adata[ 1 ], 10 );
							aaaa = parseInt( adata[ 2 ], 10 );
							xdata = new Date( Date.UTC( aaaa, mm - 1, gg, 12, 0, 0, 0 ) );
							if ( ( xdata.getUTCFullYear() === aaaa ) && ( xdata.getUTCMonth() === mm - 1 ) && ( xdata.getUTCDate() === gg ) ) {
								check = true;
							} else {
								check = false;
							}
						} else {
							check = false;
						}
						return this.optional( element ) || check;
					});
					
					$("#formulario").validate({
						rules : {
							opcion : {
								required : true,
							},
							producto : {
								required : true,
								},
							
							metodo : {
								required : true,
							},
						},
						messages : {
							opcion : {
								required : msgRequiredGeneric,
							},
							producto : {
								required : msgRequiredGeneric,
							},
							
							metodo : {
								required : msgRequiredGeneric,
							},
						},
						errorPlacement: function (error, element) {
							if($(element).is("select")){
								$(element).parent(".selectBox").addClass("error");
							}
							var isInputFile = $(element).is("input[type='file']");
							if (isInputFile) {
								$(element).parent(".fancyFiled").addClass("error");
							}
							var lastError = $(element).data("lastError"),
			                newError = $(error).text();
	
				            $(element).data("lastError", newError);
		
				            if(newError !== "" && newError !== lastError){
				            	if (isInputFile) {
				            		$(element).parent(".fancyFiled").tooltipster("content", newError);
					                $(element).parent(".fancyFiled").tooltipster("enable");
					                $(element).parent(".fancyFiled").tooltipster("show");
				            	} else {
				            		$(element).tooltipster("content", newError);
					                $(element).tooltipster("enable");
					                $(element).tooltipster("show");
				            	}
				            	
				                $("input[name].error, select[name].error, span.fancyFiled", "#formulario").focus(function(){
									$(this).tooltipster("show");
								});
				                
				                $("input[name].error, select[name].error, span.fancyFiled", "#formulario").blur(function(){
									$(this).tooltipster("hide");
								});
				            }
				        },
				        success: function (label, element) {
				        	var isInputFile = $(element).is("input[type='file']");
				        	if($(element).is("select")){
								$(element).parent(".selectBox").removeClass("error");
								$(element).tooltipster("disable");
							}
				        	if (isInputFile) {
								$(element).parent(".fancyFiled").removeClass("error");
								$(element).parent(".fancyFiled").tooltipster("hide");
						        $(element).parent(".fancyFiled").tooltipster("disable");
							}
				            $(element).tooltipster("hide");
				            $(element).tooltipster("disable");
				        }
					});
					
					$("#enviar").click(function(){
						var formulario = $("#formulario");
						console.log("Producto"+$("#producto").val());
						
						if($(formulario).valid()){
							$(formulario).submit();
						}else{
							return false;
						}
					});
					
				});
		
	});
});
