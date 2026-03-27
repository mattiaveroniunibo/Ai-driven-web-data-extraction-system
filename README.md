# AI-Driven Web Data Extraction System

## Overview

This project is a microservices-based system designed to automate the extraction and processing of web data.  
Given a URL, the system automatically determines the type of content (e.g. image or article) and applies the appropriate processing pipeline.

The system relies on AI to dynamically interpret the input URL and select the appropriate processing tool, independently from user-provided metadata.

---

## Architecture

The system follows a microservices architecture with an AI-driven orchestration layer.
```
Client → POST /api/events → Orchestrator (8080)
                                    │
                          @Scheduled every 30s
                                    │
                          AI (Ollama/phi3)
                         decides tool to use
                        ┌─────────────────┐
                        ▼                 ▼
          Image Service (8082)   News Service (8081)
          downloaded-images/     NewsResult (DB text)
          ImageResult (DB path)
```

The system is composed of three main services:

### Orchestrator Service (port 8080)
- Central coordination of the system
- Exposes REST APIs for event management
- Handles scheduled processing every 30 seconds
- Integrates with AI (Ollama/phi3 via LangChain4j) for autonomous tool selection
- Manages persistence on MySQL database

### Image Processing Service (port 8082)
- Receives image URLs
- Downloads and stores images locally in `downloaded-images/`
- Saves the file path in `ImageResult` table

### Content Extraction Service (port 8081)
- Extracts textual content from web pages
- Cleans and structures HTML content using regex-based filtering
- Saves full text and summary in `NewsResult` table

---

## How It Works

1. A new event is submitted via REST API with a URL, user-defined type (tipologia), and scheduled date
2. The event is saved in MySQL with status `lavorato = false`
3. Every 30 seconds the scheduler checks for pending events
4. The AI (Ollama/phi3) analyzes the URL and autonomously decides which tool to use — `downloadImage` or `readNews`
5. The corresponding microservice is invoked
6. Results are stored in the database (`ImageResult` or `NewsResult`) and the event is marked as processed

> **Note:** The user-provided "tipologia" is stored as metadata for classification purposes, but it is not used to determine the processing logic.  
> The AI independently analyzes the URL to decide which tool to invoke.

---

## Technologies

| Technology | Version | Purpose |
|---|---|---|
| Java | 20 (target 17) | Language |
| Quarkus | 3.29.x | Microservices framework |
| Hibernate / JPA (Panache) | — | Database persistence |
| MySQL | 8.0 | Relational database |
| Docker | — | MySQL containerization |
| LangChain4j | — | AI integration |
| Ollama + phi3 | — | Local LLM (no cloud) |
| MCP (Model Context Protocol) | — | Tool exposure between microservices and AI |
| Bootstrap 5 | — | Web interface |

---

## Prerequisites

- Java 17+
- Maven 3.9+
- Docker Desktop
- [Ollama](https://ollama.com) installed with phi3 model

---

## Getting Started

### 1. Install Ollama and download the model (first time only)

Download Ollama from [ollama.com](https://ollama.com), install it, then run:
```bash
ollama pull phi3
```

### 2. Start MySQL with Docker

Make sure Docker Desktop is running, then open a terminal and run:
```bash
docker run -d --name kayak-mysql -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=kayak_db -p 3306:3306 mysql:8.0
```

If the container already exists from a previous run:
```bash
docker start kayak-mysql
```

### 3. Start the Content Extraction Service

Open a new terminal:
```bash
cd content-extraction-service/mcp-news-server
mvn quarkus:dev
```
Wait for: `Started in Xs. Listening on: http://localhost:8081`

### 4. Start the Image Processing Service

Open a new terminal:
```bash
cd image-processing-service/mcp-image-server
mvn quarkus:dev
```
Wait for: `Started in Xs. Listening on: http://localhost:8082`

### 5. Start the Orchestrator Service

Open a new terminal:
```bash
cd main-app
mvn quarkus:dev
```
Wait for: `Started in Xs. Listening on: http://localhost:8080`

### 6. Open the web interface

Go to [http://localhost:8080](http://localhost:8080)

---

## API

### Submit a new event
```http
POST /api/events
Content-Type: application/json

{
  "url": "https://www.repubblica.it/",
  "tipologia": "NEWS",
  "data": "2025-11-01T10:00:00"
}
```

### List all events
```http
GET /api/events
```

### View results

Results are stored in MySQL and can be queried directly:
```sql
-- Full extracted text
SELECT event_id, summary, textContent FROM NewsResult;

-- Downloaded image paths
SELECT event_id, imagePath FROM ImageResult;
```

---

## Limitations

- Content extraction accuracy depends on the structure of target websites
- AI classification may not be 100% accurate for ambiguous URLs
- System is designed for local execution (AI runs locally via Ollama)

---

## Future Improvements

- Support for additional content types (for example: video, PDF)
- Automatic classification of "tipologia" using AI (removing the need for user input)
- Caching mechanisms to avoid duplicate downloads
- Monitoring dashboard
- Deployment in distributed environments

---

## Author

Mattia Veroni  
University of Bologna — Computer Science for Management
