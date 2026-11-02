package com.example;

import org.graalvm.webimage.api.JSNumber;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;

public final class FormatterResult {
    public String formattedSource;
    public double time;
    public String status;
    public String version;

    public JSObject toJSObject() {
        var result = JSObject.create();
        result.set("formattedSource", JSString.of(formattedSource));
        result.set("time", JSNumber.of(time));
        result.set("status", JSString.of(status));
        result.set("version", JSString.of(version));
        return result;
    }

    public void setFormattedSource(String formattedSource) {
        this.formattedSource = formattedSource;
    }

    public void setTime(double time) {
        this.time = time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
