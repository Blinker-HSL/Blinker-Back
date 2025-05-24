package com.example.demo.service;

import com.example.demo.web.dto.ChatReq;
import com.example.demo.web.dto.ChatRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AiService {
    // AI 응답
    ChatRes chat(ChatReq req, List<MultipartFile> images) throws Exception;
}
