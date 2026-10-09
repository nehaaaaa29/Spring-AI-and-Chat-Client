package com.example.learn_spring_ai.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.VectorStoreChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RAGService {

    private final ChatClient chatClient;
    private final EmbeddingModel embeddingModel;
    private final VectorStore vectorStore;
    private final ChatMemory chatMemory;

    @Value("classpath:faq.pdf")
      private Resource pdfFile;



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

        List<Document> documents =vectorStore.similaritySearch(SearchRequest.builder()
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

    public void ingestPdfToVectorStore(){
        PagePdfDocumentReader reader =new PagePdfDocumentReader(pdfFile);
        List<Document>pages =reader.get();
        TokenTextSplitter tokenTextSplitter = TokenTextSplitter.builder()
                .withChunkSize(200)
                .build();

       List<Document>chunks= tokenTextSplitter.apply(pages);
       vectorStore.add(chunks);
    }



    public String askAIWithAdvisors(String prompt,String userId){
        return chatClient.prompt()
                .system("""
                        you are an AI assistant called Cody.
                        Greet user with your Name(COdy) and the user name if you know their name.
                        Answer in a friendly, conversational tone.
                        """)
                .user(prompt)
                .advisors(

                        MessageChatMemoryAdvisor.builder(chatMemory)
                                        .conversationId(userId)
                                                .build(),

                   VectorStoreChatMemoryAdvisor.builder(vectorStore)
                           .conversationId(userId)
                           .defaultTopK(4)
                           .build  ()
                )
                .call()
                .content();

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
}
