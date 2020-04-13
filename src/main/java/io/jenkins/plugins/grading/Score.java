package io.jenkins.plugins.grading;

import edu.hm.hafner.util.Ensure;

/**
 * A score that has been obtained from a specific tool.
 *
 * @author Ullrich Hafner
 */
public class Score {
    private final String id;
    private final String name;
    private int totalImpact;

    Score(final String id, final String name) {
        Ensure.that(id).isNotEmpty();
        Ensure.that(name).isNotEmpty();

        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalImpact() {
        return totalImpact;
    }

    final void setTotalImpact(final int totalImpact) {
        this.totalImpact = totalImpact;
    }
}
