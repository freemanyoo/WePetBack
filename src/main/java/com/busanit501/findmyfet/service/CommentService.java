package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.Comment;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.dto.CommentDTO;
import com.busanit501.findmyfet.repository.CommentRepository;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Value("${upload.dir}")
    private String uploadDir;

    public CommentDTO createComment(CommentDTO commentDTO, Long userId, MultipartFile imageFile) { // ✅ String loginId -> Long userId
        Post post = postRepository.findById(commentDTO.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (post.getStatus() == Status.COMPLETED) {
            throw new IllegalStateException("이미 찾기 완료된 게시글에는 댓글을 작성할 수 없습니다.");
        }

        // ✅ findByLoginId -> findById
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        String imageUrl = null;
        if (imageFile != null && !imageFile.isEmpty()) {
            imageUrl = saveImage(imageFile);
        }

        Comment comment = Comment.builder()
                .post(post)
                .user(user)
                .content(commentDTO.getContent())
                .imageUrl(imageUrl)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return entityToDto(savedComment);
    }

    public CommentDTO updateComment(Long commentId, CommentDTO commentDTO, Long userId, MultipartFile imageFile) { // ✅ String loginId -> Long userId
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // ✅ comment.getUser().getLoginId().equals(loginId) -> comment.getUser().getUserId().equals(userId)
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("댓글 수정 권한이 없습니다.");
        }

        String imageUrl = comment.getImageUrl();
        if (imageFile != null && !imageFile.isEmpty()) {
            if (imageUrl != null) {
                deleteImage(imageUrl);
            }
            imageUrl = saveImage(imageFile);
        }

        comment.updateContent(commentDTO.getContent(), imageUrl);
        Comment updatedComment = commentRepository.save(comment);
        return entityToDto(updatedComment);
    }

    public void deleteComment(Long commentId, Long userId) { // ✅ String loginId -> Long userId
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));

        // ✅ comment.getUser().getLoginId().equals(loginId) -> comment.getUser().getUserId().equals(userId)
        if (!comment.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("댓글 삭제 권한이 없습니다.");
        }

        if (comment.getImageUrl() != null) {
            deleteImage(comment.getImageUrl());
        }

        commentRepository.deleteById(commentId);
    }

    public List<CommentDTO> getCommentsByPostId(Long postId) {
        List<Comment> comments = commentRepository.findByPost_Id(postId);
        return comments.stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    private String saveImage(MultipartFile imageFile) {
        try {
            String originalFilename = imageFile.getOriginalFilename();
            String savedFilename = UUID.randomUUID().toString() + "_" + originalFilename;
            Path savedPath = Paths.get(uploadDir, savedFilename);
            Files.createDirectories(savedPath.getParent());
            imageFile.transferTo(savedPath.toFile());
            return savedFilename;
        } catch (IOException e) {
            log.error("이미지 파일 저장 실패", e);
            throw new RuntimeException("이미지 파일 저장에 실패했습니다.");
        }
    }

    private void deleteImage(String filename) {
        try {
            Path filePath = Paths.get(uploadDir, filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("이미지 파일 삭제 실패: " + filename, e);
        }
    }

    private CommentDTO entityToDto(Comment comment) {
        return CommentDTO.builder()
                .commentId(comment.getCommentId())
                .postId(comment.getPost().getId())
                .userId(comment.getUser().getUserId())
                .userName(comment.getUser().getName())
                .loginId(comment.getUser().getLoginId())
                .content(comment.getContent())
                .imageUrl(comment.getImageUrl())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }
}
