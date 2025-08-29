package com.busanit501.findmyfet.repository.post;

import com.busanit501.findmyfet.domain.post.Post;
import com.busanit501.findmyfet.dto.paging.PageRequestDto;
import com.busanit501.findmyfet.dto.post.FindPetSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostRepositoryCustom {

    // [수정] PageRequestDto -> FindPetSearchCriteria로 변경
    Page<Post> search(FindPetSearchCriteria criteria, Pageable pageable);
}
