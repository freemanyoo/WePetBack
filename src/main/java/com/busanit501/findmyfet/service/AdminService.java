package com.busanit501.findmyfet.service;

import com.busanit501.findmyfet.domain.Comment;
import com.busanit501.findmyfet.domain.User;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.dto.UserInfoResponseDTO;
import com.busanit501.findmyfet.dto.admin.DashboardStatsDTO;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.repository.CommentRepository;
import com.busanit501.findmyfet.repository.post.PostRepository;
import com.busanit501.findmyfet.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class AdminService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public DashboardStatsDTO getDashboardStats() {
        long totalPosts = postRepository.count();
        long missingPosts = postRepository.countByPostType(PostType.MISSING);
        long shelterPosts = postRepository.countByPostType(PostType.SHELTER);
        long completedPosts = postRepository.countByStatus(Status.COMPLETED);

        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDateTime.now().with(LocalTime.MAX);
        long todayPosts = postRepository.countByCreatedAtBetween(todayStart, todayEnd);

        long totalUsers = userRepository.count();
        long todayUsers = userRepository.countByCreatedAtBetween(todayStart, todayEnd);

        LocalDateTime weekAgo = LocalDate.now().minusDays(6).atStartOfDay();
        List<Map<String, Object>> dailyPosts = postRepository.countPostsByDay(weekAgo)
                .stream()
                .map(result -> Map.of("date", result[0], "count", result[1]))
                .collect(Collectors.toList());

        List<PostListResponseDto> recentCompletions = postRepository.findTop5ByStatusOrderByCreatedAtDesc(Status.COMPLETED)
                .stream()
                .map(PostListResponseDto::new)
                .collect(Collectors.toList());

        return DashboardStatsDTO.builder()
                .totalPosts(totalPosts)
                .missingPosts(missingPosts)
                .shelterPosts(shelterPosts)
                .completedPosts(completedPosts)
                .todayPosts(todayPosts)
                .totalUsers(totalUsers)
                .todayUsers(todayUsers)
                .dailyPosts(dailyPosts)
                .recentCompletions(recentCompletions)
                .build();
    }

    // 6.1 모든 게시글 관리
    public Page<PostListResponseDto> getAllPosts(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostListResponseDto::new);
    }

    // 6.2 게시글 강제 삭제
    public void deletePost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));
        // TODO: 게시글에 연결된 이미지 파일도 삭제하는 로직 추가 필요
        postRepository.delete(post);
    }

    // 6.3 댓글 강제 삭제
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다."));
        // TODO: 댓글에 연결된 이미지 파일도 삭제하는 로직 추가 필요
        commentRepository.delete(comment);
    }

    // 6.5 사용자 강제 삭제
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // Delete all posts associated with this user first
        List<Post> userPosts = postRepository.findByUser_UserIdOrderByCreatedAtDesc(userId); // Use the existing method
        if (userPosts != null && !userPosts.isEmpty()) {
            postRepository.deleteAll(userPosts);
        }

        userRepository.delete(user);
    }

    // 6.4 사용자 목록 조회
    public Page<UserInfoResponseDTO> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(user -> UserInfoResponseDTO.builder()
                .userId(user.getUserId())
                .loginId(user.getLoginId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .role(user.getRole())
                .build());
    }

    }

