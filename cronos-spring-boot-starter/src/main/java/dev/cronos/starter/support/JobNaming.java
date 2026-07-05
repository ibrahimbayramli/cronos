package dev.cronos.starter.support;

import org.springframework.aop.support.AopUtils;
import org.springframework.context.ApplicationContext;

import java.beans.Introspector;
import java.lang.reflect.Method;

public final class JobNaming {

    private JobNaming() {
    }

    public static String resolve(ApplicationContext applicationContext, Object target, Method method) {
        String beanName = resolveBeanName(applicationContext, target);
        return beanName + "#" + method.getName();
    }

    public static String resolve(ApplicationContext applicationContext, Object target, String methodName) {
        return resolveBeanName(applicationContext, target) + "#" + methodName;
    }

    public static String resolveBeanName(ApplicationContext applicationContext, Object target) {
        if (target == null) {
            return "unknown";
        }

        Class<?> targetClass = AopUtils.getTargetClass(target);
        String[] names = applicationContext.getBeanNamesForType(targetClass);
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            if (AopUtils.getTargetClass(bean) == targetClass && sameTarget(bean, target)) {
                return name;
            }
        }
        if (names.length == 1) {
            return names[0];
        }
        return Introspector.decapitalize(targetClass.getSimpleName());
    }

    private static boolean sameTarget(Object bean, Object target) {
        if (bean == target) {
            return true;
        }
        return AopUtils.getTargetClass(bean) == AopUtils.getTargetClass(target);
    }
}
