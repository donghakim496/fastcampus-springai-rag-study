package com.example.demo.controller;

import com.example.demo.record.EmotionEvaluation;
import com.example.demo.record.PromptBody;
import com.example.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping(value = "/call", produces = MediaType.APPLICATION_JSON_VALUE)
    ChatResponse call(@RequestBody @Valid PromptBody promptBody) {
        return chatService.call(promptBody);
    }

    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> stream(@RequestBody @Valid PromptBody promptBody) {
        return chatService.stream(promptBody);
    }

    @PostMapping(value = "/emotion", produces = MediaType.APPLICATION_JSON_VALUE)
    EmotionEvaluation emotion(@RequestBody @Valid PromptBody promptBody) {
        return chatService.callEmotionEvaluation(promptBody);
    }
}
