package jspec.cli;

import java.io.File;
import java.io.IOException;
import java.lang.Class;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jspec.lib.Group;
import jspec.lib.Runner;

public class CLI {
  File cwd;
  Runner runner;
  ArgParser args;

  public static void main(String[] args) {
    List<String> argsList = Arrays.asList(args);
    if (argsList.contains("--help") || argsList.contains("-?")) {
      CLI.usage();
    } else {
      try {
        // initialize the CLI
        CLI cli = new CLI(args);

        // discover spec files & add to runner
        cli.discover(cli.cwd, cli.args.getPattern());
        // run the tests & print results
        cli.run();
      } catch (InvalidArgumentError exc) {
        System.err.println(exc);
        CLI.usage();
      }
    }
  }

  static void usage() {
    System.out.println("Usage: java -ea jspec [options]");
    System.out.println("           (to run tests in any file matching the pattern \"**/*Spec.java\")");
    System.out.println("   or  java -ea jspec [options] <pattern>");
    System.out.println("           (to run tests in any file matching the given pattern)");
    System.out.println("");
    System.out.println("where options include:");
    System.out.println("");
    System.out.println("--help                  Print this message.");
    System.out.println("  or -?");
    System.out.println("--outFile=<file path>   Save output to file at given path.");
    System.out.println("  or --out=<file path>  Outputs to stdout if not specified.");
    System.out.println("  or -o=<file path>");
    System.out.println("--verbose               Prints verbose output. Defaults to");
    System.out.println("  or -v                 false.");
    System.out.println("");
  }

  CLI(String[] args) throws InvalidArgumentError {
    this.runner = new Runner();
    this.cwd = new File(System.getProperty("user.dir"));
    this.args = this.parse(args);
  }

  ArgParser parse(String[] args) throws InvalidArgumentError {
    ArgParser parser = new ArgParser();

    for (String arg: args) {
      if (arg.startsWith("-")) parser.arg(arg);
      else parser.arg("--pattern=" + arg);
    }

    return parser;
  }

  void discover(File start, PathMatcher pattern) {
    try {
      // init empty list to hold list of found spec files
      ArrayList<Path> specFiles = new ArrayList<Path>();
      // then crawl the file tree & add matching paths to this list
      new Crawler(start, pattern)
        .crawl(
          // match handler
          // when the crawler finds a file that matches the given pattern, do this
          (file) -> specFiles.add(file),
          // error handler
          // each file visited can throw own error, this function is executed
          // each time a file throws an error before continuing to walk the file tree
          (file, exc) -> System.err.println(
            "Exception " + exc + " thrown on file: " + file));

      // compile the discovered files & add the initialized specs to the runner
      this
        .compileAndInitFiles(specFiles)
        .forEach(group -> this.runner.addGroup(group));
    } catch (IOException exc) {
      // The act of crawling a file tree can throw its own IO exception
      // handle that here
      System.err.println("There was an error crawling the file tree: ");
      exc.printStackTrace();
    } catch (CompilationError exc) {
      exc.printStackTrace();
    }
  }

  ArrayList<Group> compileAndInitFiles(ArrayList<Path> paths) throws CompilationError {
    // set up compiler & java file manager
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager =
      compiler.getStandardFileManager(null, null, null);
    // set up source files from Paths
    ArrayList<File> srcs = new ArrayList<File>();
    paths.forEach(path -> srcs.add(new File(path.toString())));

    // compile the file
    try {
      boolean compiled = compiler.getTask(
          null,
          fileManager,
          null,
          null,
          null,
          fileManager.getJavaFileObjectsFromFiles(srcs)
          ).call();

      if (!compiled) throw new CompilationError();
    } catch (IllegalStateException exc) {
      throw new CompilationError();
    }

    // build list of Group instances for each src file
    ArrayList<Group> specs = new ArrayList<Group>();
    paths.forEach(path -> {
      // get name from file
      String srcName = path.toString();
      // get qualified name from Path
      String clsName = srcName
        .substring(cwd.getPath().length() + 1, srcName.length() - 5)
        .replace('/', '.');
      try {
        // get the actual class object, then initialize it
        Class<?> cls = Class.forName(clsName);
        Group grp = (Group)cls.getConstructor().newInstance();
        // add instance to list of groups
        specs.add(grp);
      } catch (ClassNotFoundException exc) {
        System.err.println("Unable to find class file for " + path);
        exc.printStackTrace();
      } catch (IllegalAccessException exc) {
        System.err.println("Error initializing test class " + clsName);
        exc.printStackTrace();
      } catch (InstantiationException exc) {
        System.err.println("Error initializing test class " + clsName);
        exc.printStackTrace();
      } catch (NoSuchMethodException exc) {
        System.err.println("Error initializing test class " + clsName);
        exc.printStackTrace();
      } catch (java.lang.reflect.InvocationTargetException exc) {
        System.err.println("Error initializing test class " + clsName);
        exc.printStackTrace();
      }
    });

    return specs;
  }

  void run() {
    this.runner.run();
    this.runner.resultStrings().forEach(
      (line, i) -> System.out.println(line.getValue()));
  }
}

class CompilationError extends Exception {
  CompilationError() {
    super("Error encountered in compiling test files, see output above.");
  }
}
