package com.kh.codereview.aireview.model.service;

import com.kh.codereview.aireview.config.ClaudeProperties;
import com.kh.codereview.aireview.model.dto.claude.ClaudeChatMessage;
import com.kh.codereview.aireview.model.dto.claude.ClaudeContentBlock;
import com.kh.codereview.aireview.model.dto.claude.ClaudeMessageRequest;
import com.kh.codereview.aireview.model.dto.claude.ClaudeMessageResponse;
import com.kh.codereview.post.model.vo.Post;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

/**
 * Claude API(Messages API)를 호출해서 코드 리뷰 텍스트를 생성하는 역할만 담당한다.
 * 상태 저장(PENDING/COMPLETED/FAILED 갱신)이나 재시도 정책은 AIReviewService가 책임진다.
 */
@Component
@RequiredArgsConstructor
public class ClaudeReviewClient {

    private static final String MESSAGES_PATH = "/v1/messages";

    private final WebClient claudeWebClient;
    private final ClaudeProperties claudeProperties;

    public String requestReview(Post post) {
        ClaudeMessageRequest request = ClaudeMessageRequest.builder()
                .model(claudeProperties.getModel())
                .maxTokens(claudeProperties.getMaxTokens())
                .system(buildSystemPrompt())
                .messages(List.of(ClaudeChatMessage.builder()
                        .role("user")
                        .content(buildUserPrompt(post))
                        .build()))
                .build();

        ClaudeMessageResponse response = claudeWebClient.post()
                .uri(MESSAGES_PATH)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ClaudeMessageResponse.class)
                .block();

        return extractText(response);
    }

    public String getModel() {
        return claudeProperties.getModel();
    }

    private String buildSystemPrompt() {
        return """
                당신은 신입~주니어 개발자의 코드를 리뷰하는 친절하고 신뢰할 수 있는 코드 리뷰어입니다.
                아래 세 가지 기준을 모두 지켜 리뷰를 작성하세요.

                1. 친절함: 잘한 점을 먼저 짚어주고, 지적할 때도 상처 주지 않는 어조를 유지하세요.
                2. 정확성: 실제로 동작·성능·유지보수에 영향이 있는 문제만 지적하세요. 억지로 트집 잡지 마세요.
                3. 상세함: 왜 문제인지와 어떻게 고치면 좋을지를 구체적인 코드 예시와 함께 설명하세요.

                마크다운 형식으로, 다음 순서의 섹션으로 정리하세요: 요약 / 잘한 점 / 개선하면 좋은 점 / 종합 의견.
                """;
    }

    private String buildUserPrompt(Post post) {
        String focus = (post.getReviewFocus() == null || post.getReviewFocus().isBlank())
                ? "전반적인 코드 품질"
                : post.getReviewFocus();

        return """
                다음은 %s 언어로 작성된 "%s" 카테고리의 코드입니다.
                리뷰 시 특히 다음 부분에 집중해주세요: %s

                [코드]
                ```%s
                %s
                ```
                """.formatted(post.getLanguage(), post.getCategory(), focus, post.getLanguage(), post.getCodeContent());
    }

    private String extractText(ClaudeMessageResponse response) {
        if (response == null || response.getContent() == null || response.getContent().isEmpty()) {
            throw new IllegalStateException("Claude API 응답에 content가 없습니다.");
        }

        StringBuilder sb = new StringBuilder();
        for (ClaudeContentBlock block : response.getContent()) {
            if ("text".equals(block.getType()) && block.getText() != null) {
                sb.append(block.getText());
            }
        }

        if (sb.isEmpty()) {
            throw new IllegalStateException("Claude API 응답에서 텍스트 블록을 찾을 수 없습니다.");
        }

        return sb.toString();
    }
}
