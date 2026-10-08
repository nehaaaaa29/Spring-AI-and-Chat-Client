# Spring AI: RAG & PGVector Integration

This repository contains the implementation of **Spring AI** using Spring Boot, focusing on local LLM integration with **Ollama**, PGVector database storage, embeddings, and **Retrieval-Augmented Generation (RAG)**.

---

## 🚀 Features

- **Local LLM Integration**: Powered by Ollama.
- **Vector Store Database**: PostgreSQL with `pgvector` extension running via Docker.
- **Vector Embeddings**: Dynamic string to vector conversion and storage using `VectorStore`.
- **Metadata Support**: Storing documents along with custom metadata tags (e.g., genre, title, year).
- **Similarity Search**: Querying vector database using semantic distance matching.

---

## 🛠️ Prerequisites & Setup

### 1. Run PGVector via Docker

Execute the following command to start PostgreSQL with pre-installed `pgvector`:

```bash
docker run -d \
  --name pgvector-db \
  -p 5432:5432 \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=291026 \
  -e POSTGRES_DB=mydatabase \
  pgvector/pgvector:pg16
