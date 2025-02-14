# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

[Sequence Diagram](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZT6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HmAAvIDKN4oMDMGxONxYLlTkdLhUACxOJzNHpTUbqYD2e5TACiUDB5T0HBgUBQAEcfGowJMDgjYadsuZSsjUejMWpVDi8X1CcSYKTyVSabF6UDgYDTFCLrDCkdROUKdFYpQABSRVWZKCRam0gCUh2KohOhVk8iUKnU5VxYAAqh11Tc7kaLYplNyzUYdKUAGJITgwR2Ud1CsIu4CjUzA0HQCHuq1euEKk0qco3d3GkQqE6nWUwtkogDMnL6WJ5uPGBKJ0HKwAQFOAHDC8gA1ugJQXKCnGeV2aWYBjy9zedX+bWyjAG02WzB252YAzij2CiyMP2S2WK2PyhPBTOUM3W8AO2gJVLpZhE571HnUzmUOU0D4EAhs1RTacb9bVKUQE2mQhlAzodO6braEmd6nMYpQKBwZLAVmiq5t+kG3n+AFHpkCg+GAqTqsAeGpOB17ob+3qwfBZK4fhyFpoYKZnNClwRFEMQ6pEqivlg3ZMg+JRXAMHR3JMe6PFMRH4fUEBnmJUz7B+lD3vAyCspUABMHJDsJQxRmMMDiV8UmpDJcmGQpfQHOgHCmJ43h+IE0DsLiMAADIQNESQBGkGRZGpeQCax1R1E0rQGOoCRoN0PSRqMswvG8HDLtQyn5uchY6XF9yrNMzz6K8Sy7FZkIZWlAlKjACCeYG6oeV5epimARooYx5rkdytooA6TrZRBloYZRvowAGQZIdo4YwNlMYgpOCYddB8IMc+r7vq1Kl8UWTgAIzbqOVZ7gKdZTcExHQEgABeKDLEupUsfxRSpWU7K7UOXLYgdNaCj4p34edV03SlxxMeuYBba9w47p9+7HT9Jn-ddF5SqYP7Jktj4ZmB2gzXG4LSutTGbS96Ijh9OVHVOcNnVAl2IwcfEqX2xNvaTlZ8hTGa-akCOA0pD2g+De1k+OHMnfDNMA0jEIfl+7UDb+-6ASgtEESZpGo9BhRUQhMAq-Rj4bWVU71YGXE8XdcqMyuU7XCJ+nyblknEWZi5GUDaXMgFYOadptt6aMDsSX0Jku+eFmrNZnB2V4vj+AEXgoOg7meb4zA+ekmSYKD3p9pU0j4m5+L1PizQtBFqhRd0IeyegfMFJt07OzXaAWzCBSteU1X2KndUp3hjWGjLKgFBrf4wHaKuEU3Z79R6v4FLBI2BjRxFhjoYTV2eONzdKo9WxjMAvm+Q+Mel92CyzUPs5OnPi7TvOMgAPHCRMohD71syLN9i9T98SqDz9chMzfkLT+h1v5Uz+hLRGt1LwowWqofen50yNzotjAmcs56dSMCgbgOFiJTzQfIWeUFEEwWGtIXB3VDB6wmoQ0yzcWoMUNufQ+q1W4eyClOI+a1rYqQFj7NEmAbLRwcnHCkZI3IcRgAAcX0og9Ofks5exztbfsMii6l3sPpKu09a6MnrkbeseiW58XbstZAsQ5FYnoaHJhj4R4IK6mASem90AkIwgvYao0V5EOAJNNxLdJS43mvLNGFUUE8JPiwuUF9Ib7Wvt9LmPMJRPxfkY5m8ThbgKSXfSWt0AFwmATtUBu4vqw2SdA3mcCyJhMWhEp8qCSLoIYo4upY9LFgGsWodUHj575EXj46cb5ZHyJPoA0epRtFYizHxQBHdOndNUGbBAWA0m5CJlpNELNplqGrBURoBxClALURULZZZdmqH2Yc4RUdTAx0cgEbAPgoDYG4PAJWozRgpAzv5HIzB0aCUqLUBoWidFc1DjFXZAA5fS7soCGNYYEocMK4XbzBBCBmTFinbKyWA8pU5hQUn1OKW6jJ+Fey2rij+ZSYaEubCKEldJYHIw4Qi-IHcsJyBQN09UcAlbdIHrEexyCUBtKwTace3VXEmL6dyLx5Qhm0PkAEkx6L4y7wQUgyqUSCZn1iZkml0NRaQO5lU1J1tAGvxKZfBJX9cm-3yUcr28zTkgNtdkglt9HUwIODUve2LlpNP1qK8VpDyhcsyLy1Fow5XqAVbrHW3S2WuoxpGnl+kVlrMtek8+ZzfZTF2QASWkNWYt0gCkuqKac85Oz9IlrLfWitkdbL3LEYESwuDqrJBgAAKQgIGL5hgAg6AQKANsyj-mqKev2Ko9owotF2bo6SzcYpvOAJ2qAcAIDVSgLMct8LEWxKaaHIc67N3bt3fuptAJYw7zZUgxEKJqWs1paLIlopaQWqehS-5VLSnGu-h+plUsrxmI5ctAAVgOtAvL+2BkFZ+4VJ8w0YWcTKldM9akStUAmpVq8JrrxPc3dVeNsOkO1ZE9herCjWvfq+wDDqoF-zJTmjZGT3V4rfRAypLHnX-NTYJQ1DHEkVLyb6yU0sA2AsqmrFpDj8iTKlV0-S6py2zDjbhgZ3jl5Dsmt0ktpGIStQmU43VzDCYcYLSJ+1x1gNNW-SUK1VmX1X1s-SskxKHOVoE9Wmd7JXN2pyXZhlXmv0suliZuEkzdlZmCfemjzEDVbg9fiul9ZGxHjnAuMO8LnN5pS1xxjx1DzHnnKeRc-HzCCafU4QcRXRNTlK9liruXJNXlaqhhWfgtBRtU7F7QmmE32mwL1ww3T9NNpTX50VitsIZtGFmvm+WDW1uHOWxtowS0+eqzNxEa3C1Ns2ygbbLbRGx0CF4DdqkjywGANgN5hB4iJB+Uo7OgLgr50LsXUuxg675E2lE8DnLuB4AUA95AIBntoF6ShxTTiQCg5AkN7TpRKF4MMA2BARhsZzJm0qczT0VuFiiccpm5zbm2SAA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
