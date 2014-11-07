package com.hwtx.framework.interceptor;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hwtxframework.ioc.ApplicationContext;
import com.jfinal.aop.Interceptor;
import com.jfinal.core.ActionInvocation;
import com.jfinal.core.Controller;
import com.jfinal.core.ModuleController;
import com.jfinal.kit.StrKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ModuleIocInterceptor implements Interceptor {

    public static ApplicationContext root;
    public static Map<String, ApplicationContext> ctxs = Maps.newHashMap();

    private Logger logger = LoggerFactory.getLogger(getClass());

    public void intercept(ActionInvocation ai) {
        Controller controller = ai.getController();

        ApplicationContext ctx = root;

        if (controller instanceof ModuleController) {
            String moduleName = ((ModuleController) controller).getModuleName();
            ApplicationContext temp = ctxs.get(moduleName);

            if (temp == null) {
                logger.warn("There is no appliaction in moudle " + moduleName);
            } else {
                ctx = temp;
            }
        }
        invoke(ai, ctx, controller);
    }

    private void invoke(ActionInvocation ai, ApplicationContext ctx, Controller controller) {
        List<Field> fields = Lists.newArrayList();
        tranferAllFields(controller.getClass(), fields);
        for (Field field : fields) {
            Object bean = null;
            Resource resource = field.getAnnotation(Resource.class);
            if (resource != null) {
                String refName = resource.name();
                if (StrKit.isBlank(refName)) {
                    refName = StrKit.firstCharToLowerCase(field.getType().getSimpleName());
                }
                bean = ctx.getComponent(refName);
            } else {
                continue;
            }
            try {
                if (bean != null) {
                    field.setAccessible(true);
                    field.set(controller, bean);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        ai.invoke();
    }

    private void tranferAllFields(Class<?> clazz, List<Field> fields) {
        if (!clazz.equals(Controller.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            tranferAllFields(clazz.getSuperclass(), fields);
        }
    }
}
