///////////////////////////////////////////////////////////////////////////////
// For information as to what this class does, see the Javadoc, below.       //
// Copyright (C) 1998, 1999, 2000, 2001, 2002, 2003, 2004, 2005, 2006,       //
// 2007, 2008, 2009, 2010, 2014, 2015 by Peter Spirtes, Richard Scheines, Joseph   //
// Ramsey, and Clark Glymour.                                                //
//                                                                           //
// This program is free software; you can redistribute it and/or modify      //
// it under the terms of the GNU General Public License as published by      //
// the Free Software Foundation; either version 2 of the License, or         //
// (at your option) any later version.                                       //
//                                                                           //
// This program is distributed in the hope that it will be useful,           //
// but WITHOUT ANY WARRANTY; without even the implied warranty of            //
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the             //
// GNU General Public License for more details.                              //
//                                                                           //
// You should have received a copy of the GNU General Public License         //
// along with this program; if not, write to the Free Software               //
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA //
///////////////////////////////////////////////////////////////////////////////

package edu.cmu.tetrad.graph;

import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

/**
 * <p>Stores a graph a list of lists of edges adjacent to each node in the
 * graph, with an additional list storing all of the edges in the graph. The
 * edges are of the form N1 *-# N2. Multiple edges may be added per node pair to
 * this graph, with the caveat that all edges of the form N1 *-# N2 will be
 * considered equal. For randomUtil, if the edge X --> Y is added to the graph,
 * another edge X --> Y may not be added, although an edge Y --> X may be added.
 * Edges from nodes to themselves may also be added.</p>
 *
 * @author Joseph Ramsey
 * @author Erin Korber additions summer 2004
 * @see Endpoint
 */
public class EndpointMatrixGraph implements Graph {
    static final long serialVersionUID = 23L;

    private short[][] graphMatrix = new short[0][0];

    /**
     * A list of the nodes in the graph, in the order in which they were added.
     *
     * @serial
     */
    private List<Node> nodes;

    /**
     * Set of ambiguous triples. Note the name can't be changed due to
     * serialization.
     */
    private Set<Triple> ambiguousTriples = new HashSet<>();

    /**
     * @serial
     */
    private Set<Triple> underLineTriples = new HashSet<>();

    /**
     * @serial
     */
    private Set<Triple> dottedUnderLineTriples = new HashSet<>();

    /**
     * True iff nodes were removed since the last call to an accessor for ambiguous, underline, or dotted underline
     * triples. If there are triples in the lists involving removed nodes, these need to be removed from the lists
     * first, so as not to cause confusion.
     */
    private boolean stuffRemovedSinceLastTripleAccess = false;

    /**
     * The set of highlighted edges.
     */
    private Set<Edge> highlightedEdges = new HashSet<>();

    /**
     * A hash from node names to nodes;
     */
    private Map<String, Node> namesHash = new HashMap<>();
    private HashMap<Node, Integer> nodesHash;
    private HashMap<Short, Endpoint> shortsToEndpoints;
    private HashMap<Endpoint, Short> endpointsToShorts;
    private int numEdges = 0;

    private boolean pag;
    private boolean pattern;
    
    private Map<String,Object> attributes = new HashMap<>();

    //==============================CONSTUCTORS===========================//

    /**
     * Constructs a new (empty) EdgeListGraph.
     */
    public EndpointMatrixGraph() {
        this.nodes = new ArrayList<>();
    }

    /**
     * Constructs a EdgeListGraph using the nodes and edges of the given graph.
     * If this cannot be accomplished successfully, an exception is thrown. Note
     * that any graph constraints from the given graph are forgotten in the new
     * graph.
     *
     * @param graph the graph from which nodes and edges are is to be
     *              extracted.
     * @throws IllegalArgumentException if a duplicate edge is added.
     */
    public EndpointMatrixGraph(Graph graph) throws IllegalArgumentException {
        this();

        if (graph == null) {
            throw new NullPointerException("Graph must not be null.");
        }

        transferNodesAndEdges(graph);
        this.ambiguousTriples = graph.getAmbiguousTriples();
        this.underLineTriples = graph.getUnderLines();
        this.dottedUnderLineTriples = graph.getDottedUnderlines();


        for (Edge edge : graph.getEdges()) {
            if (graph.isHighlighted(edge)) {
                setHighlighted(edge, true);
            }
        }

        for (Node node : nodes) {
            namesHash.put(node.getName(), node);
        }

        initHashes();
    }

    /**
     * Constructs a new graph, with no edges, using the the given variable
     * names.
     */
    private EndpointMatrixGraph(List<Node> nodes) {
        this();

        if (nodes == null) {
            throw new NullPointerException();
        }

        for (Object variable : nodes) {
            if (!addNode((Node) variable)) {
                throw new IllegalArgumentException();
            }
        }

        this.graphMatrix = new short[nodes.size()][nodes.size()];

        for (Node node : nodes) {
            namesHash.put(node.getName(), node);
        }

        initHashes();
    }

    // Makes a copy with the same object identical edges in it. If you make changes to those edges they will be
    // reflected here.
    public static Graph shallowCopy(EndpointMatrixGraph graph) {
        EndpointMatrixGraph _graph = new EndpointMatrixGraph();

        _graph.graphMatrix = copy(graph.graphMatrix);
        _graph.nodes = new ArrayList<>(graph.nodes);
        _graph.ambiguousTriples = new HashSet<>(graph.ambiguousTriples);
        _graph.underLineTriples = new HashSet<>(graph.underLineTriples);
        _graph.dottedUnderLineTriples = new HashSet<>(graph.dottedUnderLineTriples);
        _graph.stuffRemovedSinceLastTripleAccess = graph.stuffRemovedSinceLastTripleAccess;
        _graph.highlightedEdges = new HashSet<>(graph.highlightedEdges);
        _graph.namesHash = new HashMap<>(graph.namesHash);
        return _graph;
    }

    private static short[][] copy(short[][] graphMatrix) {
        short[][] copy = new short[graphMatrix.length][graphMatrix[0].length];

        if (copy.length == 0) {
            return new short[0][0];
        }

        for (int i = 0; i < copy.length; i++) {
            System.arraycopy(graphMatrix[i], 0, copy[i], 0, copy[0].length);
        }

        return copy;
    }

    private void initHashes() {
        nodesHash = new HashMap<>();

        for (Node node : nodes) {
            nodesHash.put(node, nodes.indexOf(node));
        }

        endpointsToShorts = new HashMap<>();

        endpointsToShorts.put(Endpoint.TAIL, (short) 1);
        endpointsToShorts.put(Endpoint.ARROW, (short) 2);
        endpointsToShorts.put(Endpoint.CIRCLE, (short) 3);

        shortsToEndpoints = new HashMap<>();

        shortsToEndpoints.put((short) 1, Endpoint.TAIL);
        shortsToEndpoints.put((short) 2, Endpoint.ARROW);
        shortsToEndpoints.put((short) 3, Endpoint.CIRCLE);
    }

    /**
     * Generates a simple exemplar of this class to test serialization.
     */
    public static EndpointMatrixGraph serializableInstance() {
        return new EndpointMatrixGraph();
    }

    //===============================PUBLIC METHODS========================//

    /**
     * Adds a directed edge to the graph from node A to node B.
     *
     * @param node1 the "from" node.
     * @param node2 the "to" node.
     */
    public boolean addDirectedEdge(Node node1, Node node2) {
        int i = nodesHash.get(node1);
        int j = nodesHash.get(node2);

        if (graphMatrix[i][j] != 0) {
            return false;
        }

        graphMatrix[j][i] = 1;
        graphMatrix[i][j] = 2;

        numEdges++;

        return true;
    }

    /**
     * Adds an undirected edge to the graph from node A to node B.
     *
     * @param node1 the "from" node.
     * @param node2 the "to" node.
     */
    public boolean addUndirectedEdge(Node node1, Node node2) {
        return addEdge(Edges.undirectedEdge(node1, node2));
    }

    /**
     * Adds a nondirected edge to the graph from node A to node B.
     *
     * @param node1 the "from" node.
     * @param node2 the "to" node.
     */
    public boolean addNondirectedEdge(Node node1, Node node2) {
        return addEdge(Edges.nondirectedEdge(node1, node2));
    }

    /**
     * Adds a partially oriented edge to the graph from node A to node B.
     *
     * @param node1 the "from" node.
     * @param node2 the "to" node.
     */
    public boolean addPartiallyOrientedEdge(Node node1, Node node2) {
        return addEdge(Edges.partiallyOrientedEdge(node1, node2));
    }

    /**
     * Adds a bidirected edge to the graph from node A to node B.
     *
     * @param node1 the "from" node.
     * @param node2 the "to" node.
     */
    public boolean addBidirectedEdge(Node node1, Node node2) {
        return addEdge(Edges.bidirectedEdge(node1, node2));
    }

    public boolean existsDirectedCycle() {
        for (Node node : getNodes()) {
            if (existsDirectedPathFromTo(node, node)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDirectedFromTo(Node node1, Node node2) {
        List<Edge> edges = getEdges(node1, node2);
        if (edges.size() != 1) return false;
        Edge edge = edges.get(0);
        return edge.pointsTowards(node2);
    }

    public boolean isUndirectedFromTo(Node node1, Node node2) {
        Edge edge = getEdge(node1, node2);

        return edge != null && edge.getEndpoint1() == Endpoint.TAIL && edge.getEndpoint2() == Endpoint.TAIL;

        //        return getEdges(node1, node2).size() == 1
//                && getEndpoint(node2, node1) == Endpoint.TAIL
//                && getEndpoint(node1, node2) == Endpoint.TAIL;
    }

    /**
     * added by ekorber, 2004/06/11
     *
     * @return true if the given edge is definitely visible (Jiji, pg 25)
     * @throws IllegalArgumentException if the given edge is not a directed edge
     *                                  in the graph
     */
    public boolean defVisible(Edge edge) {
        if (containsEdge(edge)) {

            Node A = Edges.getDirectedEdgeTail(edge);
            Node B = Edges.getDirectedEdgeHead(edge);
            List<Node> adjToA = getAdjacentNodes(A);

            while (!adjToA.isEmpty()) {
                Node Curr = adjToA.remove(0);
                if (!((getAdjacentNodes(Curr)).contains(B)) &&
                        ((getEdge(Curr, A)).getProximalEndpoint(A) == Endpoint
                                .ARROW)) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException(
                    "Given edge is not in the graph.");
        }
    }

    /**
     * IllegalArgument exception raised (by isDirectedFromTo(getEndpoint) or by
     * getEdge) if there are multiple edges between any of the node pairs.
     */
    public boolean isDefNoncollider(Node node1, Node node2, Node node3) {
        List<Edge> edges = getEdges(node2);
        boolean circle12 = false;
        boolean circle32 = false;

        for (Edge edge : edges) {
            boolean _node1 = edge.getDistalNode(node2) == node1;
            boolean _node3 = edge.getDistalNode(node2) == node3;

            if (_node1 && edge.pointsTowards(node1)) return true;
            if (_node3 && edge.pointsTowards(node3)) return true;

            if (_node1 && edge.getProximalEndpoint(node2) == Endpoint.CIRCLE) circle12 = true;
            if (_node3 && edge.getProximalEndpoint(node2) == Endpoint.CIRCLE) circle32 = true;
            if (circle12 && circle32 && !isAdjacentTo(node1, node2)) return true;
        }

        return false;

//        if (isDirectedFromTo(node2, node1) || isDirectedFromTo(node2, node3)) {
//            return true;
//        } else if (!isAdjacentTo(node1, node3)) {
//            boolean endpt1 = getEndpoint(node1, node2) == Endpoint.CIRCLE;
//            boolean endpt2 = getEndpoint(node3, node2) == Endpoint.CIRCLE;
//            return (endpt1 && endpt2);
////        } else if (getEndpoint(node1, node2) == Endpoint.TAIL && getEndpoint(node3, node2) == Endpoint.TAIL){
////            return true;
//        } else {
//            return false;
//        }
    }

    public boolean isDefCollider(Node node1, Node node2, Node node3) {
        Edge edge1 = getEdge(node1, node2);
        Edge edge2 = getEdge(node2, node3);

        if (edge1 == null) {
            throw new NullPointerException();
        }

        if (edge2 == null) {
            throw new NullPointerException();
        }

        return edge1.getProximalEndpoint(node2) == Endpoint.ARROW &&
                edge2.getProximalEndpoint(node2) == Endpoint.ARROW;
    }

    /**
     * @return true iff there is a directed path from node1 to node2.
     * a
     */
    public boolean existsDirectedPathFromTo(Node node1, Node node2) {
        return existsDirectedPathVisit(node1, node2, new LinkedList<Node>());
    }

    public boolean existsUndirectedPathFromTo(Node node1, Node node2) {
        return existsUndirectedPathVisit(node1, node2, new LinkedList<Node>());
    }

    public boolean existsSemiDirectedPathFromTo(Node node1, Set<Node> nodes) {
        return existsSemiDirectedPathVisit(node1, nodes,
                new LinkedList<Node>());
    }

    /**
     * Determines whether a trek exists between two nodes in the graph.  A trek
     * exists if there is a directed path between the two nodes or else, for
     * some third node in the graph, there is a path to each of the two nodes in
     * question.
     */
    public boolean existsTrek(Node node1, Node node2) {

        for (Node node3 : getNodes()) {
            Node node = (node3);

            if (isAncestorOf(node, node1) && isAncestorOf(node, node2)) {
                return true;
            }

        }

        return false;
    }

    /**
     * @return the list of children for a node.
     */
    public List<Node> getChildren(Node node) {
        int i = nodesHash.get(node);
        List<Node> children = new ArrayList<>();

        for (int j = 0; j < nodes.size(); j++) {
            int m1 = graphMatrix[j][i];
            int m2 = graphMatrix[i][j];
            if (m1 == 1 && m2 == 2) {
                children.add(nodes.get(j));
            }
        }

        return children;
    }

    public int getConnectivity() {
        int connectivity = 0;

        List<Node> nodes = getNodes();

        for (Node node : nodes) {
            int n = getNumEdges(node);
            if (n > connectivity) {
                connectivity = n;
            }
        }

        return connectivity;
    }

    public List<Node> getDescendants(List<Node> nodes) {
        HashSet<Node> descendants = new HashSet<>();

        for (Object node1 : nodes) {
            Node node = (Node) node1;
            collectDescendantsVisit(node, descendants);
        }

        return new LinkedList<>(descendants);
    }

    /**
     * @return the edge connecting node1 and node2, provided a unique such edge
     * exists.
     */
    public Edge getEdge(Node node1, Node node2) {
        int i = nodesHash.get(node1);
        int j = nodesHash.get(node2);

        Endpoint e1 = shortsToEndpoints.get(graphMatrix[j][i]);
        Endpoint e2 = shortsToEndpoints.get(graphMatrix[i][j]);

        if (e1 != null) {
            return new Edge(node1, node2, e1, e2);
        } else {
            return null;
        }
    }

    public Edge getDirectedEdge(Node node1, Node node2) {
        List<Edge> edges = getEdges(node1, node2);

        if (edges == null) return null;

        if (edges.size() == 0) {
            return null;
        }

        for (Edge edge : edges) {
            if (Edges.isDirectedEdge(edge) && edge.getProximalEndpoint(node2) == Endpoint.ARROW) {
                return edge;
            }
        }

        return null;
    }

    /**
     * @return the list of parents for a node.
     */
    public List<Node> getParents(Node node) {
        int j = nodesHash.get(node);
        List<Node> parents = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            int m1 = graphMatrix[j][i];
            int m2 = graphMatrix[i][j];
            if (m1 == 1 && m2 == 2) {
                parents.add(nodes.get(i));
            }
        }

        return parents;
    }

    /**
     * @return the number of edges into the given node.
     */
    public int getIndegree(Node node) {
        return getParents(node).size();
    }

    @Override
    public int getDegree(Node node) {
        return 0;
    }

    /**
     * @return the number of edges out of the given node.
     */
    public int getOutdegree(Node node) {
        return getChildren(node).size();
    }

    /**
     * Determines whether some edge or other exists between two nodes.
     */
    public boolean isAdjacentTo(Node node1, Node node2) {
        int i = nodesHash.get(node1);
        int j = nodesHash.get(node2);

        return graphMatrix[i][j] != 0;
    }

    /**
     * Determines whether one node is an ancestor of another.
     */
    public boolean isAncestorOf(Node node1, Node node2) {
        return (node1 == node2) || isProperAncestorOf(node1, node2);
    }

    public boolean possibleAncestor(Node node1, Node node2) {
        return existsSemiDirectedPathFromTo(node1,
                Collections.singleton(node2));
    }

    /**
     * @return true iff node1 is a possible ancestor of at least one member of
     * nodes2
     */
    private boolean possibleAncestorSet(Node node1, List<Node> nodes2) {
        for (Object aNodes2 : nodes2) {
            if (possibleAncestor(node1, (Node) aNodes2)) {
                return true;
            }
        }
        return false;
    }

    public List<Node> getAncestors(List<Node> nodes) {
        HashSet<Node> ancestors = new HashSet<>();

        for (Object node1 : nodes) {
            Node node = (Node) node1;
            collectAncestorsVisit(node, ancestors);
        }

        return new ArrayList<>(ancestors);
    }

    /**
     * Determines whether one node is a child of another.
     */
    public boolean isChildOf(Node node1, Node node2) {
        for (Object o : getEdges(node2)) {
            Edge edge = (Edge) (o);
            Node sub = Edges.traverseDirected(node2, edge);

            if (sub == node1) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines whether one node is a descendent of another.
     */
    public boolean isDescendentOf(Node node1, Node node2) {
        return (node1 == node2) || isProperDescendentOf(node1, node2);
    }

    /**
     * added by ekorber, 2004/06/12
     *
     * @return true iff node2 is a definite nondecendent of node1
     */
    public boolean defNonDescendent(Node node1, Node node2) {
        return !(possibleAncestor(node1, node2));
    }

    // Assume acyclicity.
    public boolean isDConnectedTo(Node x, Node y, List<Node> z) {
        Set<Node> zAncestors = zAncestors2(z);

        Queue<Pair> Q = new ArrayDeque<>();
        Set<Pair> V = new HashSet<>();

        for (Node node : getAdjacentNodes(x)) {
            if (node == y) return true;
            Pair edge = new Pair(x, node);
            Q.offer(edge);
            V.add(edge);
        }

        while (!Q.isEmpty()) {
            Pair t = Q.poll();

            Node b = t.getY();
            Node a = t.getX();

            for (Node c : getAdjacentNodes(b)) {
                if (c == a) continue;

                boolean collider = isDefCollider(a, b, c);
                if (!((collider && zAncestors.contains(b)) || (!collider && !z.contains(b)))) continue;

                if (c == y) return true;

                Pair u = new Pair(b, c);
                if (V.contains(u)) continue;

                V.add(u);
                Q.offer(u);
            }
        }

        return false;
    }

    private boolean isDConnectedTo(List<Node> x, List<Node> y, List<Node> z) {
        Set<Node> zAncestors = zAncestors2(z);

        Queue<Pair> Q = new ArrayDeque<>();
        Set<Pair> V = new HashSet<>();

        for (Node _x : x) {
            for (Node node : getAdjacentNodes(_x)) {
//                if (node == y) return true;
                if (y.contains(node)) return true;
                Pair edge = new Pair(_x, node);
//                System.out.println("Edge " + edge);
                Q.offer(edge);
                V.add(edge);
            }
        }

        while (!Q.isEmpty()) {
            Pair t = Q.poll();

            Node b = t.getY();
            Node a = t.getX();

            for (Node c : getAdjacentNodes(b)) {
                if (c == a) continue;

                boolean collider = isDefCollider(a, b, c);
                if (!((collider && zAncestors.contains(b)) || (!collider && !z.contains(b)))) continue;

//                if (c == y) return true;
                if (y.contains(c)) return true;

                Pair u = new Pair(b, c);
                if (V.contains(u)) continue;

//                System.out.println("u = " + u);

                V.add(u);
                Q.offer(u);
            }
        }

        return false;
    }

    public boolean isDSeparatedFrom(List<Node> x, List<Node> y, List<Node> z) {
        return !isDConnectedTo(x, y, z);
    }

    @Override
    public List<String> getTriplesClassificationTypes() {
        return null;
    }

    @Override
    public List<List<Triple>> getTriplesLists(Node node) {
        return null;
    }

    @Override
    public boolean isPag() {
        return pag;
    }

    @Override
    public void setPag(boolean pag) {
        this.pag = pag;
    }

    @Override
    public boolean isPattern() {
        return pattern;
    }

    @Override
    public void setPattern(boolean pattern) {
        this.pattern = pattern;
    }

    private static class Pair {
        private Node x;
        private Node y;

        public Pair(Node x, Node y) {
            this.x = x;
            this.y = y;
        }

        public Node getX() {
            return x;
        }

        public Node getY() {
            return y;
        }

        public int hashCode() {
            return x.hashCode() + 17 * y.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) return true;
            if (!(o instanceof Pair)) return false;
            Pair pair = (Pair) o;
            return x == pair.getX() && y == pair.getY();
        }

        public String toString() {
            return "(" + x.toString() + ", " + y.toString() + ")";
        }
    }

    private Set<Node> zAncestors2(List<Node> z) {
        Queue<Node> Q = new ArrayDeque<>();
        Set<Node> V = new HashSet<>();

        for (Node node : z) {
            Q.offer(node);
            V.add(node);
        }

        while (!Q.isEmpty()) {
            Node t = Q.poll();

            for (Node c : getParents(t)) {
                if (V.contains(c)) continue;
                V.add(c);
                Q.offer(c);
            }
        }

        return V;
    }

    /**
     * Determines whether one n ode is d-separated from another. According to
     * Spirtes, Richardson & Meek, two nodes are d- connected given some
     * conditioning set Z if there is an acyclic undirected path U between them,
     * such that every collider on U is an ancestor of some element in Z and
     * every non-collider on U is not in Z.  Two elements are d-separated just
     * in case they are not d-connected.  A collider is a node which two edges
     * hold in common for which the endpoints leading into the node are both
     * arrow endpoints.
     *
     * @param node1 the first node.
     * @param node2 the second node.
     * @param z     the conditioning set.
     * @return true if node1 is d-separated from node2 given set t, false if
     * not.
     * @see #isDConnectedTo
     */

    public boolean isDSeparatedFrom(Node node1, Node node2, List<Node> z) {
        return !isDConnectedTo(node1, node2, z);
    }

    //added by ekorber, June 2004
    public boolean possDConnectedTo(Node node1, Node node2,
                                    List<Node> condNodes) {
        LinkedList<Node> allNodes = new LinkedList<>(getNodes());
        int sz = allNodes.size();
        int[][] edgeStage = new int[sz][sz];
        int stage = 1;

        int n1x = allNodes.indexOf(node1);
        int n2x = allNodes.indexOf(node2);

        edgeStage[n1x][n1x] = 1;
        edgeStage[n2x][n2x] = 1;

        List<int[]> currEdges;
        List<int[]> nextEdges = new LinkedList<>();

        int[] temp1 = new int[2];
        temp1[0] = n1x;
        temp1[1] = n1x;
        nextEdges.add(temp1);

        int[] temp2 = new int[2];
        temp2[0] = n2x;
        temp2[1] = n2x;
        nextEdges.add(temp2);

        while (true) {
            currEdges = nextEdges;
            nextEdges = new LinkedList<>();
            for (int[] edge : currEdges) {
                Node center = allNodes.get(edge[1]);
                List<Node> adj = new LinkedList<>(getAdjacentNodes(center));

                for (Node anAdj : adj) {
                    // check if we've hit this edge before
                    int testIndex = allNodes.indexOf(anAdj);
                    if (edgeStage[edge[1]][testIndex] != 0) {
                        continue;
                    }

                    // if the edge pair violates possible d-connection,
                    // then go to the next adjacent node.

                    Node X = allNodes.get(edge[0]);
                    Node Y = allNodes.get(edge[1]);
                    Node Z = allNodes.get(testIndex);

                    if (!((isDefNoncollider(X, Y, Z) &&
                            !(condNodes.contains(Y))) || (
                            isDefCollider(X, Y, Z) &&
                                    possibleAncestorSet(Y, condNodes)))) {
                        continue;
                    }

                    // if it gets here, then it's legal, so:
                    // (i) if this is the one we want, we're done
                    if (anAdj.equals(node2)) {
                        return true;
                    }

                    // (ii) if we need to keep going,
                    // add the edge to the nextEdges list
                    int[] nextEdge = new int[2];
                    nextEdge[0] = edge[1];
                    nextEdge[1] = testIndex;
                    nextEdges.add(nextEdge);

                    // (iii) set the edgeStage array
                    edgeStage[edge[1]][testIndex] = stage;
                    edgeStage[testIndex][edge[1]] = stage;
                }
            }

            // find out if there's any reason to move to the next stage
            if (nextEdges.size() == 0) {
                break;
            }

            stage++;
        }

        return false;
    }


    /**
     * Determines whether an inducing path exists between node1 and node2, given
     * a set O of observed nodes and a set sem of conditioned nodes.
     *
     * @param node1 the first node.
     * @param node2 the second node.
     * @return true if an inducing path exists, false if not.
     */
    public boolean existsInducingPath(Node node1, Node node2) {
        return GraphUtils.existsInducingPath(node1, node2, this);
    }

    /**
     * Determines whether one node is a parent of another.
     *
     * @param node1 the first node.
     * @param node2 the second node.
     * @return true if node1 is a parent of node2, false if not.
     * @see #isChildOf
     * @see #getParents
     * @see #getChildren
     */
    public boolean isParentOf(Node node1, Node node2) {
        for (Edge edge1 : getEdges(node1)) {
            Edge edge = (edge1);
            Node sub = Edges.traverseDirected(node1, edge);

            if (sub == node2) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether one node is a proper ancestor of another.
     */
    public boolean isProperAncestorOf(Node node1, Node node2) {
        return existsDirectedPathFromTo(node1, node2);
    }

    /**
     * Determines whether one node is a proper decendent of another
     */
    public boolean isProperDescendentOf(Node node1, Node node2) {
        return existsDirectedPathFromTo(node2, node1);
    }

    /**
     * Transfers nodes and edges from one graph to another.  One way this is
     * used is to change graph types.  One constructs a new graph based on the
     * old graph, and this method is called to transfer the nodes and edges of
     * the old graph to the new graph.
     *
     * @param graph the graph from which nodes and edges are to be pilfered.
     * @throws IllegalArgumentException This exception is thrown if adding some
     *                                  node or edge violates one of the
     *                                  basicConstraints of this graph.
     */
    public void transferNodesAndEdges(Graph graph)
            throws IllegalArgumentException {
        if (graph == null) {
            throw new NullPointerException("No graph was provided.");
        }

//        System.out.println("TANSFER BEFORE " + graph.getEdges());

        for (Node node : graph.getNodes()) {
        	
        	node.getAllAttributes().clear();
            
        	if (!addNode(node)) {
                throw new IllegalArgumentException();
            }
        }

        for (Edge edge : graph.getEdges()) {
            if (!addEdge(edge)) {
                throw new IllegalArgumentException();
            }
        }

//        System.out.println("TANSFER AFTER " + getEdges());
    }
    
    public void transferAttributes(Graph graph)
    		throws IllegalArgumentException {
        if (graph == null) {
            throw new NullPointerException("No graph was provided.");
        }
        attributes.putAll(graph.getAllAttributes());
    }

    /**
     * Determines whether a node in a graph is exogenous.
     */
    public boolean isExogenous(Node node) {
        return getIndegree(node) == 0;
    }

    /**
     * @return the set of nodes adjacent to the given node. If there are multiple edges between X and Y, Y will show
     * up twice in the list of adjacencies for X, for optimality; simply create a list an and array from these to
     * eliminate the duplication.
     */
    public List<Node> getAdjacentNodes(Node node) {
        int j = nodesHash.get(node);
        List<Node> adj = new ArrayList<>();

        for (int i = 0; i < nodes.size(); i++) {
            if (graphMatrix[i][j] != (short) 0) {
                adj.add(nodes.get(i));
            }
        }

        return adj;
    }

    /**
     * Removes the edge connecting the two given nodes.
     */
    public boolean removeEdge(Node node1, Node node2) {
        List<Edge> edges = getEdges(node1, node2);

        if (edges.size() > 1) {
            throw new IllegalStateException(
                    "There is more than one edge between " + node1 + " and " +
                            node2);
        }

        numEdges--;

        return removeEdges(edges);
    }

    /**
     * @return the endpoint along the edge from node to node2 at the node2 end.
     */
    public Endpoint getEndpoint(Node node1, Node node2) {
        List<Edge> edges = getEdges(node2);

        for (Edge edge : edges) {
            if (edge.getDistalNode(node2) == node1) return edge.getProximalEndpoint(node2);
        }

        return null;


//        List<Edge> edges = getEdges(node1, node2);
//
//        if (edges.size() == 0) {
//            retu rn null;
//        }
//
//        if (edges.size() > 1) {
//            throw new IllegalArgumentException(
//                    "More than one edge between " + node1 + " and " + node2);
//        }
//
//        return (edges.get(0)).getProximalEndpoint(node2);
    }

    /**
     * If there is currently an edge from node1 to node2, sets the endpoint at
     * node2 to the given endpoint; if there is no such edge, adds an edge --#
     * where # is the given endpoint. Setting an endpoint to null, provided
     * there is exactly one edge connecting the given nodes, removes the edge.
     * (If there is more than one edge, an exception is thrown.)
     *
     * @throws IllegalArgumentException if the edge with the revised endpoint
     *                                  cannot be added to the graph.
     */
    public boolean setEndpoint(Node from, Node to, Endpoint endPoint)
            throws IllegalArgumentException {
        List<Edge> edges = getEdges(from, to);

        if (endPoint == null) {
            throw new NullPointerException();
        } else if (edges.size() == 0) {
//            removeEdge(from, to);
            addEdge(new Edge(from, to, Endpoint.TAIL, endPoint));
            return true;
        } else if (edges.size() == 1) {
            Edge edge = edges.get(0);
            Edge newEdge = new Edge(from, to, edge.getProximalEndpoint(from), endPoint);

            try {
                removeEdge(edge);
                addEdge(newEdge);
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else {
            throw new NullPointerException(
                    "An endpoint between node1 and node2 " +
                            "may not be set in this graph if there is more than one " +
                            "edge between node1 and node2.");
        }
    }

    /**
     * Nodes adjacent to the given node with the given proximal endpoint.
     */
    public List<Node> getNodesInTo(Node node, Endpoint endpoint) {
        List<Node> nodes = new ArrayList<>(4);
        List<Edge> edges = getEdges(node);

        for (Object edge1 : edges) {
            Edge edge = (Edge) edge1;

            if (edge.getProximalEndpoint(node) == endpoint) {
                nodes.add(edge.getDistalNode(node));
            }
        }

        return nodes;
    }

    /**
     * Nodes adjacent to the given node with the given distal endpoint.
     */
    public List<Node> getNodesOutTo(Node node, Endpoint endpoint) {
        List<Node> nodes = new ArrayList<>(4);
        List<Edge> edges = getEdges(node);

        for (Object edge1 : edges) {
            Edge edge = (Edge) edge1;

            if (edge.getDistalEndpoint(node) == endpoint) {
                nodes.add(edge.getDistalNode(node));
            }
        }

        return nodes;
    }

    /**
     * @return a matrix of endpoints for the nodes in this graph, with nodes in
     * the same order as getNodes().
     */
    public Endpoint[][] getEndpointMatrix() {
        int size = nodes.size();
        Endpoint[][] endpoints = new Endpoint[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (i == j) {
                    continue;
                }

                Node nodei = nodes.get(i);
                Node nodej = nodes.get(j);

                endpoints[i][j] = getEndpoint(nodei, nodej);
            }
        }

        return endpoints;
    }

    /**
     * Adds an edge to the graph if the grpah constraints permit it.
     *
     * @param edge the edge to be added
     * @return true if the edge was added, false if not.
     */
    public boolean addEdge(Edge edge) {
        int i = nodesHash.get(edge.getNode1());
        int j = nodesHash.get(edge.getNode2());

        if (graphMatrix[i][j] != 0) {
            return false;
        }

        short e1 = endpointsToShorts.get(edge.getEndpoint1());
        short e2 = endpointsToShorts.get(edge.getEndpoint2());

        graphMatrix[j][i] = e1;
        graphMatrix[i][j] = e2;

        numEdges++;

        return true;
    }

    /**
     * Throws unsupported operation exception.
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a node to the graph. Precondition: The proposed name of the node
     * cannot already be used by any other node in the same graph.
     *
     * @param node the node to be added.
     * @return true if the the node was added, false if not.
     */
    public boolean addNode(Node node) {
        if (node == null) {
            throw new NullPointerException();
        }

        if (!(getNode(node.getName()) == null)) {
            return false;

            // This is problematic for the sem updater. jdramsey 7/23/2005
//            throw new IllegalArgumentException("A node by name " +
//                    node.getNode() + " has already been added to the graph.");
        }

        if (nodes.contains(node)) {
            return false;
        }

        List<Node> _nodes = new ArrayList<>();
        nodes.add(node);
        namesHash.put(node.getName(), node);

        reconstituteGraphMatrix(_nodes, nodes);

        initHashes();

        return true;
    }

    private void reconstituteGraphMatrix(List<Node> nodes, List<Node> nodes1) {
        short[][] newGraphMatrix = new short[nodes1.size()][nodes1.size()];

        for (int i = 0; i < nodes1.size(); i++) {
            for (int j = 0; j < nodes1.size(); j++) {
                int i1 = nodes.indexOf(nodes1.get(i));
                int j1 = nodes.indexOf(nodes1.get(i));

                if (i1 != -1 && j1 != -1)
                    newGraphMatrix[i][j] = graphMatrix[i1][j1];
            }
        }

        this.graphMatrix = newGraphMatrix;
    }

    /**
     * @return the list of edges in the graph.  No particular ordering of the
     * edges in the list is guaranteed.
     */
    public Set<Edge> getEdges() {
        HashSet<Edge> edges = new HashSet<>();

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = i + 1; j < nodes.size(); j++) {
                final Edge edge = getEdge(nodes.get(i), nodes.get(j));

                if (edge != null) {
                    edges.add(edge);
                }
            }
        }

        return edges;
    }

    /**
     * Determines if the graph contains a particular edge.
     */
    public boolean containsEdge(Edge edge) {
        int i = nodesHash.get(edge.getNode1());
        int j = nodesHash.get(edge.getNode2());

        return graphMatrix[i][j] != 0;
    }

    /**
     * Determines whether the graph contains a particular node.
     */
    public boolean containsNode(Node node) {
        return nodes.contains(node);
    }

    /**
     * @return the list of edges connected to a particular node. No particular
     * ordering of the edges in the list is guaranteed.
     */
    public List<Edge> getEdges(Node node) {
        List<Node> adj = getAdjacentNodes(node);

        List<Edge> edges = new ArrayList<>();

        for (Node _node : adj) {
            edges.add(getEdge(node, _node));
        }

        return edges;
    }

    public int hashCode() {
        int hashCode = 0;
        int sum = 0;

        for (Node node : getNodes()) {
            sum += node.hashCode();
        }

        hashCode += 23 * sum;
        sum = 0;

        for (Edge edge : getEdges()) {
            sum += edge.hashCode();
        }

        hashCode += 41 * sum;

        return hashCode;
    }

    /**
     * @return true iff the given object is a graph that is equal to this graph,
     * in the sense that it contains the same nodes and the edges are
     * isomorphic.
     */
    public boolean equals(Object o) {
        if (!(o instanceof EndpointMatrixGraph)) {
            return false;
        }

        EndpointMatrixGraph graph = (EndpointMatrixGraph) o;

        if (!graph.nodes.equals(this.nodes)) return false;

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (graph.graphMatrix[i][j] != this.graphMatrix[i][j]) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Resets the graph so that it is fully connects it using #-# edges, where #
     * is the given endpoint.
     */
    public void fullyConnect(Endpoint endpoint) {
        short s = endpointsToShorts.get(endpoint);

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                graphMatrix[i][j] = s;
            }
        }
    }

    public void reorientAllWith(Endpoint endpoint) {
        short s = endpointsToShorts.get(endpoint);

        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                if (i == j) continue;
                if (graphMatrix[i][j] != 0) {
                    graphMatrix[i][j] = s;
                }
            }
        }
    }

    /**
     * @return the node with the given name, or null if no such node exists.
     */
    public Node getNode(String name) {
        Node node = namesHash.get(name);

        if (node == null /*|| !name.equals(node.getNode())*/) {
            namesHash = new HashMap<>();

            for (Node _node : nodes) {
                namesHash.put(_node.getName(), _node);
            }

            node = namesHash.get(name);
        }

        return node;

//        for (Node node : nodes) {
//            if (node.getNode().equals(name)) {
//                return node;
//            }
//        }
//
//        return namesHash.get(name);

//        return null;
    }

    /**
     * @return the number of nodes in the graph.
     */
    public int getNumNodes() {
        return nodes.size();
    }

    /**
     * @return the number of edges in the (entire) graph.
     */
    public int getNumEdges() {
        return numEdges;
    }

    /**
     * @return the number of edges connected to a particular node in the graph.
     */
    public int getNumEdges(Node node) {
        return getEdges(node).size();
    }

    public List<Node> getNodes() {
        return new ArrayList<>(nodes);
    }

    /**
     * Removes all nodes (and therefore all edges) from the graph.
     */
    public void clear() {
        for (int i = 0; i < nodes.size(); i++) {
            for (int j = 0; j < nodes.size(); j++) {
                graphMatrix[i][j] = 0;
            }
        }
    }

    /**
     * Removes an edge from the graph. (Note: It is dangerous to make a
     * recursive call to this method (as it stands) from a method containing
     * certain types of iterators. The problem is that if one uses an iterator
     * that iterates over the edges of node A or node B, and tries in the
     * process to remove those edges using this method, a concurrent
     * modification exception will be thrown.)
     *
     * @param edge the edge to remove.
     * @return true if the edge was removed, false if not.
     */
    public boolean removeEdge(Edge edge) {
        int i = nodesHash.get(edge.getNode1());
        int j = nodesHash.get(edge.getNode2());

        graphMatrix[i][j] = 0;
        graphMatrix[j][i] = 0;

        return true;
    }

    /**
     * Removes any relevant edge objects found in this collection. G
     *
     * @param edges the collection of edges to remove.
     * @return true if any edges in the collection were removed, false if not.
     */
    public boolean removeEdges(Collection<Edge> edges) {
        boolean change = false;

        for (Edge edge : edges) {
            boolean _change = removeEdge(edge);
            change = change || _change;
        }

        return change;
    }

    /**
     * Removes all edges connecting node A to node B.
     *
     * @param node1 the first node.,
     * @param node2 the second node.
     * @return true if edges were removed between A and B, false if not.
     */
    public boolean removeEdges(Node node1, Node node2) {
        return removeEdges(getEdges(node1, node2));
    }

    /**
     * Removes a node from the graph.
     */
    public boolean removeNode(Node node) {
        if (nodes.contains(node)) {
            return false;
        }

        List<Node> _nodes = new ArrayList<>(nodes);
        nodes.remove(node);
        namesHash.remove(node.getName());

        reconstituteGraphMatrix(_nodes, nodes);

        initHashes();

        stuffRemovedSinceLastTripleAccess = true;

        return true;
    }

    /**
     * Removes any relevant node objects found in this collection.
     *
     * @param newNodes the collection of nodes to remove.
     * @return true if nodes from the collection were removed, false if not.
     */
    public boolean removeNodes(List<Node> newNodes) {
        boolean changed = false;

        for (Object newNode : newNodes) {
            boolean _changed = removeNode((Node) newNode);
            changed = changed || _changed;
        }

        return changed;
    }

    /**
     * @return a string representation of the graph.
     */
    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("\nGraph Nodes:\n");

        for (int i = 0; i < nodes.size(); i++) {
//            buf.append("\n" + (i + 1) + ". " + nodes.get(i));
            buf.append(nodes.get(i)).append(" ");
            if ((i + 1) % 30 == 0) buf.append("\n");
        }

        buf.append("\n\nGraph Edges: ");

        List<Edge> edges = new ArrayList<>(getEdges());
        Edges.sortEdges(edges);

        for (int i = 0; i < edges.size(); i++) {
            Edge edge = edges.get(i);
            buf.append("\n").append(i + 1).append(". ").append(edge);
        }

        buf.append("\n");
        buf.append("\n");

//        Set<Triple> ambiguousTriples = getAmbiguousTriples();

        if (!ambiguousTriples.isEmpty()) {
            buf.append("Ambiguous triples (i.e. list of triples for which there is ambiguous data" +
                    "\nabout whether they are colliders or not): \n");

            for (Triple triple : ambiguousTriples) {
                buf.append(triple).append("\n");
            }
        }

        if (!underLineTriples.isEmpty()) {
            buf.append("Underline triples: \n");

            for (Triple triple : underLineTriples) {
                buf.append(triple).append("\n");
            }
        }

        if (!dottedUnderLineTriples.isEmpty()) {
            buf.append("Dotted underline triples: \n");

            for (Triple triple : dottedUnderLineTriples) {
                buf.append(triple).append("\n");
            }
        }
//
//        buf.append("\nNode positions\n");
//
//        for (Node node : getNodes()) {
//            buf.append("\n" + node + ": (" + node.getCenterX() + ", " + node.getCenterY() + ")");
//        }

        return buf.toString();
    }

    public Graph subgraph(List<Node> nodes) {
        Graph graph = new EndpointMatrixGraph(nodes);
        Set<Edge> edges = getEdges();

        for (Object edge1 : edges) {
            Edge edge = (Edge) edge1;

            if (nodes.contains(edge.getNode1()) &&
                    nodes.contains(edge.getNode2())) {
                graph.addEdge(edge);
            }
        }

        return graph;
    }

    /**
     * @return the edges connecting node1 and node2.
     */
    public List<Edge> getEdges(Node node1, Node node2) {
        List<Edge> edges = getEdges(node1);
        List<Edge> _edges = new ArrayList<>();

        for (Edge edge : edges) {
            if (edge.getDistalNode(node1) == node2) {
                _edges.add(edge);
            }
        }

        return _edges;
    }

    public Set<Triple> getAmbiguousTriples() {
        removeTriplesNotInGraph();
        return new HashSet<>(ambiguousTriples);
    }

    public Set<Triple> getUnderLines() {
        removeTriplesNotInGraph();
        return new HashSet<>(underLineTriples);
    }

    public Set<Triple> getDottedUnderlines() {
        removeTriplesNotInGraph();
        return new HashSet<>(dottedUnderLineTriples);
    }


    /**
     * States whether r-s-r is an underline triple or not.
     */
    public boolean isAmbiguousTriple(Node x, Node y, Node z) {
        Triple triple = new Triple(x, y, z);
        if (!triple.alongPathIn(this)) {
            throw new IllegalArgumentException("<" + x + ", " + y + ", " + z + "> is not along a path.");
        }
        removeTriplesNotInGraph();
        return ambiguousTriples.contains(triple);
    }

    /**
     * States whether r-s-r is an underline triple or not.
     */
    public boolean isUnderlineTriple(Node x, Node y, Node z) {
        removeTriplesNotInGraph();
        return underLineTriples.contains(new Triple(x, y, z));
    }

    /**
     * States whether r-s-r is an underline triple or not.
     */
    public boolean isDottedUnderlineTriple(Node x, Node y, Node z) {
        Triple triple = new Triple(x, y, z);
        if (!triple.alongPathIn(this)) {
            throw new IllegalArgumentException("<" + x + ", " + y + ", " + z + "> is not along a path.");
        }
        removeTriplesNotInGraph();
        return dottedUnderLineTriples.contains(new Triple(x, y, z));
    }

    public void addAmbiguousTriple(Node x, Node y, Node z) {
        Triple triple = new Triple(x, y, z);

        if (!triple.alongPathIn(this)) {
            throw new IllegalArgumentException("<" + x + ", " + y + ", " + z + "> must lie along a path in the graph.");
        }

        ambiguousTriples.add(new Triple(x, y, z));
    }

    public void addUnderlineTriple(Node x, Node y, Node z) {
        Triple triple = new Triple(x, y, z);

        if (!triple.alongPathIn(this)) {
            throw new IllegalArgumentException("<" + x + ", " + y + ", " + z + "> must lie along a path in the graph.");
        }

        underLineTriples.add(new Triple(x, y, z));
    }

    public void addDottedUnderlineTriple(Node x, Node y, Node z) {
        Triple triple = new Triple(x, y, z);

        if (!triple.alongPathIn(this)) {
            throw new IllegalArgumentException("<" + x + ", " + y + ", " + z + "> must lie along a path in the graph.");
        }

        dottedUnderLineTriples.add(triple);
    }

    public void removeAmbiguousTriple(Node x, Node y, Node z) {
        ambiguousTriples.remove(new Triple(x, y, z));
    }

    public void removeUnderlineTriple(Node x, Node y, Node z) {
        underLineTriples.remove(new Triple(x, y, z));
    }

    public void removeDottedUnderlineTriple(Node x, Node y, Node z) {
        dottedUnderLineTriples.remove(new Triple(x, y, z));
    }


    public void setAmbiguousTriples(Set<Triple> triples) {
        ambiguousTriples.clear();

        for (Triple triple : triples) {
            addAmbiguousTriple(triple.getX(), triple.getY(), triple.getZ());
        }
    }

    public void setUnderLineTriples(Set<Triple> triples) {
        underLineTriples.clear();

        for (Triple triple : triples) {
            addUnderlineTriple(triple.getX(), triple.getY(), triple.getZ());
        }
    }


    public void setDottedUnderLineTriples(Set<Triple> triples) {
        dottedUnderLineTriples.clear();

        for (Triple triple : triples) {
            addDottedUnderlineTriple(triple.getX(), triple.getY(), triple.getZ());
        }
    }

    public List<String> getNodeNames() {
        List<String> names = new ArrayList<>();

        for (Node node : getNodes()) {
            names.add(node.getName());
        }

        return names;
    }


    //===============================PRIVATE METHODS======================//

    public void removeTriplesNotInGraph() {
        if (!stuffRemovedSinceLastTripleAccess) return;

        for (Triple triple : new HashSet<>(ambiguousTriples)) {
            if (!containsNode(triple.getX()) || !containsNode(triple.getY()) || !containsNode(triple.getZ())) {
                ambiguousTriples.remove(triple);
                continue;
            }

            if (!isAdjacentTo(triple.getX(), triple.getY()) || !isAdjacentTo(triple.getY(), triple.getZ())) {
                ambiguousTriples.remove(triple);
            }
        }

        for (Triple triple : new HashSet<>(underLineTriples)) {
            if (!containsNode(triple.getX()) || !containsNode(triple.getY()) || !containsNode(triple.getZ())) {
                underLineTriples.remove(triple);
                continue;
            }

            if (!isAdjacentTo(triple.getX(), triple.getY()) || !isAdjacentTo(triple.getY(), triple.getZ())) {
                underLineTriples.remove(triple);
            }
        }

        for (Triple triple : new HashSet<>(dottedUnderLineTriples)) {
            if (!containsNode(triple.getX()) || !containsNode(triple.getY()) || !containsNode(triple.getZ())) {
                dottedUnderLineTriples.remove(triple);
                continue;
            }

            if (!isAdjacentTo(triple.getX(), triple.getY()) || !isAdjacentTo(triple.getY(), triple.getZ())) {
                dottedUnderLineTriples.remove(triple);
            }
        }

        stuffRemovedSinceLastTripleAccess = false;
    }

    @Override
    public List<Node> getSepset(Node n1, Node n2) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setNodes(List<Node> nodes) {
        if (nodes.size() != this.nodes.size()) {
            throw new IllegalArgumentException("Sorry, there is a mismatch in the number of variables " +
                    "you are trying to set.");
        }

        this.nodes = nodes;
    }


    private void collectAncestorsVisit(Node node, Set<Node> ancestors) {
        ancestors.add(node);
        List<Node> parents = getParents(node);

        if (!parents.isEmpty()) {
            for (Object parent1 : parents) {
                Node parent = (Node) parent1;
                doParentClosureVisit(parent, ancestors);
            }
        }
    }

    private void collectDescendantsVisit(Node node, Set<Node> descendants) {
        descendants.add(node);
        List<Node> children = getChildren(node);

        if (!children.isEmpty()) {
            for (Object aChildren : children) {
                Node child = (Node) aChildren;
                doChildClosureVisit(child, descendants);
            }
        }
    }

    /**
     * closure under the child relation
     */
    private void doChildClosureVisit(Node node, Set<Node> closure) {
        if (!closure.contains(node)) {
            closure.add(node);

            for (Edge edge1 : getEdges(node)) {
                Node sub = Edges.traverseDirected(node, edge1);

                if (sub == null) {
                    continue;
                }

                doChildClosureVisit(sub, closure);
            }
        }
    }

    /**
     * This is a simple auxiliary visit method for the isDConnectedTo() method
     * used to find the closure of a conditioning set of nodes under the parent
     * relation.
     *
     * @param node    the node in question
     * @param closure the closure of the conditioning set uner the parent
     *                relation (to be calculated recursively).
     */
    private void doParentClosureVisit(Node node, Set<Node> closure) {
        if (closure.contains(node)) return;
        closure.add(node);

        for (Edge edge : getEdges(node)) {
            Node sub = Edges.traverseReverseDirected(node, edge);
            if (sub != null) {
                doParentClosureVisit(sub, closure);
            }
        }
    }

    /**
     * @return true iff there is a directed path from node1 to node2.
     */
    private boolean existsUndirectedPathVisit(Node node1, Node node2,
                                              LinkedList<Node> path) {
        path.addLast(node1);

        for (Edge edge : getEdges(node1)) {
            Node child = Edges.traverse(node1, edge);

            if (child == null) {
                continue;
            }

            if (child == node2) {
                return true;
            }

            if (path.contains(child)) {
                continue;
            }

            if (existsUndirectedPathVisit(child, node2, path)) {
                return true;
            }
        }

        path.removeLast();
        return false;
    }

    private boolean existsDirectedPathVisit(Node node1, Node node2,
                                            LinkedList<Node> path) {
        path.addLast(node1);

        for (Edge edge : getEdges(node1)) {
            Node child = Edges.traverseDirected(node1, edge);

            if (child == null) {
                continue;
            }

            if (child == node2) {
                return true;
            }

            if (path.contains(child)) {
                continue;
            }

            if (existsDirectedPathVisit(child, node2, path)) {
                return true;
            }
        }

        path.removeLast();
        return false;
    }

    /**
     * @return true iff there is a semi-directed path from node1 to node2
     */
    private boolean existsSemiDirectedPathVisit(Node node1, Set<Node> nodes2,
                                                LinkedList<Node> path) {
        path.addLast(node1);

        for (Edge edge : getEdges(node1)) {
            Node child = Edges.traverseSemiDirected(node1, edge);

            if (child == null) {
                continue;
            }

            if (nodes2.contains(child)) {
                return true;
            }

            if (path.contains(child)) {
                continue;
            }

            if (existsSemiDirectedPathVisit(child, nodes2, path)) {
                return true;
            }
        }

        path.removeLast();
        return false;
    }

    public List<Node> getCausalOrdering() {
        return GraphUtils.getCausalOrdering(this);
    }

    public void setHighlighted(Edge edge, boolean highlighted) {
        highlightedEdges.add(edge);
    }

    public boolean isHighlighted(Edge edge) {
        return highlightedEdges.contains(edge);
    }

    public boolean isParameterizable(Node node) {
        return true;
    }

    public boolean isTimeLagModel() {
        return false;
    }

    public TimeLagGraph getTimeLagGraph() {
        return null;
    }

    /**
     * Adds semantic checks to the default deserialization method. This method
     * must have the standard signature for a readObject method, and the body of
     * the method must begin with "s.defaultReadObject();". Other than that, any
     * semantic checks can be specified and do not need to stay the same from
     * version to version. A readObject method of this form may be added to any
     * class, even if Tetrad sessions were previously saved out using a version
     * of the class that didn't include it. (That's what the
     * "s.defaultReadObject();" is for. See J. Bloch, Effective Java, for help.
     *
     * @throws java.io.IOException
     * @throws ClassNotFoundException
     */
    private void readObject(ObjectInputStream s)
            throws IOException, ClassNotFoundException {
        s.defaultReadObject();

        if (nodes == null) {
            throw new NullPointerException();
        }

        if (ambiguousTriples == null) {
            ambiguousTriples = new HashSet<>();
        }

        if (highlightedEdges == null) {
            highlightedEdges = new HashSet<>();
        }

        if (underLineTriples == null) {
            underLineTriples = new HashSet<>();
        }

        if (dottedUnderLineTriples == null) {
            dottedUnderLineTriples = new HashSet<>();
        }
    }
    
	@Override
	public Map<String, Object> getAllAttributes() {
		return attributes;
	}

	@Override
	public Object getAttribute(String key) {
		return attributes.get(key);
	}

	@Override
	public void removeAttribute(String key) {
		attributes.remove(key);
	}

	@Override
	public void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}

}




