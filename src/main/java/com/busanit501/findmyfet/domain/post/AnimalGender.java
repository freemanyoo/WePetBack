package com.busanit501.findmyfet.domain.post;

import com.fasterxml.jackson.annotation.JsonCreator; // ❗❗ import 추가
import lombok.Getter;

import java.util.stream.Stream; // ❗❗ import 추가

@Getter
public enum AnimalGender {

    MALE("수컷"),
    FEMALE("암컷"),
    UNKNOWN("모름");

    private final String description;

    AnimalGender(String description) {
        this.description = description;
    }

    // ❗❗❗ [아래 메서드 추가] ❗❗❗
    // "암컷" 과 같은 문자열을 받으면, 해당하는 Enum(FEMALE)을 찾아 반환하는 메서드
    @JsonCreator
    public static AnimalGender from(String value) {
        if (value == null || value.isEmpty()) {
            return UNKNOWN;
        }

        // 1. "수컷", "암컷", "모름" 과 같은 한글 설명(description)으로 먼저 찾아봅니다.
        for (AnimalGender gender : AnimalGender.values()) {
            if (gender.getDescription().equals(value)) {
                return gender;
            }
        }

        // 2. 일치하는 한글 설명이 없다면, "MALE", "FEMALE", "UNKNOWN" 과 같은 Enum 이름으로 찾아봅니다.
        try {
            return AnimalGender.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            // 두 경우 모두 해당하지 않으면 예외를 발생시킵니다.
            throw new IllegalArgumentException(value + "에 해당하는 성별을 찾을 수 없습니다.");
        }
    }
}