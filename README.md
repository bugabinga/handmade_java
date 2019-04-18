# Handmade Java Development

What would happen if we explored the act of code development in Java from first
principles?

Let us find out\!

This project explores ideas around workflows, code editing, building, dependency
management and all the other things “modern” developers can take for
granted. Armed with nothing but the latest JDK, we set out to build a
non-trivial toy-project.

## Ground rules

  - It needs to be x-platform. Why else would we inflict the joy of Java on us?
  - No dogma. OOP, FP, BLA? We do not care\! Whatever works best to make the JVM
    happy. JVMOP\! You heard it here first.
  - No third party dependencies. The JDK is rich and vast. Also, by hand-coding
    everything, even a toy project will accumulate some meat.
  - 
  - Let’s go nuts\! Free from naming conventions, usual project layout and
    “best practices”, we can experiment with *wild* ideas\! Nothing is off
    the table\!

### Performance goals

While it may sound alien, to hear “Java” and “performance” in the same
sentence, a large part of this exploration it to prove that badly performing
Java applications are mostly a product of culture, not the technology.
Technology and culture are probably linked and converge toward each other time.
However, part of the fun of this project will be to learn how fast Java can
actually be (both application and development)

The term *performance* is very vague, so let us specify. The following aspects
of performance are important to us:

  - Application startup time. Java is notoriously bad in this sector (\[link to
    benchmarks\]), which means here exists room to grow.
    > `jlink` allows to build minimal java application images. What kind of
    > startup times are realistic here? Turns out, not much. Testing an image
    > with `java.base` and `jdk.compiler` yielded no measurable difference in
    > wall time.
  - RAM usage. Also one of Javas most cited weaknesses. We will try to keep it
    as low as practically possible. TODO: Come up with a value.
  - Compile time. A often overlooked aspect of the development workflow in Java.
    However, we assume that a fast feedback cycle is very valuable, so we aim to
    keep the build time of the *debug* build under a second. The *release* build
    is not bound by wall time since generation of optimal byte-code is most
    important here. All *unit tests* have to finish under 30 seconds, to
    increase the likelihood of a meaningful and often executes test suite.
    *integration tests* and *benchmarks* are not time bound because it is more
    important for those to be thorough than fast.
  - Most importantly: Human perception of latency\! Latency in modern software
    is the biggest problem nobody talks about (\[links, to , resources\]). There
    are many speculations about how we got to this point (latency is hard to
    measure, perceive, understand, etc.). However, logically speaking, latency
    is always accumulative. That means, there is never code that *reduces*
    latency. More code == more latency. Therein lies the danger of layered
    architectures, that dominate the software landscape today (\[the shit
    sandwich\]). Of course, as application developers, there is probably no way
    to solve this problem holistically. But we will stay vigilant to keep
    latency to a minimum. This necessitates taking measurements via benchmarks.

> The hard numbers given in these perf goals are obviously meaningless without a
> reference to a host machine. However, picking some random reference machine is
> unnecessarily restrictive. For that reason those goals are hard-coded into the
> build system. Exceeding those times will fail the build. So, whatever machines
> the project is hacked on, we will be forced to comply. Resistance is
> *fertile*…

### Workflow goals

This project will experiment with new project layouts and naming conventions and
other things of that nature. Since we cannot assume, that existing tools will
support our crazy ideas, and to free developers to choose their favorite
editor/IDE, all conventions, constraints and rules of any kind will be enforced
through our build system.

## Where to begin

The first challenge will be to compile the code. Our tool of choice must
obviously be `javac`. But how do we invoke it on every platform Java supports?

### Ideas for a custom build system/compiler invoker

  - `javac` supports a neat way to pass command line arguments: \[@file
    references\] With these, we could imagine a different *@file* for *build*,
    *debug*, *test* and so on. However, anything beyond compiling (formatting,
    code generation, deployment) will hardly be possible.
  - A combo of semantically equivalent `sh`/`bat`-scripts. These would solve the
    bootstrapping problem, but nobody likes to maintain two systems for one
    task. Besides, neither `sh` nor `bat` are part of the JDK ;).
  - The `jdk.compiler` package gives us high level access to the primitives
    `javac` itself is delegating to. What better way to give this project the
    handmade feel, than to roll our own `javac` front-end. Coupled with the
    ability of the `java` interpreter to run and compile Java code in one fell
    swoop, this is the path we shall explore. What could go wrong\!? When done,
    our “build system” will compile the entire project with: `java
    build.java`.
    > We will tackle *debug*, *test*, *benchmark* etc. later. `java` does not
    > care about the file extension `.java` when given the `--source` flag.
    > Coupled with \[@file references\] this could lead to invocations like:
    > `java @release build`. Where `debug` is a plain text file containing the
    > `--source` flag and other build arguments and `build` is a file containing
    > Java code (the “build system”). Let us come back to these ideas
    > when/if we need the complexity later on.

### What does a build system bring to the table

Besides being slow and bloated, typical build systems (think \[maven\],
\[gradle\] and \[friends\]) actually solve incredibly hard problems.

1.  Bootstrapping itself on many platforms.
2.  Providing a uniform (think: *predictable*) interface to “building”
    (henceforth, the term **building** shall refer to all typical task a build
    system performs).
3.  Predefined implementations for common tasks: compiling, testing,
    benchmarking, linting, formatting, fixing, deploying…
4.  Dependency management
5.  Scripting custom tasks

Here is how `java build.java` is expected to perform on the above qualities:

1.  Bootstrapping is solved by `java`.
2.  We cannot and will not provide a uniform interface for all Java code on
    earth. Quite the opposite. Our build system will be specifically tuned to
    our needs. The whole point of this exercise is to explore a new approach to
    “doing Java”. Exploration and Standardization are diametrically opposed.
3.  We will implement all these tasks, if, and only if, we deem them necessary,
    to the extend we deem it necessary.
    > Once this is done, it would be interesting to compare build times to a
    > more traditional approach, e.g. \[maven\].
4.  Since we have no third party code, we have no need for dependency
    management. But even if that was not so, or we change our minds about that
    later on, we would probably explore the \[maven artifact protocol\]. The
    reason are:
      - **Dependency Management** is probably too big a task to solve alone.
      - The \[maven artifact protocol\] distributes immutable artifacts
        (think:*jars*). Those are desirable for trusted, reproducible and stable
        builds.
      - Buildings against artifacts (*jars*) is probably faster than buildings
        against sources (How to substantiate that statement?)
      - The \[maven repository\] undergoes some \[vetting\] and active scrutiny
        by its \[admins\].
5.  We will be “scripting” all the tasks ;).

### What the toy project shall be

We will be *reverse justifying* our choice of Java by enumerating its strengths
and picking a scope and project goal accordingly.

  - X-platform GUI framework
  - Battle tested and high-level *concurrency* API
  - Rich ecosystem of tools, libraries and *ambient knowledge*
  - The Java Runtime Environment is very dynamic and introspect-able
    (\[reflection\], \[code generation\], \[JMX\])
  - Multiple and highly tunable GCs
  - Rich standard library

Obviously, our toy project will need a GUI component. Why otherwise use Java, if
not for the lovable, but slightly awkward on all platforms equally, GUI
framework \[Swing\].

> RIP \[JavaFx\]: You will be missed. Concurrency is best explored in network
> service type software, so we will need a server thingy. The rich ecosystem is
> what we are trying to escape here, so **pass**. A introspect-able JVM suggests
> that our GUI component needs to be able to monitor, maybe control, the server
> thing. And the vast space of GC configuration necessitates a benchmarking
> suite to learn about optimal conditions for them. The rich JDK is what will
> allow us to get away with forgoing third party dependencies, without costing
> us years of precious life force.

#### Ideas for the toy project

  - the canonical *to do*-application Adding a sever-side component to that is
    really pushing it. Besides, it is boring.
  - chat application boooring.
  - An online market for GUI widget and a customizable widget to place those
    widgets on See \[The Widget Market\] for an explanation of a prototype. TWM
    will stress the following features of Java (the JVM):
  - x-platform GUI
  - dynamic runtime: integration of widget during runtime (plugins?)
  - monitoring: monitor health and status of server side component
  - Concurrency: network calls, keeping UI response during IO
  - opportunity for multiple GCs: GUI, server and standalone mode

## The minimal Java module

What is the simplest `Hello World`-type module one can build with Java modules?
2 files, 2 folders and one build invocation.

First, a folder named after the module is needed:

``` sh
mkdir <name>
cd <name>
```

Then, a file called `module-info.java` declares the modules name, dependencies
and exports.

``` java
module <name> {}
```

The simplest module depends on nothing, except for `java.base` implicitly and
exports nothing. Now, we would like to create a main class file with the typical
`main(String[] args)` entry point. However, there is a problem. A module
requires at least one package and disallows classes in its “default
package”. So we create a package, named after the module, because at this
point, there is no other sensible name to choose.

``` sh
mkdir <name>
cd <name>
```

Here we create `Main.java`:

``` java
package main;
class Main
{
  public static void main(String[] command_line_arguments){}
}
```

This gives the following file hierarchy:

    - <name>
      - module-info.java 
      - <name> 
        - Main.java

Build and run it with:

``` sh
cd ../..
javac --module-source-path . --module <name> -d .
java --module-path . --module <name>/<name>.Main
```

TODO: link to javac docs

This probably seems like a lot of boilerplate for something simple as getting
started. But, while certainly true, it is also irrelevant when considering the
number of modules one does likely write. Assuming a module count of a medium to
large sized application to be between 3 and 300 modules, the overhead introduced
by the above ceremony is negligible, when compared to overall code amount.

### Naming

Following the conventions of a programming community is often a good idea, even
when they superficially do not make sense. If one wishes to collaborate with
others, the familiarity that the use of conventions affords, is much more
valuable than most tightly scoped technical choices.

However, here we set out to explore Java development from first principles.
First, we will discuss the current conventions in the Java programming community
and then we will develop our own set of “conventions”, guided by values we
will define.

#### Names of packages and modules

The Java community adopted the following naming convention for packages, that
now is also carried over to modules.

`reverse domain of your organization`.\[`name of the project`.\]`actual semantic
name for the package`

Some examples:

  - `com.google`.`guava`.`collection`
  - `org.junit`.`test`
  - `com.github`.`mbknor`.`json_schema_v4`

As for the actual reasons why and how these conventions got adopted so
universally, we can, of course, only speculate. However, by analyzing what
problems this naming pattern fixes, we can get a good indicator. More
importantly, we develop a informed foundation to invent our own conventions.

In a Java runtime, classes used to live in the *classpath*. The *classpath* is a
flat list of classes that get referenced by their package name + class name. It
does not take long for a software project to reach a size, where naming
collisions in class names do occur. This is especially true, when using a lot of
third-party code, where you have usually little to no (practical) control over
the names. Coupled with the fact, that a JVM is required to ignore naming
conflicts on the *classpath* (whichever class happens to be loaded first,
“wins”), this presents Java programmers with a maintenance nightmare.
Fortunately, this issue is practically (not technically) solved by the package
naming convention.

It generates unique enough names, while being easy enough to use, so that naming
collisions on the *classpath* are rare enough to not care.

> Of course, you still care about this problem, as soon as the dependency graph
> of your application is large enough, so that a transitive dependency of
> multiple versions appears. But we do not need to care about these sorts of
> issues here. No third-party code :)

The `reverse domain name` part is especially clever because is solved a couple
of issues at once.

  - It delegates to the DNS to figure out roughly unique mappings of domain to a
    “real world entity” (person or organization)
  - It is reversed, because top-level domains used to be very few in number.
    This creates a natural sorting order, that keeps the directory structure of
    packages clean. Because, incidentally, Java packages are encoded as folders
    on a disk.
  - Domain names and packages share similar naming restrictions and more
    importantly, the use of the `.` character is already similarly meaningful in
    Java package names.

The `name of the project` is sometimes redundant so it gets left out.

Enter the *modulepath*. The *modulepath* introduced in Java 9 solves the most
glaring issues with the *classpath* as discussed earlier.

  - duplicate module names are verboten
  - duplicate (exported) package names are also verboten

Technically, Java still supports the *classpath*, but we have no reason to
concern ourselves with it.

In the interest of consistency/familiarity the developers of Java recommend the
same naming conventions for modules as are in use for package names. However,
consider the comical optics of the smallest possible module, following the
conventions:

    - net.bugabinga.handmade
    	- module-info.java
    	- net
    	  - bugabinga
    	    - handmade
    	      - Main.java

A directory listing of a “simple” Java module.

Some observations:

  - It is easy to get confused about the name `net.bugabinga.handmade`. When it
    is a module name; it is encoded as a folder named
    “net.bugabinga.handmade”. When it is a package name; it is encoded as a
    folder-tree “net/bugabinga/handmade. Unless of course you are using a
    typical IDE; then it looks like “net.bugabinga.handmade”. The name is
    duplicated for the module and the (main) package.
  - Conceptually, a module is 1 thing. But one needs to create 6 things
    (files/folders) in order to declare it.
  - The code for a module is (at least) 5 levels down away from its name.

If you are thinking: “Wow, this seems ridiculous…”, you are not wrong.
However, keep in mind, that in practice, every Java developer will only have to
(mentally) deal with these issues once. They will learn and adopt these
conventions though osmosis and then henceforth perceive it as *normal*.

> Ranty tangent: Even when considering the “old world”, where we only had
> *classpath*, it is interesting to observe that the problem the package naming
> convention solved, is really only relevant for libraries. Applications are
> typically consumers of libraries, but themselves never consumed, preventing
> them from provoking naming conflicts. That means, that as long as all
> libraries adhere the the convention, applications are free to choose their own
> naming pattern. However, in practice no application does so. Not even
> proprietary ones\! If this is indicative of the persuasive power of
> conventions or the sheepishness of your average Java developer, I leave to you
> to guess ;).

#### Names in source code

Identifiers in Java use the following naming patterns:

  - **Constants**:
    &#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;upper-case
  - **Types**:&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;capitalized
    camel-case
  - **Variables, Parameters, Fields and Methods**: &#9;non-capitalized
    camel-case
  - **Type Parameters**:
    &#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;&#9;single
    upper-cased letter

The upper-casing of constants is a convention shared by most (C-like) languages.
It probably dates back to another convention: [ALL CAPS for preprocessor
macros](https://github.com/isocpp/CppCoreGuidelines/blob/master/CppCoreGuidelines.md#es9-avoid-all_caps-names).
Constants tend to be defined a preprocessor macros and the need to clearly
distinguish preprocessor macros from source code is intuitive, if you have
experience inconsistent and/or faulty macros.

Java has no preprocessor. Technically, it does not even have “constants”.
There is no `const` keyword denoting some compile-time construct and `static
final` fields, the ones that are called “constants”, are mutable at runtime
via
[reflection](https://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection#3301720).
Nevertheless, Java has inherited the convention of upper-casing constants.

Types, variables, parameters, fields and methods are typically camel-cased
(sometimes called PascalCase; probably because this style of naming originated
in Pascal?). To distinguish types from other names, they are capitalized.

The need for camel-casing, or the other popular style “snake\_casing”, is
explained with the fact, that virtually all compilers/interpreters are using
whitespace as token delimiters. The consequence is that it is syntactically
difficult for programming languages to allow names with whitespace in them.
However, programmers do have a need for multi-word names for things and because
names like “databaseconnectionport” are confusing and hard to read, we write
it as “databaseConnectionPort” or “database\_connection\_port”. Compared
to snake-case, camel-case is more terse but harder to read. This becomes
apparent, when comparing very long
names:

``` java
public static void testThatEverythingIsOkGivenTheDataWasSentWhenIPushedTheButtonThenSomeoneAcknowledgedIt(){...}

public static void test_that_everything_is_ok_given_the_data_was_sent_when_i_pushed_the_button_then_someone_acknowledged_it(){...}
```

Practitioners of camel-case also sometimes get confused about how to handle
abbreviations in names. Should it be `theVIPLounge` or `theVipLounge`? This is
not a problem with snake-case, since everything is expected to be lower-cased
(`the_vip_lounge`).

Type parameters are upper-cased to be consistent with types. The use of single
letter names might seem antithetical to other Java practices, however trying to
assign concrete names to such abstract things is simply not useful. Single
letter names allow us to distinguish them from one another, and that is
typically all we need.

#### Inventing new naming conventions

TODO: stick to JDK style with some exceptions: snake-case variables, private
fields and test methods? TODO: how to name covariant/contravariant type params?
TODO: drop CONSTANTS naming
