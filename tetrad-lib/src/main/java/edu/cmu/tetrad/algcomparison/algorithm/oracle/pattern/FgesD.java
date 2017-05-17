package edu.cmu.tetrad.algcomparison.algorithm.oracle.pattern;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.score.ScoreWrapper;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.algcomparison.utils.TakesInitialGraph;
import edu.cmu.tetrad.data.*;
import edu.cmu.tetrad.graph.EdgeListGraph;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.SearchGraphUtils;
import edu.cmu.tetrad.search.SemBicScore;
import edu.cmu.tetrad.search.SemBicScoreD;
import edu.cmu.tetrad.util.Parameters;

import java.io.PrintStream;
import java.util.List;

/**
 * FGES (the heuristic version).
 *
 * @author jdramsey
 */
public class FgesD implements Algorithm, TakesInitialGraph, HasKnowledge {

    static final long serialVersionUID = 23L;
    private boolean compareToTrue = false;
    private ScoreWrapper score;
    private Algorithm initialGraph = null;
    private IKnowledge knowledge = new Knowledge2();

    public FgesD(ScoreWrapper score) {
        this.score = score;
        this.compareToTrue = false;
    }

    public FgesD(ScoreWrapper score, boolean compareToTrueGraph) {
        this.score = score;
        this.compareToTrue = compareToTrueGraph;
    }


    public FgesD(ScoreWrapper score, Algorithm initialGraph) {
        this.score = score;
        this.initialGraph = initialGraph;
    }

    @Override
    public Graph search(DataModel dataSet, Parameters parameters) {
        Graph initial = null;

        if (initialGraph != null) {
            initial = initialGraph.search(dataSet, parameters);
        }

        edu.cmu.tetrad.search.FgesD search
                = new edu.cmu.tetrad.search.FgesD(
                        new SemBicScoreD(new CovarianceMatrix((DataSet) dataSet)));
        search.setFaithfulnessAssumed(parameters.getBoolean("faithfulnessAssumed"));
        search.setKnowledge(knowledge);
        search.setVerbose(parameters.getBoolean("verbose"));
        search.setMaxDegree(parameters.getInt("maxDegree"));
//        search.setSymmetricFirstStep(parameters.getBoolean("symmetricFirstStep"));

        Object obj = parameters.get("printStedu.cmream");
        if (obj instanceof PrintStream) {
            search.setOut((PrintStream) obj);
        }

        if (initial != null) {
            search.setInitialGraph(initial);
        }

        return search.search();
    }

    @Override
    public Graph getComparisonGraph(Graph graph) {
        if (compareToTrue) {
            return new EdgeListGraph(graph);
        } else {
            return SearchGraphUtils.patternForDag(new EdgeListGraph(graph));
        }
    }

    @Override
    public String getDescription() {
        return "FGES (Fast Greedy Equivalence Search) using " + score.getDescription();
    }

    @Override
    public DataType getDataType() {
        return score.getDataType();
    }

    @Override
    public List<String> getParameters() {
        List<String> parameters = score.getParameters();
        parameters.add("symmetricFirstStep");
        parameters.add("faithfulnessAssumed");
        parameters.add("maxDegree");
        parameters.add("verbose");
        return parameters;
    }

    @Override
    public IKnowledge getKnowledge() {
        return knowledge;
    }

    @Override
    public void setKnowledge(IKnowledge knowledge) {
        this.knowledge = knowledge;
    }

    public void setCompareToTrue(boolean compareToTrue) {
        this.compareToTrue = compareToTrue;
    }
}
