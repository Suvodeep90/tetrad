package edu.cmu.tetrad.algcomparison.algorithm.mixed;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.annotation.AlgType;
import edu.cmu.tetrad.annotation.AlgorithmDescription;
import edu.cmu.tetrad.annotation.OracleType;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.DataUtils;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.csb.mgm.MGM;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jdramsey
 */
@AlgorithmDescription(
        name = "MGM",
        algType = AlgType.produce_undirected_graphs,
        oracleType = OracleType.None
)
public class Mgm implements Algorithm {

    static final long serialVersionUID = 23L;

    public Mgm() {
    }

    @Override
    public Graph search(DataModel ds, Parameters parameters) {
        DataSet _ds = DataUtils.getMixedDataSet(ds);

        double mgmParam1 = parameters.getDouble("mgmParam1");
        double mgmParam2 = parameters.getDouble("mgmParam2");
        double mgmParam3 = parameters.getDouble("mgmParam3");

        double[] lambda = {
            mgmParam1,
            mgmParam2,
            mgmParam3
        };

        MGM m = new MGM(_ds, lambda);

        return m.search();
    }

    // Need to marry the parents on this.
    @Override
    public Graph getComparisonGraph(Graph graph) {
        return GraphUtils.undirectedGraph(graph);
    }

    @Override
    public String getDescription() {
        return "Returns the output of the MGM (Mixed Graphical Model) algorithm (a Markov random field)";
    }

    @Override
    public DataType getDataType() {
        return DataType.Mixed;
    }

    @Override
    public List<String> getParameters() {
        List<String> params = new ArrayList<>();
        params.add("mgmParam1");
        params.add("mgmParam2");
        params.add("mgmParam3");
        return params;
    }
}
