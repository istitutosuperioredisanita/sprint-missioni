package it.cnr.si.missioni.web.rest;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import it.cnr.si.missioni.domain.custom.Helpdesk;
import it.cnr.si.missioni.util.JSONResponseEntity;
import it.cnr.si.security.AuthoritiesConstants;

@RolesAllowed({AuthoritiesConstants.USER})
@RestController
@RequestMapping("/api")
public class HelpdeskResource {
	private final Logger log = LoggerFactory.getLogger(HelpdeskResource.class);

//	@RequestMapping(value = "/sendWithAttachment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)	
//	public ResponseEntity sendWithAttachment(HttpServletRequest req, @RequestParam("file") MultipartFile uploadedMultipartFile) {
//		log.debug("HelpdeskResource:send");
//		Helpdesk hd = new Helpdesk();
//		hd.setSubject(req.getParameter("subject"));
//		hd.setMessage(req.getParameter("message"));
//		hd.setCategory(req.getParameter("category"));
//		hd.setDescCategory(req.getParameter("desc-category"));
//		hd.setIp(req.getRemoteAddr());
//		hd.setAllegato(uploadedMultipartFile);
//		
//		helpdeskService.sendMessage(hdDataModel);
//		return JSONResponseEntity.ok();
//	}
//
//	@RequestMapping(value = "/sendWithoutAttachment", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)	
//	public ResponseEntity sendWithoutAttachment(HttpServletRequest req, @RequestBody Helpdesk hdDataModel) {
//		log.debug("HelpdeskResource:send");
//		hdDataModel.setIp(req.getRemoteAddr());
//		helpdeskService.sendMessage(hdDataModel);
//		return JSONResponseEntity.ok();	
//	}
}
