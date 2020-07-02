package spoon.smpl.formula;

/**
 * InnerAnd(phi) represents a special case treatment of the Formula phi in which the results of SAT(phi) should be
 * merged under environments containing only positive bindings.
 *
 * Example:
 *   Let phi = SequentialOr(Expression(x), Expression(y)) with x and y being "identifier" metavariables.
 *   Let M be a Model containing a state S labeled such that the label contains two identifier-bindable elements a and b:
 *      SAT(M, Expression(x)) contains (S, {x=a}, W1) and (S, {x=b}, W2)
 *      SAT(M, Expression(y)) contains (S, {y=a}, W3) and (S, {y=b}, W4)
 *
 *   SAT(M, phi) then contains (S, {x=a}, W1),                                     1
 *                             (S, {x=b}, W2),                                     2
 *                             (S, {x=[a], y=a}, W3),                              3
 *                             (S, {x=[b], y=a}, W3),                              4
 *                             (S, {x=[a], y=b}, W4),                              5
 *                             (S, {x=[b], y=b}, W4),                              6
 *
 *   So SAT(M, InnerAnd(phi)) should contain (S, {x=a, y=a}, W1 + W3)              1 + 4
 *                                           (S, {x=a, y=b}, W1 + W4)              1 + 6
 *                                           (S, {x=b, y=a}, W2 + W3)              2 + 3
 *                                           (S, {x=b, y=b}, W2 + W4)              2 + 5
 *
 * The following example illustrates the motivation behind this connective:
 *   Let phi = And(Del(Statement("f(x,y);")),
 *                 AllNext(SequentialOr(Del(Expression(x)), Del(Expression(y)))))
 *
 *       where Del(t) = And(t, ExistsVar("_v", SetEnv("_v", "Delete")))
 *         and x, y are identifier metavariables
 *
 *   Let M be a Model containing the states S1: [f(a,b);] -> S2: [print(a,b);]
 *
 *   Then SAT(M, Del(Statement("f(x,y);")) contains
 *      (S1, {x=a, y=b}, [{S1, _v, Delete, []}])
 *
 *   And SAT(M, AllNext(SequentialOr(Del(Expression(x)), Del(Expression(y))))) contains
 *      (S1, {x=a}, [{S2, _e, a, [{S2, _v, Delete, []}]}])
 *      (S1, {x=b}, [{S2, _e, b, [{S2, _v, Delete, []}]}])
 *      (S1, {x=[a], y=a}, [{S2, _e, a, [{S2, _v, Delete, []}]}])
 *      (S1, {x=[b], y=a}, [{S2, _e, a, [{S2, _v, Delete, []}]}])
 *      (S1, {x=[a], y=b}, [{S2, _e, b, [{S2, _v, Delete, []}]}])
 *      (S1, {x=[b], y=b}, [{S2, _e, b, [{S2, _v, Delete, []}]}])
 *
 *   So the intersection formed in SAT(M, phi) contains
 *      (S1, {x=a, y=b}, [{S1, _v, Delete, []}, {S2, _e, a, [{S2, _v, Delete, []}]}])
 *      (S1, {x=a, y=b}, [{S1, _v, Delete, []}, {S2, _e, b, [{S2, _v, Delete, []}]}])
 *
 *   Meaning the deletion operation on S1 is present twice.
 *
 *   If we let phi' = And(Del(Statement("f(x,y);")),
 *                        AllNext(InnerAnd(SequentialOr(Del(Expression(x)), Del(Expression(y))))))
 *                                ^ ^ ^ ^
 *
 *   Then SAT(M, AllNext(InnerAnd(SequentialOr(Del(Expression(x)), Del(Expression(y)))))) contains
 *      (S1, {x=a, y=a}, [{S2, _e, a, [{S2, _v, Delete, []}]}])
 *      (S1, {x=a, y=b}, [{S2, _e, a, [{S2, _v, Delete, []}]}, {S2, _e, b, [{S2, _v, Delete, []}]}])
 *      (S1, {x=b, y=a}, [{S2, _e, a, [{S2, _v, Delete, []}]}, {S2, _e, b, [{S2, _v, Delete, []}]}])
 *      (S1, {x=b, y=b}, [{S2, _e, b, [{S2, _v, Delete, []}]}])
 *
 *   So the intersection formed in SAT(M, phi) contains only
 *      (S1, {x=a, y=b}, [{S1, _v, Delete, []}, {S2, _e, a, [{S2, _v, Delete, []}]}, {S2, _e, b, [{S2, _v, Delete, []}]}])
 */
public class InnerAnd extends UnaryConnective {
    /**
     * Create a new InnerAnd connective.
     *
     * @param innerElement Inner element
     */
    public InnerAnd(Formula innerElement) {
        super(innerElement);
    }

    @Override
    public void accept(FormulaVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String toString() {
        return "InnerAnd(" + getInnerElement().toString() + ")";
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof InnerAnd && other.hashCode() == hashCode());
    }
}