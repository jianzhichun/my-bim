package com.zjjqtech.bimplatform.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BimModel implements Serializable {

    private String type;
    private String name;
    private String prefix;
    private String mainPath;
}
