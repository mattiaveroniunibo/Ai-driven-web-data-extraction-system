# AI-Driven Web Data Extraction System

## Overview
This project is a microservices-based system designed to automate the extraction and processing of web data.  
Given a URL, the system automatically determines the type of content (e.g. image or article) and applies the appropriate processing pipeline.

The system integrates artificial intelligence to dynamically select the correct processing strategy, enabling flexible and extensible workflows.

---

## Architecture

The system is composed of three main services:

### Orchestrator Service
- Central coordination of the system
- Exposes REST APIs for event management
- Handles scheduling and workflow execution
- Integrates with AI for decision making
- Manages database interactions

### Image Processing Service
- Downloads images from URLs
- Stores them locally
- Returns file paths

### Content Extraction Service
- Extracts textual content from web pages
- Cleans and structures HTML content using text processing techniques (regex-based filtering)

---

## Technologies

- Java  
- Quarkus  
- Hibernate / JPA  
- MySQL  
- REST APIs  
- Ollama (local LLM)  
- LangChain4j  

---

## Workflow

1. A URL is submitted  
2. The system schedules processing  
3. The AI determines content type  
4. The correct microservice is invoked  
5. Results are stored in the database  

---

## Status
Completed

---

## Author
Mattia Veroni  
University of Bologna — Computer Science for Management
