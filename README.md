This project illustrates loading of LLVM IR into the GraalVM using Sulong. To run it, you need [sbt](https://www.scala-sbt.org/) or IntelliJ Idea (Community Edition is sufficient) with standard Scala plugin.

You also need:
* [GraalVM](https://www.graalvm.org/downloads/) (once again, Community Edition is sufficient). For now, its path is hardcoded in my build script as `../graalvm-1.0.0-rc1/`
* Clang (if you want to rebuild the `.bc`-files). I have used Clang 6 version from standard Ubuntu repositories
* The SQLite Amalgamation distribution is already included in `3rdparty`

I have tested this on Ubuntu 18.04 LTS (64 bit).

This repository was prepared as an example project for [this article](https://habr.com/post/358700/) (in Russian: "GraalVM: смешались в кучу C и Scala…").
