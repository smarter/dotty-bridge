## dotty-bridge

### Overview

This is an sbt compiler bridge for [Dotty](https://github.com/lampepfl/dotty),
it allows you to compile your code using Dotty instead of Scala 2.

### Implementation status

- [X] Minimal `sbt compile` support
- [ ] Incremental compilation (currently, the full sources will always be recompiled)
- [ ] `sbt console` support (not possible until we have a Dotty REPL)
- [ ] `sbt doc` support (not possible until we have a Dotty doc tool)

### Usage

See https://github.com/smarter/dotty-example-project for an example.

### Discuss

Feel free to come chat with us on the
[Dotty gitter](http://gitter.im/lampepfl/dotty)!
