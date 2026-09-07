package com.researchmate.mcp.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ArxivClient {

    private final RestClient restClient;

    public ArxivClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://export.arxiv.org")
                .build();
    }

    public String search(String query) {

        return restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/query")
                        .queryParam("search_query", "all:" + query)
                        .queryParam("start", 0)
                        .queryParam("max_results", 5)
                        .build())
                .retrieve()
                .body(String.class);
    }
}
