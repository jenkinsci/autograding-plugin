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
public final class TestScore extends Score {
    private static final long serialVersionUID = 1L;

    static final String ID = "tests";

    private final int passedSize;
    private final int failedSize;
    private final int skippedSize;

    /**
     * Creates a new {@link TestScore} instance.
     *
     * @param configuration
     *         the grading configuration
     * @param totalCount
     *         total number of tests
     * @param failCount
     *         number of failed tests
     * @param skipCount
     *         number of skipped tests
     */
    public TestScore(final TestConfiguration configuration,
            final int totalCount, final int failCount, final int skipCount) {
        this("Test results", configuration, totalCount, failCount, skipCount);
    }

    /**
     * Creates a new {@link TestScore} instance.
     *
     * @param displayName
     *         human readable name of the tests results
     * @param configuration
     *         the grading configuration
     * @param totalSize
     *         total number of tests
     * @param failedSize
     *         number of failed tests
     * @param skippedSize
     *         number of skipped tests
     */
    public TestScore(final String displayName, final TestConfiguration configuration,
            final int totalSize, final int failedSize, final int skippedSize) {
        super(ID, displayName);

        this.failedSize = failedSize;
        this.skippedSize = skippedSize;
        passedSize = totalSize - this.failedSize - this.skippedSize;

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
        return passedSize + failedSize + skippedSize;
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
                && failedSize == testScore.failedSize
                && skippedSize == testScore.skippedSize;
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(super.hashCode(), passedSize, failedSize, skippedSize);
    }
}
