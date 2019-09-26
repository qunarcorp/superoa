package com.qunar.superoa.model;

import lombok.Data;

@Data
public class RequestMessage {

    private String name;

    public String getName() {
        return name;
    }
}
