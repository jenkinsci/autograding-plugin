package io.jenkins.plugins.quality.core;

import hudson.util.XStream2;
import io.jenkins.plugins.util.AbstractXmlStream;

//does save buildAction in xml
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
