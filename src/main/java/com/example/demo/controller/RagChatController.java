package com.example.demo.controller;

import com.example.demo.record.RagPromptBody;
import com.example.demo.service.RagChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.awt.*;
import java.util.Optional;

@RestController
@RequestMapping("/rag")
@RequiredArgsConstructor
public class RagChatController {

    private final RagChatService ragChatService;

    @PostMapping(value = "/call", produces = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse call(@RequestBody @Valid RagPromptBody ragPromptBody) {
        return ragChatService.call(ragPromptBody, Optional.ofNullable(ragPromptBody.filterExpression()));
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> stream(@RequestBody @Valid RagPromptBody ragPromptBody) {
        return ragChatService.stream(ragPromptBody, Optional.ofNullable(ragPromptBody.filterExpression()));
    }

}
