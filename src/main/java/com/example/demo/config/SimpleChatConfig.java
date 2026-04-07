package com.example.demo.config;

import ch.qos.logback.classic.LoggerContext;
import com.example.demo.record.PromptBody;
import com.example.demo.service.ChatService;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class SimpleChatConfig {

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor(){
        return SimpleLoggerAdvisor.builder()
                .build();
    }

    @Bean
    public ChatMemory chatMemory(){
        return MessageWindowChatMemory.builder()
                .maxMessages(10)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory){
        return MessageChatMemoryAdvisor.builder(chatMemory)
                .build();
    }

    @ConditionalOnProperty(prefix = "spring.application", name = "cli", havingValue = "true")
    @Bean
    public CommandLineRunner cli(@Value("${spring.application.name}") String applicationName, ChatService chatService){
        return args -> {
            LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            context.getLogger("ROOT").detachAppender("CONSOLE");

            System.out.println("\n" + "Welcome to " + applicationName + " CLI");

            try(Scanner scanner = new Scanner(System.in)) {
                while (true){
                    System.out.print("\n"+"User: ");
                    String userMessage = scanner.nextLine();
                    PromptBody promptBody = new PromptBody(userMessage, "cli");
                    chatService.stream(promptBody)
                            .doFirst(() -> System.out.print("\n"+"Assistant: "))
                            .doOnNext(System.out::print)
                            .doOnComplete(System.out::println)
                            .blockLast();
                }
            }
        };
    }
}
