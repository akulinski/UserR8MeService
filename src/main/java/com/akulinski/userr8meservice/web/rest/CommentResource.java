package com.akulinski.userr8meservice.web.rest;

import com.akulinski.userr8meservice.core.domain.Comment;
import com.akulinski.userr8meservice.core.domain.dto.CommentDTO;
import com.akulinski.userr8meservice.core.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/comments")
@RequiredArgsConstructor
public class CommentResource {

    private final CommentService commentService;

    @GetMapping
    public ResponseEntity getComments(Principal principal) {
        return ResponseEntity.ok(commentService.getCommentsForUser(principal.getName()));
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/{username}")
    public ResponseEntity getCommentsForUser(@PathVariable("username") String username) {
        return ResponseEntity.ok(commentService.getCommentsForUser(username));
    }

    @PostMapping
    public ResponseEntity addComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        var comment = new Comment(commentDTO.getComment(), principal.getName(),"test", new Date().toInstant());
        commentService.addCommentToUser(commentDTO.getReceiver(), comment);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping
    public ResponseEntity deleteComment(@RequestBody CommentDTO commentDTO, Principal principal) {
        var comment = new Comment(commentDTO.getComment(), principal.getName(), "test", new Date().toInstant());
        commentService.removeComment(commentDTO.getReceiver(), principal.getName(), comment);
        return ResponseEntity.accepted().build();
    }

    @DeleteMapping("/any")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity deleteCommentWithoutBeingPoster(@RequestBody CommentDTO commentDTO) {
        var comment = new Comment(commentDTO.getComment(), commentDTO.getCommenter(),"test", new Date().toInstant());
        commentService.removeComment(commentDTO.getReceiver(),comment);
        return ResponseEntity.accepted().build();
    }
}
