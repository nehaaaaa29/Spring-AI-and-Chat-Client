package com.example.learn_spring_ai.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor

public  class AIService {
     private final ChatClient chatClient;
     public String getJoke(String topic){
         String systemPrompt= """
                 you are a sarcastic joker,you make poetic jokes in 8 lines.
                 you don't make jokes about politics.
                 Give a joke on the topic:{topic}
                 """;

         PromptTemplate promptTemplate =new PromptTemplate(systemPrompt);
         String  renderedText = promptTemplate.render(Map.of("topic" , topic));



   var response = chatClient.prompt()
                 .user(renderedText)
           .advisors(
                   new SimpleLoggerAdvisor()
           )
                 .call()
                 .chatClientResponse();
   return response.chatResponse().getResult().getOutput().getText();
     }
}
