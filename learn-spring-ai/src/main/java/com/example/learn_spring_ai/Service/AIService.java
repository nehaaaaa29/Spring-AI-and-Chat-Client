package com.example.learn_spring_ai.Service;

import com.example.learn_spring_ai.dto.Joke;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
import org.stringtemplate.v4.ST;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public  class AIService {
     private final ChatClient chatClient;
     private final EmbeddingModel embeddingModel;
     private final VectorStore vectorStore;
     public float[] getEmbedding(String text){
      return    embeddingModel.embed(text);

     }


    public void ingestDataToVectorStore() {
        List<Document> movies = List.of(
                new Document(
                        "A thief who steals corporate secrets through the use of dream-sharing technology.",
                        Map.of("title", "Inception", "genre", "Sci-Fi", "year", 2010)
                ),
                new Document(
                        "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        Map.of("title", "Interstellar", "genre", "Sci-Fi", "year", 2014)
                ),
                new Document(
                        "A poor yet passionate young man falls in love with a rich young woman, giving her a sense of freedom.",
                        Map.of("title", "The Notebook", "genre", "Romance", "year", 2004)
                )
        );

        vectorStore.add(movies);
        vectorStore.add(springAiDocs());
    }

    public String askAI(String prompt){
         String template = """
                 ypu are an AI assistant helping a developer.
                 
                 Rules:
                 -Use ONLY the information provided in the context
                 -you MAY rephrase,summarize,and explain in natural language
                 -Do NOT introduce new concepts or facts
                 -If multiple context sections are relevent, combine them into a single explanation.
                 -if the answer is not present , say I don't know
                 
                 Context:
                 {context}
                 Answer in a friendly,conversational tone.""";



         List<Document>documents =vectorStore.similaritySearch(SearchRequest.builder()
                 .query(prompt)
                 .topK(2)
                 .filterExpression("topic== 'ai' or topic == 'vectorstore'")
                 .build());


        String context=documents.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n\n"));


        PromptTemplate promptTemplate =new PromptTemplate(template);
       String systemPrompt = promptTemplate.render((Map.of("context",context)));


                 return chatClient.prompt()
                         .system(systemPrompt)
                         .user(prompt)
                         .advisors(
                                 new SimpleLoggerAdvisor()
                         )
                         .call()
                         .content();
    }





    public List<Document> similaritySearch(String text){
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(text)
                .topK(3)
                .build());
    }


    public static List<Document> springAiDocs() {
        return List.of(
                new Document(
                        "Spring AI provides abstractions like ChatClient, EmbeddingModel, and VectorStore.",
                        Map.of("topic", "ai")
                ),
                new Document(
                        "A VectorStore is used to persist embeddings and perform similarity search.",
                        Map.of("topic", "vectorstore")
                ),
                new Document(
                        "Retrieval Augmented Generation combines vector search with LLMs to ground responses.",
                        Map.of("topic", "vectorstore")
                )
        );
    }



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
           .entity(Joke.class);
   return response.text();
     }
}
