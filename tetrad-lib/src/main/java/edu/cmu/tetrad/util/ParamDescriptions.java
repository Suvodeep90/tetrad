package edu.cmu.tetrad.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Stores descriptions of the parameters for the simulation box. All parameters
 * that go into the interface need to be described here.
 *
 * @author jdramsey
 */
public class ParamDescriptions {

    private static final ParamDescriptions INSTANCE = new ParamDescriptions();

    private final Map<String, ParamDescription> map = new HashMap<>();

    private ParamDescriptions() {
        map.put("numMeasures", new ParamDescription("Number of measured variables (min = 1)", 10, 1, Integer.MAX_VALUE));
        map.put("numLatents", new ParamDescription("Number of latent variables (min = 0)", 0, 0, Integer.MAX_VALUE));
        map.put("avgDegree", new ParamDescription("Average degree of graph (min = 1)", 2, 1, Integer.MAX_VALUE));
        map.put("maxDegree", new ParamDescription("The maximum degree of the graph (min = -1)", 10, -1, Integer.MAX_VALUE));
        map.get("maxDegree").setLongDescription("During the search if a node reaches the max degree value additional edges will not be added to that node.");
        map.put("maxIndegree", new ParamDescription("Maximum indegree of graph (min = 1)", 100, 1, Integer.MAX_VALUE));
        map.put("maxOutdegree", new ParamDescription("Maximum outdegree of graph (min = 1)", 100, 1, Integer.MAX_VALUE));

        map.put("connected", new ParamDescription("Yes if graph should be connected", false));
        map.put("sampleSize", new ParamDescription("Sample size (min = 1)", 1000, 1, Integer.MAX_VALUE));
        map.put("numRuns", new ParamDescription("Number of runs (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("differentGraphs", new ParamDescription("Yes if a different graph should be used for each run", false));
        map.put("alpha", new ParamDescription("Cutoff for p values (alpha) (min = 0.0)", 0.01, 0.0, 1.0));
        map.put("penaltyDiscount", new ParamDescription("Penalty discount (min = 0.0)", 2.0, 0.0, Double.MAX_VALUE));
        map.put("fgesDepth", new ParamDescription("Maximum number of new colliders (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("standardize", new ParamDescription("Yes if the data should be standardized", false));

        map.put("measurementVariance", new ParamDescription("Additive measurement noise variance (min = 0.0)", 0.0, 0, Double.MAX_VALUE));
        map.put("depth", new ParamDescription("Maximum size of conditioning set (unlimited = -1)", -1, -1, Integer.MAX_VALUE));
        map.put("meanLow", new ParamDescription("Low end of mean range (min = 0.0)", 0.5, 0.0, Double.MAX_VALUE));
        map.put("meanHigh", new ParamDescription("High end of mean range (min = 0.0)", 1.5, 0.0, Double.MAX_VALUE));
        map.put("coefLow", new ParamDescription("Low end of coefficient range (min = 0.0)", 0.2, 0.0, Double.MAX_VALUE));
        map.put("coefHigh", new ParamDescription("High end of coefficient range (min = 0.0)", 0.7, 0.0, Double.MAX_VALUE));
        map.put("covLow", new ParamDescription("Low end of covariance range (min = 0.0)", 0.5, 0.0, Double.MAX_VALUE));
        map.put("covHigh", new ParamDescription("High end of covariance range (min = 0.0)", 1.5, 0.0, Double.MAX_VALUE));
        map.put("varLow", new ParamDescription("Low end of variance range (min = 0.0)", 1.0, 0.0, Double.MAX_VALUE));
        map.put("varHigh", new ParamDescription("High end of variance range (min = 0.0)", 3.0, 0.0, Double.MAX_VALUE));

        map.put("printWinners", new ParamDescription("Yes if winning models should be printed", false));
        map.put("printAverages", new ParamDescription("Yes if averages should be printed", false));
        map.put("printAverageTables", new ParamDescription("Yes if average tables should be printed", true));
        map.put("printGraphs", new ParamDescription("Yes if graphs should be printed", false));
        map.put("dataType", new ParamDescription("Categorical or discrete", "categorical"));
        map.put("percentDiscrete", new ParamDescription("Percentage of discrete variables (0 - 100) for mixed data", 0.0, 0.0, 100.0));

        map.put("ofInterestCutoff", new ParamDescription("Cutoff for graphs considered to be of interest (min = 0.0)", 0.05, 0.0, 1.0));
        map.put("numCategories", new ParamDescription("Number of categories for discrete variables (min = 2)", 4, 2, Integer.MAX_VALUE));
        map.put("minCategories", new ParamDescription("Minimum number of categories (min = 2)", 2, 2, Integer.MAX_VALUE));
        map.put("maxCategories", new ParamDescription("Maximum number of categories (min = 2)", 2, 2, Integer.MAX_VALUE));
        map.put("samplePrior", new ParamDescription("Sample prior (min = 1.0)", 1.0, 1.0, Double.MAX_VALUE));
        map.put("structurePrior", new ParamDescription("Structure prior coefficient (min = 0.0)", 1.0, 0.0, Double.MAX_VALUE));
        map.put("mgmParam1", new ParamDescription("MGM tuning parameter #1 (min = 0.0)", 0.1, 0.0, Double.MAX_VALUE));
        map.put("mgmParam2", new ParamDescription("MGM tuning parameter #2 (min = 0.0)", 0.1, 0.0, Double.MAX_VALUE));
        map.put("mgmParam3", new ParamDescription("MGM tuning parameter #3 (min = 0.0)", 0.1, 0.0, Double.MAX_VALUE));
        map.put("scaleFreeAlpha", new ParamDescription("For scale-free graphs, the parameter alpha (min = 0.0)", 0.05, 0.0, 1.0));
        map.put("scaleFreeBeta", new ParamDescription("For scale-free graphs, the parameter beta (min = 0.0)", 0.9, 0.0, 1.0));
        map.put("scaleFreeDeltaIn", new ParamDescription("For scale-free graphs, the parameter delta_in (min = 0.0)", 3, 0.0, Double.MAX_VALUE));
        map.put("scaleFreeDeltaOut", new ParamDescription("For scale-free graphs, the parameter delta_out (min = 0.0)", 3, 0.0, Double.MAX_VALUE));
        map.put("generalSemFunctionTemplateMeasured", new ParamDescription("General function template for measured variables", "TSUM(NEW(B)*$)"));
        map.put("generalSemFunctionTemplateLatent", new ParamDescription("General function template for latent variables", "TSUM(NEW(B)*$)"));
        map.put("generalSemErrorTemplate", new ParamDescription("General function for error terms", "Beta(2, 5)"));
        map.put("generalSemParameterTemplate", new ParamDescription("General function for parameters", "Split(-1.0, -0.5, 0.5, 1.0)"));

        map.put("coefSymmetric", new ParamDescription("Yes if negative coefficient values should be considered", true));
        map.put("covSymmetric", new ParamDescription("Yes if negative covariance values should be considered", true));

        map.put("retainPreviousValues", new ParamDescription("Retain previous values", false));

        // Boolean Glass parameters.
        map.put("lagGraphVarsPerInd", new ParamDescription("Number of variables per individual (min = 1)", 5, 1, Integer.MAX_VALUE));
        map.put("lagGraphMlag", new ParamDescription("Maximum lag of graph (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("lagGraphIndegree", new ParamDescription("Indegree of graph (min = 1)", 2, 1, Integer.MAX_VALUE));
        map.put("numDishes", new ParamDescription("Number of dishes (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("numCellsPerDish", new ParamDescription("Number of cells per dish (min = 1)", 10000, 1, Integer.MAX_VALUE));
        map.put("stepsGenerated", new ParamDescription("Number of steps generated (min = 1)", 4, 1, Integer.MAX_VALUE));
        map.put("firstStepStored", new ParamDescription("The index of the first step stored (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("interval", new ParamDescription("The interval between steps stored (min = 1)", 1, 1, Integer.MAX_VALUE));
        map.put("rawDataSaved", new ParamDescription("Yes if the raw data should be saved (otherwise tossed)", false));
        map.put("measuredDataSaved", new ParamDescription("Yes if the measured data should be saved (otherwise tossed)", true));
        map.put("initSync", new ParamDescription("Yes if cells should be initialized synchronously, otherwise randomly", true));
        map.put("antilogCalculated", new ParamDescription("Yes if antilogs of data should be calculated", false));
        map.put("dishDishVariability", new ParamDescription("Dish to dish variability (min = 0.0)", 10.0, 0.0, Double.MAX_VALUE));
        map.put("numChipsPerDish", new ParamDescription("Number of chips per dish (min = 1)", 4, 1, Integer.MAX_VALUE));
        map.put("sampleSampleVariability", new ParamDescription("Sample to sample variability (min = 0.0)", 0.025, 0.0, Double.MAX_VALUE));
        map.put("chipChipVariability", new ParamDescription("Chip to chip variability (min = 0.0)", 0.1, 0.0, Double.MAX_VALUE));
        map.put("pixelDigitalization", new ParamDescription("Pixel digitalization (min = 0.0)", 0.025, 0.0, Double.MAX_VALUE));
        map.put("includeDishAndChipColumns", new ParamDescription("Yes if Dish and Chip columns should be included in output", true));

        map.put("numRuns", new ParamDescription("The number runs", 1));
        map.put("randomSelectionSize", new ParamDescription("The number of datasets that should be taken in each random sample", 1));

        map.put("maxit", new ParamDescription("MAXIT parameter (GLASSO) (min = 1)", 10000, 1, Integer.MAX_VALUE));
        map.put("ia", new ParamDescription("IA parameter (GLASSO)", false));
        map.put("is", new ParamDescription("IS parameter (GLASSO)", false));
        map.put("itr", new ParamDescription("ITR parameter (GLASSO)", false));
        map.put("ipen", new ParamDescription("IPEN parameter (GLASSO)", false));
        map.put("thr", new ParamDescription("THR parameter (GLASSO) (min = 0.0)", 1e-4, 0.0, Double.MAX_VALUE));

        map.put("targetName", new ParamDescription("Target variable name", ""));
        map.put("verbose", new ParamDescription("Yes if verbose output should be printed or logged", true));
        map.put("faithfulnessAssumed", new ParamDescription("Yes if (one edge) faithfulness should be assumed", true));

        map.put("useWishart", new ParamDescription("Yes if the Wishart test shoud be used. No if the Delta test should be used", false));
        map.put("useGap", new ParamDescription("Yes if the GAP algorithms should be used. No if the SAG algorithm should be used", false));

        // Multiple indicator random graphs
        map.put("numStructuralNodes", new ParamDescription("Number of structural nodes", 3));
        map.put("numStructuralEdges", new ParamDescription("Number of structural edges", 3));
        map.put("measurementModelDegree", new ParamDescription("Number of measurements per Latent", 5));
        map.put("latentMeasuredImpureParents", new ParamDescription("Number of Latent --> Measured impure edges", 0));
        map.put("measuredMeasuredImpureParents", new ParamDescription("Number of Measured --> Measured impure edges", 0));
        map.put("measuredMeasuredImpureAssociations", new ParamDescription("Number of Measured <-> Measured impure edges", 0));

//        map.put("useRuleC", new ParamDescription("Yes if rule C for CCD should be used", false));
        map.put("applyR1", new ParamDescription("Yes if the orient away from arrow rule should be applied", true));
        map.put("probCycle", new ParamDescription("The probability of adding a cycle to the graph", 1.0, 0.0, 1.0));
        map.put("intervalBetweenShocks", new ParamDescription("Interval beween shocks (R. A. Fisher simulation model) (min = 1)", 10, 1, Integer.MAX_VALUE));
        map.put("intervalBetweenRecordings", new ParamDescription("Interval between data recordings for the linear Fisher model (min = 1)", 10, 1, Integer.MAX_VALUE));

        map.put("skipNumRecords", new ParamDescription("Number of records that should be skipped between recordings (min = 0)", 0, 0, Integer.MAX_VALUE));
        map.put("fisherEpsilon", new ParamDescription("Epsilon where |xi.t - xi.t-1| < epsilon, criterion for convergence", .001, Double.MIN_VALUE, Double.MAX_VALUE));

        map.put("useMaxPOrientationHeuristic", new ParamDescription(
                "Yes if the heuristic for orienting unshielded colliders for max P should be used",
                false));
        map.put("maxPOrientationMaxPathLength", new ParamDescription("Maximum path length for the unshielded collider heuristic for max P (min = 0)", 3, 0, Integer.MAX_VALUE));
        map.put("orientTowardDConnections", new ParamDescription(
                "Yes if Richardson's step C (orient toward d-connection) should be used",
                true));
        map.put("orientVisibleFeedbackLoops", new ParamDescription(
                "Yes if visible feedback loops should be oriented",
                true));
        map.put("doColliderOrientation", new ParamDescription(
                "Yes if unshielded collider orientation should be done",
                true));

        map.put("completeRuleSetUsed", new ParamDescription(
                "Yes if the complete FCI rule set should be used",
                false));

        map.put("maxDistinctValuesDiscrete", new ParamDescription("The maximum number of distinct values in a column for discrete variables (min = 0)", 0, 0, Integer.MAX_VALUE));

        map.put("ngAlpha", new ParamDescription("Alpha for testing non-Gaussianity (min = 0.0)", 0.05, 0.0, 1.0));

        map.put("twoCycleAlpha", new ParamDescription("Alpha orienting 2-cycles (min = 0.0)", .05, 0.0, 1.0));

        map.put("symmetricFirstStep", new ParamDescription("Yes if the first step step for FGES should do scoring for both X->Y and Y->X", false));

        map.put("discretize", new ParamDescription(
                "Yes if continuous variables should be discretized when child is discrete",
                true));

        map.put("determinismThreshold", new ParamDescription("Threshold for judging a regression of a variable onto its parents to be deternimistic (min = 0.0)", 0.1, 0.0, Double.POSITIVE_INFINITY));

        map.put("cgExact", new ParamDescription(
                "Yes if the exact algorithm should be used for continuous parents and discrete children",
                false));

        map.put("numCategoriesToDiscretize", new ParamDescription(
                "The number of categories used to discretize continuous variables, if necessary (min = 2)", 3, 2, Integer.MAX_VALUE));

        map.put("maxPathLength", new ParamDescription("The maximum length for any discriminating path. -1 if unlimited (min = -1)", -1, -1, Integer.MAX_VALUE));

        map.put("thresholdForReversing", new ParamDescription("Variables with skewnesses less than this value will be reversed in sign (min = 0)", 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));

        // Resampling
        map.put("numberResampling", new ParamDescription("The number of bootstraps/resampling iterations (min = 0)", 0, 0, Integer.MAX_VALUE));
        map.put("percentResampleSize", new ParamDescription("The percentage of resample size (min = 0.1)", 100, 0.1, Double.MAX_VALUE));
        map.put("resamplingWithReplacement", new ParamDescription("Yes, if sampling with replacement (bootstrapping)", true));
        map.put("resamplingEnsemble", new ParamDescription("Ensemble method: Preserved (0), Highest (1), Majority (2)", 1, 0, 2));
        map.put("addOriginalDataset", new ParamDescription("Yes, if adding an original dataset as another bootstrapping", false));
        //~Resampling

        map.put("fasRule", new ParamDescription(
                "Adjacency search: 1 = PC, 2 = PC-Stable, 3 =f Concurrent PC-Stable",
                1, 1, 3));

        map.put("colliderDiscoveryRule", new ParamDescription(
                "Collider discovery: 1 = Lookup from adjacency sepsets, 2 = Conservative (CPC), 3 = Max-P",
                1, 1, 3));

        map.put("conflictRule", new ParamDescription(
                "Collider conflicts: 1 = Overwrite, 2 = Orient bidirected, 3 = Prioritize existing colliders",
                1, 1, 3));

        map.put("randomizeColumns", new ParamDescription(
                "Yes if the order of the columns in each datasets should be randomized",
                false));

        map.put("logScale", new ParamDescription(
                "Yes if the parameters are in log scale",
                false));

        map.put("StARS.percentageB", new ParamDescription(
                "Percentage of rows to include in each subsample",
                0.5, 0.0, 1.0));

        map.put("StARS.tolerance", new ParamDescription(
                "Parameter tolerance for binary search",
                .5, 0.0, Double.POSITIVE_INFINITY));

        map.put("StARS.cutoff", new ParamDescription(
                "Cutoff for D in the StARS procedure",
                0.01, 0.0, 1.0));

        map.put("numSubsamples", new ParamDescription(
                "The number of subsamples to take for the StARZ procedure",
                8, 1, Integer.MAX_VALUE));

        map.put("percentSubsampleSize", new ParamDescription(
                "Percentage of records to include in a random subsample",
                0.5, 0.0, 1.0));

        map.put("percentStability", new ParamDescription(
                "Percentage of subsamples each feature in the output must agree on",
                0.5, 0.0, 1.0));

        map.put("includePositiveCoefs", new ParamDescription(
                "Yes if positive coefficients should be included in the model",
                true));

        map.put("includeNegativeCoefs", new ParamDescription(
                "Yes if negative coefficients should be included in the model",
                true));

        map.put("includePositiveSkewsForBeta", new ParamDescription(
                "Yes if positive skew values should be included in the model, if Beta errors are chosen",
                true));

        map.put("includeNegativeSkewsForBeta", new ParamDescription(
                "Yes if negative skew values should be included in the model, if Beta errors are chosen",
                true));


        map.put("errorsNormal", new ParamDescription(
                "Yes if errors should be Normal; No if they should be Beta",
                true));

        map.put("betaLeftValue", new ParamDescription(
                "For Beta(x, y), the 'x'",
                1, 1, Double.POSITIVE_INFINITY));

        map.put("betaRightValue", new ParamDescription(
                "For Beta(x, y), the 'y'",
                5, 1, Double.POSITIVE_INFINITY));

        map.put("extraEdgeThreshold", new ParamDescription(
                "Threshold for including extra edges",
                0.3, 0.0, Double.POSITIVE_INFINITY));

        map.put("maskThreshold", new ParamDescription(
                "Mask threshold for including extra edges (default 0.3)",
                0.3, 0.0, Double.POSITIVE_INFINITY));

        map.put("skewEdgeAlpha", new ParamDescription(
                "Skew edge alpha",
                0.001, 0.0, Double.POSITIVE_INFINITY));


        map.put("skewEd        map.put(\"numMeasures\", new ParamDescription(\"Number of measured variables (min = 1)\", 10, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"numLatents\", new ParamDescription(\"Number of latent variables (min = 0)\", 0, 0, Integer.MAX_VALUE));\n" +
                "        map.put(\"avgDegree\", new ParamDescription(\"Average degree of graph (min = 1)\", 2, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"maxDegree\", new ParamDescription(\"The maximum degree of the graph (min = -1)\", 4, -1, Integer.MAX_VALUE));\n" +
                "        map.get(\"maxDegree\").setLongDescription(\"During the search if a node reaches the max degree value additional edges will not be added to that node.\");\n" +
                "        map.put(\"maxIndegree\", new ParamDescription(\"Maximum indegree of graph (min = 1)\", 100, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"maxOutdegree\", new ParamDescription(\"Maximum outdegree of graph (min = 1)\", 100, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"connected\", new ParamDescription(\"Yes if graph should be connected\", false));\n" +
                "        map.put(\"sampleSize\", new ParamDescription(\"Sample size (min = 1)\", 1000, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"numRuns\", new ParamDescription(\"Number of runs (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"differentGraphs\", new ParamDescription(\"Yes if a different graph should be used for each run\", false));\n" +
                "        map.put(\"alpha\", new ParamDescription(\"Cutoff for p values (alpha) (min = 0.0)\", 0.01, 0.0, 1.0));\n" +
                "        map.put(\"penaltyDiscount\", new ParamDescription(\"Penalty discount (min = 0.0)\", 2.0, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"fgesDepth\", new ParamDescription(\"Maximum number of new colliders (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"standardize\", new ParamDescription(\"Yes if the data should be standardized\", false));\n" +
                "\n" +
                "        map.put(\"measurementVariance\", new ParamDescription(\"Additive measurement noise variance (min = 0.0)\", 0.0, 0, Double.MAX_VALUE));\n" +
                "        map.put(\"depth\", new ParamDescription(\"Maximum size of conditioning set (unlimited = -1)\", -1, -1, Integer.MAX_VALUE));\n" +
                "        map.put(\"meanLow\", new ParamDescription(\"Low end of mean range (min = 0.0)\", 0.5, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"meanHigh\", new ParamDescription(\"High end of mean range (min = 0.0)\", 1.5, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"coefLow\", new ParamDescription(\"Low end of coefficient range (min = 0.0)\", 0.2, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"coefHigh\", new ParamDescription(\"High end of coefficient range (min = 0.0)\", 0.7, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"covLow\", new ParamDescription(\"Low end of covariance range (min = 0.0)\", 0.5, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"covHigh\", new ParamDescription(\"High end of covariance range (min = 0.0)\", 1.5, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"varLow\", new ParamDescription(\"Low end of variance range (min = 0.0)\", 1.0, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"varHigh\", new ParamDescription(\"High end of variance range (min = 0.0)\", 3.0, 0.0, Double.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"printWinners\", new ParamDescription(\"Yes if winning models should be printed\", false));\n" +
                "        map.put(\"printAverages\", new ParamDescription(\"Yes if averages should be printed\", false));\n" +
                "        map.put(\"printAverageTables\", new ParamDescription(\"Yes if average tables should be printed\", true));\n" +
                "        map.put(\"printGraphs\", new ParamDescription(\"Yes if graphs should be printed\", false));\n" +
                "        map.put(\"dataType\", new ParamDescription(\"Categorical or discrete\", \"categorical\"));\n" +
                "        map.put(\"percentDiscrete\", new ParamDescription(\"Percentage of discrete variables (0 - 100) for mixed data\", 0.0, 0.0, 100.0));\n" +
                "\n" +
                "        map.put(\"ofInterestCutoff\", new ParamDescription(\"Cutoff for graphs considered to be of interest (min = 0.0)\", 0.05, 0.0, 1.0));\n" +
                "        map.put(\"numCategories\", new ParamDescription(\"Number of categories for discrete variables (min = 2)\", 4, 2, Integer.MAX_VALUE));\n" +
                "        map.put(\"minCategories\", new ParamDescription(\"Minimum number of categories (min = 2)\", 2, 2, Integer.MAX_VALUE));\n" +
                "        map.put(\"maxCategories\", new ParamDescription(\"Maximum number of categories (min = 2)\", 2, 2, Integer.MAX_VALUE));\n" +
                "        map.put(\"samplePrior\", new ParamDescription(\"Sample prior (min = 1.0)\", 1.0, 1.0, Double.MAX_VALUE));\n" +
                "        map.put(\"structurePrior\", new ParamDescription(\"Structure prior coefficient (min = 1.0)\", 1.0, 1.0, Double.MAX_VALUE));\n" +
                "        map.put(\"mgmParam1\", new ParamDescription(\"MGM tuning parameter #1 (min = 0.0)\", 0.1, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"mgmParam2\", new ParamDescription(\"MGM tuning parameter #2 (min = 0.0)\", 0.1, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"mgmParam3\", new ParamDescription(\"MGM tuning parameter #3 (min = 0.0)\", 0.1, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"scaleFreeAlpha\", new ParamDescription(\"For scale-free graphs, the parameter alpha (min = 0.0)\", 0.05, 0.0, 1.0));\n" +
                "        map.put(\"scaleFreeBeta\", new ParamDescription(\"For scale-free graphs, the parameter beta (min = 0.0)\", 0.9, 0.0, 1.0));\n" +
                "        map.put(\"scaleFreeDeltaIn\", new ParamDescription(\"For scale-free graphs, the parameter delta_in (min = 0.0)\", 3, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"scaleFreeDeltaOut\", new ParamDescription(\"For scale-free graphs, the parameter delta_out (min = 0.0)\", 3, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"generalSemFunctionTemplateMeasured\", new ParamDescription(\"General function template for measured variables\", \"TSUM(NEW(B)*$)\"));\n" +
                "        map.put(\"generalSemFunctionTemplateLatent\", new ParamDescription(\"General function template for latent variables\", \"TSUM(NEW(B)*$)\"));\n" +
                "        map.put(\"generalSemErrorTemplate\", new ParamDescription(\"General function for error terms\", \"Beta(2, 5)\"));\n" +
                "        map.put(\"generalSemParameterTemplate\", new ParamDescription(\"General function for parameters\", \"Split(-1.0, -0.5, 0.5, 1.0)\"));\n" +
                "\n" +
                "        map.put(\"coefSymmetric\", new ParamDescription(\"Yes if negative coefficient values should be considered\", true));\n" +
                "        map.put(\"covSymmetric\", new ParamDescription(\"Yes if negative covariance values should be considered\", true));\n" +
                "\n" +
                "        map.put(\"retainPreviousValues\", new ParamDescription(\"Retain previous values\", false));\n" +
                "\n" +
                "        // Boolean Glass parameters.\n" +
                "        map.put(\"lagGraphVarsPerInd\", new ParamDescription(\"Number of variables per individual (min = 1)\", 5, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"lagGraphMlag\", new ParamDescription(\"Maximum lag of graph (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"lagGraphIndegree\", new ParamDescription(\"Indegree of graph (min = 1)\", 2, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"numDishes\", new ParamDescription(\"Number of dishes (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"numCellsPerDish\", new ParamDescription(\"Number of cells per dish (min = 1)\", 10000, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"stepsGenerated\", new ParamDescription(\"Number of steps generated (min = 1)\", 4, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"firstStepStored\", new ParamDescription(\"The index of the first step stored (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"interval\", new ParamDescription(\"The interval between steps stored (min = 1)\", 1, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"rawDataSaved\", new ParamDescription(\"Yes if the raw data should be saved (otherwise tossed)\", false));\n" +
                "        map.put(\"measuredDataSaved\", new ParamDescription(\"Yes if the measured data should be saved (otherwise tossed)\", true));\n" +
                "        map.put(\"initSync\", new ParamDescription(\"Yes if cells should be initialized synchronously, otherwise randomly\", true));\n" +
                "        map.put(\"antilogCalculated\", new ParamDescription(\"Yes if antilogs of data should be calculated\", false));\n" +
                "        map.put(\"dishDishVariability\", new ParamDescription(\"Dish to dish variability (min = 0.0)\", 10.0, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"numChipsPerDish\", new ParamDescription(\"Number of chips per dish (min = 1)\", 4, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"sampleSampleVariability\", new ParamDescription(\"Sample to sample variability (min = 0.0)\", 0.025, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"chipChipVariability\", new ParamDescription(\"Chip to chip variability (min = 0.0)\", 0.1, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"pixelDigitalization\", new ParamDescription(\"Pixel digitalization (min = 0.0)\", 0.025, 0.0, Double.MAX_VALUE));\n" +
                "        map.put(\"includeDishAndChipColumns\", new ParamDescription(\"Yes if Dish and Chip columns should be included in output\", true));\n" +
                "\n" +
                "        map.put(\"numRuns\", new ParamDescription(\"The number runs\", 1));\n" +
                "        map.put(\"randomSelectionSize\", new ParamDescription(\"The number of datasets that should be taken in each random sample\", 1));\n" +
                "\n" +
                "        map.put(\"maxit\", new ParamDescription(\"MAXIT parameter (GLASSO) (min = 1)\", 10000, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"ia\", new ParamDescription(\"IA parameter (GLASSO)\", false));\n" +
                "        map.put(\"is\", new ParamDescription(\"IS parameter (GLASSO)\", false));\n" +
                "        map.put(\"itr\", new ParamDescription(\"ITR parameter (GLASSO)\", false));\n" +
                "        map.put(\"ipen\", new ParamDescription(\"IPEN parameter (GLASSO)\", false));\n" +
                "        map.put(\"thr\", new ParamDescription(\"THR parameter (GLASSO) (min = 0.0)\", 1e-4, 0.0, Double.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"targetName\", new ParamDescription(\"Target variable name\", \"\"));\n" +
                "        map.put(\"verbose\", new ParamDescription(\"Yes if verbose output should be printed or logged\", true));\n" +
                "        map.put(\"faithfulnessAssumed\", new ParamDescription(\"Yes if (one edge) faithfulness should be assumed\", true));\n" +
                "\n" +
                "        map.put(\"useWishart\", new ParamDescription(\"Yes if the Wishart test shoud be used. No if the Delta test should be used\", false));\n" +
                "        map.put(\"useGap\", new ParamDescription(\"Yes if the GAP algorithms should be used. No if the SAG algorithm should be used\", false));\n" +
                "\n" +
                "        // Multiple indicator random graphs\n" +
                "        map.put(\"numStructuralNodes\", new ParamDescription(\"Number of structural nodes\", 3));\n" +
                "        map.put(\"numStructuralEdges\", new ParamDescription(\"Number of structural edges\", 3));\n" +
                "        map.put(\"measurementModelDegree\", new ParamDescription(\"Number of measurements per Latent\", 5));\n" +
                "        map.put(\"latentMeasuredImpureParents\", new ParamDescription(\"Number of Latent --> Measured impure edges\", 0));\n" +
                "        map.put(\"measuredMeasuredImpureParents\", new ParamDescription(\"Number of Measured --> Measured impure edges\", 0));\n" +
                "        map.put(\"measuredMeasuredImpureAssociations\", new ParamDescription(\"Number of Measured <-> Measured impure edges\", 0));\n" +
                "\n" +
                "//        map.put(\"useRuleC\", new ParamDescription(\"Yes if rule C for CCD should be used\", false));\n" +
                "        map.put(\"applyR1\", new ParamDescription(\"Yes if the orient away from arrow rule should be applied\", true));\n" +
                "        map.put(\"probCycle\", new ParamDescription(\"The probability of adding a cycle to the graph\", 1.0, 0.0, 1.0));\n" +
                "        map.put(\"intervalBetweenShocks\", new ParamDescription(\"Interval beween shocks (R. A. Fisher simulation model) (min = 1)\", 10, 1, Integer.MAX_VALUE));\n" +
                "        map.put(\"intervalBetweenRecordings\", new ParamDescription(\"Interval between data recordings for the linear Fisher model (min = 1)\", 10, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"skipNumRecords\", new ParamDescription(\"Number of records that should be skipped between recordings (min = 0)\", 0, 0, Integer.MAX_VALUE));\n" +
                "        map.put(\"fisherEpsilon\", new ParamDescription(\"Epsilon where |xi.t - xi.t-1| < epsilon, criterion for convergence\", .001, Double.MIN_VALUE, Double.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"useMaxPOrientationHeuristic\", new ParamDescription(\n" +
                "                \"Yes if the heuristic for orienting unshielded colliders for max P should be used\",\n" +
                "                false));\n" +
                "        map.put(\"maxPOrientationMaxPathLength\", new ParamDescription(\"Maximum path length for the unshielded collider heuristic for max P (min = 0)\", 3, 0, Integer.MAX_VALUE));\n" +
                "        map.put(\"orientTowardDConnections\", new ParamDescription(\n" +
                "                \"Yes if Richardson's step C (orient toward d-connection) should be used\",\n" +
                "                true));\n" +
                "        map.put(\"orientVisibleFeedbackLoops\", new ParamDescription(\n" +
                "                \"Yes if visible feedback loops should be oriented\",\n" +
                "                true));\n" +
                "        map.put(\"doColliderOrientation\", new ParamDescription(\n" +
                "                \"Yes if unshielded collider orientation should be done\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"completeRuleSetUsed\", new ParamDescription(\n" +
                "                \"Yes if the complete FCI rule set should be used\",\n" +
                "                false));\n" +
                "\n" +
                "        map.put(\"maxDistinctValuesDiscrete\", new ParamDescription(\"The maximum number of distinct values in a column for discrete variables (min = 0)\", 0, 0, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"ngAlpha\", new ParamDescription(\"Alpha for testing non-Gaussianity (min = 0.0)\", 0.05, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"twoCycleAlpha\", new ParamDescription(\"Alpha orienting 2-cycles (min = 0.0)\", .05, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"symmetricFirstStep\", new ParamDescription(\"Yes if the first step step for FGES should do scoring for both X->Y and Y->X\", false));\n" +
                "\n" +
                "        map.put(\"discretize\", new ParamDescription(\n" +
                "                \"Yes if continuous variables should be discretized when child is discrete\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"determinismThreshold\", new ParamDescription(\"Threshold for judging a regression of a variable onto its parents to be deternimistic (min = 0.0)\", 0.1, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"cgExact\", new ParamDescription(\n" +
                "                \"Yes if the exact algorithm should be used for continuous parents and discrete children\",\n" +
                "                false));\n" +
                "\n" +
                "        map.put(\"numCategoriesToDiscretize\", new ParamDescription(\n" +
                "                \"The number of categories used to discretize continuous variables, if necessary (min = 2)\", 3, 2, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"maxPathLength\", new ParamDescription(\"The maximum length for any discriminating path. -1 if unlimited (min = -1)\", -1, -1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"thresholdForReversing\", new ParamDescription(\"Variables with skewnesses less than this value will be reversed in sign (min = 0)\", 0.0, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        // Resampling\n" +
                "        map.put(\"numberResampling\", new ParamDescription(\"The number of bootstraps/resampling iterations (min = 0)\", 0, 0, Integer.MAX_VALUE));\n" +
                "        map.put(\"percentResampleSize\", new ParamDescription(\"The percentage of resample size (min = 0.1)\", 100, 0.1, Double.MAX_VALUE));\n" +
                "        map.put(\"resamplingWithReplacement\", new ParamDescription(\"Yes, if sampling with replacement (bootstrapping)\", true));\n" +
                "        map.put(\"resamplingEnsemble\", new ParamDescription(\"Ensemble method: Preserved (0), Highest (1), Majority (2)\", 1, 0, 2));\n" +
                "        //~Resampling\n" +
                "\n" +
                "        map.put(\"fasRule\", new ParamDescription(\n" +
                "                \"Adjacency search: 1 = PC, 2 = PC-Stable, 3 = Concurrent PC-Stable\",\n" +
                "                1, 1, 3));\n" +
                "\n" +
                "        map.put(\"colliderDiscoveryRule\", new ParamDescription(\n" +
                "                \"Collider discovery: 1 = Lookup from adjacency sepsets, 2 = Conservative (CPC), 3 = Max-P\",\n" +
                "                1, 1, 1));\n" +
                "\n" +
                "        map.put(\"conflictRule\", new ParamDescription(\n" +
                "                \"Collider conflicts: 1 = Overwrite, 2 = Orient bidirected, 3 = Prioritize existing colliders\",\n" +
                "                1, 1, 1));\n" +
                "\n" +
                "        map.put(\"randomizeColumns\", new ParamDescription(\n" +
                "                \"Yes if the order of the columns in each datasets should be randomized\",\n" +
                "                false));\n" +
                "\n" +
                "        map.put(\"logScale\", new ParamDescription(\n" +
                "                \"Yes if the parameters are in log scale\",\n" +
                "                false));\n" +
                "\n" +
                "        map.put(\"StARS.percentageB\", new ParamDescription(\n" +
                "                \"Percentage of rows to include in each subsample\",\n" +
                "                0.5, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"StARS.tolerance\", new ParamDescription(\n" +
                "                \"Parameter tolerance for binary search\",\n" +
                "                .5, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"StARS.cutoff\", new ParamDescription(\n" +
                "                \"Cutoff for D in the StARS procedure\",\n" +
                "                0.01, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"numSubsamples\", new ParamDescription(\n" +
                "                \"The number of subsamples to take for the StARZ procedure\",\n" +
                "                8, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"percentSubsampleSize\", new ParamDescription(\n" +
                "                \"Percentage of records to include in a random subsample\",\n" +
                "                0.5, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"percentStability\", new ParamDescription(\n" +
                "                \"Percentage of subsamples each feature in the output must agree on\",\n" +
                "                0.5, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"includePositiveCoefs\", new ParamDescription(\n" +
                "                \"Yes if positive coefficients should be included in the model\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"includeNegativeCoefs\", new ParamDescription(\n" +
                "                \"Yes if negative coefficients should be included in the model\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"errorsNormal\", new ParamDescription(\n" +
                "                \"Yes if errors should be Normal; No if they should be Beta\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"betaLeftValue\", new ParamDescription(\n" +
                "                \"For Beta(x, y), the 'x'\",\n" +
                "                1, 1, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"betaRightValue\", new ParamDescription(\n" +
                "                \"For Beta(x, y), the 'y'\",\n" +
                "                5, 1, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"extraEdgeThreshold\", new ParamDescription(\n" +
                "                \"Threshold for including extra edges\",\n" +
                "                0.3, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"maskThreshold\", new ParamDescription(\n" +
                "                \"Mask threshold for including extra edges (default 0.3)\",\n" +
                "                0.3, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"skewEdgeAlpha\", new ParamDescription(\n" +
                "                \"Alpha for including extra edges based on skewness\",\n" +
                "                0.05, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"useFasAdjacencies\", new ParamDescription(\n" +
                "                \"Yes if adjacencies from the FAS search (correlation) should be used\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"useSkewAdjacencies\", new ParamDescription(\n" +
                "                \"Yes if adjacencies based on skewness should be used\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"useMask\", new ParamDescription(\n" +
                "                \"Yes if the correlation difference mask should be used\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"faskDelta\", new ParamDescription(\n" +
                "                \"Threshold for judging negative coefficient edges as X->Y (range (-1, 0))\",\n" +
                "                -0.2, -1.0, 1.0));\n" +
                "\n" +
                "        map.put(\"faskDelta2\", new ParamDescription(\n" +
                "                \"Threshold for judging negative coefficient edges as X->Y\",\n" +
                "                0.0, Double.NaN, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"errorsPositivelySkewed\", new ParamDescription(\n" +
                "                \"Yes if errors should be assumed to be positively skewed\",\n" +
                "                true));\n" +
                "\n" +
                "        map.put(\"numLags\", new ParamDescription(\n" +
                "                \"The number of lags in the time lag model\",\n" +
                "                1, 1, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"saveLatentVars\", new ParamDescription(\"Save latent variables.\", false));\n" +
                "\n" +
                "        map.put(\"probTwoCycle\", new ParamDescription(\n" +
                "                \"The probability of creating a 2-cycles in the graph (0 - 1)\",\n" +
                "                0.0, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"numBasisFunctions\", new ParamDescription(\n" +
                "                \"Number of functions to use in (truncated) basis\",\n" +
                "                30, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"kciCutoff\", new ParamDescription(\n" +
                "                \"Cutoff\",\n" +
                "                6, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"kernelWidth\", new ParamDescription(\n" +
                "                \"Kernel width\",\n" +
                "                1.0, Double.MIN_VALUE, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"kernelMultiplier\", new ParamDescription(\n" +
                "                \"Bowman and Azzalini (1997) default kernel bandwidhts should be multiplied by...\",\n" +
                "                1.0, Double.MIN_VALUE, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"kernelType\", new ParamDescription(\n" +
                "                \"Kernel type (1 = Gaussian, 2 = Epinechnikov)\",\n" +
                "                2, 1, 2));\n" +
                "\n" +
                "        map.put(\"basisType\", new ParamDescription(\n" +
                "                \"Basis type (1 = Polynomial, 2 = Cosine)\",\n" +
                "                2, 1, 2));\n" +
                "\n" +
                "        map.put(\"kciNumBootstraps\", new ParamDescription(\n" +
                "                \"Number of bootstraps for Theorems 4 and Proposition 5 for KCI\",\n" +
                "                5000, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"thresholdForNumEigenvalues\", new ParamDescription(\n" +
                "                \"Threshold to determine how many eigenvalues to use--the lower the more (0 to 1)\",\n" +
                "                0.001, 0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"rcitNumFeatures\", new ParamDescription(\n" +
                "                \"The number of random features to use\",\n" +
                "                10, 1, Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"kciUseAppromation\", new ParamDescription(\n" +
                "                \"Use the approximate Gamma approximation algorithm\", true));\n" +
                "\n" +
                "        map.put(\"kciEpsilon\", new ParamDescription(\n" +
                "                \"Epsilon for Proposition 5, a small positive number\", 0.001, 0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"rcitApproxType\", new ParamDescription(\n" +
                "                \"Approximation Type: 1 = LPD4, 2 = Gamma, 3 = HBE, 4 = PERM\",\n" +
                "                4, 1, 4));\n" +
                "\n" +
                "        map.put(\"possibleDsepDone\", new ParamDescription(\n" +
                "                \"Yes if the possible dsep search should be done\", true));\n" +
                "\n" +
                "        map.put(\"selfLoopCoef\", new ParamDescription(\n" +
                "                \"The coefficient for the self-loop (default 0.0)\", 0.0, 0.0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"tsbetathr\", new ParamDescription(\n" +
                "                \"Threshold to determine whether a coefficient in the Beta matrix implies an edge int h graph\",\n" +
                "                0.01, 0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"tstheta\", new ParamDescription(\n" +
                "                \"Alasso threshold\",\n" +
                "                1.0, 0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"tssigma\", new ParamDescription(\n" +
                "                \"ICA threshold\",\n" +
                "                1.0, 0, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"kernelRegressionSampleSize\", new ParamDescription(\n" +
                "                \"Minimum sample size to use per conditioning for kernel regression\",\n" +
                "                100, 1, Double.POSITIVE_INFINITY));\n" +
                "\n" +
                "        map.put(\"kciAlpha\", new ParamDescription(\"Cutoff for p values (alpha) (min = 0.0)\", 0.05, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"cciScoreAlpha\", new ParamDescription(\"Cutoff for p values (alpha) (min = 0.0)\", 0.01, 0.0, 1.0));\n" +
                "\n" +
                "        map.put(\"numDependenceSpotChecks\", new ParamDescription(\n" +
                "                \"The number of specific <z1,...,zn> values for which to check X _||_ Y | Z = <z1,...,zn>\",\n" +
                "                0, 0,Integer.MAX_VALUE));\n" +
                "\n" +
                "        map.put(\"stableFAS\", new ParamDescription(\n" +
                "                \"Yes if the 'stable' FAS should be done\", false));\n" +
                "\n" +
                "        map.put(\"concurrentFAS\", new ParamDescription(\n" +
                "                \"Yes if a concurrent FAS should be done\", true));\n" +
                "\n" +
                "        map.put(\"stableFASFDR\", new ParamDescription(\n" +
                "                \"Yes if the 'stable' FAS should be done with the StableFDR adjustment\", false));\ngeAlpha", new ParamDescription(
                "Alpha for including extra edges based on skewness",
                0.05, 0.0, Double.POSITIVE_INFINITY));

        map.put("useFasAdjacencies", new ParamDescription(
                "Yes if adjacencies from the FAS search (correlation) should be used",
                true));

        map.put("useSkewAdjacencies", new ParamDescription(
                "Yes if adjacencies based on skewness should be used",
                true));

        map.put("useCorrDiffAdjacencies", new ParamDescription(
                "Yes if adjacencies from conditional correlation differences should be used",
                true));

        map.put("faskDelta", new ParamDescription(
                "Threshold for judging negative coefficient edges as X->Y (range (-1, 0))",
                -0.2, -1.0, 1.0));

        map.put("faskDelta2", new ParamDescription(
                "Threshold for judging negative coefficient edges as X->Y",
                0.0, Double.NaN, Double.POSITIVE_INFINITY));

        map.put("smallCorrelation", new ParamDescription(
                "Abolute threshold below which edges are reversed and drawn in orange",
                0.01, 0.0, 1.0));

        map.put("maxIterations", new ParamDescription(
                "The maximum number of iterations the algorithm should go through orienting edges",
                15, 0, Integer.MAX_VALUE));

        map.put("numLags", new ParamDescription(
                "The number of lags in the time lag model",
                1, 1, Double.POSITIVE_INFINITY));

        map.put("saveLatentVars", new ParamDescription("Save latent variables.", false));

        map.put("probTwoCycle", new ParamDescription(
                "The probability of creating a 2-cycles in the graph (0 - 1)",
                0.0, 0.0, 1.0));

        map.put("numBasisFunctions", new ParamDescription(
                "Number of functions to use in (truncated) basis",
                30, 1, Integer.MAX_VALUE));

        map.put("kciCutoff", new ParamDescription(
                "Cutoff",
                6, 1, Integer.MAX_VALUE));

        map.put("kernelWidth", new ParamDescription(
                "Kernel width",
                1.0, Double.MIN_VALUE, Double.POSITIVE_INFINITY));

        map.put("kernelMultiplier", new ParamDescription(
                "Bowman and Azzalini (1997) default kernel bandwidhts should be multiplied by...",
                1.0, Double.MIN_VALUE, Double.POSITIVE_INFINITY));

        map.put("kernelType", new ParamDescription(
                "Kernel type (1 = Gaussian, 2 = Epinechnikov)",
                2, 1, 2));

        map.put("basisType", new ParamDescription(
                "Basis type (1 = Polynomial, 2 = Cosine)",
                2, 1, 2));

        map.put("kciNumBootstraps", new ParamDescription(
                "Number of bootstraps for Theorems 4 and Proposition 5 for KCI",
                5000, 1, Integer.MAX_VALUE));

        map.put("thresholdForNumEigenvalues", new ParamDescription(
                "Threshold to determine how many eigenvalues to use--the lower the more (0 to 1)",
                0.001, 0, Double.POSITIVE_INFINITY));

        map.put("rcitNumFeatures", new ParamDescription(
                "The number of random features to use",
                10, 1, Integer.MAX_VALUE));

        map.put("kciUseAppromation", new ParamDescription(
                "Use the approximate Gamma approximation algorithm", true));

        map.put("kciEpsilon", new ParamDescription(
                "Epsilon for Proposition 5, a small positive number", 0.001, 0, Double.POSITIVE_INFINITY));

        map.put("rcitApproxType", new ParamDescription(
                "Approximation Type: 1 = LPD4, 2 = Gamma, 3 = HBE, 4 = PERM",
                4, 1, 4));

        map.put("possibleDsepDone", new ParamDescription(
                "Yes if the possible dsep search should be done", true));

        map.put("selfLoopCoef", new ParamDescription(
                "The coefficient for the self-loop (default 0.0)", 0.0, 0.0, Double.POSITIVE_INFINITY));

        map.put("tsbetathr", new ParamDescription(
                "Threshold to determine whether a coefficient in the Beta matrix implies an edge int h graph",
                0.01, 0, Double.POSITIVE_INFINITY));

        map.put("tstheta", new ParamDescription(
                "Alasso threshold",
                1.0, 0, Double.POSITIVE_INFINITY));

        map.put("tssigma", new ParamDescription(
                "ICA threshold",
                1.0, 0, Double.POSITIVE_INFINITY));

        map.put("kernelRegressionSampleSize", new ParamDescription(
                "Minimum sample size to use per conditioning for kernel regression",
                100, 1, Double.POSITIVE_INFINITY));

        map.put("kciAlpha", new ParamDescription("Cutoff for p values (alpha) (min = 0.0)", 0.05, 0.0, 1.0));

        map.put("cciScoreAlpha", new ParamDescription("Cutoff for p values (alpha) (min = 0.0)", 0.01, 0.0, 1.0));

        map.put("numDependenceSpotChecks", new ParamDescription(
                "The number of specific <z1,...,zn> values for which to check X _||_ Y | Z = <z1,...,zn>",
                0, 0,Integer.MAX_VALUE));

        map.put("stableFAS", new ParamDescription(
                "Yes if the 'stable' FAS should be done", false));

        map.put("concurrentFAS", new ParamDescription(
                "Yes if a concurrent FAS should be done", true));

        map.put("stableFASFDR", new ParamDescription(
                "Yes if the 'stable' FAS should be done with the StableFDR adjustment", false));

        map.put("empirical", new ParamDescription(
                "Yes if skew corrections should be done (\"empirical\")", true));

        map.put("faskbDelta", new ParamDescription(
                "Threshold for judging negative coefficient edges as X->Y (range (-1, 0))",
                -0.7, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY));

        map.put("minnesotaRule", new ParamDescription(
                "Use the Minnesota rule for left-right orientation",
                false)
        );

    }

    public static ParamDescriptions getInstance() {
        return INSTANCE;
    }

    public ParamDescription get(String name) {
        ParamDescription paramDesc = map.get(name);

        return (paramDesc == null)
                ? new ParamDescription(String.format("Please add a description to ParamDescriptions for %s.", name), 0)
                : paramDesc;
    }

    public void put(String name, ParamDescription paramDescription) {
        map.put(name, paramDescription);
    }

    public Set<String> getNames() {
        return map.keySet();
    }

}
