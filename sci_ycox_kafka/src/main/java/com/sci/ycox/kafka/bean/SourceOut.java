package com.sci.ycox.kafka.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SourceOut {

    @JsonProperty(value = "uri_args")
	private SourceEntity args;

    public SourceEntity getArgs() {
        return args;
    }

    public void setArgs(SourceEntity args) {
        this.args = args;
    }
}
