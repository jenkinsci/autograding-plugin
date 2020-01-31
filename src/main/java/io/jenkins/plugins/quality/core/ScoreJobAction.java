package io.jenkins.plugins.quality.core;


import hudson.model.Job;
import io.jenkins.plugins.util.JobAction;
import jline.internal.Nullable;

/**
 * This job action displays a link on the side panel of a job that refers to the last build that contains
 * Code Quality Score results (i.e. a {@link ScoreBuildAction} with a {@link Score} instance)
 *
 * @author Eva-Maria Zeintl
 */

public class ScoreJobAction extends JobAction {
    static final String SMALL_ICON = "/plugins/quality/icons/quality.png";
    static final String CODEQUALITY_ID = "Code Quality";

    /**
     * Creates a new instance of {@link ScoreJobAction}.
     *
     * @param owner the job that owns this action
     */
    public ScoreJobAction(final Job<?, ?> owner) {
        super(owner, ScoreBuildAction.class);
    }

    @Override
    public String getDisplayName() {
        return "qualityEvaluator";
    }

    /**
     * Returns the icon URL for the side-panel in the job screen. If there is no valid result yet, then {@code null} is
     * returned.
     *
     * @return the icon URL for the side-panel in the job screen
     */
    @Override
    @Nullable
    public String getIconFileName() {
        return SMALL_ICON;
    }

    @Override
    public String getUrlName() {
        return CODEQUALITY_ID;
    }


}
