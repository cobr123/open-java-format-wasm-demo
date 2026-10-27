/*
 * Copyright (c) 2026, Oracle and/or its affiliates.
 *
 * Licensed under the Universal Permissive License v 1.0 as shown at https://opensource.org/license/UPL.
 */


package com.example;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.palantir.javaformat.java.Formatter;
import com.palantir.javaformat.java.Main;
import org.graalvm.webimage.api.JS;
import org.graalvm.webimage.api.JSBoolean;
import org.graalvm.webimage.api.JSObject;
import org.graalvm.webimage.api.JSString;


public class WebMain {
    public static final JSObject COMPILE_BUTTON = getElementById("format");
    public static final JSObject OUTPUT = getElementById("formatted_source");
    public static final JSObject OUTPUT_TIME = getElementById("formatted_source_time");
    public static final JSObject INPUT = getElementById("source");

    public static void main(String[] args) {
        addEventListener(COMPILE_BUTTON, "click", e -> compileCallback());
        addEventListener(INPUT, "keydown", e -> {
            if (e.get("ctrlKey") instanceof JSBoolean b && b.asBoolean() && e.get("key") instanceof JSString jsString && "Enter".equals(jsString.asString())) {
                ((JSObject) e.get("preventDefault")).call(e);
                compileCallback();
            }
        });
        setDisabled(false);
    }

    private static String versionString() {
        return "open-java-format: Version " + Main.class.getPackage().getImplementationVersion();
    }

    private static void compileCallback() {
        resetOutput();
        setDisabled(true);

        runAsync(() -> {
            long start = System.nanoTime();
            try {
                var f = Formatter.create();
                var res = f.formatSource(getSource());

                long nanos = System.nanoTime() - start;
                double millis = nanos / 1e6;
                setOutputTime("SUCCESS " + millis + "ms (" + versionString() + ")");
                appendOutput("");
                appendOutput(res);
                highlightCode();
            } catch (Throwable e) {
                long nanos = System.nanoTime() - start;
                double millis = nanos / 1e6;
                appendOutput("ERROR " + millis + "ms (" + versionString() + ")");
                appendOutput("");
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                appendOutput(sw.toString());
            } finally {
                setDisabled(false);
            }
        });
    }

    /**
     * Runs the given {@link Runnable} in {@code setTimeout} without delay.
     * <p>
     * Use this to let the browser repaint before running other code (otherwise repainting is
     * blocked until the Java code returns).
     */
    @JS.Coerce
    @JS("setTimeout(r, 0);")
    private static native void runAsync(Runnable r);

    private static void runWithUncaughtHandler(Runnable r) {
        try {
            r.run();
        } catch (Throwable e) {
            System.err.println("Uncaught exception in event listener");
            e.printStackTrace();
        }
    }

    @JS.Coerce
    @JS("o.addEventListener(event, (e) => handler(e));")
    static native void addEventListenerImpl(JSObject o, String event, EventHandler handler);

    static void addEventListener(JSObject o, String event, EventHandler handler) {
        addEventListenerImpl(o, event, e -> runWithUncaughtHandler(() -> handler.handleEvent(e)));
    }

    @JS.Coerce
    @JS("return document.getElementById(id);")
    public static native JSObject getElementById(String id);

    @JS.Coerce
    @JS("elem.setAttribute(attribute, value);")
    public static native void setAttribute(JSObject elem, String attribute, Object value);

    @JS.Coerce
    @JS("highlightCode();")
    public static native void highlightCodeImpl();

    public static void highlightCode(){
        try {
            highlightCodeImpl();
        } catch (Throwable e) {
            if (e.getLocalizedMessage().contains("highlightCode is not defined")) {
                System.err.println(e.getLocalizedMessage());
            } else {
                System.err.println("Uncaught exception in highlightCode");
                e.printStackTrace();
            }
        }
    }

    public static String getSource() {
        return ((JSString) INPUT.get("innerText")).asString();
    }

    public static void resetOutput() {
        OUTPUT.set("innerHTML", "");
        OUTPUT_TIME.set("innerHTML", "");
    }

    public static void appendOutput(String val) {
        OUTPUT.set("innerHTML", ((JSString) OUTPUT.get("innerHTML")).asString() + val + '\n');
    }

    public static void setOutputTime(String val) {
        OUTPUT_TIME.set("innerHTML", val);
    }

    private static void setDisabled(boolean state) {
        setAttribute(INPUT, "contenteditable", JSBoolean.of(!state));
        COMPILE_BUTTON.set("disabled", JSBoolean.of(state));
    }

}

@FunctionalInterface
interface EventHandler {
    void handleEvent(JSObject event);
}
