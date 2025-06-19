package com.example.demo.service;


import com.example.demo.web.dto.ChatReq;
import com.example.demo.web.dto.ChatRes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class AiServiceImpl implements AiService {
    private final OpenAiChatModel openAiChatModel;
    private final Message sys = new SystemMessage(
            "result must translate in Korean. useless conversation is BAN. dont use English.");

    @Value("${openai.fine-tuned-model}")
    private String fineTunedModel;

    @Value("${spring.ai.openai.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(OpenAiChatModel openAiChatModel, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.openAiChatModel = openAiChatModel;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatRes chat(ChatReq req, List<MultipartFile> images) throws Exception {
        int age = req.getAge();
        double vision = req.getVision();
        List<String> diseaseTags = req.getDiseaseTags();
        int count = 10; // 기본값, 아래에서 Flask 서버 호출로 업데이트

        // Flask 서버 호출 - 이미지 리스트 전송하여 blink count 얻기
        if (images != null && !images.isEmpty()) {
            // flask 서버 주소소
            String flaskUrl = "http://localhost:5050/analyze-eyes";
            org.springframework.util.MultiValueMap<String, Object> body = new org.springframework.util.LinkedMultiValueMap<>();
            for (MultipartFile img : images) {
                org.springframework.core.io.ByteArrayResource imageResource = new org.springframework.core.io.ByteArrayResource(img.getBytes()) {
                    @Override
                    public String getFilename() {
                        return img.getOriginalFilename();
                    }
                };
                body.add("images", imageResource);
            }

            HttpHeaders flaskHeaders = new HttpHeaders();
            flaskHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<org.springframework.util.MultiValueMap<String, Object>> flaskRequest = new HttpEntity<>(body, flaskHeaders);
            try {
                ResponseEntity<String> flaskRes = restTemplate.postForEntity(flaskUrl, flaskRequest, String.class);
                if (flaskRes.getStatusCode().is2xxSuccessful()) {
                    JsonNode blinkJson = objectMapper.readTree(flaskRes.getBody());
                    count = blinkJson.get("blink_count").asInt();
                    log.info("Flask 응답 blink_count: {}", count);
                }
            } catch (Exception e) {
                log.error("Flask 서버 호출 실패: {}", e.getMessage());
                count = 10; // 기본값
            }
        }

        String systemMessage = "Respond only in Korean. Format must be JSON. No extra words.";

        // user 메시지: 실제 사용자 입력을 포함한 메시지
        String userMessage = String.format("age:%d, vision:%.1f, disease:%s", age, vision, diseaseTags);

        // 프롬프트

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemMessage));
        messages.add(Map.of("role", "user", "content", userMessage));

        // content
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", fineTunedModel);  // 파인튜닝 모델 ID
        requestBody.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);
        log.info(headers.toString());
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> res = null;
        try {
            res = restTemplate
                    .exchange("https://api.openai.com/v1/chat/completions",
                            HttpMethod.POST,
                            entity,
                            String.class);
            log.info("호출 끝 - OpenAI 응답 수신 성공");
            String responseBody = res.getBody();
        } catch (HttpStatusCodeException e) {
            log.info("🔑 OpenAI API KEY = {}", apiKey);
            log.error("OpenAI API 호출 오류 발생: 상태 코드={} 응답 본문={}",
                    e.getStatusCode(), e.getResponseBodyAsString());
        }

        if (res == null || res.getBody() == null) {
            log.error("API 응답이 비어 있습니다.");
            throw new Exception("OpenAI API 응답 없음");
        }

        // 응답 본문에서 'choices' 추출
        String responseBody = res.getBody();
        JsonNode rootNode = objectMapper.readTree(responseBody);  // 응답을 JsonNode로 파싱
        JsonNode choicesNode = rootNode.get("choices");

        // 'choices' 배열이 비어 있지 않으면 첫 번째 'choice'에서 'message.content' 추출
        String content = null;
        if (choicesNode != null && choicesNode.isArray() && choicesNode.size() > 0) {
            JsonNode messageNode = choicesNode.get(0).get("message");
            content = messageNode != null ? messageNode.get("content").asText() : "No content found";

            log.info("API 응답 내용: " + content);

            // 파싱
            System.out.println("파싱 시작\n");
            JsonNode parsed = null;
            try {
                parsed = objectMapper.readTree(content);  // content가 JSON 형식인지 파싱
            } catch (Exception e) {
                log.error("content 파싱 오류 발생: {}", e.getMessage());
                throw new Exception("content 파싱 오류", e);
            }

            List<ChatRes.Food> foods = new ArrayList<>();

            for (int i = 0; i < 3; i++) {
                String name = parsed.get("4-name").get(i).asText();
                List<String> ingredients = new ArrayList<>();
                for (JsonNode ing : parsed.get("4-ingredient").get(i)) {
                    ingredients.add(ing.asText());
                }
                List<String> effects = new ArrayList<>();
                for (JsonNode eff : parsed.get("4-effect").get(i)) {
                    effects.add(eff.asText());
                }
                foods.add(new ChatRes.Food(name, ingredients, effects));
            }
            List<String> ingDummy1 = List.of("베타카로틴", "안토시아닌");
            List<String> ingDummy2 = List.of("루테인","비타민E");
            List<String> effDummy1 = List.of("야맹증 예방", "시력 개선");
            List<String> effDummy2 = List.of("혈류 개선", "눈 피로 개선");
            foods.add(new ChatRes.Food("고구마", ingDummy1, effDummy1));
            foods.add(new ChatRes.Food("아몬드", ingDummy2, effDummy2));
            List<String> stretches = List.of(
                    parsed.get("5-1").asText(),
                    parsed.get("5-2").asText(),
                    parsed.get("5-3").asText()
            );

            System.out.println("파싱 끝\n");
            return new ChatRes(
                    openAiChatModel.call(String.format("%d", count) + userMessage + "Calculate the time between appropriate blinks. response is only number"),
                    openAiChatModel.call(userMessage + "calculate my eye-score(1~100).ONLY NUMBER, DON'T USE SENTENCE"),
                    openAiChatModel.call(userMessage + sys + "tell your opinion in my eye condition in 2 lines. compare similar age. 1 sentence can use in 6~7 word."),
                    foods,
                    stretches
            );
        } else {
            log.error("응답에서 'choices'가 비어 있습니다.");
            throw new Exception("OpenAI API 응답 처리 오류");
        }
    }
}