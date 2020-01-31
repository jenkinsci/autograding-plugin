package io.jenkins.plugins.quality.core;

import edu.hm.hafner.util.VisibleForTesting;
import hudson.model.Run;
import io.jenkins.plugins.util.BuildAction;
import org.kohsuke.stapler.StaplerProxy;

/**
 * @author Eva-Maria Zeintl
 */

public class ScoreBuildAction extends BuildAction<Score> implements StaplerProxy {
    private static final long serialVersionUID = -1165416468486465651L;

    /**
     * Creates a new instance of {@link ScoreBuildAction}.
     *
     * @param owner the associated build that created the scores
     */
    public ScoreBuildAction(final Run<?, ?> owner, final Score score) {
        this(owner, score, true);
    }

   @VisibleForTesting
    ScoreBuildAction(final Run<?, ?> owner, final Score score, final boolean canSerialize) {
        super(owner, score, canSerialize);
    }

    @Override
    protected ScoreXmlStream createXmlStream() {
        return new ScoreXmlStream();
    }

    @Override
    protected ScoreJobAction createProjectAction() {
        return new ScoreJobAction(getOwner().getParent());
    }

    @Override
    protected String getBuildResultBaseName() {
        return "code-quality-score.xml";
    }

    @Override
    public String getIconFileName() {
        return ScoreJobAction.SMALL_ICON;
    }

    @Override
    public String getDisplayName() {
        return "qualityEvaluator";
    }

    /**
     * returns the detail view for issues for all Stapler requests.
     *
     * @return the detail view for issues
     */
    @Override
    public Object getTarget() {
        return new ScoreViewModel(getOwner(), (Score) getResult());
    }

    @Override
    public String getUrlName() {
        return ScoreJobAction.CODEQUALITY_ID;
    }

    public String getTotalScore() {
        return getResult().getScore() + "/"+getResult().getMaxScore();
    }
}
