package com.example.demo.entity;

import lombok.*;
import org.springframework.ai.openai.OpenAiChatModel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ai {
    private  OpenAiChatModel openAiChatModel;
}
