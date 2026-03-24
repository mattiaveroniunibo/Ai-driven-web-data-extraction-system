package com.kayak.service;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import io.quarkiverse.langchain4j.RegisterAiService;

@RegisterAiService
public interface AiOrchestratorService {

    @SystemMessage("""
        You are an AI orchestrator for a travel data aggregation system.
        Your job is to analyze requests and decide which tool to use.
        
        Available tools:
        - downloadImage: Use this for image URLs (e.g., .jpg, .png, picsum.photos)
        - readNews: Use this for web pages, articles, HTML content
        
        Respond ONLY with the tool name: either "downloadImage" or "readNews"
        """)
    @UserMessage("Analyze this URL and decide which tool to use: {url}")
    String decideTool(String url);
}