package com.researchmate.mcp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AcademicPaper {

    private PaperProvider provider;

    private String externalId;

    private String title;

    private List<String> authors;

    private String abstractText;

    private LocalDateTime publishedAt;

    private LocalDateTime updatedAt;

    private List<String> categories;

    private String paperUrl;

    private String pdfUrl;
}