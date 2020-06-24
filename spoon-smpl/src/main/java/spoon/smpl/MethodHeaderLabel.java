package spoon.smpl;

import spoon.reflect.declaration.CtMethod;
import spoon.smpl.formula.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * A MethodHeaderLabel is used to associate a state of a CTL model with an inner MethodHeaderModel that
 * can be matched with a MethodHeaderPredicate.
 *
 * The label requires the result of matching the inner formula of a MethodHeaderPredicate to be a single
 * result containing a single branch of witnesses, and when this is the case collects all metavariable
 * binding from said branch.
 *
 * For example, the following result would be accepted as a match:
 *   [r(some-state, some-env, [w(some-state, mv1, x, [w(some-state, mv2, y, [])])])]
 *      r(...) a result, w(...) a witness
 *
 * The following results would not be accepted:
 *   [r(some-state, some-env, [w(some-state, mv1, x, []), w(some-state, mv2, y, [])])]
 *      (two branches of witnesses)
 *   [r(some-state, some-env, [w(some-state, mv1, x, [])]), r(some-state, some-env, [w(some-state, mv2, y, [])])]
 *      (two results)
 */
public class MethodHeaderLabel implements Label {
    /**
     * Create a new MethodHeaderLabel using a given method element.
     *
     * @param method Method to generate header label for
     */
    public MethodHeaderLabel(CtMethod<?> method) {
        // TODO: test potential performance benefit of lazy initialization of the header model
        //this.method = method;
        headerModel = new MethodHeaderModel(method);

        stringRep = method.toString().replaceFirst("(?s)\\s*\\{.+", "");
        metavarBindings = new HashMap<>();
    }

    /**
     * Test whether this label matches a given predicate.
     *
     * @param predicate Predicate to test
     * @return True if the predicate is a MethodHeaderPredicate with formula matching (under additional constraints) the header model of the label, false otherwise.
     */
    @Override
    public boolean matches(Predicate predicate) {
        if (predicate instanceof MethodHeaderPredicate) {
            //if (headerModel == null) {
            //    headerModel = new MethodHeaderModel(method);
            //}

            ModelChecker checker = new ModelChecker(headerModel);
            ((MethodHeaderPredicate) predicate).getHeaderFormula().accept(checker);

            ModelChecker.ResultSet result = checker.getResult();

            if (verifyResultAndCollectMetavars(result, metavarBindings)) {
                return true;
            } else {
                reset();
                return false;
            }
        } else {
            // there is no point in supporting VariableUsePredicates here since no statement-level
            //   dots should be able to traverse the method header state

            return false;
        }
    }

    /**
     * Retrieve any metavariable bindings involved in matching the most recently given predicate.
     *
     * @return Most recent metavariable bindings, or null if there are none
     */
    @Override
    public Map<String, Object> getMetavariableBindings() {
        return metavarBindings;
    }

    /**
     * Reset/clear metavariable bindings.
     */
    @Override
    public void reset() {
        metavarBindings = new HashMap<>();
    }

    @Override
    public String toString() {
        return stringRep;
    }

    /**
     * Remove matched-element witnesses from a given witness set.
     *
     * @param witnesses Witness set to process
     * @return Input set with matched-element witnesses excluded.
     */
    private static Set<ModelChecker.Witness> withoutMatchedElements(Set<ModelChecker.Witness> witnesses) {
        return witnesses.stream().filter(x -> !x.metavar.equals("_e")).collect(Collectors.toSet());
    }

    /**
     * Verify that the given result set contains a single branch of witnesses and collect all the
     * bindings on the path of that branch, storing each binding in the given map.
     *
     * @param result Result set to verify and collect bindings from
     * @param bindings Map where metavariable bindings should be stored
     * @return True if the result set contains a single branch of witnesses, false otherwise
     */
    private static boolean verifyResultAndCollectMetavars(ModelChecker.ResultSet result, Map<String, Object> bindings) {
        if (result.size() != 1) {
            return false;
        }

        Set<ModelChecker.Witness> witnesses = withoutMatchedElements(result.getAllWitnesses());

        switch (witnesses.size()) {
            case 0:
                return true;
            case 1:
                return verifyResultAndCollectMetavars(witnesses.iterator().next(), bindings);
            default:
                return false;
        }
    }

    /**
     * Verify that the given witness contains a single-branch witness forest and collect all the
     * bindings on the path of that branch, storing each binding in the given map.
     *
     * @param witness Witness to verify and collect bindings from
     * @param bindings Map where metavariable bindings should be stored
     * @return True if the result set contains a single branch of witnesses, false otherwise
     */
    private static boolean verifyResultAndCollectMetavars(ModelChecker.Witness witness, Map<String, Object> bindings) {
        bindings.put(witness.metavar, witness.binding);

        Set<ModelChecker.Witness> witnesses = withoutMatchedElements(witness.witnesses);

        if (witnesses.size() == 0) {
            return true;
        } else if (witnesses.size() == 1) {
            return verifyResultAndCollectMetavars(witnesses.iterator().next(), bindings);
        } else {
            return false;
        }
    }

    /**
     * The most recently matched metavariable bindings.
     */
    private Map<String, Object> metavarBindings;

    /**
     * String representation of method header.
     */
    private final String stringRep;

    ///**
    // * Method element stored for enabling lazy initialization of the model.
    // */
    //private CtMethod<?> method;

    /**
     * Model for the method header (a MethodHeaderModel, accessed under a more general interface).
     */
    private final Model headerModel;
}