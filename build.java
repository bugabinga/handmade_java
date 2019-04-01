import static java.lang.System.out;
import static java.lang.System.err;
import static java.nio.file.Paths.get;
import static java.nio.file.Files.deleteIfExists;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Files.exists;
import static javax.tools.ToolProvider.getSystemJavaCompiler;
import static java.text.MessageFormat.format;

import java.nio.charset.*;
import java.nio.file.*;
import java.nio.file.attribute.*;
import java.util.*;
import java.io.*;
import javax.tools.*;

class build
{
  public static void main(String[] args)
  throws IOException
  {
    out.println("BUILDING THE HANDMADE JAVA PROJECT (LOL)!");
 		var src = get("src");
 		var mainMod = get("src/main/module-info.java");
 		var main = get("src/main/main/Main.java");
 		var bld = get("bld");
 		var locale = Locale.getDefault();
    var utf8 = StandardCharsets.UTF_8;
		/*
		* These flags are the same as one would use with the `javac` cli interface.
		* TODO: this is a debug build right now. we will need different flags for other builds (release,test,etc.)
		* These settings are unrealistically strict right now.
		* The idea is to start out with a very strict set of rules.
		* And then, as we get increaingly annoyed by stupid rules, we deactivate those.
		* This should converge to a resonable set of rules.
		* TODO: different parts of the application probably will require different strictness. we can apply them per package/module/naming-pattern/...
		*/
		var options = List.of(
			"--module-source-path", src.toString(), // input location of all source files. they have to be modules!
			"-d", bld.toString(), //output class files to build folder
			"-g", //generate debug info
			"-Werror", //quit on warnings
			"-Xdoclint:all", //warn about malformed docs
			"--doclint-format", "html5", //use modern docs
			"-Xlint:all", //lint our code
			"-Xpkginfo:always", //require package-info.java files so that generated Javadoc gets nice comments for packages.
			"-verbose", //to learn what happens
			"--limit-modules", "java.base", //by limiting the modules, we prevent loading of unused ones.
			"-deprecation", //print uses of deprecated code
			"-encoding", utf8.toString() //expected encoding of source files
			);
    var javac = getSystemJavaCompiler();
		var diagnostics = new DiagnosticCollector<JavaFileObject>();
    var file_manager = javac.getStandardFileManager(diagnostics, locale, utf8);
    var compilation_units = file_manager.getJavaFileObjects(mainMod, main);
    deleteDir(bld);
    javac.getTask(new PrintWriter(err), file_manager, diagnostics, options, null, compilation_units).call();
    diagnostics.getDiagnostics().stream()
    .map(d -> format("{0} [{1}] ''{2}:{3}'' MSG: {4}",
    								 d.getKind(),
    								 d.getCode(),
    								 d.getSource().getName(),
    								 d.getLineNumber(),
    								 d.getMessage(locale)))
	  .forEach(err::println);
  }

  static void deleteDir(Path dir)
  throws IOException
  {
	  if(!exists(dir)){
			return;
	  }
	  
  	walkFileTree(dir,
      new SimpleFileVisitor<>() {
        @Override public FileVisitResult postVisitDirectory( Path dir, IOException __)
        throws IOException
        {
            deleteIfExists(dir);
            return FileVisitResult.CONTINUE;
        }
         
        @Override public FileVisitResult visitFile( Path file, BasicFileAttributes __)
        throws IOException
        {
            deleteIfExists(file);
            return FileVisitResult.CONTINUE;
        }
    });
 		assert !exists(dir): "The directory still exists!";
  }
}
