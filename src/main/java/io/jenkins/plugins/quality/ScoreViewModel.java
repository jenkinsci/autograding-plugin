package io.jenkins.plugins.quality.core;

import hudson.model.ModelObject;
import hudson.model.Run;

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
        return "HI";
    }

}
