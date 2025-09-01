package com.busanit501.findmyfet.controller;

import com.busanit501.findmyfet.dto.response.CommonResponse;
import com.busanit501.findmyfet.dto.UserInfoResponseDTO;
import com.busanit501.findmyfet.dto.admin.DashboardStatsDTO;
import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import com.busanit501.findmyfet.service.AdminService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Log4j2
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CommonResponse<DashboardStatsDTO>> getDashboardStats() {
        log.info("Admin: getDashboardStats 호출");
        DashboardStatsDTO stats = adminService.getDashboardStats();
        return ResponseEntity.ok(CommonResponse.of("대시보드 통계 조회 성공", stats));
    }

    // 6.1 모든 게시글 관리 (GET /api/admin/posts)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/posts")
    public ResponseEntity<CommonResponse<Page<PostListResponseDto>>> getAllPosts(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Admin: getAllPosts 호출 (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<PostListResponseDto> posts = adminService.getAllPosts(pageable);
        return ResponseEntity.ok(CommonResponse.of("게시글 목록 조회 성공", posts));
    }

    // 6.2 게시글 강제 삭제 (DELETE /api/admin/posts/{postId})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<CommonResponse<String>> deletePost(@PathVariable Long postId) {
        log.info("Admin: deletePost 호출 (postId: {})", postId);
        adminService.deletePost(postId);
        return ResponseEntity.ok(CommonResponse.of("게시글이 성공적으로 삭제되었습니다.", null));
    }

    // 6.3 댓글 강제 삭제 (DELETE /api/admin/comments/{commentId})
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommonResponse<String>> deleteComment(@PathVariable Long commentId) {
        log.info("Admin: deleteComment 호출 (commentId: {})", commentId);
        adminService.deleteComment(commentId);
        return ResponseEntity.ok(CommonResponse.of("댓글이 성공적으로 삭제되었습니다.", null));
    }

    // 6.4 사용자 목록 조회 (GET /api/admin/users)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<CommonResponse<Page<UserInfoResponseDTO>>> getAllUsers(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        log.info("Admin: getAllUsers 호출 (page: {}, size: {})", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<UserInfoResponseDTO> users = adminService.getAllUsers(pageable);
        return ResponseEntity.ok(CommonResponse.of("사용자 목록 조회 성공", users));
    }

    // 6.5 사용자 강제 삭제 (DELETE /api/admin/users/{userId})
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<CommonResponse<String>> deleteUser(@PathVariable Long userId) {
        log.info("Admin: deleteUser 호출 (userId: {})", userId);
        adminService.deleteUser(userId);
        return ResponseEntity.ok(CommonResponse.of("사용자가 성공적으로 삭제되었습니다.", null));
    }
    
}
