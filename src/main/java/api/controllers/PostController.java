package api.controllers;

import api.DAO.ForumDAO;
import api.DAO.PostDAO;
import api.DAO.ThreadDAO;
import api.DAO.UserDAO;
import api.models.Post;
import api.models.PostFull;
import api.models.PostUpdate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.jws.soap.SOAPBinding;
import java.util.List;
import java.util.Set;

/**
 * Created by algys on 24.02.17.
 */

@RestController
@RequestMapping(path = "/api/post")
public class PostController {

    final private ForumDAO forumDAO;
    final private ThreadDAO threadDAO;
    final private UserDAO userDAO;
    final private PostDAO postDAO;

    @Autowired
    public PostController(ForumDAO forumDAO, ThreadDAO threadDAO, UserDAO userDAO, PostDAO postDAO){
        this.forumDAO = forumDAO;
        this.threadDAO = threadDAO;
        this.userDAO = userDAO;
        this.postDAO = postDAO;
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.GET)
    public ResponseEntity detailsPost(@PathVariable(name = "id") int id,
                                      @RequestParam(name = "related", required = false) Set<String> related){

        PostFull postFull = new PostFull();
        postFull.setPost(postDAO.getById(id));
        if(postFull.getPost() == null){
            return ResponseEntity.notFound().build();
        }

        if(related != null){
            if(related.contains("user")){
                postFull.setAuthor(userDAO.getByNickname(postFull.getPost().getAuthor()));
                if(postFull.getAuthor() == null){
                    return ResponseEntity.notFound().build();
                }
            }
            if(related.contains("forum")){
                postFull.setForum(forumDAO.getBySlug(postFull.getPost().getForum()));
                if(postFull.getForum() == null){
                    return ResponseEntity.notFound().build();
                }
            }
            if(related.contains("thread")){
                postFull.setThread(threadDAO.getById(postFull.getPost().getThread()));
                if(postFull.getThread() == null){
                    return ResponseEntity.notFound().build();
                }
            }
        }
        return ResponseEntity.ok(postFull);
    }

    @RequestMapping(path = "/{id}/details", method = RequestMethod.POST)
    public ResponseEntity updatePost(@PathVariable(name = "id") int id,
                                     @RequestBody PostUpdate body){

        Post post = postDAO.update(body, id);
        if(post == null){
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(post);
    }
}
