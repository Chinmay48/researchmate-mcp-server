package com.researchmate.mcp.service;

import com.researchmate.mcp.client.ArxivClient;
import com.researchmate.mcp.client.SemanticScholarClient;
import com.researchmate.mcp.exception.AcademicProviderException;
import com.researchmate.mcp.model.AcademicPaper;
import com.researchmate.mcp.parser.ArxivParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AcademicSearchService {

    private final ArxivClient arxivClient;
    private final ArxivParser arxivParser;
    private final SemanticScholarClient semanticScholarClient;

    public AcademicSearchResult search(String query) {

        List<String> messages = new ArrayList<>();

        messages.add("Searching arXiv...");

        try {
            String xml = arxivClient.search(query);

            List<AcademicPaper> papers =
                    arxivParser.parse(xml);

            messages.add(
                    "arXiv search completed successfully."
            );

            return AcademicSearchResult.builder()
                    .providerUsed("ARXIV")
                    .messages(messages)
                    .papers(papers)
                    .build();

        } catch (AcademicProviderException exception) {

            messages.add(
                    "arXiv failed: "
                            + exception.getMessage()
            );

            messages.add(
                    "Trying Semantic Scholar..."
            );
        }

        try {
            List<AcademicPaper> papers =
                    semanticScholarClient.search(query);

            messages.add(
                    "Semantic Scholar search completed successfully."
            );

            return AcademicSearchResult.builder()
                    .providerUsed("SEMANTIC_SCHOLAR")
                    .messages(messages)
                    .papers(papers)
                    .build();

        } catch (AcademicProviderException exception) {

            messages.add(
                    "Semantic Scholar failed: "
                            + exception.getMessage()
            );

            throw new AcademicProviderException(
                    String.join(" ", messages),
                    503,
                    exception
            );
        }
    }
}