package com.example.learn_spring_ai.Service;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public  class AIService {
     private final ChatClient chatClient;
     public String getJoke(String topic){
       return  chatClient.prompt()
                 .user("give me a joke the topic: "+topic)
                 .call()
                 .content();
     }
}
