package com.kh.codereview.aireview.model.dto.claude;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ClaudeMessageRequest {

    private String model;

    @JsonProperty("max_tokens")
    private int maxTokens;

    private String system;

    private List<ClaudeChatMessage> messages;
}
