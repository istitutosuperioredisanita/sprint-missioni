/*
 *  Copyright (C) 2023  Consiglio Nazionale delle Ricerche
 *
 *      This program is free software: you can redistribute it and/or modify
 *      it under the terms of the GNU Affero General Public License as
 *      published by the Free Software Foundation, either version 3 of the
 *      License, or (at your option) any later version.
 *
 *      This program is distributed in the hope that it will be useful,
 *      but WITHOUT ANY WARRANTY; without even the implied warranty of
 *      MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *      GNU Affero General Public License for more details.
 *
 *      You should have received a copy of the GNU Affero General Public License
 *      along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *
 *
 */

package it.cnr.si.missioni.util.proxy.json;

import java.io.Serializable;

public class JSONClause implements Serializable {
    private String condition;
    private String fieldName;
    private String operator;

    private Object fieldValue;

    public JSONClause() {
        super();
    }

    public JSONClause(String condition, String fieldName, String operator, Object fieldValue) {
        super();
        this.condition = condition;
        this.fieldName = fieldName;
        this.operator = operator;
        if (this.operator != null && this.operator.equals("LIKE")) {
            this.fieldValue = fieldValue + "%";
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
