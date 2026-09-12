package com.researchmate.mcp.tool;

import com.researchmate.mcp.model.ArxivPaper;
import com.researchmate.mcp.service.ArxivService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResearchTools {
    private final ArxivService arxivService;

    @McpTool(name = "searchArXiv", description = "Search academic papers on arXiv using a research query")
    public List<ArxivPaper> searchArXiv(
            @McpToolParam(description = "Research topic or question to search for", required = true) String query
    ) {

        return arxivService.searchPapers(query);
    }
}
