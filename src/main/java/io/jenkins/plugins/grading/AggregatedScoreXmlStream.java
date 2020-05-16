package io.jenkins.plugins.grading;

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.grading.CoverageScore;
import edu.hm.hafner.grading.PitScore;
import edu.hm.hafner.grading.TestScore;

import hudson.util.XStream2;

import io.jenkins.plugins.util.AbstractXmlStream;

/**
 * Reads {@link AggregatedScore} from an XML file.
 *
 * @author Eva-Maria Zeintl
 */
public class AggregatedScoreXmlStream extends AbstractXmlStream<AggregatedScore> {
    /**
     * creates a new {@link AggregatedScoreXmlStream}.
     */
    public AggregatedScoreXmlStream() {
        super(AggregatedScore.class);
    }

    @Override
    protected AggregatedScore createDefaultValue() {
        return new AggregatedScore();
    }

    @Override
    protected void configureXStream(final XStream2 xStream) {
        xStream.alias("scores", AggregatedScore.class);
        xStream.alias("analysisScore", AnalysisScore.class);
        xStream.alias("testScore", TestScore.class);
        xStream.alias("coverageScore", CoverageScore.class);
        xStream.alias("pitScore", PitScore.class);
    }
}
