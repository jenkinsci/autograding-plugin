package io.jenkins.plugins.quality.core;

import hudson.FilePath;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;

class ConfigXmlStreamTest {

    @Test
    void asserThatConfigurationsAreCorrect() {
        ConfigXmlStream configReader = new ConfigXmlStream();
        //Configuration configs = configReader.read(Paths.get("\\Config.xml"));

        /*Assertions.assertThat(configs).isEqualTo(new Configuration(25, "default", true, -4,
                -3,-2, -1, 25, "PIT", true, -2,
                1, 25, "COCO",true, 1,-2, 25, "JUNIT", true,
                -1, -2, 1));*/
    }

}