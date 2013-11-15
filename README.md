Julekarenalender v2.0.0
=======================

Julekarenalender is a secret santa desktop application written in Java and Scala. It is adapted for the holidays, but can be used for any purpose that involves random drawing of winners. It is ideal for a project team or office with access to a TV connected with a computer.

Features:

* One-armed bandit style wheel with random winner draw
* Bonus wheel for custom purpose
* User management and winner tracking

The purpose of this project is to cooperate on writing code, learning Scala and migrate a desktop application from Java to Scala.

**Please note:** All active development takes place in the `develop` branch, which currently holds the in-development Scala version.
Stable releases are merged to [`master`](https://github.com/joakibj/julekarenalender/tree/master) and tagged. The development branch for the Java version of Julekarenalender can be found in the [`julekarenalender-java`](https://github.com/joakibj/julekarenalender/tree/julekarenalender-java) branch (This branch is **only** for bugfixes to the Java version).

The last stable release was v1.3.1 and [can be found here](https://github.com/joakibj/julekarenalender/releases/tag/v1.3.1).

Prerequisites
-------------

The tools needed to build julekarenalender are:

* Java 6 JRE
* [sbt](http://www.scala-sbt.org/)

`sbt` will fetch itself and the required Scala compiler.

Building
--------

Start sbt with:

    sbt

The following commands are used in the `sbt` command prompt.

Compile and run tests with:

    compile

    test

Build the application distributable with:

    assembly

The packaged .jar can be found in `julekarenalender\target\scala-2.10`

Configuration
-------------

TODO: Write this in english when it is ready

Usage
-----

TODO: Write this in english when it is ready

Roadmap
-------

Goal: Migrate application from Java to Scala and play with new technologies along the way.

Please refer to the [issue list](https://github.com/joakibj/julekarenalender/issues?state=open) on github for a more comprehensive overview.

######v2.0.0

- [x] Migrated from maven to sbt
- [X] SQLite as persistent datastore instead of CSV
- [X] Slick integration with persistent datastore, as the Configuration repository
- [ ] Configuration module UI to set up participants and track winners. TBD, either command-line based, webapp or GUI module.
- [/] Supply self-contained jar that has a minimum set of configuration to run
- [x] Package jar with dependencies
- [ ] Features needed for holidays 2013

######v2.1.0

- [ ] Swing/AWT GUI swapped with ScalaFX

Contributing
------------

Pull requests are always welcome. :-)

How you can contribute:

* Implement missing items on the [issue list](https://github.com/joakibj/julekarenalender/issues?state=open).
* Graphics
* Feature requests
* Report bugs

To contribute code: fork `develop`, create your own `feature/branch`, submit a pull request

History
-------

Julekarenalender was created by Arne C. Jervell in 2008 as a Java project.

It has been used for several years at the NAV Arena project during the holidays from 2008-2011. In 2012-2013 it was used at the NAV Brev&Arkiv project.

The current maintainer is Joakim Bjørnstad who is porting the application to Scala.

Other contributors:

* Esben Stenwig
* Lotta Nordling

License
-------

MIT License.

Please see the LICENSE file for the license text in verbatim.
