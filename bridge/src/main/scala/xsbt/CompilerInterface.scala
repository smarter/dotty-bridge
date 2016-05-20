/* sbt -- Simple Build Tool
 * Copyright 2008, 2009 Mark Harrah
 */
package xsbt

import xsbti.{ AnalysisCallback, Logger, Problem, Reporter, Severity, DependencyContext }
import xsbti.api.SourceAPI
import xsbti.compile._
import Log.debug
import java.io.File

import dotty.tools.dotc.core.Contexts.ContextBase
import dotty.tools.dotc.{ Main => DottyMain }
import dotty.tools.dotc.interfaces._

import java.net.URLClassLoader

final class CompilerInterface {
  def newCompiler(options: Array[String], output: Output, initialLog: Logger,
                  initialDelegate: Reporter, resident: Boolean): CachedCompiler = {
    // The classloader that sbt uses to load the compiler bridge is broken
    // (see CompilerClassLoader#fixBridgeLoader for details). To workaround
    // this we construct our own ClassLoader and then run the following code
    // with it:
    //   new CachedCompilerImpl(options, output, resident)

    val bridgeLoader = getClass.getClassLoader
    val fixedLoader = CompilerClassLoader.fixBridgeLoader(bridgeLoader)
    val cciClass = fixedLoader.loadClass("xsbt.CachedCompilerImpl")
    cciClass.getConstructors.head
      .newInstance(options, output, resident: java.lang.Boolean)
      .asInstanceOf[CachedCompiler]
  }

  def run(sources: Array[File], changes: DependencyChanges, callback: AnalysisCallback, log: Logger,
          delegate: Reporter, progress: CompileProgress, cached: CachedCompiler): Unit =
    cached.run(sources, changes, callback, log, delegate, progress)
}

class CachedCompilerImpl(args: Array[String], output: Output, resident: Boolean) extends CachedCompiler {
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
    val ctx = (new ContextBase).initialCtx.fresh
      .setSbtCallback(callback)
    val cl = getClass.getClassLoader.asInstanceOf[URLClassLoader]

    val reporter = DottyMain.process(commandArguments(sources.toArray), ctx)
    if (reporter.hasErrors) {
      throw new InterfaceCompileFailed(args, Array())
    }
  }
}

class InterfaceCompileFailed(override val arguments: Array[String], override val problems: Array[Problem]) extends xsbti.CompileFailed {
  override val toString = "Compilation failed"
}
