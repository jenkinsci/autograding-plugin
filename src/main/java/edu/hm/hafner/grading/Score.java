package edu.hm.hafner.grading;

import java.io.Serializable;
import java.util.Objects;

import edu.hm.hafner.util.Ensure;
import edu.hm.hafner.util.Generated;

/**
 * A score that has been obtained from a specific tool.
 *
 * @author Ullrich Hafner
 */
public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

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

    @Override @Generated
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Score score = (Score) o;
        return totalImpact == score.totalImpact && id.equals(score.id) && name.equals(score.name);
    }

    @Override @Generated
    public int hashCode() {
        return Objects.hash(id, name, totalImpact);
    }
}
