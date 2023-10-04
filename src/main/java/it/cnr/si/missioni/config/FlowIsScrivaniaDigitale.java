package it.cnr.si.missioni.config;

import it.cnr.si.spring.storage.StorageDriver;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

public class FlowIsScrivaniaDigitale implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        String driverName = context.getEnvironment().getProperty("flows.type", FlowsType.SCRIVANIADIGITALE.toString());
        return driverName.equalsIgnoreCase(FlowsType.SCRIVANIADIGITALE.toString() );
    }
}
