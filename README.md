# AI-Driven Web Data Extraction System

## Overview

This project is a microservices-based system designed to automate the extraction and processing of web data.  
Given a URL, the system uses a local LLM to infer, from the URL alone, whether it points to an image or to a web page, and applies the appropriate processing pipeline.

The system relies on AI to interpret the input URL and select the processing tool, independently from user-provided metadata. The page content itself is not inspected by the AI: the decision is based only on the URL.

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
          saves file in          returns cleaned text
          downloaded-images/     (first 500 chars)
                        └────────┬────────┘
                                 ▼
              Orchestrator stores results in MySQL
              (ImageResult / NewsResult)
```

The system is composed of three main services:

### Orchestrator Service (port 8080)
- Central coordination of the system
- Exposes REST APIs for event management
- Handles scheduled processing every 30 seconds
- Integrates with AI (Ollama/phi3 via LangChain4j) for autonomous tool selection
- Calls the two processing services and stores their results in MySQL (`NewsResult` / `ImageResult`)

### Image Processing Service (port 8082)
- Receives image URLs
- Downloads and stores images locally in `downloaded-images/` (file name `image_<timestamp>.jpg`)
- Returns a confirmation message with the absolute file path, which the Orchestrator stores in the `ImageResult` table

### Content Extraction Service (port 8081)
- Downloads web pages and extracts their textual content
- Removes HTML markup using regex-based filtering
- Returns the first 500 characters of the cleaned text, which the Orchestrator stores in the `NewsResult` table

---

## How It Works

1. A new event is submitted via REST API with a URL, user-defined type (tipologia), and scheduled date
2. The event is saved in MySQL with status `lavorato = false`
3. Every 30 seconds the scheduler checks for pending events
4. The AI (Ollama/phi3) analyzes the URL and answers with the name of the tool to use — `downloadImage` or `readNews`
5. The Orchestrator invokes the corresponding microservice via REST (`POST /mcp/execute`); if the AI answer does not contain "image", the text extraction service is used by default
6. Results are stored in the database (`ImageResult` or `NewsResult`) and the event is marked as processed; if processing fails, the error message is stored as the event result

> **Note:** The user-provided "tipologia" is stored as metadata for classification purposes, but it is not used to determine the processing logic.  
> The AI independently analyzes the URL to decide which tool to invoke.

---

## Technologies

| Technology | Version | Purpose |
|---|---|---|
| Java | 17 | Language |
| Quarkus | 3.29.x | Microservices framework |
| Hibernate / JPA (Panache) | — | Database persistence |
| MySQL | 8.0 | Relational database |
| Docker | — | MySQL containerization |
| LangChain4j | — | AI integration |
| Ollama + phi3 | — | Local LLM (no cloud) |
| MCP (Model Context Protocol) | — | Tool exposure by the two microservices (MCP servers); see Limitations |
| Bootstrap 5 | — | Web interface |

---

## Prerequisites

- Java 17+
- Maven 3.9+ (the Quarkus guides recommend a recent 3.9.x release)
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
-- Extracted text (first 500 characters)
SELECT event_id, summary, textContent FROM NewsResult;

-- Downloaded image paths
SELECT event_id, imagePath FROM ImageResult;
```

---

## Limitations

- Content extraction accuracy depends on the structure of target websites; the regex-based cleaning may leave JavaScript residues in the extracted text
- Only the first 500 characters of each page are stored
- AI classification is based on the URL only and may not be accurate for ambiguous URLs; with the default temperature (1.0) the answer may vary between runs
- The two services expose MCP tools, but the Orchestrator invokes them through a separate REST endpoint (`/mcp/execute`), not through the MCP protocol
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
