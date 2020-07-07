package spoon.smpl;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtConstructor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.factory.Factory;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

import java.util.Arrays;
import java.util.Stack;

// TODO: find a way to get rid of this entire thing

/**
 * TypeAccessReplacer replaces certain CtTypeAccess elements in a given AST with potentially nested CtFieldReads.
 * The elements replaced are CtTypeAccesses parented by either a CtMethod or CtConstructor.
 *
 * The motivation is illustrated by the following example:
 *
 *  Input 1:
 *      class A {
 *          static class WebSettings { public enum TextSize { SMALL, NORMAL, LARGE } } // inner class
 *          void setTextSize(WebSettings.TextSize size) { ... }
 *          void m() { setTextSize(WebSettings.TextSize.LARGE); }
 *      }
 *
 *  Input 2:
 *      class WebSettings { public enum TextSize { SMALL, NORMAL, LARGE } } // external class
 *
 *      class A {
 *          void setTextSize(WebSettings.TextSize size) { ... }
 *          void m() { setTextSize(WebSettings.TextSize.LARGE); }
 *      }
 *
 *  Input 3:
 *      import android.webkit.WebSettings; // not pulled into Spoon model
 *
 *      class A {
 *          void setTextSize(WebSettings.TextSize size) { ... }
 *          void m() { setTextSize(WebSettings.TextSize.LARGE); } // missing information
 *      }
 *
 *  AST for method call produced by Spoon with auto-imports disabled:
 *      Input 1: setTextSize(CtFieldRead("LARGE", target=CtTypeAccess("A.WebSettings.TextSize"))
 *      Input 2: setTextSize(CtFieldRead("LARGE", target=CtTypeAccess("WebSettings.TextSize"))
 *      Input 3: setTextSize(CtTypeAccess("WebSettings.TextSize.LARGE"))
 *
 *  AST for method call after TypeAccessReplacer processing:
 *      Inputs 1, 2, 3: setTextSize(CtFieldRead("LARGE", target=CtFieldRead("TextSize", target=CtFieldRead("WebSettings", target=null)))
 *
 *  Thus the idea is to make matching easier while outputting semantically equivalent source programs.
 */
public class TypeAccessReplacer extends CtScanner {
    @SuppressWarnings("unchecked")
    private static CtExpression createTypeAccessReplacement(CtElement originalElement, String typeSpec) {
        Factory factory = originalElement.getFactory();
        Stack<String> parts = new Stack<>();
        parts.addAll(Arrays.asList(typeSpec.split("\\.")));
        String lastPart = parts.pop();

        CtTypeReference fieldType = factory.createTypeReference();
        fieldType.setSimpleName(String.join(".", parts));

        CtFieldReference fieldRef = factory.createFieldReference();
        fieldRef.setSimpleName(lastPart);
        fieldRef.setType(fieldType);

        CtFieldRead fieldRead = factory.createFieldRead();
        fieldRead.setVariable(fieldRef);

        if (!parts.isEmpty()) {
            // TODO: find a way to get rid of the need for this hack (removal of "Self." part of CtTypeAccess on inner class)
            if (parts.size() == 1 && parts.peek().equals(((CtClass<?>) originalElement.getParent(CtClass.class)).getSimpleName())) {
                return fieldRead;
            }

            CtExpression<?> target = createTypeAccessReplacement(originalElement, String.join(".", parts));
            target.setParent(fieldRead);
            fieldRead.setTarget(target);
        }

        return fieldRead;
    }

    @Override
    public void enter(CtElement e) {
        if (e instanceof CtTypeAccess && (e.getParent(CtMethod.class) != null || e.getParent(CtConstructor.class) != null)
            && !(e.getParent() instanceof CtThisAccess)) {
            e.replace(createTypeAccessReplacement(e, e.toString()));
        }
    }
}
