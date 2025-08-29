package com.busanit501.findmyfet.repository;

import com.busanit501.findmyfet.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 게시글의 모든 댓글을 찾는 메서드 (DAY 1 목표)
    List<Comment> findByPost_Id(Long postId);

    // 댓글 ID와 게시글 ID로 댓글을 찾는 메서드 (DAY 2 삭제 기능 등에서 활용)
    // Optional<Comment> findByCommentIdAndPostPostId(Long commentId, Long postId);
}