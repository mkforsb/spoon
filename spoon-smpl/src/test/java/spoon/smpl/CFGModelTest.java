package spoon.smpl;

import fr.inria.controlflow.ControlFlowNode;

import org.junit.Before;
import org.junit.Test;

import spoon.reflect.code.*;
import spoon.reflect.declaration.*;
import spoon.smpl.formula.*;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static spoon.smpl.TestUtils.*;

public class CFGModelTest {
    @Before
    public void resetControlFlowNodeCounter() {
        try {
            Field field = ControlFlowNode.class.getDeclaredField("count");
            field.setAccessible(true);
            ControlFlowNode.count = 0;
        } catch (Exception e) {
            fail("Unable to reset ControlFlowNode id counter");
        }
    }

    @Test
    public void testSimple() {
        CtMethod<?> method = parseMethod("int m() { int x = 1; return x + 1; }");
        CFGModel model = new CFGModel(methodCfg(method));

        Formula phi = new And(
                new StatementPattern(makePattern(parseStatement("int x = 1;"))),
                new ExistsNext(
                        new StatementPattern(makePattern(parseReturnStatement("return x + 1;")))));

        ModelChecker checker = new ModelChecker(model);
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(4, env()), checker.getResult());
    }

    @Test
    public void testBranch() {
        CtMethod<?> method = parseMethod("int m() { int x = 8; if (x > 0) { return 1; } else { return 0; } }");
        CFGModel model = new CFGModel(methodCfg(method));

        assertTrue(ModelChecker.isValid(model));

        ModelChecker checker = new ModelChecker(model);
        Formula phi;

        phi = new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class);
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                      new ExistsNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(5, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new StatementPattern(makePattern(parseStatement("return 0;")))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(), checker.getResult());

        phi = new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                        new StatementPattern(makePattern(parseStatement("return 0;"))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(8, env(), 11, env()), checker.getResult());

        phi = new And(new BranchPattern(makePattern(parseExpression("x > 0")), CtIf.class),
                new AllNext(new Or(new StatementPattern(makePattern(parseStatement("return 1;"))),
                                   new StatementPattern(makePattern(parseStatement("return 0;"))))));
        phi.accept(checker);
        //System.out.println(model.getCfg().toGraphVisText());
        assertEquals(res(5, env()), checker.getResult());
    }
}