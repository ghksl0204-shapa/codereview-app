package com.kh.codereview.aireview.model.dto.claude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ClaudeChatMessage {

    private String role;
    private String content;
}
