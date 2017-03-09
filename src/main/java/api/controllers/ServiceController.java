package api.controllers;

import api.DAO.ServiceDAO;
import api.models.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/api/service")
public class ServiceController {

    final private ServiceDAO serviceDAO;

    @Autowired
    public ServiceController(ServiceDAO serviceDAO){
        this.serviceDAO = serviceDAO;
    }

    @RequestMapping(path = "/clear", method = RequestMethod.POST)
    public ResponseEntity clear(){
        serviceDAO.dropTables();
        serviceDAO.createTables();
        return ResponseEntity.ok("Очистка прошла успешно");
    }

    @RequestMapping(path = "/status", method = RequestMethod.GET)
    public ResponseEntity getStatus(){
        return ResponseEntity.ok(serviceDAO.getStatus());
    }
}
