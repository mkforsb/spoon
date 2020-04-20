package spoon.smpl;

//import spoon.pattern.Match;
import spoon.reflect.declaration.CtElement;
import spoon.smpl.formula.StatementPattern;
import spoon.smpl.formula.Predicate;
import spoon.smpl.pattern.*;

import java.util.Arrays;

/**
 * A StatementLabel is a Label used to associate states with CtElement code
 * elements that can be matched using StatementPattern Formula elements.
 *
 * The intention is for a StatementLabel to contain code that corresponds
 * to a Java statement, but the current implementation does not enforce this.
 */
public class StatementLabel extends CodeElementLabel {
    /**
     * Create a new StatementLabel.
     * @param codeElement Code element
     */
    public StatementLabel(CtElement codeElement) {
        super(codeElement);
    }

    /**
     * Test whether the label matches the given predicate.
     * @param obj Predicate to test
     * @return True if the predicate is a StatementPattern element whose Pattern matches the code, false otherwise.
     */
    public boolean matches(Predicate obj) {
        if (obj instanceof StatementPattern) {
            StatementPattern sp = (StatementPattern) obj;
            PatternMatcher matcher = new PatternMatcher(sp.getPattern());
            codePattern.accept(matcher);
            metavarBindings = Arrays.asList(matcher.getParameters());
            return matcher.getResult() && sp.processMetavariableBindings(metavarBindings.get(0));
        } else {
            return super.matches(obj);
        }
    }
}
