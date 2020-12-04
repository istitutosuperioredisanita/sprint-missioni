package it.cnr.si.missioni.util.proxy.json;

import java.io.Serializable;

public class JSONClause  implements Serializable{
	private String condition;
	private String fieldName;
	private String operator;

	private Object fieldValue;
	
	public JSONClause(){
		super();
	}

	public JSONClause(String condition, String fieldName, String operator, Object fieldValue){
		super();
		this.condition = condition;
		this.fieldName = fieldName;
		this.operator = operator;
		if (this.operator != null && this.operator.equals("LIKE")){
			this.fieldValue = fieldValue+"%";
		} else {
			this.fieldValue = fieldValue;
		}
	}
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Object getFieldValue() {
		return fieldValue;
	}


	public void setFieldValue(Object fieldValue) {
		this.fieldValue = fieldValue;
	}

	@Override
	public String toString() {
		return "JSONClause{" +
				"condition='" + condition + '\'' +
				", fieldName='" + fieldName + '\'' +
				", operator='" + operator + '\'' +
				", fieldValue=" + fieldValue +
				'}';
	}
}
