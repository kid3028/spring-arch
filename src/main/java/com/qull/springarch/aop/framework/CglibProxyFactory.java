package com.qull.springarch.aop.framework;

import com.qull.springarch.aop.Advice;
import com.qull.springarch.util.Assert;
import org.omg.PortableInterceptor.Interceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.core.CodeGenerationException;
import org.springframework.cglib.core.SpringNamingPolicy;
import org.springframework.cglib.proxy.*;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 22:15
 */
public class CglibProxyFactory implements AopProxyFactory{

    private static final Logger log = LoggerFactory.getLogger(CglibProxyFactory.class);

    private static final int AOP_PROXY = 0;
    private static final int INVOKE_TARGET = 1;
    private static final int NO_OVERRIDEE = 2;
    private static final int DISPATCH_TARGET = 3;
    private static final int DISPATCH_ADVISED = 4;
    private static final int INVOKE_EQUALS = 5;
    private static final int INVOKE_HASHCODE = 6;

    protected final AopConfig config;

    private Object[] constructorArgs;

    private Class<?>[] constructorArgTypes;

    public CglibProxyFactory(AopConfig config) {
        Assert.notNull(config, "AdvisedSupport must not be null");
        if (config.getAdvices().size() == 0) {
            throw new AopconfigException("No advised and not TargetSource specified");
        }
        this.config = config;
    }

    /**
     * Set constructor arguments to use for creating the proxy.
     * @param constructorArgs the constructor argument values
     * @param constructorArgTypes the constructor argument types
     */
	/*public void setConstructorArguments(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
		if (constructorArgs == null || constructorArgTypes == null) {
			throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
		}
		if (constructorArgs.length != constructorArgTypes.length) {
			throw new IllegalArgumentException("Number of 'constructorArgs' (" + constructorArgs.length +
					") must match number of 'constructorArgTypes' (" + constructorArgTypes.length + ")");
		}
		this.constructorArgs = constructorArgs;
		this.constructorArgTypes = constructorArgTypes;
	}*/

    public void setConstructorArgTypes(Object[] constructorArgs, Class<?>[] constructorArgTypes) {
        if (constructorArgs == null || constructorArgTypes == null) {
            throw new IllegalArgumentException("Both 'constructorArgs' and 'constructorArgTypes' need to be specified");
        }
        if (constructorArgs.length != constructorArgTypes.length) {
            throw new IllegalArgumentException("Number of 'constructorArgs' ( " + constructorArgs.length + " ) must match of 'constructorArgTypes' ( " + constructorArgTypes.length + ")");
        }
        this.constructorArgs = constructorArgs;
        this.constructorArgTypes = constructorArgTypes;
    }



    @Override
    public Object getProxy() {
        return getProxy(null);
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        if (log.isDebugEnabled()) {
            log.debug("Creating CGLIB proxy : target source is " + this.config.getTargetClass());
        }

        try {
            Class<?> rootClass = this.config.getTargetClass();
            Enhancer enhancer = new Enhancer();
            if (classLoader != null) {
                enhancer.setClassLoader(classLoader);
            }
            enhancer.setSuperclass(rootClass);
            // BySpringCGLIB
            enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
            enhancer.setInterceptDuringConstruction(false);
            Callback[] callbacks = getCallbacks(rootClass);
            Class<?>[] callbackTypes = new Class<?>[callbacks.length];
            for (int i = 0; i < callbacks.length; i++) {
                callbackTypes[i] = callbacks[i].getClass();
            }
            
            enhancer.setCallbacks(callbacks);
            enhancer.setCallbackTypes(callbackTypes);
            enhancer.setCallbackFilter(new ProxyCallbackFilter(this.config));

            // Generate the proxy class an create a proxy instance
            Object proxy = enhancer.create();
//            if (this.constructorArgs != null) {
//                proxy = enhancer.create(constructorArgTypes, constructorArgs);
//            }else {
//                proxy =enhancer.create();
//            }
            return proxy;
        } catch (CodeGenerationException e) {
            throw new AopconfigException("Could not generate CGLIB subclass of class [ " + this.config.getTargetClass() + " ]: Common causes of this problem include using a final class or a no-visible class", e);
        }catch (IllegalArgumentException e) {
            throw new AopconfigException("Could not generate CGLIB subclass of class [" + this.config.getTargetClass() + "] Common causes of this problem include using a final class or a non-visible class", e);
        }catch (Exception e) {
            throw new AopconfigException("Unexpected AOP exception ", e);
        }
    }

    private Callback[] getCallbacks(Class<?> rootClass) {
        Callback aopInterceptor = new DynamicAdvisedInterceptor(this.config);


        //Callback targetInterceptor = new StaticUnadvisedExposedInterceptor(this.advised.getTargetObject());

        //Callback targetDispatcher = new StaticDispatcher(this.advised.getTargetObject());

        Callback[] callbacks = new Callback[] {
                aopInterceptor,  // AOP_PROXY for normal advice  
                /*targetInterceptor,  // INVOKE_TARGET invoke target without considering advice, if optimized
                new SerializableNoOp(),  // NO_OVERRIDE  no override for methods mapped to this
                targetDispatcher,        //DISPATCH_TARGET
                this.advisedDispatcher,  //DISPATCH_ADVISED
                new EqualsInterceptor(this.advised),
                new HashCodeInterceptor(this.advised)*/
        };
        return callbacks;
    }

    private class DynamicAdvisedInterceptor implements MethodInterceptor, Serializable {
        private final AopConfig config;

        public DynamicAdvisedInterceptor(AopConfig config) {
            this.config = config;
        }

        @Override
        public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            Object target = this.config.getTargetObject();
            List<Advice> chain = this.config.getAdvices(method);
            Object retval;

            // Check whether we only have one InvokerInterceptor: thar is, no real advice, but just reflective invocation of the target
            if (chain.isEmpty() && Modifier.isStatic(method.getModifiers())) {
                // we can skip creating a MethodInvocation: just invoke the target directly.
                // Note that the final invoker must be an InvokerInterceptor, so we know it does nothing
                // but a reflective operation on the target, and no hot swapping or fancy proxying
                retval = method.invoke(target, args);
            }else {
                List<org.aopalliance.intercept.MethodInterceptor> interceptors = new ArrayList<>();
                interceptors.addAll(chain);
                // we need to create a method invocation...
                retval = new ReflectiveMethodInvocation(target, method, args, interceptors).proceed();
            }
            // retval = processReturnType(proxy, target, method, retval)
            return retval;
        }
    }

    private class ProxyCallbackFilter implements CallbackFilter {
        private AopConfig config;
        public ProxyCallbackFilter(AopConfig config) {
            this.config = config;
        }


        @Override
        public int accept(Method method) {
            // 简化处理
            return AOP_PROXY;
        }
    }
}
