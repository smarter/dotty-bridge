## dotty-bridge

### Overview

This is an sbt compiler bridge for [Dotty](https://github.com/lampepfl/dotty),
it allows you to compile your code using Dotty instead of Scala 2.

### Implementation status

- [X] `sbt compile` support, including [incremental recompilation](http://www.scala-sbt.org/0.13/docs/Understanding-Recompilation.html)
- [ ] `sbt console` support (may involve some changes to the Dotty REPL)
- [ ] `sbt doc` support

### Usage

See https://github.com/smarter/dotty-example-project for an example.

### Discuss

Feel free to come chat with us on the
[Dotty gitter](http://gitter.im/lampepfl/dotty)!
