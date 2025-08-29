package com.busanit501.findmyfet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 기능 사용 어노테이션
// ✅ 엔티티 클래스가 있는 패키지 경로를 지정합니다.
@EntityScan(basePackages = "com.busanit501.findmyfet.domain")
// ✅ 리포지토리 인터페이스가 있는 패키지 경로를 지정합니다.
@EnableJpaRepositories(basePackages = "com.busanit501.findmyfet.repository")
public class FindMyFetApplication {

    public static void main(String[] args) {
        SpringApplication.run(FindMyFetApplication.class, args);
    }

}
