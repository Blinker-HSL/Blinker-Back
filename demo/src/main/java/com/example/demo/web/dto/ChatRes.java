package com.example.demo.web.dto;

import java.util.List;

public record ChatRes(
        String term,
        String score,
        String tip,
        List<Food> food,
        List<String> stretchTips
) {
    public record Food(
            String name,
            List<String> ingredient,
            List<String> effect) {

    }
}
