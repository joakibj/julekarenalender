Julekarenalender v3.0.0
=======================

Julekarenalender is a secret santa desktop application written in Java and Kotlin. 
It is adapted for the holidays, but can be used for any purpose that involves random drawing of winners. 
It is ideal for a project team or office with access to a TV connected with a computer.

Features:

* One-armed bandit style wheel with random winner draw
* Bonus wheel for custom purpose
* User management and winner tracking

The purpose of this project is to cooperate on writing code, learning Kotlin and migrate a desktop application from Java to Kotlin.

**Please note:** All active development takes place in the `develop` branch, which currently holds the in-development Kotlin version.
Stable releases are merged to [`master`](https://github.com/joakibj/julekarenalender/tree/master) and tagged. The development branch for the Java version of Julekarenalender can be found in the [`julekarenalender-java`](https://github.com/joakibj/julekarenalender/tree/julekarenalender-java) branch (This branch is **only** for bugfixes to the Java version).

The last stable release was v2.0.0 and [can be found here](https://github.com/joakibj/julekarenalender/releases/tag/v2.0.0).

Prerequisites
-------------

The tools needed to build julekarenalender are:

* Minimum Java 8 JDK
* [kotlin](https://kotlinlang.org/)
* Gradle


Building
--------

Use Gradle and task shadow/shadowJar to build and create a "fat jar". 
`julekarenalender-3.0.0-all.jar` will be available at `julekarenalender\build\libs`.

Configuration
-------------

First time configuration of julekarenalender requires a set of images representing the participants to be put in an `images` folder where the julekarenalender jar is located.
Each image should have the same filename as the name of the participant. Supported image formats are `png` and `jpg`. 
Lowercase, uppercase, spaces and diacritics is irrelevant. e.g.:

    julekarenalender-3.0.0/
        julekarenalender-3.0.0.jar
        images/
            Dolly.png
            Donald.jpg
            Huey.jpg
            Dewey.jpg
            Louie.jpg
            Åsmünd Leifdïrsæn.jpg

For first time import of participants, open up a `cmd.exe` or *nix shell, `cd` to the julekarenalender directory and run:

    java -jar julekarenalender-3.0.0.jar --scan

If someone joins late in the drawing period, simply add her image and re-run this command. 
Then julekarenalender detects a new image based on the name, a new participant will be added.

Make use of the `--dryRun` option to play around without any changes saved to the database.
Use it in combination with other options.

     java -jar julekarenalender-3.0.0.jar --dryRun --scan

To list the stored information about the participants run:

    java -jar julekarenalender-3.0.0.jar --list

In the chance that a mistake is made one can reset all configuration with:

    java -jar julekarenalender-3.0.0.jar --reset

Both `--list` and `--reset` will not launch the GUI.

Test data
-------------

To comply with GDPR the Julekarenalender now comes with its own Synthetic Test Data Generator.
Useful for both development and getting familiar with the Julekarenalender without dealing with real image files.

Test participants are automatically added if either occour:
* started with the `--scan` options and no images is found
* normal run and the database is empty

Works well in combination with the `--dryRun` (`-x`) command to avoid saving any to the database. 

Usage
-----

#### Default

![julekarenalender-default](http://joakibj.github.io/julekarenalender/images/julekarenalender-default.PNG)

To draw once per day, simply run julekarenalender by double-clicking in Windows or running the jar in `cmd.exe` or a *nix shell:

    java -jar julekarenalender-3.0.0.jar

On the left side is a wheel representing each day in december until christmas eve [1..24]. On the right side is a wheel with each participant.
Simply grab the participant wheel, swipe it down vertically and release to make it spin. The participant wheel will eventually stop and the text will blink red on the winner.

If one wants to draw more than one winner per day, this is configured via the command line. For example, assume day 2 is a monday, starting the application with:

    java -jar julekarenalender-3.0.0.jar -d 2,3,4,5,6,6

This means that there will be six drawings. One for each workday in the week and two on the friday. This is useful if your team only does one secret santa drawing per workweek.
Note that this means that you can draw winners for any day, at any point.

Julekarenalender tracks winners and excludes anyone that have won previously. There is **only one winner** allowed per participant.

Tapping the ALT key toggles the menu bar. Here one can:

* Exit the application (CTRL+Q)
* View and edit the participants (CTRL+W)
* Redraw a winner that was not physically available to retrieve a prize (CTRL+R)

#### Bonus wheel

![julekarenalender-bonus](http://joakibj.github.io/julekarenalender/images/julekarenalender-bonus.PNG)

The bonus wheel is activated with the `--bonus` option. It can also be used in conjunction with multiple drawings.

    java -jar julekarenalender-3.0.0.jar --bonus

If your team wants some extra spice, julekarenalender can be started with the bonus option. This will add a bonus wheel on the right side that has to be spun after a winner is found.
The bonus wheel can be some sort of extra reward or act as a minigame, be creative!

Per default there are three items in the bonus wheel: an image of a holiday present and two images of a joker cap. This can be overriden by adding custom images in the image folder.
Julekarenalender scans the images folder for any `png` or `jpg` containing "bonus" in the filename. For example:

    julekarenalender-3.0.0/
        julekarenalender-3.0.0.jar
        images/
            bonus0.jpg
            bonus1.jpg
            bonus2.jpg
            bonus3.jpg
            Dolly.png
            Donald Duck.jpg
            Huey.jpg
            Dewey.jpg
            Louie.jpg

This adds four custom bonus images to the bonus wheel.

#### Printed usage

```
Julekarenalender 3.0.0-SNAPSHOT
Usage: julekarenalender-3.0.0 options_list
Options:
    --days, -d -> Days separated by comma where there should be a draw. Ex: --days 1,2,5,5 { String }
    --bonus, -b -> Enable the bonus wheel
    --scan, -s -> Scan for participants in the images-folder. Filename = name of participant
    --dryRun, -x -> Dry run. Feel free to play around! Winners wont saved. Database wont be updated. Use it in combination with --scan to test import :)
    --list, -l -> List participants
    --reset -> Resets all configuration. Danger, danger!
    --debug, -D -> Turn on debug mode
    --help, -h -> Usage info
```

Roadmap
-------

Please refer to the [issue list](https://github.com/joakibj/julekarenalender/issues?state=open) on github for a more comprehensive overview.

######v3.0.0 (November 2020)
- Ported all Scala to Kotlin
- Migrated project to Gradle
- Added "dry run" possibility with `-x` to play with current settings without saving results. Have fun! 🎅
- Added `--list`-argument to list participants and all stored info  
- Synthetic test data generator implemented to comply with GDPR
- Minor adjustments without changing any of the original game (java-code)

Note: Now using Nitrite as embedded NoSQL database. 
Would however recommend switching to [Kodein](https://github.com/Kodein-Framework/Kodein-DB) whenever it becomes somewhat more stable 😊

Kotlin migration by [Rene](https://github.com/72656e65)

######v2.0.0 (holiday 2013)

- [X] Migrated from maven to sbt
- [X] SQLite as persistent datastore instead of CSV
- [X] Slick integration with persistent datastore, as the Configuration repository
- [X] Configuration module UI to set up participants and track winners. GUI panel
- [X] Supply self-contained jar that has a minimum set of configuration to run
- [X] Package jar with dependencies
- [X] Features needed for holidays 2013

######v2.1.0 (holiday 2014)

- [ ] Swing/AWT GUI swapped with ScalaFX
- [ ] Feature: remove participant

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

* [Rene](https://github.com/72656e65)
* Esben Stenwig
* Lotta Nordling

License
-------

MIT License.

Please see the LICENSE file for the license text in verbatim.
