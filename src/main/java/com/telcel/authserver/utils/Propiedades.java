package com.telcel.authserver.utils;

import java.io.FileInputStream;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Propiedades{
	static final Logger log = LoggerFactory.getLogger(Propiedades.class);
	private static Propiedades instance;
	private Properties props;
	
	private Propiedades()
	{
		cargaProperties();
	}
	
	public static Propiedades getInstance()
	{
		if(instance == null)
		{
			synchronized(Propiedades.class) {
				instance = new Propiedades();
			}
		}
		return instance;
	}
	
	private Properties cargaProperties()
	{
		try
		{
			props = new Properties();
			props.load(new FileInputStream(Constantes.PATH_ARCHIVO_PROPIEDADES));
			log.info(" ===== Propiedades Cargadas ======");
			for (Entry<Object,Object> entry : props.entrySet()) {
				log.info(entry.getKey().toString() + ": " + entry.getValue().toString());
			}
			return props;
		}
		catch (Exception e)
		{
			log.error("causa de error: ", e);
		}
		return null;
	}
	
	public String getProperty(String nombre)
	{
		return props.getProperty(nombre);
	}
	
	public void setProperty(String nombre, String valor)
	{
		props.setProperty(nombre, valor);
	}
}
