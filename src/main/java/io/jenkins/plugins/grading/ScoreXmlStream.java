package io.jenkins.plugins.grading;

import hudson.util.XStream2;

import io.jenkins.plugins.util.AbstractXmlStream;

/**
 * Reads {@link AggregatedScore} from an XML file.
 *
 * @author Eva-Maria Zeintl
 */
public class ScoreXmlStream extends AbstractXmlStream<AggregatedScore> {
    /**
     * creates a new {@link ScoreXmlStream}.
     */
    public ScoreXmlStream() {
        super(AggregatedScore.class);
    }

    @Override
    protected AggregatedScore createDefaultValue() {
        return new AggregatedScore();
    }

    @Override
    protected void configureXStream(final XStream2 xStream) {
        xStream.alias("scores", AggregatedScore.class);
    }

}
