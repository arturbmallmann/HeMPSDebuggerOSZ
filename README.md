# GraphicalDebugger

MPSoC Debugger by GAPH.

This is a Maven project, it can be loaded on Netbeans or Eclipse of any version.

When compiling or building the project maven will download the dependencies: AbsoluteLayout.

If 'mvn package' is called, the .jar file of the project will be generated inside the project folder 'target'. The dependencies will be pasted inside 'target/lib' before building.

After running 'mvn install' dist folder will be generated with the package and dependencies, ready to be distributed.
