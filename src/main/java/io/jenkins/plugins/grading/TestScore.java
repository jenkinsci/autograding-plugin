package io.jenkins.plugins.grading;

import hudson.tasks.junit.TestResultAction;

/**
 * Computes the {@link Score} impact of test results. These results are obtained by inspecting a {@link
 * TestResultAction} instance of the JUnit plugin.
 *
 * @author Eva-Maria Zeintl
 */
@SuppressWarnings("PMD.DataClass")
public class TestScore {
    private final String id;

    private final int totalImpact;

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
     */
    public TestScore(final TestConfiguration configuration, final TestResultAction action) {
        id = action.getDisplayName();

        failedSize = action.getFailCount();
        skippedSize = action.getSkipCount();
        totalSize = action.getTotalCount();
        passedSize = totalSize - failedSize - skippedSize;

        totalImpact = computeImpact(configuration);
    }

    private int computeImpact(final TestConfiguration configs) {
        int change = 0;
        change = change + configs.getPassedImpact() * passedSize;
        change = change + configs.getFailureImpact() * failedSize;
        change = change + configs.getSkippedImpact() * skippedSize;

        return change;
    }

    public String getId() {
        return id;
    }

    public int getTotalImpact() {
        return totalImpact;
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
}
