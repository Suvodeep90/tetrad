package edu.cmu.tetrad.algcomparison.algorithm.external;

import edu.cmu.tetrad.algcomparison.algorithm.ExternalAlgorithm;
import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataReader;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.graph.*;
import edu.cmu.tetrad.search.SearchGraphUtils;
import edu.cmu.tetrad.util.Parameters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * An API to allow results from external algorithms to be included in a report through the algrorithm
 * comparison tool. This one is for matrix generated by PC in pcalg. See below. This script can generate
 * the files in R.
 * <p>
 * library("MASS");
 * library("pcalg");
 * <p>
 * path<-"/Users/user/tetrad/comparison-final";
 * simulation<-1;
 * <p>
 * subdir<-"pc.solve.confl.TRUE";
 * dir.create(paste(path, "/save/", simulation, "/", subdir, sep=""));
 * <p>
 * for (i in 1:10) {
 * data<-read.table(paste(path, "/save/", simulation, "/data/data.", i, ".txt", sep=""), header=TRUE)
 * n<-nrow(data)
 * C<-cor(data)
 * v<-names(data)
 * suffStat<-list(C = C, n=n)
 * pc.fit<-pc(suffStat=suffStat, indepTest=gaussCItest, alpha=0.001, labels=v,
 * solve.conf=TRUE)
 * A<-as(pc.fit, "amat")
 * name<-paste(path, "/save/", simulation, "/", subdir, "/graph.", i, ".txt", sep="")
 * print(name)
 * write.matrix(A, file=name, sep="\t")
 * }
 *
 * @author jdramsey
 */
public class ExternalAlgorithmBnlearnMmhc implements ExternalAlgorithm {
    static final long serialVersionUID = 23L;
    private final String extDir;
    private String shortDescription = null;
    private String path;
    private List<String> usedParameters = new ArrayList<>();
    private Simulation simulation;
    private int simIndex = -1;

    public ExternalAlgorithmBnlearnMmhc(String extDir) {
        this.extDir = extDir;
        this.shortDescription = new File(extDir).getName().replace("_", " ");
    }

    public ExternalAlgorithmBnlearnMmhc(String extDir, String shortDecription) {
        this.extDir = extDir;
        this.shortDescription = shortDecription;
    }

    @Override
    /**
     * Reads in the relevant graph from the file (see above) and returns it.
     */
    public Graph search(DataModel dataSet, Parameters parameters) {
        int index = -1;

        for (int i = 0; i < getNumDataModels(); i++) {
            if (dataSet == simulation.getDataModel(i)) {
                index = i + 1;
                break;
            }
        }

        if (index == -1) {
            throw new IllegalArgumentException("Not a dataset for this simulation.");
        }

        File file = new File(path, "/results/" + extDir + "/" + (simIndex + 1) + "/graph." + index + ".txt");

        System.out.println(file.getAbsolutePath());

        try {
            BufferedReader r = new BufferedReader(new FileReader(file));

            r.readLine(); // Skip the first line.
            String line;

            Graph graph = new EdgeListGraph();

            while ((line = r.readLine()) != null) {
                if (line.isEmpty()) continue;
                String[] tokens = line.split("\t");
                String name1 = tokens[0].replace(" ", "").replace("\"","");
                String name2 = tokens[1].replace(" ", "").replace("\"","");

                if (graph.getNode(name1) == null) {
                    graph.addNode(new GraphNode(name1));
                }

                if (graph.getNode(name2) == null) {
                    graph.addNode(new GraphNode(name2));
                }

                Node node1 = graph.getNode(name1);
                Node node2 = graph.getNode(name2);

                if (!graph.isAdjacentTo(node1, node2)) {
                    graph.addDirectedEdge(node1, node2);
                } else {
                    graph.removeEdge(node1, node2);
                    graph.addUndirectedEdge(node1, node2);
                }
            }

            GraphUtils.circleLayout(graph, 225, 200, 150);

            System.out.println(graph);

            return graph;
        } catch (IOException e) {
            throw new RuntimeException("Couldn't parse graph.");
        }
    }

    @Override
    /**
     * Returns the pattern of the supplied DAG.
     */
    public Graph getComparisonGraph(Graph graph) {
        return new EdgeListGraph(graph);
//        return SearchGraphUtils.patternForDag(new EdgeListGraph(graph));
    }

    public String getDescription() {
        if (shortDescription == null) {
            return "Load data from " + path + "/" + extDir;
        } else {
            return shortDescription;
        }
    }

    @Override
    public List<String> getParameters() {
        return usedParameters;
    }

    public int getNumDataModels() {
        return simulation.getNumDataModels();
    }

    @Override
    public DataType getDataType() {
        return DataType.Continuous;
    }

    public void setSimulation(Simulation simulation) {
        this.simulation = simulation;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSimIndex(int simIndex) {
        this.simIndex = simIndex;
    }

    @Override
    public Simulation getSimulation() {
        return simulation;
    }

    @Override
    public long getElapsedTime(String resultsPath, int index) {
        if (index == -1) {
            throw new IllegalArgumentException("Not a dataset for this simulation.");
        }

        File file = new File(path, "/elapsed/" + extDir + "/" + (simIndex + 1) + "/graph." + index + ".txt");

//        System.out.println(file.getAbsolutePath());

        try {
            BufferedReader r = new BufferedReader(new FileReader(file));
            String l = r.readLine(); // Skip the first line.
            return Long.parseLong(l);
        } catch (IOException e) {
            return -99;
        }
    }

}
