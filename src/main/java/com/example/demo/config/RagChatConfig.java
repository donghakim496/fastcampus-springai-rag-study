package com.example.demo.config;

import ch.qos.logback.classic.LoggerContext;
import com.example.demo.record.PromptBody;
import com.example.demo.record.RagPromptBody;
import com.example.demo.service.RagChatService;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;
import java.util.Scanner;

@Configuration
public class RagChatConfig {

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

    @ConditionalOnProperty(prefix = "app.cli", name = "enabled", havingValue = "true")
    @Bean
    public CommandLineRunner cli(@Value("${spring.application.name}") String applicationName, RagChatService ragChatService,
                                 @Value("${app.cli.filter-expression:") String filterExpression) {
        return args -> {
            LoggerContext context = (LoggerContext) org.slf4j.LoggerFactory.getILoggerFactory();
            context.getLogger("ROOT").detachAppender("CONSOLE");

            System.out.println("\n" + "Welcome to " + applicationName + " CLI");

            try(Scanner scanner = new Scanner(System.in)) {
                while (true){
                    System.out.print("\n"+"User: ");
                    String userMessage = scanner.nextLine();
                    RagPromptBody ragPromptBody = new RagPromptBody(userMessage, "cli");
                    ragChatService.stream(ragPromptBody, Optional.ofNullable(filterExpression).filter(String::isBlank))
                            .doFirst(() -> System.out.print("\n"+"Assistant: "))
                            .doOnNext(System.out::print)
                            .doOnComplete(System.out::println)
                            .blockLast();
                }
            }
        };
    }
}
