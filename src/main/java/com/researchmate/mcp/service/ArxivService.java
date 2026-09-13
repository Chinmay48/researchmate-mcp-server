package com.researchmate.mcp.service;

import com.researchmate.mcp.client.ArxivClient;
import com.researchmate.mcp.model.AcademicPaper;
import com.researchmate.mcp.parser.ArxivParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArxivService {

    private final ArxivClient arxivClient;
    private final ArxivParser arxivParser;

    public List<AcademicPaper> searchPapers(String query) {

        System.out.println(
                "Searching arXiv for query: " + query
        );

        String xml = arxivClient.search(query);

        return arxivParser.parse(xml);
    }
}