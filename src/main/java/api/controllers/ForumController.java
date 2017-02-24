package api.controllers;

import api.models.Forum;
import api.models.Thread;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path="/api/forum")
public class ForumController {

    @RequestMapping(path="/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody Forum body){
        return null;
    }

    @RequestMapping(path="/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity createSlug(@RequestBody Thread body){
        return null;
    }

    @RequestMapping(path="/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity detailsSlug(@PathVariable(name = "slug") String slug){
        return null;
    }

    @RequestMapping(path="/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity usersSlug(@PathVariable(name = "slug") String slug,
                                    @RequestParam(name = "limit", required = false) int limit,
                                    @RequestParam(name = "since", required = false) String since,
                                    @RequestParam(name = "desc", required = false) boolean desc){
        return null;
    }

    @RequestMapping(path="/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity threadsSlug(@PathVariable(name = "slug") String slug,
                                      @RequestParam(name = "limit", required = false) int limit,
                                      @RequestParam(name = "since", required = false) String since,
                                      @RequestParam(name = "desc", required = false) boolean desc){
        return null;
    }
}
