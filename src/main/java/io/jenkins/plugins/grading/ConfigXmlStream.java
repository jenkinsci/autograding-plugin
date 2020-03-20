package io.jenkins.plugins.grading;

import hudson.util.XStream2;
import io.jenkins.plugins.util.AbstractXmlStream;


/**
 * Reads {@link Configuration} from an XML file.
 *
 * @author Eva-Maria Zeintl
 */

public class ConfigXmlStream extends AbstractXmlStream<Configuration> {
    /**
     * creates a new {@link ConfigXmlStream}.
     */
    public ConfigXmlStream() {
        super(Configuration.class);
    }

    @Override
    protected Configuration createDefaultValue() {
        return new Configuration();
    }

    @Override
    protected void configureXStream(final XStream2 xStream) {

        xStream.alias("configs", Configuration.class);
    }

}
