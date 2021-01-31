package io.jenkins.plugins.grading;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;

import edu.hm.hafner.analysis.Issue;
import edu.hm.hafner.analysis.Report;
import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.GradingReport;

import hudson.model.Run;
import hudson.model.TaskListener;

import io.jenkins.plugins.checks.api.ChecksAnnotation;
import io.jenkins.plugins.checks.api.ChecksAnnotation.ChecksAnnotationBuilder;
import io.jenkins.plugins.checks.api.ChecksAnnotation.ChecksAnnotationLevel;
import io.jenkins.plugins.checks.api.ChecksConclusion;
import io.jenkins.plugins.checks.api.ChecksDetails;
import io.jenkins.plugins.checks.api.ChecksDetails.ChecksDetailsBuilder;
import io.jenkins.plugins.checks.api.ChecksOutput.ChecksOutputBuilder;
import io.jenkins.plugins.checks.api.ChecksPublisher;
import io.jenkins.plugins.checks.api.ChecksPublisherFactory;
import io.jenkins.plugins.checks.api.ChecksStatus;
import io.jenkins.plugins.util.JenkinsFacade;

/**
 * Publishes the autograding results to SCM providers.
 *
 * @author Ullrich Hafner
 */
class AutoGradingChecksPublisher {
    void publishChecks(final Run<?, ?> run, final TaskListener listener,
            final AggregatedScore score, final List<Report> warnings) {
        ChecksPublisher publisher = ChecksPublisherFactory.fromRun(run, listener);

        GradingReport report = new GradingReport();

        ChecksDetails details = new ChecksDetailsBuilder()
                .withName("Autograding")
                .withStatus(ChecksStatus.COMPLETED)
                .withConclusion(ChecksConclusion.SUCCESS)
                .withOutput(new ChecksOutputBuilder()
                        .withTitle(report.getHeader())
                        .withSummary(report.getSummary(score))
                        .withText(report.getDetails(score, Collections.emptyList())) // FIXME: we need to show the failures here as well
                        .withAnnotations(createAnnotations(warnings))
                        .build())
                .withDetailsURL(getAbsoluteUrl(run))
                .build();

        publisher.publish(details);
    }

    @SuppressWarnings("deprecation")
    private String getAbsoluteUrl(final Run<?, ?> run) {
        return new JenkinsFacade().getAbsoluteUrl(run.getUrl(), AutoGradingJobAction.ID);
    }

    private List<ChecksAnnotation> createAnnotations(final List<Report> reports) {
        return reports.stream().flatMap(Report::stream).map(this::createAnnotation).collect(Collectors.toList());
    }

    private ChecksAnnotation createAnnotation(final Issue warning) {
        ChecksAnnotationBuilder builder = new ChecksAnnotationBuilder()
                .withPath(warning.getFileName())
                .withTitle(warning.getType())
                .withAnnotationLevel(ChecksAnnotationLevel.WARNING)
                .withMessage(warning.getSeverity() + ":\n" + parseHtml(warning.getMessage()))
                .withStartLine(warning.getLineStart())
                .withEndLine(warning.getLineEnd())
                .withRawDetails(warning.getDescription());

        if (warning.getLineStart() == warning.getLineEnd()) {
            builder.withStartColumn(warning.getColumnStart())
                    .withEndColumn(warning.getColumnEnd());
        }
        return builder.build();
    }

    private String parseHtml(final String html) {
        Set<String> contents = new HashSet<>();
        parseHtml(Jsoup.parse(html), contents);
        return String.join("\n", contents);
    }

    private void parseHtml(final Element html, final Set<String> contents) {
        for (TextNode node : html.textNodes()) {
            contents.add(node.text().trim());
        }

        for (Element child : html.children()) {
            if (child.hasAttr("href")) {
                contents.add(child.text().trim() + ":" + child.attr("href").trim());
            }
            else {
                parseHtml(child, contents);
            }
        }
    }
}
