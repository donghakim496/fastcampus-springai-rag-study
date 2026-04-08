package com.example.demo.record;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.ai.chat.prompt.DefaultChatOptions;

public record RagPromptBody(  @NotEmpty String conversationId,
                              @NotEmpty String userPrompt,
                              @Nullable String systemPrompt,
                              @Nullable DefaultChatOptions chatOptions,
                              @Nullable String filterExpression
) {
    public RagPromptBody(String userMessage, String conversationId) {
        this(conversationId, userMessage, null, null, null);
    }

}
