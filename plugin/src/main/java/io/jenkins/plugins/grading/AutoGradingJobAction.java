package io.jenkins.plugins.grading;

import edu.hm.hafner.grading.AggregatedScore;

import hudson.model.Job;

import io.jenkins.plugins.util.JobAction;

/**
 * A job action displays a link on the side panel of a job that refers to the last build that contains autograding
 * results (i.e., a {@link AutoGradingBuildAction} with a {@link AggregatedScore} instance). This action also is responsible to
 * render the historical trend via its associated 'floatingBox.jelly' view.
 *
 * @author Eva-Maria Zeintl
 */
public class AutoGradingJobAction extends JobAction<AutoGradingBuildAction> {
    static final String ICON = "symbol-solid/graduation-cap plugin-font-awesome-api";
    static final String ID = "autograding";

    /**
     * Creates a new instance of {@link AutoGradingJobAction}.
     *
     * @param owner
     *         the job that owns this action
     */
    public AutoGradingJobAction(final Job<?, ?> owner) {
        super(owner, AutoGradingBuildAction.class);
    }

    @Override
    public String getDisplayName() {
        return Messages.Action_Name();
    }

    @Override
    public String getIconFileName() {
        return ICON;
    }

    @Override
    public String getUrlName() {
        return ID;
    }
}
