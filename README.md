# PROXX — a game of proximity
**JavaFX version of the Proxx game**

## The Rules of PROXX

PROXX has a very simple set of rules that are clearly defined allowing distinct elements to be
designed and developed to cater for each requirement.

- The game consists of a grid of cells.
- Some of the cells on the grid are black holes.
- The state of each cell on the grid is hidden until interacted with by a player.
- Revealing a cell shows the content of that cell.
- Revealing a cell that is a black hole results in a game over.
- Revealed cells show the number of adjacent black holes.
- Revealing an empty (has no nearby black holes) reveals additional nearby empty cells and those bordering on these cells (except black holes).
- The game is won when all non black hole cells have been revealed.



## Running

Requires Java 17. The build enforces exactly that: a `maven-enforcer-plugin`
rule pins the JDK to the `[17,18)` range and fails with an explicit message if
you run Maven under any other version, 18 and newer included.

    ./mvnw javafx:run

Or build a self-contained jar and run it:

    ./mvnw clean package
    java -jar target/proxx-game-1.0-jar-with-dependencies.jar

The JavaFX native libraries are selected automatically by the platform
profiles in `pom.xml`, which cover Windows x64, Linux x64, Linux ARM64,
macOS Intel and macOS Apple Silicon. Override the detection with
`-Djavafx.platform=<win|linux|linux-aarch64|mac|mac-aarch64>` when
cross-building.

Note that the packaged jar carries native libraries for the platform that
built it, so build on the machine you intend to run on. A single jar cannot
carry every platform at once: the macOS Intel and Apple Silicon artifacts both
contain `libglass.dylib` at the same path, as do the two Linux artifacts, so
bundling both architectures of one OS would have one overwrite the other.

## Playing

Choose a difficulty, or set Width, Height and Black holes yourself, then press
START.

- **Clear** mode: click a cell to reveal it. The background turns red while you
  are in Clear mode, because this is the move that can lose the game.
- **Flag** mode: click an unrevealed cell to mark it. Flagging cannot lose the
  game, so the background settles back to blue.
- Right-click flags a cell in either mode.
- Arrow keys move the focus, Space reveals, F flags.
- A flagged cell cannot be revealed until you unflag it.

Reveal every cell that is not a black hole to win. Open a black hole and the
board is revealed; from there you can deal a new board with the same settings
(Try again) or return to the menu (Main menu).