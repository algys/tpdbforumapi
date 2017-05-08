package api.controllers;

import api.DAO.ServiceDAO;
import api.models.Status;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/api/service")
public class ServiceController {

    final private ServiceDAO serviceDAO;
    final private Logger LOG = LogManager.getLogger();

    @Autowired
    public ServiceController(ServiceDAO serviceDAO){
        this.serviceDAO = serviceDAO;
    }

//    @ModelAttribute
//    public void log(HttpServletRequest request){
//        LOG.info(request.getMethod() + " " + request.getRequestURI() + " " + request.getParameterMap().toString());
//    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity clear(){
        serviceDAO.truncateTables();
        return ResponseEntity.ok("Очистка прошла успешно");
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public ResponseEntity getStatus(){
        return ResponseEntity.ok(serviceDAO.getStatus());
    }
}
