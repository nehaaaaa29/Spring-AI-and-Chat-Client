package com.example.learn_spring_ai.service;

import com.example.learn_spring_ai.Service.AIService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AIServiceTests {
    @Autowired
    private AIService aiService;

    @Test
    public void testGetJoke(){
        //arrange
var joke =aiService.getJoke("cat");
        //act
        System.out.println(joke);
        //assert
    }
}
