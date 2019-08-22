package edu.cmu.tetrad.algcomparison.statistic;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.Node;
import edu.pitt.dbmi.data.Dataset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The adjacency precision. The true positives are the number of adjacencies in both
 * the true and estimated graphs.
 *
 *
 *
 * @author jdramsey
 */
public class ClusterPrecision implements Statistic {
    static final long serialVersionUID = 23L;

    @Override
    public String getAbbreviation() {
        return "Precision";
    }

    @Override
    public String getDescription() {
        return "Maximum of true edges over all edges";
    }


    @Override
    public double getValue(Graph trueGraph, Graph estGraph, DataModel dataModel) {

        //Precision = tp/(tp+fp)

        if(estGraph.getNumEdges()>trueGraph.getNumEdges()){//MGMgraph

            List<List<Node>> trueClusters = GrabCluster(trueGraph);

            List<List<Node>> fakeClusters = new ArrayList<>();

            //System.out.println("Children graph: "+ trueGraph.subgraph(dataModel.getVariables()));

            double tp = 0;
            double fp = 0;


            //build fakeClusters to calculate fp
            //form as many fake clusters as true clusters, as large as each true cluster

            for (int i=0; i<trueClusters.size();i++) {

                List<Node> fakeCluster = new ArrayList<>();


                for (int j = 0; j < trueClusters.get(i).size(); j++) {

                    int clusterInd = j % trueClusters.size();

                    double Nodeind = trueClusters.get(clusterInd).size() * Math.random();
                    int Nodeindex = (int) Nodeind;

                    fakeCluster.add(trueClusters.get(clusterInd).get(Nodeindex));

                }

                fakeClusters.add(fakeCluster);

            }


//                while (fakeCluster.size() < trueClusters.get(i).size()) {
//                    System.out.println("working...fake cluster size: " + fakeCluster.size());
//
//
//
//                    double cluserInd = (trueClusters.size()) * Math.random();
//                    int clusterIndex = (int) cluserInd;
//
//
//                    if (!fakeCluster.contains(trueClusters.get(clusterIndex).get(Nodeindex))) {
//
//                        System.out.println("adding...fake cluster size: " + fakeCluster.size());
//
//                        fakeCluster.add(trueClusters.get(clusterIndex).get(Nodeindex));
//                    } else {
//                        for (Node node : estGraph.getNodes()) {
//                            if ((!fakeCluster.contains(node))&&isMeaningful(node,estGraph)){
//
//                                System.out.println("adding different nodes...fake cluster size: " + fakeCluster.size());
//
//                                fakeCluster.add(node);
//
//                                break;
//                            }
//                        }
//
//                    }
//                }
//
//                System.out.println("fake cluster size: "+fakeCluster.size());
//
//
//                fakeClusters.add(fakeCluster);
//
//            }

//            if(fakeClusters.size()==trueClusters.size()){
//
//                System.out.println("fake cluster is as many as true cluster");
//            }

            for (List<Node> cluster:trueClusters){

                if (isComplete(estGraph.subgraph(cluster))){

                    tp ++;
                }
            }

            for (List<Node> cluster:fakeClusters){

                System.out.println("fakeClusters: "+estGraph.subgraph(cluster));

                if (isComplete(estGraph.subgraph(cluster))){

                    fp ++;
                }
            }

            double precision = tp/(tp+fp);

            return precision;

        }



        List<List<Node>> trueClusters = GrabCluster(trueGraph);
        List<List<Node>> estClusters = GrabCluster(estGraph);

        double precision = 0;

        for (List<Node> estcluster : estClusters){

            double sPrecision = 0;

            for (List<Node> truecluster : trueClusters){

                double nodeCounter = 0;

                for (Node node : truecluster){

                    if (estcluster.contains(node)){
                        nodeCounter++;
                    }

                }

                double trueRatio = nodeCounter/estcluster.size();

                if (sPrecision<trueRatio){  sPrecision = trueRatio; } //for each true cluster, pick the highest precision

            }

            precision += sPrecision;

        }


        return precision/estClusters.size();//for all estimated clusters, how precise they reflex the true cluster

//

    }

    @Override
    public double getNormValue(double value) {
        return value;
    }

    private boolean isComplete(Graph graph){

        //System.out.println("For Precision: "+ graph);

        int numEdges = graph.getNumEdges();
        int numNodes = graph.getNumNodes();
        int fullyNumEdges = numNodes*(numNodes-1)/2;

        //System.out.println("cluster edges " +numEdges+" nodes "+numNodes+" connectivity "+ fullyNumEdges);

        if (numEdges == fullyNumEdges){
            return true;
        }else {
            return false;
        }


    }

    private List<List<Node>> GrabCluster(Graph graph){

        List<List<Node>> Clusters = new ArrayList<List<Node>>();

        for (Node d : graph.getNodes()){

            if (d.getName().contains("L") ) {//if it is a latent variable


                List<Node> legalChildren = new ArrayList<Node>();


                for (Node c : graph.getChildren(d)) {

                    //System.out.println("children of "+d.getName()+": "+c.getName());

                    if (!c.getName().contains("L")) {//checking if the child is a measured variable

                        if (graph.getNumEdges(c) == 1) {//if the child forms a pure model

                                legalChildren.add(c);

                        }

                    }

                }

                if (legalChildren.size() > 0) Clusters.add(legalChildren);

            }
        }

        return Clusters;

    }

    private boolean isMeaningful(Node node, Graph graph){

        if(graph.getAdjacentNodes(node).size()>0){
            return true;
        }else {
            return false;
        }

    }
}