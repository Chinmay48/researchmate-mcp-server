package com.researchmate.mcp.client;

import com.researchmate.mcp.exception.AcademicProviderException;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class ArxivClient {

    private final RestClient restClient;

    public ArxivClient() {
        this.restClient = RestClient.builder()
                .baseUrl("https://export.arxiv.org")
                .defaultHeader(
                        "User-Agent",
                        "ResearchMate/1.0 academic-research-application"
                )
                .build();
    }

    public String search(String query) {

        try {

            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/query")
                            .queryParam(
                                    "search_query",
                                    "all:" + query
                            )
                            .queryParam("start", 0)
                            .queryParam("max_results", 5)
                            .build()
                    )
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, response) -> {

                                int statusCode =
                                        response.getStatusCode().value();

                                String message;

                                if (statusCode == 429) {
                                    message =
                                            "arXiv rate limit exceeded";
                                } else if (statusCode == 503) {
                                    message =
                                            "arXiv service is temporarily unavailable";
                                } else {
                                    message =
                                            "arXiv request failed";
                                }

                                throw new AcademicProviderException(
                                        message,
                                        statusCode,
                                        null
                                );
                            }
                    )
                    .body(String.class);

        } catch (AcademicProviderException exception) {

            System.err.println(
                    "arXiv provider error: HTTP "
                            + exception.getStatusCode()
                            + " - "
                            + exception.getMessage()
            );

            throw exception;

        } catch (RestClientException exception) {

            System.err.println(
                    "Unable to connect to arXiv: "
                            + exception.getMessage()
            );

            throw new AcademicProviderException(
                    "Unable to connect to arXiv",
                    503,
                    exception
            );
        }
    }
}