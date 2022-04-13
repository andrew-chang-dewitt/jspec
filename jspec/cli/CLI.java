package jspec.cli;

import java.io.File;
import java.io.IOException;
import java.lang.Class;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.concurrent.Callable;
import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import jspec.vendor.picocli.CommandLine;
import jspec.vendor.picocli.CommandLine.Command;
import jspec.vendor.picocli.CommandLine.Option;
import jspec.vendor.picocli.CommandLine.Parameters;

import jspec.lib.Group;
import jspec.lib.Runner;

@Command(
  name = "jspec",
  mixinStandardHelpOptions = true,
  description = "Discovers & runs tests in the current directory tree."
)
public class CLI implements Callable<Integer> {
  File cwd;
  Runner runner;

  @Option(
    names = { "-o", "--out", "--outfile" },
    description = "A file to save the output to instead of printing to stdout."
  )
  File outFile;

  @Option(
    names = {"-c", "-concise"},
    defaultValue = "false",
    description = "Restrict output to concise reporting only; includes progress indicator, reports on any failures & stats, but skips reporting list of all tests ran. Defaults to ${DEFAULT-VALUE}."
  )
  boolean concise = false;

  @Parameters(
    index = "0",
    defaultValue = "**/*Spec.java",
    description = "Glob pattern for finding test files,\ndefaults to `${DEFAULT-VALUE}`."
  )
  String pattern;

  @Override
  public Integer call() {
    this.discover(this.cwd, this.pattern);
    this.run();

    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new CLI()).execute(args);
    System.exit(exitCode);
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
    } catch (DiscoveryError exc) {
      System.err.println(exc.getMessage());
      System.err.println("Searched in " + this.cwd);
    }
  }

  ArrayList<Group> compileAndInitFiles(ArrayList<Path> paths) throws DiscoveryError {
    // set up compiler & java file manager
    JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    StandardJavaFileManager fileManager =
      compiler.getStandardFileManager(null, null, null);
    // set up source files from Paths
    ArrayList<File> srcs = new ArrayList<File>();
    paths.forEach(path -> srcs.add(new File(path.toString())));

    // compile the file
    try {
      compiler.getTask(
        null,
        fileManager,
        null,
        null,
        null,
        fileManager.getJavaFileObjectsFromFiles(srcs)
        ).call();
    } catch (IllegalStateException exc) {
      throw new DiscoveryError("No matching files found for pattern:\n   " + this.pattern);
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

class DiscoveryError extends Exception {
  DiscoveryError(String msg) {
    super("Error encountered while discovering files: " + msg);
  }
}
