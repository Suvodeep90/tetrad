package edu.cmu.tetrad.search;

import edu.cmu.tetrad.data.CovarianceMatrixOnTheFly;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.regression.RegressionDataset;
import edu.cmu.tetrad.regression.RegressionResult;
import edu.cmu.tetrad.util.DepthChoiceGenerator;

import java.util.*;

import static java.lang.Math.abs;
import static java.lang.Math.min;

/**
 * Implements the IDA algorithm, Maathuis, Marloes H., Markus Kalisch, and Peter Bühlmann.
 * "Estimating high-dimensional intervention effects from observational data."
 * The Annals of Statistics 37.6A (2009): 3133-3164.
 *
 * @author jdramsey@andrew.cmu.edu
 */
public class Ida {
    private DataSet dataSet;
    private final Graph pattern;
    private final RegressionDataset regression;

    public Ida(DataSet dataSet, List<Node> targets) {
        this.dataSet = dataSet;
        FgesMb fges = new FgesMb(new SemBicScore(new CovarianceMatrixOnTheFly(dataSet)));
        fges.setParallelism(1);
        this.pattern = fges.search(targets);
        regression = new RegressionDataset(dataSet);
    }

    public Ida(DataSet dataSet) {
        this.dataSet = dataSet;
        Fges fges = new Fges(new SemBicScore(new CovarianceMatrixOnTheFly(dataSet)));
        fges.setParallelism(1);
        this.pattern = fges.search();
        regression = new RegressionDataset(dataSet);
    }


    /**
     * Returns a list of the possible effects of X on Y (with different possible parents from the pattern),
     * sorted low to high in absolute value.
     * <p>
     * 1. First, estimate a pattern P from the data.
     * 2. Then, consider all combinations C of adjacents of X that include all fo the parents of X in P.
     * 3. For each such C, regress Y onto {X} U C and record the coefficient beta for X in the regression.
     * 4. Report the minimum such beta.
     *
     * @param x The first variable.
     * @param y The second variable
     * @return a list of the possible effects of X on Y.
     */
    public LinkedList<Double> getEffects(Node x, Node y) {
        if (x == y) return new LinkedList<>();

        List<Node> parents = pattern.getParents(x);
        List<Node> children = pattern.getChildren(x);

        List<Node> siblings = pattern.getAdjacentNodes(x);
        siblings.removeAll(parents);
        siblings.removeAll(children);

        DepthChoiceGenerator gen = new DepthChoiceGenerator(siblings.size(), siblings.size());
        int[] choice;

        LinkedList<Double> effects = new LinkedList<>();

        while ((choice = gen.next()) != null) {
            List<Node> sibbled = GraphUtils.asList(choice, siblings);

            List<Node> regressors = new ArrayList<>();
            regressors.add(x);
            for (Node n : parents) if (!regressors.contains(n)) regressors.add(n);
            for (Node n : sibbled) if (!regressors.contains(n)) regressors.add(n);

            double beta;

            if (regressors.contains(y)) {
                continue;
            } else {
                RegressionResult result = regression.regress(y, regressors);
                beta = result.isZeroInterceptAssumed() ? result.getCoef()[0] : result.getCoef()[1];
            }

            effects.add(abs(beta));
        }

        Collections.sort(effects);

        return effects;
    }

    /**
     * Returns a map from nodes in V \ {Y} to their minimum effects.
     *
     * @param y The target variable
     * @return Thia map.
     */
    private Map<Node, Double> calculateMinimumEffectsOnY(Node y) {
        SortedMap<Node, Double> minEffects = new TreeMap<>();

        for (Node x : dataSet.getVariables()) {
            final List<Double> effects = getEffects(x, y);

            if (!effects.isEmpty()) {
                minEffects.put(x, effects.get(0));
            }
        }

        return minEffects;
    }

    /**
     * Returns the minimum effects of X on Y for X in V \ {Y}, sorted downward by minimum effect
     *
     * @param y The target variable.
     * @return Two sorted lists, one of nodes, the other of corresponding minimum effects, sorted downward by
     * minimum effect size.
     */
    public NodeEffects getSortedMinEffects(Node y) {
        Map<Node, Double> allEffects = calculateMinimumEffectsOnY(y);

        List<Node> nodes = new ArrayList<>(allEffects.keySet());

        nodes.sort((o1, o2) -> {
            final Double d1 = allEffects.get(o1);
            final Double d2 = allEffects.get(o2);
            return -Double.compare(abs(d1), abs(d2));
        });

        List<Double> effects = new ArrayList<>();

        for (Node node : nodes) {
            effects.add(allEffects.get(node));
        }

        return new NodeEffects(nodes, effects);
    }

    /**
     * A list of nodes and corresonding minimum effects.
     *
     * @author jdramsey@andrew.cmu.edu
     */
    public class NodeEffects {
        private List<Node> nodes;
        private List<Double> effects;

        public NodeEffects(List<Node> nodes, List<Double> effects) {
            this.setNodes(nodes);
            this.setEffects(effects);
        }

        public List<Node> getNodes() {
            return nodes;
        }

        public void setNodes(List<Node> nodes) {
            this.nodes = nodes;
        }

        public List<Double> getEffects() {
            return effects;
        }

        public void setEffects(List<Double> effects) {
            this.effects = effects;
        }
    }

    public double trueEffect(Node x, Node y, Graph trueDag) {
        trueDag = GraphUtils.replaceNodes(trueDag, dataSet.getVariables());

        List<Node> regressors = new ArrayList<>();
        regressors.add(x);
        regressors.addAll(trueDag.getParents(x));

        if (regressors.contains(y)) return Double.NaN;

        RegressionResult result = regression.regress(y, regressors);
        final double effect = result.isZeroInterceptAssumed() ? result.getCoef()[0] : result.getCoef()[1];
        return abs(effect);
    }

    public double distance(double minEffect, double maxEffect, double trueEffect) {
        double min, max;

        if ((Double) minEffect <= (Double) maxEffect) {
            min = minEffect;
            max = maxEffect;
        } else {
            min = maxEffect;
            max = minEffect;
        }

        if (trueEffect >= min && trueEffect <= max) {
            return 0.0;
        } else {
            final double m1 = abs(trueEffect - min);
            final double m2 = abs(trueEffect - max);
            return min(m1, m2);
        }
    }
}
