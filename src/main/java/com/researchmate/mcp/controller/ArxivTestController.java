//package com.researchmate.mcp.controller;
//
//import com.researchmate.mcp.client.ArxivClient;
//import com.researchmate.mcp.model.ArxivPaper;
//import com.researchmate.mcp.parser.ArxivParser;
//import lombok.RequiredArgsConstructor;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.util.List;
//
//@RestController
//@RequiredArgsConstructor
//public class ArxivTestController {
//
//    private final ArxivClient arxivClient;
//    private final ArxivParser arxivParser;
//
//    @GetMapping("/api/test/arxiv")
//    public List<ArxivPaper> testArxiv(
//            @RequestParam String query
//    ) {
//
//        String xml = arxivClient.search(query);
//
//        return arxivParser.parse(xml);
//    }
//}