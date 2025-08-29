package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.security.UserDetailsImpl; // ✅ UserDetailsImpl import
import com.busanit501.findmyfet.service.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Log4j2
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getCommentList(@PathVariable Long postId) {
        log.info("getCommentList 호출, postId: " + postId);
        List<CommentDTO> commentList = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(commentList);
    }

    @PostMapping(value = "/posts/{postId}/comments", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> createComment(@PathVariable Long postId,
                                                    @RequestPart("commentDTO") CommentDTO commentDTO,
                                                    @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                    // ✅ 타입을 UserDetailsImpl로 변경
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // ✅ userDetails에서 userId를 직접 추출
        Long userId = userDetails.getUserid();
        log.info("createComment 호출, postId: {}, by user: {}", postId, userId);

        commentDTO.setPostId(postId);
        CommentDTO createdComment = commentService.createComment(commentDTO, userId, imageFile);
        return ResponseEntity.status(HttpStatus.CREATED).contentType(MediaType.APPLICATION_JSON).body(createdComment);
    }

    @PutMapping(value = "/comments/{commentId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CommentDTO> updateComment(@PathVariable Long commentId,
                                                    @RequestPart("commentDTO") CommentDTO commentDTO,
                                                    @RequestPart(value = "imageFile", required = false) MultipartFile imageFile,
                                                    // ✅ 타입을 UserDetailsImpl로 변경
                                                    @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // ✅ userDetails에서 userId를 직접 추출
        Long userId = userDetails.getUserid();
        log.info("updateComment 호출, commentId: {}, by user: {}", commentId, userId);

        CommentDTO updatedComment = commentService.updateComment(commentId, commentDTO, userId, imageFile);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(updatedComment);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,
                                              // ✅ 타입을 UserDetailsImpl로 변경
                                              @AuthenticationPrincipal UserDetailsImpl userDetails) {
        // ✅ userDetails에서 userId를 직접 추출
        Long userId = userDetails.getUserid();
        log.info("deleteComment 호출, commentId: {}, by user: {}", commentId, userId);

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.noContent().build();
    }
}