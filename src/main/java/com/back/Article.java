package com.back;

import java.time.LocalDateTime;

public class Article {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime createdDate;
    private LocalDateTime modifiedDate;
    private boolean isBlind;

    public Article(){
        //NoSuchMethodException 예외 해결을 위한 기본 생성자.
    }

    public Long getId() {
        return id;
    }
    public String getBody() {
        return body;
    }
    public String getTitle() {
        return title;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public LocalDateTime getModifiedDate() {
        return modifiedDate;
    }

    public boolean isBlind(){
        return isBlind;
    }
}
