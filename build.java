import static java.lang.System.out;
import static java.nio.charset.StandardCharsets.UTF_8;

import javax.tools.ToolProvider;

class build {
  private static String[] SOURCES = new String[] {"src/Main.java"};

  public static void main(String[] args) {
    out.println("BUILDING THE HANDMADE JAVA PROJECT!");
    var javac = ToolProvider.getSystemJavaCompiler();
    var file_manager = javac.getStandardFileManager(null, null, UTF_8);
    var compilation_units = file_manager.getJavaFileObjects(SOURCES);
    javac.getTask(null, file_manager, null, null, null, compilation_units).call();
  }
}
