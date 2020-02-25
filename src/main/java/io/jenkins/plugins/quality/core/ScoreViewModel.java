package io.jenkins.plugins.quality.core;

import hudson.model.ModelObject;
import hudson.model.Run;

/**
 * Server side model that provides the data for the details view of the score.
 * The layout of the associated view is defined in the corresponding jelly view 'index.jelly'.
 *
 * @author Eva-Maria Zeintl
 */
public class ScoreViewModel implements ModelObject {
    private final Run<?, ?> owner;
    private final Score score;
    /**
     * Creates a new instance of {@link ScoreViewModel}.
     *
     * @param owner the build as owner of this view
     * @param score the scores to show in the view
     */
    ScoreViewModel(final Run<?, ?> owner, final Score score) {
        super();
        this.owner = owner;
        this.score = score;
    }

    public Run<?, ?> getOwner() {
        return owner;
    }

    @Override
    public String getDisplayName() {
        return "Code Quality Score";
    }

    public Score getScore() {
        return score;
    }

}
