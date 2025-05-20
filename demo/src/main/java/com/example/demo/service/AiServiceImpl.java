package com.example.demo.service;

import com.example.demo.web.dto.ChatReq;
import com.example.demo.web.dto.ChatRes;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiServiceImpl implements AiService {
    private final OpenAiChatModel openAiChatModel;
    private final Message sys = new SystemMessage(
            "result must translate in Korean. useless conversation is BAN. dont use English.");


    @Override
    public ChatRes chat(ChatReq request) {
        //String openAiResponse = openAiChatModel.call(request.getMessage() + sys + etc);

        return new ChatRes(
                openAiChatModel.call(request.getMessage() + sys + "call back recommended between eye-blink time(sec). only number. not sentence."),
                openAiChatModel.call(request.getMessage() + sys + "calculate my eye-score(1~100). 보통 시력은 0.6이야.ONLY NUMBER, DON'T USE SENTENCE."),
                openAiChatModel.call(request.getMessage() + sys + "tell your opinion in my eye condition in 2 lines. compare similar age I DONT NEED FLOWERY LANGUAGE"),
                openAiChatModel.call(request.getMessage() + sys + "tell food for my eye health. 2~3."),
                openAiChatModel.call(request.getMessage() + sys + "for my eyes tell me eye stretch. 3 is enough. in 100 word")
//        String duration,
//        String score,
//        String tip,
//        String food,
//        String stretch
        );
    }
}
