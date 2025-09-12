
package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long>, PostRepositoryCustom {

    List<Post> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.images WHERE p.id = :postId")
    Post findByIdWithImages(@Param("postId") Long postId);

    // Dashboard stats
    long countByPostType(PostType postType);

    long countByStatus(Status status);

    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT FUNCTION('DATE', p.createdAt) as date, count(p) as count FROM Post p WHERE p.createdAt >= :startDate GROUP BY FUNCTION('DATE', p.createdAt) ORDER BY date ASC")
    List<Object[]> countPostsByDay(@Param("startDate") LocalDateTime startDate);

    List<Post> findTop5ByStatusOrderByCreatedAtDesc(Status status);
}

