
package com.busanit501.findmyfet.dto.admin;

import com.busanit501.findmyfet.dto.post.PostListResponseDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class DashboardStatsDTO {
    // Post stats
    private long totalPosts;
    private long missingPosts;
    private long shelterPosts;
    private long completedPosts;
    private long todayPosts;

    // User stats
    private long totalUsers;
    private long todayUsers;

    // Chart data
    private List<Map<String, Object>> dailyPosts; // For line chart: [{date: "YYYY-MM-DD", count: N}]
    private List<PostListResponseDto> recentCompletions; // For list view
}
