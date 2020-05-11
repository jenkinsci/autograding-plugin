package edu.hm.hafner.grading;

import java.util.Objects;

import edu.hm.hafner.util.Generated;

import hudson.tasks.junit.TestResultAction;

/**
 * Computes the {@link AggregatedScore} impact of test results. These results are obtained by inspecting a {@link
 * TestResultAction} instance of the JUnit plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class TestScore extends Score {
    private static final long serialVersionUID = 1L;

    static final String ID = "tests";

    private final int passedSize;
    private final int totalSize;
    private final int failedSize;
    private final int skippedSize;

    /**
     * Creates a new {@link TestScore} instance.
     *
     * @param configuration
     *         the grading configuration
     * @param action
     *         the action that contains the test results
     * @param failCount
     * @param skipCount
     * @param totalCount
     */
    public TestScore(final TestConfiguration configuration, final TestResultAction action, final int failCount,
            final int skipCount, final int totalCount) {
        super(ID, action.getDisplayName());

        failedSize = failCount;
        skippedSize = skipCount;
        totalSize = totalCount;
        passedSize = totalSize - failedSize - skippedSize;

        setTotalImpact(computeImpact(configuration));
    }

    private int computeImpact(final TestConfiguration configs) {
        int change = 0;
        change = change + configs.getPassedImpact() * passedSize;
        change = change + configs.getFailureImpact() * failedSize;
        change = change + configs.getSkippedImpact() * skippedSize;

        return change;
    }

    public int getPassedSize() {
        return passedSize;
    }

    public int getTotalSize() {
        return totalSize;
    }

    public int getFailedSize() {
        return failedSize;
    }

    public int getSkippedSize() {
        return skippedSize;
    }

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        TestScore testScore = (TestScore) o;
        return passedSize == testScore.passedSize
                && totalSize == testScore.totalSize
                && failedSize == testScore.failedSize
                && skippedSize == testScore.skippedSize;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), passedSize, totalSize, failedSize, skippedSize);
    }
}
