package com.example;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

import com.palantir.javaformat.java.Formatter;
import com.palantir.javaformat.java.Main;
import org.graalvm.webimage.api.JS;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;

public class WebMain {

    // Native helper snippet that attaches the Java Function to browser's globalThis
    @JS(args = {"formatter"}, value = "globalThis.formatByOpenJavaFormatter = formatter;")
    private static native void exportToBrowser(Function<JSString, JSObject> formatter);

    public static void main(String[] args) {
        // Expose a Java math logic function to JavaScript
        exportToBrowser(WebMain::format);
    }

    private static String versionString() {
        return "open-java-format: Version " + Main.class.getPackage().getImplementationVersion();
    }

    private static JSObject format(JSString source) {
        long start = System.nanoTime();
        var result = new FormatterResult();
        result.setVersion(versionString());
        try {
            var f = Formatter.create();
            result.setFormattedSource(f.formatSource(source.asString()));
            result.setStatus("SUCCESS");

            long nanos = System.nanoTime() - start;
            result.setTime(nanos / 1e6);
        } catch (Throwable e) {
            long nanos = System.nanoTime() - start;
            result.setTime(nanos / 1e6);
            result.setStatus("ERROR");

            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            result.setFormattedSource(sw.toString());
        }
        return result.toJSObject();
    }

}
