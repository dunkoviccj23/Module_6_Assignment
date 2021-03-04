package edu.wctc;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparingInt;

public class DiceGame {
    private final List<Player> players = new ArrayList<>();
    private final List<Die> dice = new ArrayList<>();
    private final int maxRolls;
    private Player currentPlayer;

    public DiceGame(int countPlayers, int countDice, int maxRolls) throws IllegalArgumentException {
        if (countPlayers < 2) {
            throw new IllegalArgumentException("Player count must be more than 2.");
        }
        this.maxRolls = maxRolls;

        for (int i = 0; i < countPlayers; i++) {
            this.players.add(new Player());
        }

        for (int i = 0; i < countDice; i++) {
            this.dice.add(new Die(6));
        }
    }

    private boolean allDiceHeld() {
        return dice.stream().allMatch(dice -> dice.isBeingHeld());
    }

    public boolean autoHold(int faceValue) {
        if (this.isHoldingDie(faceValue)) {
            return true;
        }

        Stream<Die> unheldDice = dice.stream().filter(dice -> !dice.isBeingHeld());

        if (unheldDice.anyMatch(dice -> dice.getFaceValue() == faceValue)) {
            for(Die die : dice) {
                if (die.getFaceValue() == faceValue) {
                    die.holdDie();
                }
            }
            return true;
        }

        return false;
    }


    public boolean currentPlayerCanRoll() {
        return this.currentPlayer.getRollsUsed() < this.maxRolls && !this.allDiceHeld();
    }

    public int getCurrentPlayerNumber() {
        return currentPlayer.getPlayerNumber();
    }

    public int getCurrentPlayerScore() {
            return currentPlayer.getScore();
    }

    public String getDiceResults() {
        return dice.stream()
                .map(dice -> dice.toString())
                .collect(Collectors.joining(", "));
    }

    public String getFinalWinner() {
        return this.players.stream()
                .max(comparingInt(player -> player.getWins()))
                .get()
                .toString();
    }

    public String getGameResults() {
        Player highestScorePlayer = this.players.stream().max(comparingInt(player -> player.getWins())).get();
        highestScorePlayer.addWin();

        Stream<Player> losingPlayers = this.players.stream().filter(player -> player.getPlayerNumber() != highestScorePlayer.getPlayerNumber());
        losingPlayers.forEach(player -> player.addLoss());

        return this.players.stream()
                .map(player -> player.toString())
                .collect(Collectors.joining(", "));
    }

    private boolean isHoldingDie(int faceValue) {
        Stream<Die> heldDice = dice.stream().filter(dice -> dice.isBeingHeld());
        return heldDice.anyMatch(dice -> dice.getFaceValue() == faceValue);
    }

    public boolean nextPlayer() {
        int currentPlayerNumber = currentPlayer.getPlayerNumber();
        if(currentPlayerNumber != players.size()) {
            this.currentPlayer = this.players.get(currentPlayerNumber);
            return true;
        }
        return false;
    }

    public void playerHold(char dieNum) {
        Die dieToHold = this.dice.stream().filter(dice -> dice.getDieNum() == dieNum).collect(Collectors.toList()).get(0);
        dieToHold.holdDie();
    }

    public void resetDice() {
        dice.stream().forEach(dice -> dice.resetDie());
    }

    public void resetPlayers() {
        players.stream().forEach(dice -> dice.resetPlayer());
    }

    public void rollDice() {
        System.out.println("Current roll: " + this.getDiceResults());
        dice.stream().forEach(dice -> dice.rollDie());
        currentPlayer.roll();
    }

    public void scoreCurrentPlayer() {
        boolean has6 = dice.stream().anyMatch(dice -> dice.getFaceValue() == 6);
        boolean has5 = dice.stream().anyMatch(dice -> dice.getFaceValue() == 5);
        boolean has4 = dice.stream().anyMatch(dice -> dice.getFaceValue() == 4);
        if (has6 && has5 && has4) {
            int total = 0;
            for(Die die : dice) {
                total += die.getFaceValue();
            }
            currentPlayer.setScore(total - 15);
        }
    }

    public void startNewGame() {
        this.resetPlayers();
        currentPlayer = this.players.get(0);
    }
}

