# Compile `open-java-format` into a Wasm Module

This demo illustrates how to use the new experimental WebAssembly backend for GraalVM to compile the [open-java-format](https://github.com/openjavaformat/open-java-format) tool into a Wasm module that can then run in the browser. [Check out the live demo here](https://cobr123.github.io/open-java-format-wasm-demo/web/).

## Prerequisites

This demo requires:

1. Oracle GraalVM for JDK 25 or later.
    For example, using SDKMAN!: `sdk install java 25.0.1-graal`
2. The [Binaryen toolchain](https://github.com/WebAssembly/binaryen) in version 119 or later and on the system path.
    For example, using Homebrew: `brew install binaryen`

## Run `open-java-format` in the Browser

1. Build the Wasm module with the `open-java-format-web` profile:
    ```bash
    mvn -Popen-java-format-web package
    ```
    This command generates a Wasm file and a corresponding JavaScript binding in the `web` directory.

2. Run a local web server in the `web` directory:
    ```bash
    cd web
    jwebserver
    ```
   This will serve the `web` directory locally on port `8000`.

3. Navigate to http://localhost:8000 to format Java programs in the browser.

## See also
- [javac](https://github.com/graalvm/graalvm-demos/blob/master/web-image/javac/README.md)
- [web-image](https://github.com/oracle/graal/tree/master/web-image)
