package com.researchmate.mcp.service;


import com.researchmate.mcp.client.ArxivClient;
import com.researchmate.mcp.model.ArxivPaper;
import com.researchmate.mcp.parser.ArxivParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArxivService {
    private final ArxivClient arxivClient;
    private final ArxivParser arxivParser;
    public List<ArxivPaper> searchPapers(String query){
        String xml=arxivClient.search(query);
        return  arxivParser.parse(xml);
    }
}
