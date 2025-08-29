package com.busanit501.findmyfet.dto.user;

import com.busanit501.findmyfet.domain.User;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthorDto {
    private Long userId;
    private String name;

    public AuthorDto(User user) {
        this.userId = user.getUserId();
        this.name = user.getName();
    }
}
