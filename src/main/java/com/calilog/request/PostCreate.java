package com.calilog.request;

import com.calilog.exception.InvalidRequestException;
import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class PostCreate {

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;

    @Builder
    public PostCreate(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void validate() {
        if (this.content.contains("바보")) {
            throw new InvalidRequestException("title", "내용에 부적절한 단어가 감지되었습니다.");
        }
    }
}
