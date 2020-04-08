package spoon.smpl;

import org.junit.Before;
import org.junit.Test;
import spoon.smpl.formula.*;
import spoon.smpl.metavars.IdentifierConstraint;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static spoon.smpl.TestUtils.*;

/**
 * This suite is meant for testing bugs that may involve a large stack of components, making them
 * unsuitable for including as parts of unit test suites of individual components.
 */
public class IntegrationBugsTest {
    @Before
    public void before() {
        resetControlFlowNodeCounter();
    }

    @Test
    public void testModelCheckerAllNextBug01() {

        // contract: AX(Predicate) should include the successor constraint when generating results

        // Background: The model checker is supposed to compute SAT(AX(Predicate)) according to
        //             SAT(AX(Predicate)) = { (s,env) | (s',env) in SAT(Predicate),
        //                                              s in StatesOnlyTransitioningTo(StatesIn(SAT(Predicate))),
        //                                              s' in successors(s) }
        //
        //             The bug consisted of missing to check the constraint "s' in successors(s)"
        //             leading to incorrect results.

        Model model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; int y = 1; }")));
        ModelChecker checker = new ModelChecker(model);

        Map<String, MetavariableConstraint> meta = makeMetavars("v", new IdentifierConstraint());
        List<String> metakeys = new ArrayList<>(meta.keySet());

        new AllNext(new StatementPattern(makePattern(parseStatement("int v = 1;"), metakeys), meta))
                .accept(checker);

        assertEquals("[(4, {v=y}, [])]", sortedEnvs(checker.getResult().toString()));

        // Before bugfix was [(4, {v=y}), (4, {v=x})]
    }

    @Test
    public void testModelCheckerExistsNextBug01() {

        // contract: EX(Predicate) should include the successor constraint when generating results

        // Background: Failing to check successor constraint, see testModelCheckerAllNextBug01

        Model model = new CFGModel(methodCfg(parseMethod("void foo() { int x = 1; int y = 1; }")));
        ModelChecker checker = new ModelChecker(model);

        Map<String, MetavariableConstraint> meta = makeMetavars("v", new IdentifierConstraint());
        List<String> metakeys = new ArrayList<>(meta.keySet());

        new ExistsNext(new StatementPattern(makePattern(parseStatement("int v = 1;"), metakeys), meta))
                .accept(checker);

        assertEquals("[(4, {v=y}, [])]", sortedEnvs(checker.getResult().toString()));

        // Before bugfix was [(4, {v=y}), (4, {v=x})]
    }
}