package com.vulturi.trading.api.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SpringCtx implements ApplicationContextAware {

    private static Environment environment;

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringCtx.applicationContext = applicationContext;
    }

    @Autowired
    public SpringCtx(Environment environment) {
        SpringCtx.environment = environment;
    }

    public static String getActiveProfile() {
        String[] activeProfiles = SpringCtx.environment.getActiveProfiles();
        if (activeProfiles.length > 1) {
            throw new RuntimeException("There are more than 1 active profiles");
        }
        return activeProfiles.length == 0 ? null : activeProfiles[0];
    }

    public static ApplicationContext getAppCtx() { return SpringCtx.applicationContext; }
}
