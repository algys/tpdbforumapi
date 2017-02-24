package api.controllers;

import api.models.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/user")
public class UserController {

    @RequestMapping(path = "/{nickname}/create", method = RequestMethod.POST)
    public ResponseEntity createUser(@PathVariable(name = "nickname") String nickname,
                                     @RequestBody User body){
        return null;
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.GET)
    public ResponseEntity detailsUser(@PathVariable(name = "nickname") String nickname){
        return null;
    }

    @RequestMapping(path = "/{nickname}/profile", method = RequestMethod.POST)
    public ResponseEntity updateUser(@PathVariable(name = "nickname") String nickname,
                                     @RequestBody User body){
        return null;
    }


}
