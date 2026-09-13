package com.researchmate.mcp.tool;

import com.researchmate.mcp.model.AcademicPaper;
import com.researchmate.mcp.service.AcademicSearchResult;
import com.researchmate.mcp.service.AcademicSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResearchTools {

    private final AcademicSearchService academicSearchService;

    @McpTool(
            name = "searchArXiv",
            description = "Search academic papers using arXiv with Semantic Scholar fallback"
    )
    public AcademicSearchResult searchArXiv(
            @McpToolParam(
                    description = "Research topic or question to search for",
                    required = true
            )
            String query
    ) {
        return academicSearchService.search(query);
    }
}