/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package xsbt

import xsbti.{ AnalysisCallback, Logger, Problem, Reporter, Severity }
import xsbti.compile._
import Log.debug
import java.io.File

import dotty.tools.dotc.{ Main => DottyMain }

final class CompilerInterface {
  def newCompiler(options: Array[String], output: Output, initialLog: Logger, initialDelegate: Reporter, resident: Boolean): CachedCompiler =
    new CachedCompiler0(options, output, resident)

  def run(sources: Array[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, delegate: Reporter, progress: CompileProgress, cached: CachedCompiler): Unit =
    cached.run(sources, changes, callback, log, delegate, progress)
}

private final class CachedCompiler0(args: Array[String], output: Output, resident: Boolean) extends CachedCompiler {
  val outputArgs =
    output match {
      case multi: MultipleOutput =>
        ???
      case single: SingleOutput =>
        List("-d", single.outputDirectory.getAbsolutePath.toString)
    }

  def commandArguments(sources: Array[File]): Array[String] =
    (outputArgs ++ args.toList ++ sources.map(_.getAbsolutePath).sortWith(_ < _)).toArray[String]

  def run(sources: Array[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, delegate: Reporter, progress: CompileProgress): Unit = synchronized {
    run(sources.toList, changes, callback, log, progress)
  }
  private[this] def run(sources: List[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger, compileProgress: CompileProgress): Unit = {
    debug(log, args.mkString("Calling Dotty compiler with arguments  (CompilerInterface):\n\t", "\n\t", ""))
    val reporter = DottyMain.process(commandArguments(sources.toArray))
    if (reporter.hasErrors) {
      throw new InterfaceCompileFailed(args, Array())
    }
  }
}

class InterfaceCompileFailed(override val arguments: Array[String], override val problems: Array[Problem]) extends xsbti.CompileFailed {
  override val toString = "Compilation failed"
}
