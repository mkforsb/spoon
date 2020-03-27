package spoon.smpl;

import fr.inria.controlflow.BranchKind;
import fr.inria.controlflow.ControlFlowGraph;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.BranchPattern;

import java.util.*;

/**
 * A CFGModel builds a CTL model from a given CFG.
 */
public class CFGModel implements Model {
    /**
     * Create a new CTL model from a given CFG.
     * @param cfg The ControlFlowGraph to use as a model, must have been simplified
     */
    public CFGModel(ControlFlowGraph cfg) {
        if (cfg.findNodesOfKind(BranchKind.BLOCK_BEGIN).size() > 0) {
            throw new IllegalArgumentException("The CFG must be simplified (see ControlFlowGraph::simplify)");
        }

        this.cfg = cfg;

        states = new ArrayList<Integer>();
        successors = new HashMap<Integer, List<Integer>>();
        labels = new HashMap<Integer, List<Label>>();

        cfg.vertexSet().forEach(node -> {
            int state = node.getId();

            // Add a state ID for each vertex ID and prepare lists of successors and labels
            states.add(state);
            successors.put(state, new ArrayList<Integer>());
            labels.put(state, new ArrayList<Label>());

            // Add successors
            cfg.outgoingEdgesOf(node).forEach(edge -> {
                successors.get(state).add(edge.getTargetNode().getId());
            });

            // Add self-loop on exit node
            if (node.getKind() == BranchKind.EXIT) {
                successors.get(state).add(state);
            }

            CtElement stmt = node.getStatement();

            // Add label
            if (stmt != null) {
                switch (node.getKind()) {
                    case BRANCH:
                        labels.get(state).add(new BranchLabel(stmt));
                        break;
                    case STATEMENT:
                        labels.get(state).add(new StatementLabel(stmt));
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * @return the set of state IDs in the model
     */
    @Override
    public List<Integer> getStates() {
        return states;
    }

    /**
     * @param state Parent state
     * @return the set of immediate successors of the given state
     */
    @Override
    public List<Integer> getSuccessors(int state) {
        return successors.get(state);
    }

    /**
     * @param state Target state
     * @return the set of labels associated with the given state
     */
    @Override
    public List<Label> getLabels(int state) {
        return labels.get(state);
    }

    /**
     * @return the Control Flow Graph used to generate the model
     */
    public ControlFlowGraph getCfg() { return cfg; }

    /**
     * The Control Flow Graph used to generate the model.
     */
    private ControlFlowGraph cfg;

    /**
     * The set of state IDs.
     */
    private List<Integer> states;

    /**
     * The set of immediate successors.
     */
    private Map<Integer, List<Integer>> successors;

    /**
     * The set of state labels.
     */
    private Map<Integer, List<Label>> labels;
}