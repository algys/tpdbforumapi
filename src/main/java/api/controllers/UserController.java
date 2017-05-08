package api.controllers;

import api.DAO.Code;
import api.DAO.UserDAO;
import api.models.User;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/api/user")
public class UserController {

    final private UserDAO userDAO;
    final private Logger LOG = LogManager.getLogger();

    @Autowired
    public UserController(UserDAO userDAO){
        this.userDAO = userDAO;
    }

//    @ModelAttribute
//    public void log(HttpServletRequest request){
//        LOG.info(request.getMethod() + " " + request.getRequestURI() + " " + request.getParameterMap().toString());
//    }

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@PathVariable(name = "nickname") String nickname,
                                     @RequestBody User body){
        body.setNickname(nickname);
        if(!body.check()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        int code = userDAO.add(body);

        if(code == Code.ERR_DUPLICATE){
            List<User> dupUsers = null;
            dupUsers = userDAO.getDuplicate(body);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(dupUsers);
        }
        if(code == Code.ERR_UNDEFINED){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET)
    public ResponseEntity detailsUser(@PathVariable(name = "nickname") String nickname){

        User user = userDAO.getByNickname(nickname);

        if(user == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST)
    public ResponseEntity updateUser(@PathVariable(name = "nickname") String nickname,
                                     @RequestBody User body){
        body.setNickname(nickname);
        int code = userDAO.update(body);

        if(code == Code.ERR_DUPLICATE){
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        if(code == Code.ERR_UNDEFINED){
            return ResponseEntity.notFound().build();
        }

        return detailsUser(nickname);

    }


}
