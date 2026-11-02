package com.example;

import com.google.googlejavaformat.java.Formatter;
import com.google.googlejavaformat.java.Main;
import org.graalvm.webimage.api.JS;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.function.Function;

public class WebGoogleMain {

    // Native helper snippet that attaches the Java Function to browser's globalThis
    @JS(args = {"formatter"}, value = "globalThis.formatByGoogleJavaFormatter = formatter;")
    private static native void exportToBrowser(Function<JSString, JSObject> formatter);

    public static void main(String[] args) {
        // Expose a Java math logic function to JavaScript
        exportToBrowser(WebGoogleMain::format);
    }

    private static String versionString() {
        return "google-java-format: Version " + Main.class.getPackage().getImplementationVersion();
    }

    private static JSObject format(JSString source) {
        long start = System.nanoTime();
        var result = new FormatterResult();
        result.setVersion(versionString());
        try {
            var f = new Formatter();
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
