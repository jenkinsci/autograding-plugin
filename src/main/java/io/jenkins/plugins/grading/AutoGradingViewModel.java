package io.jenkins.plugins.grading;

import hudson.model.ModelObject;
import hudson.model.Run;

/**
 * Server side model that provides the data for the details view of the autograding results. The layout of the
 * associated view is defined in the corresponding jelly view 'index.jelly'.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGradingViewModel implements ModelObject {
    private final Run<?, ?> owner;
    private final AggregatedScore score;

    /**
     * Creates a new instance of {@link AutoGradingViewModel}.
     *
     * @param owner
     *         the build as owner of this view
     * @param score
     *         the scores to show in the view
     */
    AutoGradingViewModel(final Run<?, ?> owner, final AggregatedScore score) {
        super();

        this.owner = owner;
        this.score = score;
    }

    public Run<?, ?> getOwner() {
        return owner;
    }

    @Override
    public String getDisplayName() {
        return Messages.Action_Name();
    }

    public AggregatedScore getScore() {
        return score;
    }
}
