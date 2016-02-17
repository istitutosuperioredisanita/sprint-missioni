package it.cnr.si.missioni.cmis.flows;

public enum FlowResubmitType {
    ABORT_FLOW ("Annulla"),
    RESTART_FLOW ("Riproponi");

    private String operation;

    FlowResubmitType(String operation)
    {
        this.operation = operation;
    }
    
    public String operation()
    {
        return operation;
    }
}
