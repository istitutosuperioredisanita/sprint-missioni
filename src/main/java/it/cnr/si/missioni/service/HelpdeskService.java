package it.cnr.si.missioni.service;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.hibernate.service.spi.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;

import it.cnr.si.missioni.awesome.exception.AwesomeException;
import it.cnr.si.missioni.domain.custom.Helpdesk;
import it.cnr.si.missioni.util.Costanti;
import it.cnr.si.missioni.util.proxy.ResultProxy;
import it.cnr.si.missioni.util.proxy.json.JSONBody;

public class HelpdeskService {
    private final Logger log = LoggerFactory.getLogger(HelpdeskService.class);

    @Autowired
    ProxyService proxyService;
    
    @Value("${proxy.OIL.newProblem}")
    private static String helpdeskUrl;

	public Long newProblem(Helpdesk hd, String instance) throws ServiceException {

			JSONBody jBody = new JSONBody();
			jBody.setHelpdesk(hd);
			
//			, String app, String url, Boolean value, ) {
			String risposta = null;
			ResultProxy result = proxyService.process(HttpMethod.PUT, jBody, Costanti.APP_HELPDESK, helpdeskUrl, null, null, false);
			if (HttpStatus.SC_CREATED != result.getStatus().value()){
				
			risposta = result.getBody();
			return new Long(risposta);
		}
		
//			if (HttpStatus.SC_CREATED!=response.getStatusLine().getStatusCode())
//				throw new ServiceException(response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase());
//			HttpEntity resEntity = response.getEntity();
//		    if (resEntity != null)
//		    	id = Long.parseLong(EntityUtils.toString(resEntity));
//		    if (resEntity != null) 
//		      EntityUtils.consume(resEntity);
//		} catch (AuthenticationException e) {
//			e.printStackTrace();
//			throw new AwesomeException("Errore nell'autenticazione per l'invio della segnalazione ad OIL: "+e.getLocalizedMessage());
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
//			throw new AwesomeException("Errore di protocollo per l'invio della segnalazione ad OIL: "+e.getLocalizedMessage());
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new AwesomeException("Errore IO per l'invio della segnalazione ad OIL: "+e.getLocalizedMessage());
//		} finally {
//			try {
//				if (response!=null) response.close();
//				if (client!=null) client.close();
//			} catch (IOException e) {
//				// Do nothing
//				e.printStackTrace();
//			}
//		}
//		return id;
			return null;
	}
	
	public static void addAttachments(long id, List<File> files, String instance) throws ServiceException {
//		CloseableHttpClient client = null;
//		CloseableHttpResponse response = null;
//		try {
//			for (File file : files) {
//				String filename = file.getName();
//				System.out.println(FileUtils.readFileToString(file));
//				CredentialsProvider provider = new BasicCredentialsProvider();
//				UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(helpdeskUsername, helpdeskPassword);
//				provider.setCredentials(AuthScope.ANY, credentials);
//				client = HttpClientBuilder.create().setDefaultCredentialsProvider(provider).build();
//				MultipartEntityBuilder builder = MultipartEntityBuilder.create();
//				builder.addBinaryBody(Helpdesk.ALLEGATO, file, ContentType.DEFAULT_BINARY, filename);
//				HttpEntity multipart = builder.build();
//				HttpPost request = new HttpPost(helpdeskUrl+instance+"/"+id);
//				request.addHeader(new BasicScheme().authenticate(credentials, request, null));
//				request.setEntity(multipart);
//				response = client.execute(request);
//				if (HttpStatus.SC_NO_CONTENT!=response.getStatusLine().getStatusCode())
//					System.out.println("Errore nel POST del file: "+filename);
//			}
//		} catch (AuthenticationException e) {
//			e.printStackTrace();
//			throw new AwesomeException("Errore nell'autenticazione per l'invio della segnalazione ad OIL: "+e.getLocalizedMessage());
//		} catch (IOException e) {
//			e.printStackTrace();
//			throw new AwesomeException("Errore IO per l'invio della segnalazione ad OIL: "+e.getLocalizedMessage());
//		} finally {
//			try {
//				if (response!=null) response.close();
//				if (client!=null) client.close();
//			} catch (IOException e) {
//				// Do nothing
//				e.printStackTrace();
//			}
//		}
	}

}
