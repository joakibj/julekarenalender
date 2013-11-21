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

* Java 7 JDK
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

First time configuration of julekarenalender requires a set of images representing the participants to be put in an `images` folder where the julekarenalender jar is located.
Each image should have the same filename as the name of the participant. Supported image formats are `png` and `jpg`. Lowercase or uppercase is irrelevant. e.g.:

    julekarenalender-2.0.0/
        julekarenalender-2.0.0.jar
        images/
            Dolly.png
            Donald.jpg
            Huey.jpg
            Dewey.jpg
            Louie.jpg

For first time import of participants, open up a `cmd.exe` or *nix shell and `cd` to the julekarenalender directory and run:

    java -jar julekarenalender-2.0.0.jar --scan

If someone joins late in the drawing period, simply add his image and re-run this command. If julekarenalender detects a new image based on the name, a new participant is added.

If a mistake is made one can reset all configuration with:

    java -jar julekarenalender-2.0.0.jar --reset

Usage
-----

#### Default

![julekarenalender-default](http://joakibj.github.io/julekarenalender/images/julekarenalender-default.PNG)

On the left side is a wheel representing each day in december until christmas eve [0..24]. On the right side is a wheel with each participant.
Simply grab the participant wheel, swipe it down vertically and release to make it spin. The participant wheel will eventually stop and the text will blink red on the winner.

If one wants to draw more than one winner per day, this is configured via the command line. For example, assume day 2 is a monday, starting the application with:

    java -jar julekarenalender-2.0.0.jar 2 3 4 5 6 6

This means that there will be six drawings. One for each workday in the week and two on the friday. This is useful if your team only does one secret santa drawing per workweek.
Note that this means that you can draw winners for any day, at any point.

Julekarenalender tracks winners and excludes anyone that have won previously. There is **only one winner** allowed per participant.

Tapping the ALT key toggles the menu bar. This is to check who won last week.

#### Bonus wheel

![julekarenalender-bonus](http://joakibj.github.io/julekarenalender/images/julekarenalender-bonus.PNG)

The bonus wheel is activated with the `--bonus` option. It can also be used in conjunction with multiple drawings.

    java -jar julekarenalender-2.0.0.jar --bonus

If your team wants some extra spice, julekarenalender can be started with the bonus option. This will add a bonus wheel on the right side that has to be spun after a winner is found.
The bonus wheel can be some sort of extra reward or act as a minigame, be creative!

Per default there are three items in the bonus wheel: an image of a holiday present and two images of a joker cap. This can be overriden by adding custom images in the image folder.
Julekarenalender scans the images folder for any `png` or `jpg` containing "bonus" in the filename. For example:

    julekarenalender-2.0.0/
        julekarenalender-2.0.0.jar
        images/
            bonus0.jpg
            bonus1.jpg
            bonus2.jpg
            bonus3.jpg
            Dolly.png
            Donald.jpg
            Huey.jpg
            Dewey.jpg
            Louie.jpg

This adds four custom bonus images to the bonus wheel.

#### Printed usage

```
Julekarenalender 2.0.0-SNAPSHOT
Usage: julekarenalender [options] [days]

  days
        List of days there should be a draw. Optional
  --scan
        Scans the images/ folder for participants. No GUI is launched
  --bonus
        Enables the bonus wheel
  --debug
        Turns on debug mode
  --reset
        Resets all configuration. Use at own risk! No GUI is launched
  --help
        prints this usage text
```

Roadmap
-------

Goal: Migrate application from Java to Scala and play with new technologies along the way.

Please refer to the [issue list](https://github.com/joakibj/julekarenalender/issues?state=open) on github for a more comprehensive overview.

######v2.0.0 (holiday 2013)

- [x] Migrated from maven to sbt
- [X] SQLite as persistent datastore instead of CSV
- [X] Slick integration with persistent datastore, as the Configuration repository
- [X] Configuration module UI to set up participants and track winners. GUI panel
- [/] Supply self-contained jar that has a minimum set of configuration to run
- [x] Package jar with dependencies
- [/] Features needed for holidays 2013

######v2.1.0 (holiday 2014)

- [ ] Swing/AWT GUI swapped with ScalaFX

Contributing
------------

Pull requests are always welcome. :-)

How you can contribute:

* Make the project more *idiomatic Scala*
* Implement items on the [issue list](https://github.com/joakibj/julekarenalender/issues?state=open)
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
