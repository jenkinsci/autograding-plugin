package io.jenkins.plugins.grading;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.util.SerializableTest;

import static io.jenkins.plugins.grading.assertions.Assertions.*;

/**
 * Tests serialization of {@link AggregatedScore} instances.
 *
 * @author Ullrich Hafner
 */
class AggregatedScoreXmlStreamTest extends SerializableTest<AggregatedScore> {
    @Override
    protected AggregatedScore createSerializable() {
        return read("auto-grading.xml");
    }

    @Test
    void shouldReadVersion1Serialization() {
        AggregatedScore score = createSerializable();

        verifyStream(score);
    }

    @Test
    void shouldReturnDefaultForBrokenFile() {
        AggregatedScore score = read("checkstyle.xml");

        assertThat(score).hasAnalysisAchieved(0).hasTestAchieved(0).hasCoverageAchieved(0).hasPitAchieved(0);
    }

    @Test
    void shouldReadAndWriteScores() throws IOException {
        AggregatedScoreXmlStream reader = new AggregatedScoreXmlStream();

        AggregatedScore restored = createSerializable();
        Path saved = createTempFile();
        reader.write(saved, restored);

        List<String> content = Files.readAllLines(saved);
        assertThat(content).doesNotContain(AnalysisScore.class.getName());

        AggregatedScore written = reader.read(saved);
        verifyStream(written);
    }

    private AggregatedScore read(final String fileName) {
        return new AggregatedScoreXmlStream().read(getResourceAsFile(fileName));
    }

    private void verifyStream(final AggregatedScore score) {
        assertThat(score).hasAnalysisAchieved(79);
        assertThat(score.getAnalysisScores()).hasSize(8);

        assertThat(score).hasTestAchieved(60);
        assertThat(score.getTestScores()).hasSize(1);

        assertThat(score).hasCoverageAchieved(78);
        assertThat(score.getCoverageScores()).hasSize(2);

        assertThat(score).hasPitAchieved(73);
        assertThat(score.getPitScores()).hasSize(1);
    }
}
