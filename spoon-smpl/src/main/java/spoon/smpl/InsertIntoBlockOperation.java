package spoon.smpl;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;

import java.util.Map;

/**
 * An Operation that inserts a statement into a certain block associated with a given
 * anchor statement.
 */
public class InsertIntoBlockOperation implements Operation {
    /**
     * Type of targeted block.
     */
    public enum BlockType {
        /**
         * Target block is the then-statement of an if-statement.
         */
        TRUEBRANCH,

        /**
         * Target block is the else-statement of an if-statement.
         */
        FALSEBRANCH,

        /**
         * Target block is the method body.
         */
        METHODBODY
    }

    /**
     * Anchor position in the block.
     */
    public enum Anchor {
        /**
         * Anchored to top of block.
         */
        TOP,

        /**
         * Anchored to bottom of block.
         */
        BOTTOM
    }

    /**
     * Create a new InsertIntoBlockOperation given the targeted block type, position anchor
     * and the statement to insert.
     * @param tpe Block type
     * @param anchor Position anchor
     * @param statementToInsert Statement to insert
     */
    public InsertIntoBlockOperation(BlockType tpe, Anchor anchor, CtStatement statementToInsert) {
        this.blockType = tpe;
        this.anchor = anchor;
        this.statementToInsert = statementToInsert;
    }

    /**
     * Insert the contained statement to the appropriate block of a given target element.
     * @param category Operation is only applied in appropriate categories
     * @param targetElement Target element to append to
     * @param bindings Metavariable bindings to use
     */
    @Override
    public void accept(OperationFilter category, CtElement targetElement, Map<String, Object> bindings) {
        if (anchor == Anchor.TOP && category == OperationFilter.APPEND) {
            getTargetBlock(targetElement).insertBegin((CtStatement) Substitutor.apply(statementToInsert.clone(), bindings));
        } else if (anchor == Anchor.BOTTOM && category == OperationFilter.PREPEND) {
            getTargetBlock(targetElement).insertEnd((CtStatement) Substitutor.apply(statementToInsert.clone(), bindings));
        }
    }

    /**
     * Pick out the appropriate block element from the given target element.
     * @param targetElement Target element
     * @return Appropriate block element associated with target element
     */
    private CtBlock<?> getTargetBlock(CtElement targetElement) {
        switch (blockType) {
            case TRUEBRANCH:
                return ((CtIf) targetElement).getThenStatement();
            case FALSEBRANCH:
                return ((CtIf) targetElement).getElseStatement();
            case METHODBODY:
                return ((CtMethod<?>) targetElement).getBody();
            default:
                return null;
        }
    }

    @Override
    public String toString() {
        return "BlockIns(" + blockType.toString() + ", " + anchor.toString() + ", " + statementToInsert.toString() + ")";
    }

    /**
     * Type of targeted block.
     */
    private BlockType blockType;

    /**
     * Position anchor in targeted block.
     */
    private Anchor anchor;

    /**
     * Statement to insert.
     */
    private CtStatement statementToInsert;
}
