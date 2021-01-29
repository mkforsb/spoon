package spoon.smpl;

import org.junit.BeforeClass;
import org.junit.Test;
import spoon.reflect.declaration.CtMethod;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class C4JGetColorTest {
    private static ZippedCodeBaseTestContext ctx = null;

    @BeforeClass
    public static void initializeContext() {
        if (ctx != null) {
            return;
        }

        String smpl = "@@\n" +
                      "Context ctx;\n" +
                      "expression E;\n" +
                      "@@\n" +
                      "- ctx.getResources().getColor(E)\n" +
                      "+ ContextCompat.getColor(ctx , E)\n" +
                      "\n";

        ctx = new ZippedCodeBaseTestContext(smpl, "src/test/resources/C4JGetColor.zip", false);
    }

    @Test
    public void testNotificationBuilder() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.PushNotifications::notificationBuilder");
        assertTrue(method.toString().contains("context.getResources().getColor(R.color.green)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(R.color.green)"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.green)"));
    }

    @Test
    public void testPrimaryColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::primaryColor");
        assertTrue(method.toString().contains("context.getResources().getColor(R.color.discovery_primary)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(R.color.discovery_primary)"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.discovery_primary)"));
    }

    @Test
    public void testSecondaryColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::secondaryColor");
        assertTrue(method.toString().contains("context.getResources().getColor(R.color.discovery_secondary)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(R.color.discovery_secondary)"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, R.color.discovery_secondary)"));
    }

    @Test
    public void testOverlayTextColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.DiscoveryUtils::overlayTextColor", 1);
        assertTrue(method.toString().contains("context.getResources().getColor(color)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(color)"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, color)"));
    }

    @Test
    public void testDarkColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::darkColor");
        assertTrue(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, com.kickstarter.libs.utils.KSColorUtils.darkColorId())"));
    }

    @Test
    public void testLightColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::lightColor");
        assertTrue(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("context.getResources().getColor(com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));
        assertTrue(method.toString().contains("ContextCompat.getColor(context, com.kickstarter.libs.utils.KSColorUtils.lightColorId())"));
    }

    @Test
    public void testForegroundColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.libs.utils.KSColorUtils::foregroundColor");
        assertTrue(method.toString().contains("return context.getResources().getColor(colorId)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("return context.getResources().getColor(colorId)"));
        assertTrue(method.toString().contains("return ContextCompat.getColor(context, colorId)"));
    }

    @Test
    public void testCategorySecondaryColor() {

        // contract: patch (lines 5-6) should perform replacement in target method

        CtMethod<?> method = ctx.getMethod("com.kickstarter.models.Category::secondaryColor");
        assertTrue(method.toString().contains("return context.getResources().getColor(identifier)"));

        ctx.testExecutable(method);
        assertFalse(method.toString().contains("return context.getResources().getColor(identifier)"));
        assertTrue(method.toString().contains("return ContextCompat.getColor(context, identifier)"));
    }
}