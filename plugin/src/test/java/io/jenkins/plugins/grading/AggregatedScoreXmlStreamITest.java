package io.jenkins.plugins.grading;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.grading.AggregatedScore;
import edu.hm.hafner.grading.AnalysisScore;
import edu.hm.hafner.util.ResourceTest;

import static edu.hm.hafner.grading.assertions.Assertions.*;

/**
 * Tests serialization of {@link AggregatedScore} instances.
 *
 * @author Ullrich Hafner
 */
class AggregatedScoreXmlStreamITest extends ResourceTest {
    private AggregatedScore createScore() {
        var serialization = readAllBytes("aggregated-score.ser");

        try (ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(serialization))) {
            return (AggregatedScore)inputStream.readObject();
        }
        catch (IOException | ClassNotFoundException e) {
            throw new AssertionError("Can't resolve instance from byte array", e);
        }
    }

    @Test
    void shouldReturnDefaultForBrokenFile() {
        AggregatedScore score = read("checkstyle.xml");

        assertThat(score)
                .hasAnalysisAchievedScore(0)
                .hasTestAchievedScore(0)
                .hasCoverageAchievedScore(0);
    }

    @Test
    void shouldReadAndWriteScores() throws IOException {
        AggregatedScoreXmlStream reader = new AggregatedScoreXmlStream();

        AggregatedScore restored = createScore();
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
        assertThat(score).hasAnalysisAchievedScore(30);
        assertThat(score.getAnalysisScores()).hasSize(2);

        assertThat(score).hasTestAchievedScore(77);
        assertThat(score.getTestScores()).hasSize(1);

        assertThat(score).hasCodeCoverageAchievedScore(40);
        assertThat(score.getCodeCoverageScores()).hasSize(1);

        assertThat(score).hasMutationCoverageAchievedScore(20);
        assertThat(score.getMutationCoverageScores()).hasSize(1);
    }
}
