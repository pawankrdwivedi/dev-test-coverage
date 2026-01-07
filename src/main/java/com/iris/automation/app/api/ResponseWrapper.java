package com.iris.automation.app.api;

import com.fasterxml.jackson.annotation.JsonProperty;


public class ResponseWrapper<T> {
    @JsonProperty("status")
    private String status;


    @JsonProperty("data")
    private T data;


    @JsonProperty("message")
    private String message;


    public ResponseWrapper() {}


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}