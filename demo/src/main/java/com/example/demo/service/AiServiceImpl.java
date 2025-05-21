package com.example.demo.service;


import com.example.demo.web.dto.ChatReq;
import com.example.demo.web.dto.ChatRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;


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


    @Value("${FINE_TUNED_MODEL}")
    private String fineTunedModel;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public AiServiceImpl(OpenAiChatModel openAiChatModel, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.openAiChatModel = openAiChatModel;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public ChatRes chat(ChatReq req) throws Exception {
        //String openAiResponse = openAiChatModel.call(request.getMessage() + sys + etc);

        int age = req.getAge();
        double vision = req.getVision();
        List<String> diseaseTags = req.getDiseaseTags();
        int count = 10; // openCV 호출 필요

        String systemMessage = "Respond only in Korean. Format must be JSON. No extra words.";

        // user 메시지: 실제 사용자 입력을 포함한 메시지
        String userMessage = String.format("age:%d, vision:%.1f, disease:%s", age, vision, diseaseTags);

        // 프롬프트
        //String prompt = String.format("age:%d, vision:%.1f, disease:%s", age, vision, diseaseTags);
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
//        return new ChatRes(
//                openAiChatModel.call(request.getMessage() + sys + "call back recommended between eye-blink time(sec). only number. not sentence."),
//                openAiChatModel.call(request.getMessage() + sys + "calculate my eye-score(1~100). 보통 시력은 0.6이야.ONLY NUMBER, DON'T USE SENTENCE."),
//                openAiChatModel.call(request.getMessage() + sys + "tell your opinion in my eye condition in 2 lines. compare similar age I DONT NEED FLOWERY LANGUAGE"),
//                openAiChatModel.call(request.getMessage() + sys + "tell food for my eye health. 2~3."),
//                openAiChatModel.call(request.getMessage() + sys + "for my eyes tell me eye stretch. 3 is enough. in 100 word")
//        String duration,
//        String score,
//        String tip,
//        String food,
//        String stretch
    }
}