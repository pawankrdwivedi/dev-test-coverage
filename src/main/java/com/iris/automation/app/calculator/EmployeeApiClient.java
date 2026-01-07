package com.iris.automation.app.calculator;

import com.iris.automation.app.api.Employee;
import com.iris.automation.app.api.ResponseWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class EmployeeApiClient {
    private final String baseUrl;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;


    public EmployeeApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }


    public List<Employee> getAllEmployees() throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/employees";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();


        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseWrapper<List<Employee>> wrapper = objectMapper.readValue(response.body(), new TypeReference<ResponseWrapper<List<Employee>>>() {
        });
        return wrapper.getData();
    }


    public Employee getEmployeeById(String id) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/employee/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseWrapper<Employee> wrapper = objectMapper.readValue(response.body(), new TypeReference<ResponseWrapper<Employee>>() {
        });
        return wrapper.getData();
    }


    public Employee createEmployee(Employee newEmployee) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/create";
        String payload = objectMapper.writeValueAsString(newEmployee);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .POST(HttpRequest.BodyPublishers.ofString(payload))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseWrapper<Employee> wrapper = objectMapper.readValue(response.body(), new TypeReference<ResponseWrapper<Employee>>() {
        });
        return wrapper.getData();
    }
    public Employee updateEmployee(String id, Employee update) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/update/" + id;
        String payload = objectMapper.writeValueAsString(update);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .PUT(HttpRequest.BodyPublishers.ofString(payload))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseWrapper<Employee> wrapper = objectMapper.readValue(response.body(), new TypeReference<ResponseWrapper<Employee>>(){});
        return wrapper.getData();
    }


    public String deleteEmployee(String id) throws IOException, InterruptedException {
        String url = baseUrl + "/api/v1/delete/" + id;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .DELETE()
                .header("Accept", "application/json")
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        ResponseWrapper<Object> wrapper = objectMapper.readValue(response.body(), new TypeReference<ResponseWrapper<Object>>(){});
        return wrapper.getMessage();
    }
}