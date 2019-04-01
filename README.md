# Handmade Java Development

What would happen if we explored the act of code development in Java from first principles?

Let us find out!

This project explores ideas around workflows, code editing, building, dependency management and all the other things "modern" developers can take for granted.
Armed with nothing but the latest JDK, we set out to build a non-trivial toy-project.

## Ground rules

- It needs to be x-platform. Why else would we inflict the joy of Java on us?
- No dogma. OOP, FP, BLA? We do not care! Whatever works best to make the JVM happy. JVMOP! You heard it here first.
- No third party dependencies. The JDK is rich and vast. Also, by hand-coding everything, even a toy project will accumulate some meat.
- Let's go nuts! Free from naming conventions, usual project layout and "best practices", we can experiment with _wild_ ideas! Nothing is off the table!

### Performance goals

While it may sound alien, to hear "Java" and "performance" in the same sentence, a large part of this exploration it to prove that badly performing Java applications
are mostly a product of culture, not the technology.
Technology and culture are probably linked and converge toward each other time.
However, part of the fun of this project will be to learn how fast Java can actually be (both application and development)

The term _performance_ is very vague, so let us specify.
The following aspects of performance are important to us:

- Application startup time.
	Java is notoriously bad in this sector ([link to benchmarks]), which means here exists room to grow.
	> `jlink` allows to build minimal java application images. What kind of startup times are realistic here?
	> Turns out, not much. Testing an image with `java.base` and `jdk.compiler` yielded no measurable difference in wall time.
- RAM usage.
  Also one of Javas most cited weaknesses. 
  We will try to keep it as low as practically possible.
  TODO: Come up with a value.
- Compile time.
  A often overlooked aspect of the development workflow in Java.
	However, we assume that a fast feedback cycle is very valuable, so we aim to keep the build time of the *debug* build under a second.
	The *release* build is not bound by wall time since generation of optimal bytecode is most important here.
	All *unit tests* have to finish under 30 seconds, to increase the likelihood of a meaningful and often executes test suite.
	*integration tests* and *benchmarks* are not time bound because it is more important for those to be thorough than fast.
- Most importantly: Human perception of latency!
	Latency in modern software is the biggest problem nobody talks about ([links, to , resources]).
	There are many speculations about how we got to this point (latency is hard to measure, perceive, understand, etc.).
	However, logically speaking, latency is always accumulative.
	That means, there is never code that _reduces_ latency.
	More code == more latency.
	Therein lies the danger of layered architectures, that dominate the software landscape today ([the shit sandwich]).
	Of course, as application developers, there is probably no way to solve this problem holistically.
	But we will stay vigilant to keep latency to a minimum.
	This necessitates taking measurements via benchmarks.
	
> The hard numbers given in these perf goals are obviously meaningless without a reference to a host machine.
> However, picking some random reference machine is unnecessarily restrictive.
> For that reason those goals are hard-coded into the build system.
> Exceeding those times will fail the build.
> So, whatever machines the project is hacked on, we will be forced to comply.
> Resistance is _fertile_... 
	
### Workflow goals

This project will experiment with new project layouts and naming conventions and other things of that nature.
Since we cannot assume, that existing tools will support our crazy ideas, and to free developers to choose their favourite editor/IDE,
all conventions, constraints and rules of any kind will be enforced through our build system.

## Where to begin

The first challenge will be to compile the code.
Our tool of choice must obviously be `javac`.
But how do we invoke it on every platform Java supports?

### Ideas for a custom build system/compiler invoker

- `javac` supports a neat way to pass command line arguments: [@file references]
	With these, we could imagine a different _@file_ for *build*, *debug*, *test* and so on.
	However, anything beyond compiling (formatting, code generation, deployment) will hardly be possible.
- A combo of semantically equivalent `sh`/`bat`-scripts.
	These would solve the bootstrapping problem, but nobody likes to maintain two systems for one task. Besides, neither `sh` nor `bat` are part of the JDK ;).
- The `jdk.compiler` package gives us high level access to the primitives `javac` itself is delegating to.
  What better way to give this project the handmade feel, than to roll our own `javac` front-end.
  Coupled with the ability of the `java` interpreter to run and compile Java code in one fell swoop, this is the path we shall explore.
  What could go wrong!?
  When done, our "build system" will compile the entire project with: `java build.java`.
  > We will tackle *debug*, *test*, *benchmark* etc. later.
  > `java` does not care about the file extension `.java` when given the `--source` flag.
  > Coupled with [@file references] this could lead to invocations like: `java @release build`.
  > Where `debug` is a plain text file containing the `--source` flag and other build arguments and `build` is a file containing Java code (the "build system").
  > Let us come back to these ideas when/if we need the complexity later on.

### What does a build system bring to the table

Besides being slow and bloated, typical build systems (think [maven], [gradle] and [friends]) actually solve incredibly hard problems.

1. Bootstrapping itself on many platforms.
1. Providing a uniform (think: _predictable_) interface to "building" (henceforth, the term __building__ shall refer to all typical task a build system performs).
1. Predefined implementations for common tasks: compiling, testing, benchmarking, linting, formatting, fixing, deploying...
1. Dependency management
1. Scripting custom tasks

Here is how `java build.java` is expected to perform on the above qualities:

1. Bootstrapping is solved by `java`.
1. We cannot and will not provide a uniform interface for all Java code on earth.
	 Quite the opposite.
	 Our build system will be specifically tuned to our needs.
	 The whole point of this exercise is to explore a new approach to "doing Java".
	 Exploration and Standardisation are diametrically opposed.
1. We will implement all these tasks, if, and only if, we deem them necessary, to the extend we deem it necessary.
	 > Once this is done, it would be interesting to compare build times to a more traditional approach, e.g. [maven].
1. Since we have no third party code, we have no need for dependency management.
	 But even if that was not so, or we change our minds about that later on, we would probably explore the [maven artefact protocol].
	 The reason are:
	 - __Dependency Management__ is probably too big a task to solve alone.
	 - The [maven artefact protocol] distributes immutable artefacts (think:_jars_).
	 	 Those are desirable for trusted, reproducible and stable builds.
	 - Buildings against artefacts (_jars_) is probably faster than buildings against sources (How to substantiate that statement?)
	 - The [maven repository] undergoes some [vetting] and active scrutiny by its [admins].
1. We will be "scripting" all the tasks ;).

### What the toy project shall be

We will be _reverse justifying_ our choice of Java by enumerating its strengths and picking a scope and project goal accordingly.

- X-platform GUI framework
- Battle tested and high-level _concurrency_ API
- Rich ecosystem of tools, libraries and _ambient knowledge_
- The Java Runtime Environment is very dynamic and introspect-able ([reflection], [code generation], [JMX])
- Multiple and highly tunable GCs
- Rich standard library

Obviously, our toy project will need a GUI component.
Why otherwise use Java, if not for the lovable, but slightly awkward on all platforms equally, GUI framework [Swing].
> RIP [JavaFx]: You will be missed.
Concurrency is best explored in network service type software, so we will need a server thingy.
The rich ecosystem is what we are trying to escape here, so __pass__.
A introspectable JVM suggests that our GUI component needs to be able to monitor, maybe control, the server thing.
And the vast space of GC configuration necessitates a benchmarking suite to learn about optimal conditions for them.
The rich JDK is what will allow us to get away with forgoing third party dependencies, without costing us years of precious life force.

#### Ideas for the toy project

- the canonical *to do*-application
	Adding a sever-side component to that is really pushing it.
	Besides, it is boring.
- chat application
  boooring.
- An online market for GUI widget and a customizable widget to place those widgets on
	See [The Widget Market] for an explanation of a prototype.
	TWM will stress the following features of Java (the JVM):
	- x-platform GUI
	- dynamic runtime: integration of widget during runtime (plugins?)
	- monitoring: monitor health and status of server side component
	- Concurrency: network calls, keeping UI response during IO
	- opportunity for multiple GCs: GUI, server and standalone mode

## The minimal Java module

What is the simplest `Hello World`-type module one can build with Java modules?
2 files, 2 folders and one build imvocation.

First, a folder named after the module is needed:

```sh
mkdir <name>
cd <name>
```
Then, a file called `module-info.java` declares the modules name, dependencies and exports.
```java
module <name> {}
```

The simplest module depends on nothing, except for `java.base` implicitly and exports nothing.
Now, we would like to create a main class file with the typical `main(String[] args)` entry point.
However, there is a problem.
A module requires at least one package and disallows classes in its "default package".
So we create a package, named after the module, because at this point, there is no other sensible name to choose.

```sh
mkdir <name>
cd <name>
```

Here we create `Main.java`:

```java
package main;
class Main
{
  public static void main(String[] command_line_arguments){}
}
```

This gives the following file hierarchy:

```
- <name>
  - module-info.java 
  - <name> 
    - Main.java
```

Build and run it with:

```sh
cd ../..
javac --module-source-path . --module <name> -d .
java --module-path . --module <name>/<name>.Main
```

TODO: link to javac docs

This probably seems like a lot of boilerplate for something simple as getting started.
But, while certainly true, it is also irrelevant when considering the number of modules one does likely write.
Assuming a module count of a medium to large sized application to be between 3 and 300 modules,
the overhead introduced by the above ceremony is negligible, wehn compared to overall code amount.

### Naming

TODO: app vs lib names


TODO: Fill links
