package spoon.smpl.formula;

import spoon.reflect.declaration.CtElement;

import java.util.Map;

/**
 * A Proposition is a constant, unparameterized predicate. The actual proposition is represented
 * by a given String.
 */
public class Proposition implements Predicate {
    /**
     * Create a new Proposition.
     *
     * @param proposition The proposition String
     */
    public Proposition(String proposition) {
        this.proposition = proposition;
    }

    /**
     * Implements the Visitor pattern.
     *
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Propositions do not support metavariables.
     *
     * @return null
     */
    @Override
    public Map<String, MetavariableConstraint> getMetavariables() {
        return null;
    }

    /**
     * Propositions do not support metavariables.
     *
     * @return true
     */
    @Override
    public boolean processMetavariableBindings(Map<String, Object> parameters) {
        return true;
    }

    /**
     * Propositions cannot match specific elements.
     *
     * @param element Irrelevant
     */
    @Override
    public void setMatchedElement(CtElement element) {

    }

    /**
     * Propositions cannot match specific elements.
     *
     * @return false
     */
    @Override
    public boolean hasMatchedElement() {
        return false;
    }

    /**
     * Propositions cannot match specific elements.
     *
     * @return null
     */
    @Override
    public CtElement getMatchedElement() {
        return null;
    }

    /**
     * Get the proposition String.
     *
     * @return The proposition String
     */
    public String getProposition() {
        return proposition;
    }

    @Override
    public String toString() {
        return proposition;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof Proposition && other.hashCode() == hashCode());
    }

    /**
     * The proposition String.
     */
    private String proposition;
}