package spoon.smpl.formula;

/**
 * Neg represents negation, the classical "NOT" logical connective.
 */
public class Neg extends UnaryConnective {
    /**
     * Create a new "NOT" logical connective.
     *
     * @param innerElement
     */
    public Neg(Formula innerElement)
    {
        super(innerElement);
    }

    /**
     * Implements the Visitor pattern.
     * @param visitor Visitor to accept
     */
    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * @return a string representation of this element and its children
     */
    @Override
    public String toString() {
        return "-" + getInnerElement().toString();
    }
}