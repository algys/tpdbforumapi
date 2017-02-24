package api.controllers;

import api.models.PostUpdate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/post")
public class PostController {

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity detailsPost(@PathVariable(name = "id") int id,
                                      @RequestParam(name = "related", required = false) List<String> related){
        return null;
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.POST)
    public ResponseEntity updatePost(@PathVariable(name = "id") int id,
                                     @RequestBody PostUpdate post){
        return null;
    }
}
