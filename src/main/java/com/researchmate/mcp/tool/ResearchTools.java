package com.researchmate.mcp.tool;

import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;

@Component
public class ResearchTools {

    @McpTool(name = "searchArXiv", description = "Search academic papers on arXiv using a research query")
    public String searchArXiv(
            @McpToolParam(description = "Research topic or question to search for", required = true) String query
    ) {

        return "Dummy arXiv search result for query: " + query;
    }
}
