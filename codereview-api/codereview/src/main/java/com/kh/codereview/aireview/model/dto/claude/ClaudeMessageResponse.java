package com.kh.codereview.aireview.model.dto.claude;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClaudeMessageResponse {

    private String id;
    private String model;
    private List<ClaudeContentBlock> content;
}
