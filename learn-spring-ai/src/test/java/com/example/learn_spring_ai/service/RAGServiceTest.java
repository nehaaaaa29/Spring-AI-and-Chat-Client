package com.example.learn_spring_ai.service;

import com.example.learn_spring_ai.Service.RAGService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class RAGServiceTest {

    @Autowired
    private RAGService ragService;
    @Test
   public void testIngest(){
        ragService.ingestPdfToVectorStore();

    }

    @Test
    public  void testAskAi(){
        var response = ragService.askAI("what is vector store?");
        System.out.println(response);
    }
    @Test
    public  void testAskAiwithAdvisiors(){
        var response = ragService.askAIWithAdvisors("Can u tell me best quality of a java developer and my name is neha","neha123");
        System.out.println(response);
    }

}