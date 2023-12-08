package io.jenkins.plugins.grading;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import org.jenkinsci.test.acceptance.po.PageObject;

/**
 * {@link PageObject} representing the details page of the static analysis tool results.
 *
 * @author Lukas Kirner
 */
@SuppressWarnings("PMD.DataClass")
public class AutoGradePageObject extends PageObject {
    private final String totalScoreInPercent;

    private final List<String> totalScores;

    private final List<String> testHeaders;
    private final Map<String, List<Integer>> testBody;
    private final List<String> testFooter;

    private final List<String> coverageHeaders;
    private final Map<String, List<Integer>> coverageBody;
    private final List<String> coverageFooter;

    private final List<String> pitHeaders;
    private final Map<String, List<Integer>> pitBody;
    private final List<String> pitFooter;

    private final List<String> analysisHeaders;
    private final Map<String, List<Integer>> analysisBody;
    private final List<String> analysisFooter;

    /**
     * Creates an instance of the page displaying the details of the issues for a specific tool.
     *
     * @param pageObject
     *         a finished build configured with a static analysis tool and configured AutoGrading
     * @param url
     *         the base url of the job
     */
    public AutoGradePageObject(final PageObject pageObject, final URL url) {
        super(pageObject, url);
        this.open();

        WebElement page = this.getElement(by.tagName("body"));

        totalScoreInPercent = page.findElement(by.id("total-progress-chart")).getAttribute("data-title");

        totalScores = page.findElements(by.css("div.progress-container")).stream()
                .map(p -> p.findElement(by.css("div.progress-bar")))
                .map(WebElement::getText)
                .collect(Collectors.toList());

        WebElement testTable = page.findElement(By.id("test.tests"));
        testHeaders = getTableHeaders(testTable);
        testBody = getTableBody(testTable);
        testFooter = getTableFooter(testTable);

        WebElement coverageTable = page.findElement(By.id("coverage.coverage"));
        coverageHeaders = getTableHeaders(coverageTable);
        coverageBody = getTableBody(coverageTable);
        coverageFooter = getTableFooter(coverageTable);

        WebElement pitTable = page.findElement(By.id("pit.mutation"));
        pitHeaders = getTableHeaders(pitTable);
        pitBody = getTableBody(pitTable);
        pitFooter = getTableFooter(pitTable);

        WebElement analysisTable = page.findElement(By.id("analysis.analysis"));
        analysisHeaders = getTableHeaders(analysisTable);
        analysisBody = getTableBody(analysisTable);
        analysisFooter = getTableFooter(analysisTable);
    }

    public String getTotalScoreInPercent() {
        return totalScoreInPercent;
    }

    public List<String> getTotalScores() {
        return totalScores;
    }

    public List<String> getTestHeaders() {
        return testHeaders;
    }

    public Map<String, List<Integer>> getTestBody() {
        return testBody;
    }

    public List<String> getTestFooter() {
        return testFooter;
    }

    public List<String> getCoverageHeaders() {
        return coverageHeaders;
    }

    public Map<String, List<Integer>> getCoverageBody() {
        return coverageBody;
    }

    public List<String> getCoverageFooter() {
        return coverageFooter;
    }

    public List<String> getPitHeaders() {
        return pitHeaders;
    }

    public Map<String, List<Integer>> getPitBody() {
        return pitBody;
    }

    public List<String> getPitFooter() {
        return pitFooter;
    }

    public List<String> getAnalysisHeaders() {
        return analysisHeaders;
    }

    public Map<String, List<Integer>> getAnalysisBody() {
        return analysisBody;
    }

    public List<String> getAnalysisFooter() {
        return analysisFooter;
    }

    private List<String> getTableHeaders(final WebElement table) {
        return table.findElement(by.tagName("thead"))
            .findElements(by.tagName("th")).stream()
            .map(WebElement::getText)
            .map(String::trim)
            .collect(Collectors.toList());
    }

    private Map<String, List<Integer>> getTableBody(final WebElement table) {
        return table.findElement(by.tagName("tbody"))
            .findElements(by.tagName("tr")).stream()
            .collect(Collectors.toMap(
                tr -> tr.findElements(by.tagName("td")).get(0).getText().trim(),
                tr -> tr.findElements(by.tagName("td")).stream()
                    .skip(1)
                    .map(WebElement::getText)
                    .map(String::trim)
                    .map(Integer::parseInt)
                    .collect(Collectors.toList())
            ));
    }

    private List<String> getTableFooter(final WebElement table) {
        return table.findElement(by.tagName("tfoot"))
            .findElements(by.tagName("th")).stream()
            .skip(1)
            .map(WebElement::getText)
            .map(String::trim)
            .collect(Collectors.toList());
    }
}
