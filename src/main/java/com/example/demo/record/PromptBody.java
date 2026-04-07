package com.example.demo.record;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.ai.chat.prompt.DefaultChatOptions;

public record PromptBody(
        @NotEmpty String conversationId,
        @NotEmpty String userPrompt,
        @Nullable String systemPrompt,
        @NotEmpty DefaultChatOptions chatOptions
) {
    public PromptBody(String userMessage, String conversationId) {
        this(conversationId, userMessage, null, null);
    }
}
