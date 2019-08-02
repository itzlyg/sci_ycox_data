package com.sci.ycox.kafka.bean;

import com.alibaba.fastjson.annotation.JSONField;

public class SourceOut {

    @JSONField(name = "uri_args")
	private SourceEntity args;

    public SourceEntity getArgs() {
        return args;
    }

    public void setArgs(SourceEntity args) {
        this.args = args;
    }
}
