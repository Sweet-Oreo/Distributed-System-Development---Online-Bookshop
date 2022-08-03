/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vo;

/**
 * Response Object: construct JSON string for response
 * 
 * @author Team 6
 */
public class RespObj<T> {
    
    private boolean result;
    private long time;
    private T object = null;
    private String objectName = "object";
    private String error = null;

    public RespObj(boolean result, T object, String objectName, String error) {
        this.result = result;
        this.time = System.currentTimeMillis();
        this.object = object;
        if (objectName != null && !"".equals(objectName)) {
            this.objectName = objectName;
        }
        this.error = error;
    }

    public RespObj(boolean result, T object, String objectName) {
        this.result = result;
        this.time = System.currentTimeMillis();
        this.object = object;
        this.objectName = objectName;
    }

    public RespObj(boolean result, String error) {
        this.result = result;
        this.time = System.currentTimeMillis();
        this.error = error;
    }

    public RespObj(boolean result) {
        this.result = result;
        this.time = System.currentTimeMillis();
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @Override
    public String toString() {
        String prefix = "{\"result\":" + result + ",\"time\":" + time;
        String extra = "";
        String postfix = "}";
        if (object == null) {
            if (error != null && !"".equals(error)) {
                extra = ",\"error\":\"" + error + "\"";
            }
        } else {
            if (error != null && !"".equals(error)) {
                extra = ",\"" + objectName + "\":" + object + ",\"error\":\"" + error + "\"";
            } else {
                extra = ",\"" + objectName + "\":" + object;
            }
        }
        return prefix + extra + postfix;
    }
    
}
