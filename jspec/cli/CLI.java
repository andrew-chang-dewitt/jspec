package jspec.cli;

import java.io.File;
import java.io.IOException;
import java.lang.Class;
import java.nio.file.Path;
import java.util.ArrayList;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jspec.lib.Group;
import jspec.lib.Runner;

public class CLI {
  File cwd;
  Runner runner;

  public static void main(String[] args) {
    // initialize the CLI
    CLI cli = new CLI();

    // FIXME: for now, both of the below are just the defaults ArgsParser will give
    // FIXME: this will need to be updated to get the starting directory from ArgsParser
    File start = cli.cwd;
    // FIXME: and this to get the pattern from ArgsParser
    String pattern =  "**/*Spec.java";

    // discover spec files & add to runner
    cli.discover(start, pattern);
    // run the tests & print results
    cli.run();
  }

  CLI() {
    this.runner = new Runner();
    this.cwd = new File(System.getProperty("user.dir"));
  }

  void discover(File start, String pattern) {
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
      System.err.println(exc);
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
    boolean compiled = compiler.getTask(
        null,
        fileManager,
        null,
        null,
        null,
        fileManager.getJavaFileObjectsFromFiles(srcs)
      ).call();

    if (!compiled) throw new CompilationError();

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
