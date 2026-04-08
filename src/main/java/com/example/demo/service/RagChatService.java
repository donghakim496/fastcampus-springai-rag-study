package com.example.demo.service;

import com.example.demo.record.PromptBody;
import com.example.demo.record.RagPromptBody;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RagChatService {
    private final ChatClient chatClient;

    public RagChatService(ChatClient.Builder chatClientBuilder, Advisor[] advisors) {
        this.chatClient = chatClientBuilder
                .defaultOptions(
                        ChatOptions.builder()
                                .temperature(0.0)
                                .build()
                )
                .defaultAdvisors(advisors)
                .build();
    }

    public ChatResponse call(RagPromptBody ragPromptBody, Optional<String> filterExpressionAsOpt) {
        return buildChatClientRequestSpec(buildRagPrompt(ragPromptBody), ragPromptBody.conversationId(), filterExpressionAsOpt)
                .call()
                .chatResponse();
    }

    public Flux<String> stream(RagPromptBody ragPromptBody, Optional<String> filterExpressionAsOpt) {
        return buildChatClientRequestSpec(buildRagPrompt(ragPromptBody), ragPromptBody.conversationId(), filterExpressionAsOpt)
                .stream()
                .content();
    }

    private ChatClient.ChatClientRequestSpec buildChatClientRequestSpec(Prompt prompt, String conversationId, Optional<String> filterExpressionAsOpt) {
        ChatClient.ChatClientRequestSpec chatClientRequestSpec = chatClient.prompt(prompt)
                .advisors(advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId)
                );
        filterExpressionAsOpt.ifPresent(filterExpression ->
                chatClientRequestSpec.advisors(advisorSpec ->
                        advisorSpec.param(VectorStoreDocumentRetriever.FILTER_EXPRESSION, filterExpression)
                )
        );
        return chatClientRequestSpec;
    }



    private static Prompt buildRagPrompt(RagPromptBody ragPromptBody) {
        List<Message> messages = new ArrayList<>();
        String systemPrompt = ragPromptBody.systemPrompt();
        String userPrompt = ragPromptBody.userPrompt();

        Optional.ofNullable(systemPrompt)
                .filter(s -> !s.isBlank())
                .ifPresent(s -> messages.add(
                        SystemMessage.builder()
                                .text(s)
                                .build()
                ));

        messages.add(UserMessage.builder().text(userPrompt).build());

        Prompt.Builder promptBuilder = Prompt.builder().messages(messages);

        Optional.ofNullable(ragPromptBody.chatOptions()).ifPresent(promptBuilder::chatOptions);
        return promptBuilder.build();
    }
}
