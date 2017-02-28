package it.cnr.si.missioni.util;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.bulk.OggettoBulk;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.cmis.MimeTypes;
import it.cnr.si.missioni.util.proxy.json.object.CommonJsonRest;
import it.cnr.si.missioni.util.proxy.json.object.sigla.ErrorRestSigla;

public class Utility {
	public static final java.math.BigDecimal ZERO = new java.math.BigDecimal(0);
	public static boolean equalsNull(Object object1, Object object2){
		if (object1 == null && object2 == null)
			return true;
		else if ((object1 == null && object2 != null)||(object1 != null && object2 == null))
			return false;
		else if (object1 != null && object2 != null)
			return object1.equals(object2);
		return false;
	}
	/**
	 * Restituisce true se i due oggetti sono uguali o sono entrambi null
	 * false altrimenti
	 */
	public static boolean equalsBulkNull(OggettoBulk object1, OggettoBulk object2){
		if (object1 == null && object2 == null)
			return true;
		else if ((object1 == null && object2 != null)||(object1 != null && object2 == null))
			return false;
		else if (object1 != null && object2 != null)
			return object1.equalsByPrimaryKey(object2);
		return false;
	}
	
	public static BigDecimal nvl(BigDecimal imp){
		if (imp != null)
		  return imp.setScale(2);
		return ZERO;  
	}

	public static String nvl(String str){
		if (str != null)
		  return str;
		return "";  
	}

	public static String nvl(String str, String anotherValue){
		if (str != null)
		  return str;
		return anotherValue;  
	}
	/**
	 * Restituisce una Stringa ottenuta sostituendo
	 * nella stringa sorgente alla stringa pattern la stringa replace,
	 * se la stringa pattern non è presente restituisce la stringa sorgente
	 */
	public static String replace(String source, String pattern, String replace)
	{
		if (source!=null){
			final int len = pattern.length();
			StringBuffer sb = new StringBuffer();
			int found = -1;
			int start = 0;

			while( (found = source.indexOf(pattern, start) ) != -1) {
				sb.append(source.substring(start, found));
				sb.append(replace);
				start = found + len;
			}

			sb.append(source.substring(start));
			return sb.toString();
		}
		else 
		  return null;
	}
	
	
	public static String lpad(double d, int size, char pad) {
        return lpad(Double.toString(d), size, pad);
	}

	public static String lpad(long l, int size, char pad) {
        return lpad(Long.toString(l), size, pad);
	}

	public static String lpad(String s, int size, char pad) {
        StringBuilder builder = new StringBuilder();
        while (builder.length() + s.length() < size) {
                builder.append(pad);
        }
        builder.append(s);
        return builder.toString();
	}	
	
	public static Date getDateWithoutHours(Date data){
    	Calendar cal = Calendar.getInstance();
    	cal.setTime (data);
	    cal.set( Calendar.HOUR_OF_DAY, 0 );
	    cal.set( Calendar.MINUTE, 0 );
	    cal.set( Calendar.SECOND, 0 );
	    cal.set( Calendar.MILLISECOND, 0 );		
	    Date dataSenzaOre = cal.getTime();
	    return dataSenzaOre;
	}

	public static ZonedDateTime getDateWithoutHours(ZonedDateTime data){
		ZonedDateTime zdt = data.truncatedTo(ChronoUnit.DAYS);		
		return zdt;
	}

	public static String numberFormat(BigDecimal importo) {
		if (importo != null){
			DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.ITALIAN);
			DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
			symbols.setGroupingSeparator('.');
			formatter.setDecimalFormatSymbols(symbols);
			formatter.setMaximumFractionDigits(2);
			formatter.setMinimumFractionDigits(2);
	    	return formatter.format((importo.setScale(2)).doubleValue());
		} else {
	    	return "";
		}
	}
	
	public static String getMessageException(Exception e){
		e.printStackTrace();
		String obj = e.getMessage() == null ? (e.getCause() == null ? "Errore Generico" : e.getCause().toString()) : e.getMessage();

		ErrorRestSigla errorRest = null;
		try {
			Class<?> classJson= ErrorRestSigla.class;
			errorRest = (ErrorRestSigla)new ObjectMapper().readValue(obj,classJson);
			return errorRest.getError();
		} catch (IOException ex) {
				try {
					JSONObject json = new JSONObject(obj);
					String message = json.getString("userMessage");
					if (message != null){
						return message;
					}
				} catch (JSONException e1) {
					try {
						JSONObject json = new JSONObject(obj);
						String message = json.getString("originalMessage");
						if (message != null){
							return message;
						}
					} catch (JSONException e2) {
						return obj;		
					}
				}
				return obj;		
		}
	}
	public static MimeTypes getMimeType(String contentType){
		for(MimeTypes m : MimeTypes.values()) {
			if (m.mimetype().equals(contentType)){
				return m;
			}
		}
		return null;
	}
	
}
