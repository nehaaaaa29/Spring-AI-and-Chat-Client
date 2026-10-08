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
    public void testAskAI(){
        //arrange
        var response =aiService.askAI("what is cells in human");
        //act
        System.out.println(response);
        //assert
    }

    @Test
    public void testGetJoke(){
        //arrange
var joke =aiService.getJoke("sipper");
        //act
        System.out.println(joke);
        //assert
    }
    @Test

    public void testEmbedText(){
        var embed=aiService.getEmbedding("this is a ig text here");
        System.out.println(embed.length);
        for(float e: embed){
            System.out.println(e+" ");
        }
    }

    @Test
    public void testStoreData(){
        aiService.ingestDataToVectorStore();
    }
    @Test
    public void testSimilaritySearch(){
       var res= aiService.similaritySearch("space movie");
       for(var doc:res)
        System.out.println(doc);
    }

}
