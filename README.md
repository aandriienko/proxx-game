# PROXX — a game of proximity
**Java CLI version of Proxx game**

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



## Java Requirements

The application requires **Java 17** to run.

## Building & Running

### Using Maven wrapper


1. Open your terminal and navigate to the project root directory.

2. To compile the application and build a jar file, run the following command:

    ```bash
    mvnw clean package
    ```

3. To run the application, execute the jar file that was created in the `target` directory:

    ```bash
    java -jar target/proxx-game-1.0-jar-with-dependencies.jar
    ```
> **Note:** If you are seeing escape characters in the console output when running locally, you may want to run the application using Docker, as shown below.
The Docker container has been set up to correctly handle these characters.   

### Using Docker

1. Install [Docker](https://docs.docker.com/get-docker/) if you haven't done so.

2. Open your terminal and navigate to the project root directory.

3. Build the Docker image:

    ```bash
    docker build -t proxx-game .
    ```

4. Run the Docker container:

    ```bash
    docker run -p 8080:8080 -it --name proxx-game-container proxx-game
    ```