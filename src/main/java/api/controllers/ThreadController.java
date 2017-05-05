package api.controllers;

import api.DAO.PostDAO;
import api.DAO.ThreadDAO;
import api.DAO.UserDAO;
import api.DAO.VoteDAO;
import api.models.*;
import api.models.Thread;
import javafx.geometry.Pos;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/api/thread")
public class ThreadController {

    final private ThreadDAO threadDAO;
    final private PostDAO postDAO;
    final private UserDAO userDAO;
    final private VoteDAO voteDAO;

    @Autowired
    public ThreadController(ThreadDAO threadDAO, PostDAO postDAO, UserDAO userDAO, VoteDAO voteDAO){
        this.threadDAO = threadDAO;
        this.postDAO = postDAO;
        this.userDAO = userDAO;
        this.voteDAO = voteDAO;
    }

    @RequestMapping(path = "/{slug_or_id}/create", method = RequestMethod.POST)
    public ResponseEntity createPost(@PathVariable(name = "slug_or_id") String slug_or_id,
                                     @RequestBody List<Post> body){
        Thread thread;
        if(isNumeric(slug_or_id)) {
            thread = threadDAO.getById(Integer.parseInt(slug_or_id));
        } else
            thread = threadDAO.getBySlug(slug_or_id);

        if(thread == null) {
            return ResponseEntity.notFound().build();
        }
        for (Post post: body){
            if(post.getParent() != 0) {
                Post parent = postDAO.getById(post.getParent());
                post.setForum(thread.getForum());
                post.setThread(thread.getId());
                if (parent == null || thread.getId() != parent.getThread()) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
            post.setForum(thread.getForum());
            post.setThread(thread.getId());
        }

        List<Post> newPosts;
        newPosts = postDAO.addMany(body);
        if (newPosts == null){
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(newPosts);
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.GET)
    public ResponseEntity detailsThread(@PathVariable(name = "slug_or_id") String slug_or_id){
        Thread thread;
        if(isNumeric(slug_or_id)) {
            thread = threadDAO.getById(Integer.parseInt(slug_or_id));
        } else
            thread = threadDAO.getBySlug(slug_or_id);

        if(thread == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(thread);
    }

    @RequestMapping(path = "/{slug_or_id}/details", method = RequestMethod.POST)
    public ResponseEntity updateThread(@PathVariable(name = "slug_or_id") String slug_or_id,
                                       @RequestBody ThreadUpdate body){
        Thread thread;
        if(isNumeric(slug_or_id)) {
            thread = threadDAO.getById(Integer.parseInt(slug_or_id));
        } else
            thread = threadDAO.getBySlug(slug_or_id);
        if(thread == null){
            return ResponseEntity.notFound().build();
        }
        Thread updatedThread = threadDAO.update(body, thread.getId());
        return ResponseEntity.ok(updatedThread);
    }

    @RequestMapping(path = "/{slug_or_id}/vote", method = RequestMethod.POST)
    public ResponseEntity voteThread(@PathVariable(name = "slug_or_id") String slug_or_id,
                                       @RequestBody Vote body){
        Thread thread;
        if(isNumeric(slug_or_id)) {
            thread = threadDAO.getById(Integer.parseInt(slug_or_id));
        } else
            thread = threadDAO.getBySlug(slug_or_id);
        if(thread == null){
            return ResponseEntity.notFound().build();
        }
        if(!userDAO.exist(body.getNickname())){
            return ResponseEntity.notFound().build();
        }

        Thread votedThread = voteDAO.add(body, thread.getId());
        return ResponseEntity.ok(votedThread);
    }

    @RequestMapping(path="/{slug_or_id}/posts", method = RequestMethod.GET)
    public ResponseEntity postsThread(@PathVariable(name = "slug_or_id") String slug_or_id,
                                      @RequestParam(name = "limit", required = false) Integer limit,
                                      @RequestParam(name = "marker", required = false) String marker,
                                      @RequestParam(name = "sort", required = false) String sort,
                                      @RequestParam(name = "desc", required = false) Boolean desc){
        Thread thread;
        if(isNumeric(slug_or_id)) {
            thread = threadDAO.getById(Integer.parseInt(slug_or_id));
        } else
            thread = threadDAO.getBySlug(slug_or_id);

        if(thread == null){
            return ResponseEntity.notFound().build();
        }
        if(marker == null) {
            marker = "0";
        }
        if(sort == null) {
            sort = "flat";
        }
        if(desc == null) {
            desc = false;
        }


        return ResponseEntity.ok(postDAO.getByThread(thread.getId(), limit, marker, sort, desc));
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
    }
}
