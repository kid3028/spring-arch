package com.qull.springarch.aop.aspectj;

import com.qull.springarch.aop.MethodMatcher;
import com.qull.springarch.aop.Pointcut;
import com.qull.springarch.util.ClassUtils;
import com.qull.springarch.util.StringUtils;
import org.aspectj.weaver.reflect.ReflectionWorld;
import org.aspectj.weaver.tools.*;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/17 21:12
 */
public class AspectJExpressionPointcut implements Pointcut, MethodMatcher {

    private static final Set<PointcutPrimitive> SUPPORTED_PRIMITIVES = new HashSet<>();

    static {
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.EXECUTION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.REFERENCE);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.THIS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.TARGET);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ANNOTATION);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_WITHIN);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_ARGS);
        SUPPORTED_PRIMITIVES.add(PointcutPrimitive.AT_TARGET);
    }

    private String expression;

    private PointcutExpression pointcutExpression;

    private ClassLoader pointcutClassLoader;

    public AspectJExpressionPointcut() {}

    public MethodMatcher getMethodMatcher() {
        return this;
    }

    public String getExpression() {
        return this.expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public boolean matches(Method method) {
        checkReadyToMatch();
        ShadowMatch shadowMatch = getShadowMatch(method);
        if (shadowMatch.alwaysMatches()) {
            return true;
        }
        return false;
    }

    private ShadowMatch getShadowMatch(Method method) {
        ShadowMatch shadowMatch = null;
        try {
            shadowMatch = this.pointcutExpression.matchesMethodExecution(method);
        }catch (ReflectionWorld.ReflectionWorldException e) {
            throw new RuntimeException("not implemented yet");
        }
        return shadowMatch;
    }

    private void checkReadyToMatch() {
        if (getExpression() == null) {
            throw new IllegalStateException("Must set property 'expression' before attempting to match");
        }
        if (this.pointcutExpression == null) {
            this.pointcutClassLoader = ClassUtils.class.getClassLoader();
            this.pointcutExpression = buildPointCutExpression(this.pointcutClassLoader);
        }

    }

    private PointcutExpression buildPointCutExpression(ClassLoader classLoader) {
        PointcutParser parser = PointcutParser
                .getPointcutParserSupportingSpecifiedPrimitivesAndUsingSpecifiedClassLoaderForResolution(
                        SUPPORTED_PRIMITIVES, classLoader);
        return parser.parsePointcutExpression(replaceBooleanOperators(getExpression()), null, new PointcutParameter[0]);
    }

    private String replaceBooleanOperators(String pcExpression) {
        String result = StringUtils.replace(pcExpression, " and ", " && ");
        result = StringUtils.replace(result, " or ", " || ");
        result = StringUtils.replace(result, " not ", "!");
        return result;
    }

}
