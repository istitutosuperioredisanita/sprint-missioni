package it.cnr.si.missioni.util;

import it.cnr.si.missioni.awesome.exception.AwesomeException;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionUtils {
	public static Object getMethodInvocation(Object anObject, String paramName) {
		return getMethodValue(anObject,createMethodName("get", paramName));
	}
	public static String createMethodName (String suffix, String paramName){
		return suffix + paramName.substring(0,1).toUpperCase() + paramName.substring(1);
	}
	public static Object getMethodValue(Object anObject, String aMethodName) {		
		return getMethodValue(anObject, aMethodName, new Object[]{}); 
	}

	public static Method searchMethod(Object anObject, String aMethodName) {	
		Method[] methods = anObject.getClass().getMethods();
		//ciclo i metodi delle classe
		for(int i=0; i<methods.length; i++) {
			Method method = methods[i];
			//se il nome del metodo coincide ritorno il tipo di dato
			if(method.getName().equalsIgnoreCase(aMethodName)) {
				return method;
			}
		}
		return null;
	}	
	
	public static Object getMethodValue(Object anObject, String aMethodName, Object args[]) {		
		Method method = searchMethod(anObject,aMethodName);
		if(method == null) {
        	throw new AwesomeException(CodiciErrore.ERRGEN, "Errore nella ricerca del metodo "+aMethodName+" sull'oggetto "+anObject.toString());
		}
		
		try {
			return method.invoke(anObject,args);
		}
		catch(InvocationTargetException itex) {
        	throw new AwesomeException(CodiciErrore.ERRGEN, Utility.getMessageException(itex));
		}
		catch(IllegalAccessException iaex) {
        	throw new AwesomeException(CodiciErrore.ERRGEN, Utility.getMessageException(iaex));
		}
		catch(IllegalArgumentException iae) {
        	throw new AwesomeException(CodiciErrore.ERRGEN, Utility.getMessageException(iae));
		}
	}
}
