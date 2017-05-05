package api.controllers;

import api.DAO.Code;
import api.DAO.ForumDAO;
import api.DAO.ThreadDAO;
import api.DAO.UserDAO;
import api.models.Forum;
import api.models.Thread;
import api.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path="/api/forum")
public class ForumController {

    final private ForumDAO forumDAO;
    final private UserDAO userDAO;
    final private ThreadDAO threadDAO;

    @Autowired
    public ForumController(ForumDAO forumDAO, UserDAO userDAO, ThreadDAO threadDAO){
        this.forumDAO = forumDAO;
        this.userDAO = userDAO;
        this.threadDAO = threadDAO;
    }

    @RequestMapping(path="/create", method = RequestMethod.POST)
    public ResponseEntity createForum(@RequestBody Forum body){
        User user = null;
        user = userDAO.getByNickname(body.getUser());
        if(user == null){
            return ResponseEntity.notFound().build();
        }
        body.setUser(user.getNickname());
        int code = forumDAO.add(body);

        if(code == Code.ERR_DUPLICATE){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(forumDAO.getBySlug(body.getSlug()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(forumDAO.getBySlug(body.getSlug()));
    }

    @RequestMapping(path="/{slug}/create", method = RequestMethod.POST)
    public ResponseEntity createThread(@PathVariable(name = "slug") String slug,
                                     @RequestBody Thread body){
        User user = userDAO.getByNickname(body.getAuthor());
        Forum forum = forumDAO.getBySlug(slug);
        if(user == null || forum == null){
            return ResponseEntity.notFound().build();
        }
        body.setAuthor(user.getNickname());
        body.setForum(forum.getSlug());

        Thread thread = threadDAO.getDuplicate(body);
        if(thread != null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(thread);
        }

        Thread newThread = threadDAO.add(body);
        if(newThread == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(newThread);
    }

    @RequestMapping(path="/{slug}/details", method = RequestMethod.GET)
    public ResponseEntity details(@PathVariable(name = "slug") String slug){
        Forum forum;

        forum = forumDAO.getBySlug(slug);

        if(forum == null)
            return ResponseEntity.notFound().build();

        return ResponseEntity.ok(forum);
    }

    @RequestMapping(path="/{slug}/users", method = RequestMethod.GET)
    public ResponseEntity users(@PathVariable(name = "slug") String slug,
                                    @RequestParam(name = "limit", required = false) Integer limit,
                                    @RequestParam(name = "since", required = false) String since,
                                    @RequestParam(name = "desc", required = false) Boolean desc){
        if(forumDAO.getBySlug(slug) == null){
            return ResponseEntity.notFound().build();
        }

        if(desc == null){
            desc = false;
        }

        List<User> users = userDAO.getByForum(slug, limit, since, desc);
        if(users == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(users);
    }

    @RequestMapping(path="/{slug}/threads", method = RequestMethod.GET)
    public ResponseEntity threads(@PathVariable(name = "slug") String slug,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "since", required = false) String since,
                                      @RequestParam(name = "desc", required = false) Boolean desc){

        if(forumDAO.getBySlug(slug) == null){
            return ResponseEntity.notFound().build();
        }

        if(desc == null){
            desc = false;
        }

        List<Thread> threads = threadDAO.getByForum(slug, limit, since, desc);
        if(threads == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(threads);
    }
}
