package com.busanit501.findmyfet.dto.post;

import com.busanit501.findmyfet.domain.post.AnimalGender;
import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.domain.post.PostType;
import com.busanit501.findmyfet.domain.post.Status;
import com.busanit501.findmyfet.dto.user.AuthorDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class PostListResponseDto {
    private Long postId;
    private String title;
    private String animalName;
    private String thumbnailUrl;
    private PostType postType;
    private Status status;
    private LocalDateTime createdAt;
    private AuthorDto author;

    private LocalDateTime lostTime;
    private String location;
    private String animalBreed;
    private AnimalGender gender;

    public PostListResponseDto(Post entity) {
        this.postId = entity.getId();
        this.title = entity.getTitle();
        this.animalName = entity.getAnimalName();

        // 올바른 메서드인 `getImageUrl()`로 수정
        this.thumbnailUrl = entity.getImages().isEmpty() ? null : entity.getImages().get(0).getImageUrl();

        this.postType = entity.getPostType();
        this.status = entity.getStatus();
        this.createdAt = entity.getCreatedAt();

        // ✅ 올바른 메서드인 `getName()`으로 수정
        this.author = new AuthorDto(entity.getUser().getUserId(), entity.getUser().getName());

        this.lostTime = entity.getLostTime();
        this.location = entity.getLocation();
        this.animalBreed = entity.getAnimalBreed();
        this.gender = entity.getGender();
    }
}