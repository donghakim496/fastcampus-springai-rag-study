package com.example.demo.service;

import com.example.demo.record.EmotionEvaluation;
import com.example.demo.record.PromptBody;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ChatService {
    private final ChatClient chatClient;

    public ChatService(ChatClient.Builder chatClientBuilder, Advisor[] advisors) {
        this.chatClient = chatClientBuilder.defaultAdvisors(advisors).build();
    }

    public ChatResponse call(PromptBody promptBody) {
        return buildChatClientRequestSpec(buildPrompt(promptBody), promptBody.conversationId())
                .call()
                .chatResponse();
    }

    public Flux<String> stream(PromptBody promptBody) {
        return buildChatClientRequestSpec(buildPrompt(promptBody), promptBody.conversationId())
                .stream()
                .content();
    }

    private ChatClient.ChatClientRequestSpec buildChatClientRequestSpec(Prompt prompt, String conversationId) {
        return chatClient.prompt(prompt)
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId));
    }

    public EmotionEvaluation callEmotionEvaluation(PromptBody promptBody) {
        return buildChatClientRequestSpec(buildPrompt(promptBody), promptBody.conversationId())
                .call()
                .entity(EmotionEvaluation.class);
    }


    private static Prompt buildPrompt(PromptBody promptBody) {
        List<Message> messages = new ArrayList<>();
        String systemPrompt = promptBody.systemPrompt();
        String userPrompt = promptBody.userPrompt();

        Optional.ofNullable(systemPrompt)
                .filter(s -> !s.isBlank())
                .ifPresent(s -> messages.add(
                        SystemMessage.builder()
                                .text(s)
                                .build()
                ));

        messages.add(UserMessage.builder().text(userPrompt).build());

        Prompt.Builder promptBuilder = Prompt.builder().messages(messages);

        Optional.ofNullable(promptBody.chatOptions()).ifPresent(promptBuilder::chatOptions);
        return promptBuilder.build();
    }
}
