package io.jenkins.plugins.quality.core;

import hudson.model.ModelObject;
import hudson.model.Run;

import java.util.ArrayList;
import java.util.List;

import static groovy.xml.Entity.divide;

/**
 * Server side model that provides the data for the details view of the score.
 * The layout of the associated view is defined in the corresponding jelly view 'index.jelly'.
 *
 * @author Eva-Maria Zeintl
 */
public class ScoreViewModel implements ModelObject {
    private final Run<?, ?> owner;
    private final Score score;
    private List<BaseResults> defaults;
    private List<BaseResults> testresults;
    private List<BaseResults> pits;
    private List<BaseResults> cocos;

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
        divide();
    }

    private void divide() {
        List<BaseResults> valueList = new ArrayList(score.getBases().values());
        for (int i = 0; i < valueList.size(); i++) {
            if (valueList.get(i).getId().equals("pitmutation")) {
                pits.add(score.getBases().get(valueList.get(i).getId()));
            }
            else if (valueList.get(i).getId().equals("Testabdeckung")) {
                cocos.add(score.getBases().get(valueList.get(i).getId()));
            }
            else if (valueList.get(i).getId().equals("Testergebnis")) {
                testresults.add(score.getBases().get(valueList.get(i).getId()));
            }
            else {
                defaults.add(score.getBases().get(valueList.get(i).getId()));
            }
        }
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

    public List<BaseResults> getDefaults() {
        return defaults;
    }

    public List<BaseResults> getTestresults() {
        return testresults;
    }

    public List<BaseResults> getPits() {
        return pits;
    }

    public List<BaseResults> getCocos() {
        return cocos;
    }
}
