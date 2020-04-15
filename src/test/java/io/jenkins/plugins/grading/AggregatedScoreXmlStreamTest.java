package io.jenkins.plugins.grading;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.util.SerializableTest;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests serialization of {@link AggregatedScore} instances.
 *
 * @author Ullrich Hafner
 */
public class AggregatedScoreXmlStreamTest extends SerializableTest<AggregatedScore> {
    @Override
    protected AggregatedScore createSerializable() {
        AggregatedScoreXmlStream xmlStream = new AggregatedScoreXmlStream();

        return xmlStream.read(getResourceAsFile("auto-grading.xml"));
    }

    @Test
    void shouldReadVersion1Serialization() {
        AggregatedScore score = createSerializable();

        assertThat(score).hasAnalysisAchieved(83);
        assertThat(score).hasTestAchieved(53);
        assertThat(score).hasCoverageAchieved(50);
        assertThat(score).hasPitAchieved(56);
    }
}
