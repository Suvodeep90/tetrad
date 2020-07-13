package edu.cmu.tetrad.algcomparison.statistic;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.graph.Graph;

import static java.lang.Math.tanh;

/**
 * The adjacency precision. The true positives are the number of adjacencies in both
 * the true and estimated graphs.
 *
 * @author jdramsey
 */
public class NumNodesEst implements Statistic {
    static final long serialVersionUID = 23L;

    @Override
    public String getAbbreviation() {
        return "NodE";
    }

    @Override
    public String getDescription() {
        return "Number of nodes in the estimated graph";
    }

    @Override
    public double getValue(Graph trueGraph, Graph estGraph, DataModel dataModel) {
        return estGraph.getNumNodes();
    }

    @Override
    public double getNormValue(double value) {
        return tanh(value);
    }
}
