package com.busanit501.findmyfet.dto.post;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FindPetSearchCriteria {

    // 검색 조건
    private String title;
    private String animalName;

    // 필터 조건
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lostTimeFrom;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime lostTimeTo;

    private String location;
    private String animalCategory;
    private String animalBreed;

    private String gender; // ✅ 이 필드를 추가해주세요.

    private String postType;
    private String status;

    // 동물 나이 범위
    private Integer animalAgeFrom;
    private Integer animalAgeTo;

    // 페이징 및 정렬
    @Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다")
    @Builder.Default
    private int page = 0;

    @Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다")
    @Max(value = 100, message = "페이지 크기는 100 이하여야 합니다")
    @Builder.Default
    private int size = 20;

    @Builder.Default
    private String sortBy = "createdAt";

    @Builder.Default
    private String sortDir = "DESC";

    // (이하 헬퍼 메서드는 그대로 유지)
    public boolean hasTitle() { return title != null && !title.trim().isEmpty(); }
    public boolean hasAnimalName() { return animalName != null && !animalName.trim().isEmpty(); }
    public boolean hasDateTimeRange() { return lostTimeFrom != null || lostTimeTo != null; }
    public boolean hasLocation() { return location != null && !location.trim().isEmpty(); }
    public boolean hasAnimalCategory() { return animalCategory != null && !animalCategory.trim().isEmpty(); }
    public boolean hasAnimalBreed() { return animalBreed != null && !animalBreed.trim().isEmpty(); }
    public boolean hasAgeRange() { return animalAgeFrom != null || animalAgeTo != null; }

    public boolean isDateTimeRangeValid() {
        if (lostTimeFrom != null && lostTimeTo != null) {
            return !lostTimeFrom.isAfter(lostTimeTo);
        }
        return true;
    }

    public boolean isAgeRangeValid() {
        if (animalAgeFrom != null && animalAgeTo != null) {
            return animalAgeFrom <= animalAgeTo;
        }
        return true;
    }
}