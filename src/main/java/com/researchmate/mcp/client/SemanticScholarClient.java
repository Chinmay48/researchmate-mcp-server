package com.researchmate.mcp.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.researchmate.mcp.exception.AcademicProviderException;
import com.researchmate.mcp.model.AcademicPaper;
import com.researchmate.mcp.model.PaperProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class SemanticScholarClient {

    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    private final String apiKey;

    public SemanticScholarClient(
            ObjectMapper objectMapper,
            @Value("${semantic-scholar.base-url}") String baseUrl,
            @Value("${semantic-scholar.api-key:}") String apiKey
    ) {
        this.objectMapper = objectMapper;
        this.apiKey = apiKey;

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public List<AcademicPaper> search(String query) {
        try {
            String rawResponse = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/graph/v1/paper/search")
                            .queryParam("query", query)
                            .queryParam("limit", 5)
                            .queryParam(
                                    "fields",
                                    "paperId,title,authors,abstract,year,url,openAccessPdf"
                            )
                            .build())
                    .headers(headers -> {
                        if (apiKey != null && !apiKey.isBlank()) {
                            headers.set("x-api-key", apiKey);
                        }

                        headers.set(
                                "User-Agent",
                                "ResearchMate/1.0 academic-research-application"
                        );
                    })
                    .retrieve()
                    .onStatus(
                            HttpStatusCode::isError,
                            (request, response) -> {
                                int statusCode = response.getStatusCode().value();

                                System.err.println(
                                        "Semantic Scholar HTTP status: " + statusCode
                                );

                                System.err.println(
                                        "Semantic Scholar response headers: "
                                                + response.getHeaders()
                                );

                                String message;

                                if (statusCode == 429) {
                                    message = "Semantic Scholar rate limit exceeded";
                                } else if (statusCode == 503) {
                                    message = "Semantic Scholar is temporarily unavailable";
                                } else {
                                    message = "Semantic Scholar request failed with HTTP "
                                            + statusCode;
                                }

                                throw new AcademicProviderException(
                                        message,
                                        statusCode,
                                        null
                                );
                            }
                    )
                    .body(String.class);

            return parseResponse(rawResponse);

        } catch (AcademicProviderException exception) {
            throw exception;

        } catch (RestClientException exception) {
            throw new AcademicProviderException(
                    "Unable to connect to Semantic Scholar",
                    503,
                    exception
            );
        }
    }

    private List<AcademicPaper> parseResponse(String rawResponse) {
        try {
            JsonNode root = objectMapper.readTree(rawResponse);
            JsonNode data = root.path("data");

            List<AcademicPaper> papers = new ArrayList<>();

            for (JsonNode paperNode : data) {
                String paperId = textOrNull(
                        paperNode,
                        "paperId"
                );

                String title = textOrNull(
                        paperNode,
                        "title"
                );

                String abstractText = textOrNull(
                        paperNode,
                        "abstract"
                );

                String paperUrl = textOrNull(
                        paperNode,
                        "url"
                );

                String pdfUrl = null;

                JsonNode openAccessPdf =
                        paperNode.path("openAccessPdf");

                if (!openAccessPdf.isMissingNode()
                        && !openAccessPdf.isNull()) {
                    pdfUrl = textOrNull(
                            openAccessPdf,
                            "url"
                    );
                }

                List<String> authors = new ArrayList<>();

                for (JsonNode authorNode :
                        paperNode.path("authors")) {

                    String authorName = textOrNull(
                            authorNode,
                            "name"
                    );

                    if (authorName != null
                            && !authorName.isBlank()) {
                        authors.add(authorName);
                    }
                }

                Integer year = null;

                if (paperNode.has("year")
                        && !paperNode.get("year").isNull()) {
                    year = paperNode.get("year").asInt();
                }

                LocalDateTime publishedAt = null;

                if (year != null) {
                    publishedAt = LocalDateTime.of(
                            year,
                            1,
                            1,
                            0,
                            0
                    );
                }

                AcademicPaper paper = AcademicPaper.builder()
                        .provider(PaperProvider.SEMANTIC_SCHOLAR)
                        .externalId(paperId)
                        .title(title)
                        .authors(authors)
                        .abstractText(abstractText)
                        .publishedAt(publishedAt)
                        .updatedAt(null)
                        .categories(new ArrayList<>())
                        .paperUrl(paperUrl)
                        .pdfUrl(pdfUrl)
                        .build();

                papers.add(paper);
            }

            return papers;

        } catch (Exception exception) {
            throw new AcademicProviderException(
                    "Unable to parse Semantic Scholar response",
                    502,
                    exception
            );
        }
    }

    private String textOrNull(
            JsonNode node,
            String fieldName
    ) {
        JsonNode field = node.get(fieldName);

        if (field == null || field.isNull()) {
            return null;
        }

        return field.asText();
    }
}