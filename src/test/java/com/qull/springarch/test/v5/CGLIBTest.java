package com.qull.springarch.test.v5;

import com.qull.springarch.service.v5.PetStoreService;
import com.qull.springarch.tx.TransactionManager;
import org.junit.Test;
import org.springframework.cglib.proxy.*;

import java.lang.reflect.Method;

/**
 * @author kzh
 * @description
 * @DATE 2019/10/18 14:31
 */
public class CGLIBTest {

    @Test
    public void testCallback() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        enhancer.setCallback(new TransactionInterceptor());
        PetStoreService petStoreService = (PetStoreService) enhancer.create();
        petStoreService.placeOrder();
//        petStoreService.toString();
    }

    public static class TransactionInterceptor implements MethodInterceptor {

        TransactionManager tx = new TransactionManager();

        @Override
        public Object intercept(Object target, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            tx.start();
            Object result = methodProxy.invokeSuper(target, args);
            tx.commit();
            return result;
        }
    }

    @Test
    public void testFilter() {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(PetStoreService.class);
        enhancer.setInterceptDuringConstruction(false);

        Callback[] callbacks = {new TransactionInterceptor(), NoOp.INSTANCE};
        Class<?>[] types = new Class<?>[callbacks.length];
        for (int i = 0; i < callbacks.length; i++) {
            types[i] = callbacks[i].getClass();
        }
        enhancer.setCallbackFilter(new ProxyCallbackFilter());
        enhancer.setCallbacks(callbacks);
        enhancer.setCallbackTypes(types);

        PetStoreService petStoreService = (PetStoreService) enhancer.create();
        petStoreService.placeOrder();
        System.out.println(petStoreService.toString());

    }

    public static class ProxyCallbackFilter implements CallbackFilter{

        @Override
        public int accept(Method method) {
            if (method.getName().startsWith("place")) {
                return 0;
            }else {
                return 1;
            }
        }
    }
}
