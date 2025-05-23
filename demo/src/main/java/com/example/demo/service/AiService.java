package com.example.demo.service;

import com.example.demo.web.dto.ChatReq;
import com.example.demo.web.dto.ChatRes;

public interface AiService {
    // AI 응답
    ChatRes chat(ChatReq req) throws Exception;
}
