package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.*;
import com.busanit501.findmyfet.dto.post.FindPetSearchCriteria;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class PostRepositoryCustomImpl implements PostRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Post> search(FindPetSearchCriteria criteria, Pageable pageable) {
        QPost post = QPost.post;
        BooleanBuilder booleanBuilder = new BooleanBuilder();

        // ===== String 타입 필드 검색 (contains 또는 equalsIgnoreCase 사용) =====
        if (criteria.hasTitle()) {
            booleanBuilder.and(post.title.contains(criteria.getTitle()));
        }
        if (criteria.hasAnimalName()) {
            booleanBuilder.and(post.animalName.contains(criteria.getAnimalName()));
        }
        if (criteria.hasLocation()) {
            booleanBuilder.and(post.location.contains(criteria.getLocation()));
        }
        if (criteria.hasAnimalCategory()) {
            // animalCategory는 String이므로 대소문자 무시하고 비교
            booleanBuilder.and(post.animalCategory.equalsIgnoreCase(criteria.getAnimalCategory().trim()));
        }
        if (criteria.hasAnimalBreed()) {
            // animalBreed는 String이므로 대소문자 무시하고 비교
            booleanBuilder.and(post.animalBreed.equalsIgnoreCase(criteria.getAnimalBreed().trim()));
        }

        // ===== Enum 타입 필드 검색 (문자열을 Enum으로 변환하여 비교) =====
        if (StringUtils.hasText(criteria.getPostType())) {
            // ✅ [수정] 문자열을 PostType Enum으로 변환하여 비교
            booleanBuilder.and(post.postType.eq(PostType.valueOf(criteria.getPostType().toUpperCase())));
        }
        if (StringUtils.hasText(criteria.getStatus())) {
            // ✅ [수정] 문자열을 Status Enum으로 변환하여 비교
            booleanBuilder.and(post.status.eq(Status.valueOf(criteria.getStatus().toUpperCase())));
        }
        if (StringUtils.hasText(criteria.getGender())) {
            // ✅ [수정] 문자열을 AnimalGender Enum으로 변환하여 비교
            booleanBuilder.and(post.gender.eq(AnimalGender.valueOf(criteria.getGender().toUpperCase())));
        }

        // ===== 날짜 범위 검색 =====
        if (criteria.getLostTimeFrom() != null) {
            booleanBuilder.and(post.lostTime.goe(criteria.getLostTimeFrom())); // greater than or equal
        }
        if (criteria.getLostTimeTo() != null) {
            booleanBuilder.and(post.lostTime.loe(criteria.getLostTimeTo())); // less than or equal
        }

        // 쿼리 생성 (N+1 문제 방지를 위해 fetchJoin 유지)
        JPAQuery<Post> query = queryFactory.selectFrom(post)
                .leftJoin(post.user).fetchJoin()
                .where(booleanBuilder)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(post.createdAt.desc()); // 기본 정렬

        List<Post> content = query.fetch();

        // 전체 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory.select(post.count())
                .from(post)
                .where(booleanBuilder);

        long total = countQuery.fetchOne() != null ? countQuery.fetchOne() : 0L;

        return new PageImpl<>(content, pageable, total);
    }
}