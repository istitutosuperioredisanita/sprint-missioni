package it.cnr.si.missioni.service;

import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

import it.cnr.jada.ejb.session.ComponentException;
import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.util.Utility;
import net.sf.jasperreports.engine.DefaultJasperReportsContext;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JsonDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.engine.util.LocalJasperReportsContext;

@Service
public class PrintService{

    private final Logger log = LoggerFactory.getLogger(PrintService.class);

	@Autowired	
	private Environment env;

	private RelaxedPropertyResolver propertyResolver;

    public String createJsonForPrint(Object object){
		ObjectMapper mapper = new ObjectMapper();
		String myJson = null;
		try {
			myJson = mapper.writeValueAsString(object);
		} catch (Exception ex) {
			throw new ComponentException("Errore nella generazione del file JSON per l'esecuzione della stampa ("+Utility.getMessageException(ex)+").",ex);
		}
		return myJson;
    }
    
    public byte[] print(String myJson, String printNameJasper) throws AwesomeException, ComponentException {
    	return print(myJson, printNameJasper, null);
    }
    
    public byte[] print(String myJson, String printNameJasper, Map<String, String> parametersSubReport) throws AwesomeException, ComponentException {
		try {
	    	this.propertyResolver = new RelaxedPropertyResolver(env, "spring.print.");
	    	String dir = "";
	    	if (propertyResolver != null && propertyResolver.getProperty("baseDir") != null) {
	    		dir = propertyResolver.getProperty("baseDir");
	    	}
			Map<String, Object> parameters = new HashMap<String, Object>();
			JRDataSource datasource = new JsonDataSource(new ByteArrayInputStream(myJson.getBytes(Charset.forName("UTF-8"))));
//			JRGzipVirtualizer vir = new JRGzipVirtualizer(100);
			final ResourceBundle resourceBundle = ResourceBundle.getBundle(
					"net.sf.jasperreports.view.viewer", Locale.ITALIAN);
			parameters.put(JRParameter.REPORT_LOCALE, Locale.ITALIAN);
			parameters.put(JRParameter.REPORT_RESOURCE_BUNDLE, resourceBundle);
			parameters.put(JRParameter.REPORT_DATA_SOURCE, datasource);
			parameters.put("DIR_IMAGE", this.getClass().getResourceAsStream("/it/cnr/missioni/print/LogoCNR.png"));
//			parameters.put(JRParameter.REPORT_VIRTUALIZER, vir);

			LocalJasperReportsContext ctx = new LocalJasperReportsContext(DefaultJasperReportsContext.getInstance());
			ctx.setClassLoader(ClassLoader.getSystemClassLoader());
 
			JasperFillManager fillmgr = JasperFillManager.getInstance(ctx);

	    	if (parametersSubReport != null){
		    	for (Map.Entry<String, String> entry : parametersSubReport.entrySet()){
		    		parameters.put(entry.getKey(), (JasperReport)JRLoader.loadObject(this.getClass().getResourceAsStream(dir+entry.getValue())));
		    	}
	    	}
	    	log.debug(myJson);
			JasperPrint jasperPrint = fillmgr.fill(this.getClass().getResourceAsStream(dir+printNameJasper), parameters);
			return JasperExportManager.exportReportToPdf(jasperPrint);
		} catch (Exception e) {
			throw new ComponentException("Error in JASPER ("+ e + ").",e);
		}
	}
}
