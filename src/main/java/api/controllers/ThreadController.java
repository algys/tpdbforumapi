package api.controllers;

import api.models.Post;
import api.models.ThreadUpdate;
import api.models.Vote;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/thread")
public class ThreadController {

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST)
    public ResponseEntity createPost(@PathVariable(name = "slug") String slug,
                                     @RequestBody List<Post> body){
        return null;
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity detailsThread(@PathVariable(name = "slug") String  slug){
        return null;
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity updateThread(@PathVariable(name = "slug_or_id") String  slug_or_id,
                                       @RequestBody ThreadUpdate body){
        return null;
    }

    @RequestMapping(path="/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity postsThread(@PathVariable(name = "slug_or_id)") String slug_or_id,
                                      @RequestParam(name = "limit", required = false) int limit,
                                      @RequestParam(name = "marker", required = false) String marker,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "desc", required = false) boolean desc){
        return null;
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity voteThread(@PathVariable(name = "slug_or_id") String  slug_or_id,
                                     @RequestBody Vote body){
        return null;
    }
}
