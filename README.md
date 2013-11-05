Julekarenalender v2.0.0
=======================

Julekarenalender is a secret santa desktop application written in Java and Scala. It is adapted for the holidays, but can be used for any purpose that involves random drawing of winners. It is ideal for a project team or office with access to a TV connected with a computer.

Features:
* One-armed bandit style wheel with random winner draw
* Bonus wheel for custom purpose

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

1. Extract zip-filen i target mappen til ønsket sted
1. Samle sammen bilder som representerer alle i teamet i **jpg** format
1. Endre julekarenalender.csv
  * Filen har 4 semikolon ; separerte kolonner
  * Første kolonne har navnet til deltakeren. Fornavn holder her
  * Andre kolonne har **jpg** bildefilen til deltakeren. Unngå mellomrom
  * Tredje kolonne har hvilken dag vedkommende vant. Denne skal være blank under førstegangskonfigurasjon. Denne kolonnen utelukker at deltakeren kan trekkes etter den dagen.
  * Fjerde kolonne er reservert notater og blir ikke lest av applikasjonen
1. **jpg** bildefiler legges i img undermappen
1. (Valgfritt) Man kan konfigurere de 3 bildene i bonushjulet (det lille hjulet til høyre) ved å legge bilder "bonus0.jpg", "bonus1.jpg" og "bonus_1.jpg" i **img** mappen. På denne måten kan man skreddersy applikasjonen til prosjektet. Hvis ikke disse bildene finnes, vil standard bli brukt:
  * bonus0.jpg overstyrer et bilde av en jokerlue
  * bonus1.jpg overstyrer et bilde av en pakke
  * bonus_1.jpg overstyrer et bilde av Joakim Lystad (NAV direktør)

Usage
-----

TODO: Write this in english when it is ready

1. Julekarenalenderen kan startes på to måter, avhengig av bruk:
  * **Én trekning per dag:** Dobbelklikk på jar fil i mappen som er inneholdt i zip-filen
  * **Flere trekninger på en dag:**
      * Start cmd.exe eller et *nix Shell
      * cd til mappen som har jar-filen
      * Start julekarenalenderen med: **java -jar [jar-fil] [ARGS]**
          * **ARGS** er en liste av tall med hvilke dager det skal foregå trekning. For eksempel: Det er mandag 3. desember og det skal trekkes totalt 6 pakker. Det trekkes to pakker for 1. desember, to pakker for 2. desember og to pakker for 3. desember. Julekarenalenderen startes da med ARGS: 1 1 2 2 3 3
          * Etter at to vinnere for 1. desember er funnet i eksempelet ovenfor, vil dag-hjulet helt til venstre endres til neste dag
1. **Forrige dags vinner** (ved flere trekninger samme dag: Forrige vinner) snurrer det store hjulet for å finne en vinner. Dette gjøres ved å ta tak i det store hjulet, hvor man tar tak i det og drar ned i en vertikal bevegelse og slipper. Deltakeren hjulet stopper på er dagens vinner
  * Hvis vedkommende ikke er til stede til å motta gaven, kan det store hjulet snurres på nytt for å finne en annen vinner
1. Når en vinner er funnet for dagen får vedkommende sin gave, samt har anledning til å snurre på bonushjulet
  * På Arena ble bonushjulet brukt til å få bonusgave hvis man fikk bildet av pakken. Her er det åpent for andre ritualer, avhengig av teamet. Det kan være et bilde for gevinst eller "straff"
1. Etter at bonushjulet har landet, blir vinneren for dagen registrert i csv filen. Man får ikke snurre på nytt
1. De som tidligere har fått gave (har fått registert dag nummer i csv filen) blir utelatt i hjulet for videre trekninger

Roadmap
-------

######v2.0.0

- [x] Migrated from maven to sbt
- [ ] SQLite as persistent datastore instead of CSV
- [ ] Slick integration with persistent datastore, as the Configuration repository
- [ ] Configuration module UI to set up participants and track winners. TBD, either command-line based, webapp or GUI module.
- [ ] Supply self-contained jar that has a minimum set of configuration to run
- [x] Package jar with dependencies
- [ ] Features needed for holidays 2013

######v2.1.0

- [ ] Swing/AWT GUI swapped with ScalaFX

Trivia
------

This application has been used for several years at the NAV Arena project during the holidays. The first versions was created by Arne C. Jervell.

In 2012 and 2013 it is used at the NAV BA project.


License
-------

To Be Determined
