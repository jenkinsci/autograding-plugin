package io.jenkins.plugins.quality.core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.*;

import com.google.common.collect.ImmutableList;
import com.google.inject.internal.cglib.core.$Constants;
import edu.hm.hafner.analysis.Report;
import edu.umd.cs.findbugs.annotations.NonNull;

import org.kohsuke.stapler.DataBoundConstructor;
import org.jenkinsci.Symbol;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import io.jenkins.plugins.analysis.core.model.ResultAction;
import sun.security.ec.point.ProjectivePoint;

public class QualityEvaluator extends Recorder implements SimpleBuildStep {
    @DataBoundConstructor
    /**
     * Creates a new instance of {@link  QualityEvaluator}.
     */

    public QualityEvaluator() {
        super();

        // empty constructor required for Stapler
    }

    @Override
    public void perform(@Nonnull final Run<?, ?> run, @Nonnull final FilePath workspace,
            @Nonnull final Launcher launcher,
            @Nonnull final TaskListener listener) throws InterruptedException, IOException {

        listener.getLogger().println("[CodeQuality] Starting extraction of previous performed checks");
        List<ResultAction> actions = run.getActions(ResultAction.class);
        //also get action von junit
        List<Configuration> configs = new ArrayList<>();
        List<Integer> maxScore = new ArrayList<>();

        try {
            listener.getLogger().println("[CodeQuality] Try to read Configurations.");
            readFile(configs, maxScore);
            listener.getLogger().println("[CodeQuality] -> found the following Configurations");
            // if config vorhanden dann loggen , außerdem entweder max score von dem abgezogen wird oder es wird bei null begonnen zum hochzählen von pkt.
            listener.getLogger().println("[CodeQuality] The max Score to achieve is: " + maxScore.get(0) );
            listener.getLogger().println(configs.toString());

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        listener.getLogger().println("[CodeQuality] -> found "+actions.size()+" checks");
        listener.getLogger().println("[CodeQuality] Code Quality Results are: ");

        final int finalScore = computeScore(actions, maxScore.get(0), configs, listener);

        for(ResultAction action : actions){
            listener.getLogger().println("[CodeQuality] For "+action.getResult().getId()+ " the following issues where found:");
            listener.getLogger().println("[CodeQuality] Number of Errors: "+action.getResult().getTotalErrorsSize());
            listener.getLogger().println("[CodeQuality] Number of High Issues: "+action.getResult().getTotalHighPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Normal Issues: "+action.getResult().getTotalNormalPrioritySize());
            listener.getLogger().println("[CodeQuality] Number of Low Issues: "+action.getResult().getTotalLowPrioritySize());
        }

        listener.getLogger().println("[CodeQuality] Total score achieved: "+ finalScore +" Points");
        //listener.getLogger().println(actions.stream().map(ResultAction::getId).collect(Collectors.joining()));
    }

    private void readFile(List<Configuration> configs, List<Integer>  maxScore) throws FileNotFoundException, XMLStreamException {
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader eventReader;
        eventReader = factory.createXMLEventReader(new FileInputStream("C:\\Users\\schattenpc\\Documents\\Studium\\sem7\\Bachelor\\code-quality-plugin\\Config.xml"));

        Configuration some = new Configuration();

        while (eventReader.hasNext()){
            XMLEvent event = eventReader.nextEvent();
            if(event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                switch (startElement.getName().getLocalPart()) {
                    case "score":
                        event = eventReader.nextEvent();
                        maxScore.add(Integer.parseInt(event.asCharacters().getData()));
                        break;
                    case "checks":
                        some = new Configuration();
                        break;
                    case "id":
                        event = eventReader.nextEvent();
                        some.setId(event.asCharacters().getData());
                        break;
                    case "toCheck":
                        event = eventReader.nextEvent();
                        some.setToCheck(Boolean.parseBoolean(event.asCharacters().getData()));
                        break;
                    case "kindOfGrading":
                        event = eventReader.nextEvent();
                        some.setKindOfGrading(event.asCharacters().getData());
                        break;
                    case "weightError":
                        event = eventReader.nextEvent();
                        some.setWeightError(Integer.parseInt(event.asCharacters().getData()));
                        break;
                    case "weightHigh":
                        event = eventReader.nextEvent();
                        some.setWeightHigh(Integer.parseInt(event.asCharacters().getData()));
                        break;
                    case "weightNormal":
                        event = eventReader.nextEvent();
                        some.setWeightNormal(Integer.parseInt(event.asCharacters().getData()));
                        break;
                    case "weightLow":
                        event = eventReader.nextEvent();
                        some.setWeightLow(Integer.parseInt(event.asCharacters().getData()));
                        break;
                    default:
                        break;
                }
            }
            if(event.isEndElement()) {
                EndElement endElement = event.asEndElement();
                if (endElement.getName().getLocalPart().equals("checks")) {
                    configs.add(some);
                }
            }

        }
    }

    private int computeScore(List<ResultAction> actions, int maxScore, List<Configuration> configs,  @Nonnull final TaskListener listener) {
        int score = maxScore;
        int error = 0;
        int high = 0;
        int normal = 0;
        int low = 0;

        for(Configuration conf : configs) {
            if(conf.getID() != null && conf.getID().equals("default")) {
                error = conf.getWeightError();
                high = conf.getWeightHigh();
                normal = conf.getWeightNormal();
                low = conf.getWeightLow();
            } else if (conf.getID() != null && conf.getID().equals("pit")) {

            } else if (conf.getID() != null && conf.getID().equals("junit")) {

            }
        }

        for(ResultAction action : actions){

            switch (action.getId()) {
                case "pit":
                    break;
                case "junit":
                    break;
                default:
                    if(action.getResult().getTotalErrorsSize() > 0) {
                        score = score + (action.getResult().getTotalErrorsSize() * error);
                    }
                    if(action.getResult().getTotalHighPrioritySize() > 0) {
                        score = score + (action.getResult().getTotalHighPrioritySize() * high);
                    }
                    if(action.getResult().getTotalNormalPrioritySize() > 0) {
                        score = score + (action.getResult().getTotalNormalPrioritySize() * normal);
                    }
                    if(action.getResult().getTotalLowPrioritySize() > 0) {
                        score = score + (action.getResult().getTotalLowPrioritySize() * low);
                    }
            }
        }
        return score;
    }

    public static boolean tester() {
        return false;
    }

    /**
     * Descriptor for this step: defines the context and the UI elements.
     */
    @Extension(ordinal = -100_000)
    @Symbol("computeQuality")
    @SuppressWarnings("unused") // most methods are used by the corresponding jelly view
    public static class Descriptor extends BuildStepDescriptor<Publisher> {
        @NonNull
        @Override
        public String getDisplayName() {
            return "Compute code quality";
        }

        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> jobType) {
            return true;
        }
    }
}
