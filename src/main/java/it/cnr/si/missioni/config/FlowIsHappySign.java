package it.cnr.si.missioni.config;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class FlowIsHappySign implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String driverName = context.getEnvironment().getProperty("flows.driver", "");
        return driverName.equalsIgnoreCase(FlowsType.HAPPPYSIGN.toString() );
    }
}
