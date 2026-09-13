package com.researchmate.mcp.parser;

import com.researchmate.mcp.model.AcademicPaper;
import com.researchmate.mcp.model.PaperProvider;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class ArxivParser {

    private static final String ATOM_NAMESPACE =
            "http://www.w3.org/2005/Atom";

    public List<AcademicPaper> parse(String xml) {

        List<AcademicPaper> papers = new ArrayList<>();

        try {
            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(true);

            Document document = factory
                    .newDocumentBuilder()
                    .parse(
                            new ByteArrayInputStream(
                                    xml.getBytes(StandardCharsets.UTF_8)
                            )
                    );

            NodeList entries =
                    document.getElementsByTagNameNS(
                            ATOM_NAMESPACE,
                            "entry"
                    );

            for (int i = 0; i < entries.getLength(); i++) {

                Element entry = (Element) entries.item(i);

                AcademicPaper paper = AcademicPaper.builder()
                        .provider(PaperProvider.ARXIV)
                        .externalId(getText(entry, "id"))
                        .title(getText(entry, "title"))
                        .abstractText(getText(entry, "summary"))
                        .publishedAt(
                                parseDate(getText(entry, "published"))
                        )
                        .updatedAt(
                                parseDate(getText(entry, "updated"))
                        )
                        .authors(getAuthors(entry))
                        .categories(getCategories(entry))
                        .paperUrl(getPaperUrl(entry))
                        .pdfUrl(getPdfUrl(entry))
                        .build();

                papers.add(paper);
            }

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Failed to parse arXiv response",
                    exception
            );
        }

        return papers;
    }

    private String getText(Element parent, String tagName) {

        NodeList nodes =
                parent.getElementsByTagNameNS(
                        ATOM_NAMESPACE,
                        tagName
                );

        if (nodes.getLength() == 0) {
            return null;
        }

        return nodes.item(0)
                .getTextContent()
                .trim();
    }

    private List<String> getAuthors(Element entry) {

        List<String> authors = new ArrayList<>();

        NodeList authorNodes =
                entry.getElementsByTagNameNS(
                        ATOM_NAMESPACE,
                        "author"
                );

        for (int i = 0; i < authorNodes.getLength(); i++) {

            Element author =
                    (Element) authorNodes.item(i);

            String name = getText(author, "name");

            if (name != null && !name.isBlank()) {
                authors.add(name);
            }
        }

        return authors;
    }

    private List<String> getCategories(Element entry) {

        List<String> categories = new ArrayList<>();

        NodeList categoryNodes =
                entry.getElementsByTagNameNS(
                        ATOM_NAMESPACE,
                        "category"
                );

        for (int i = 0; i < categoryNodes.getLength(); i++) {

            Element category =
                    (Element) categoryNodes.item(i);

            String term = category.getAttribute("term");

            if (!term.isBlank()) {
                categories.add(term);
            }
        }

        return categories;
    }

    private String getPaperUrl(Element entry) {

        NodeList links =
                entry.getElementsByTagNameNS(
                        ATOM_NAMESPACE,
                        "link"
                );

        for (int i = 0; i < links.getLength(); i++) {

            Element link = (Element) links.item(i);

            String type = link.getAttribute("type");
            String title = link.getAttribute("title");
            String rel = link.getAttribute("rel");
            String href = link.getAttribute("href");

            boolean isPaperLink =
                    "text/html".equalsIgnoreCase(type)
                            || "abs".equalsIgnoreCase(title)
                            || (
                            "alternate".equalsIgnoreCase(rel)
                                    && href != null
                                    && href.contains("/abs/")
                    );

            if (isPaperLink && href != null && !href.isBlank()) {
                return href;
            }
        }

        return null;
    }

    private String getPdfUrl(Element entry) {

        NodeList links =
                entry.getElementsByTagNameNS(
                        ATOM_NAMESPACE,
                        "link"
                );

        for (int i = 0; i < links.getLength(); i++) {

            Element link = (Element) links.item(i);

            String type = link.getAttribute("type");
            String title = link.getAttribute("title");
            String rel = link.getAttribute("rel");
            String href = link.getAttribute("href");

            boolean isPdfLink =
                    "application/pdf".equalsIgnoreCase(type)
                            || "pdf".equalsIgnoreCase(title)
                            || (
                            "related".equalsIgnoreCase(rel)
                                    && href != null
                                    && href.contains("/pdf/")
                    )
                            || (
                            href != null
                                    && href.contains("/pdf/")
                    );

            if (isPdfLink && href != null && !href.isBlank()) {
                return href;
            }
        }

        return null;
    }

    private LocalDateTime parseDate(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return OffsetDateTime.parse(value)
                .toLocalDateTime();
    }
}