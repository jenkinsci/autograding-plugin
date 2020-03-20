package io.jenkins.plugins.grading;

import hudson.util.XStream2;
import io.jenkins.plugins.util.AbstractXmlStream;

/**
 * Reads {@link Score} from an XML file.
 *
 * @author Eva-Maria Zeintl
 */
public class ScoreXmlStream extends AbstractXmlStream<Score> {
    /**
     * creates a new {@link ScoreXmlStream}.
     */
    public ScoreXmlStream() {
        super(Score.class);
    }

    @Override
    protected Score createDefaultValue() {
        return new Score();
    }

    @Override
    protected void configureXStream(final XStream2 xStream) {
        xStream.alias("scores", Score.class);
    }

}
