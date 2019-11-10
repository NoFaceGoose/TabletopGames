package core;

import components.*;
import content.PropertyString;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import utilities.Hash;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public abstract class Game {

    protected List<AIPlayer> players;
    protected GameState gameState;
    protected ForwardModel forwardModel;

    protected List<Board> boards;
    protected List<Deck> decks;
    protected List<Token> tokens;
    protected List<Counter> counters;
    protected List<Dice> dice;


    public void setPlayers(List<AIPlayer> players) {
        this.players = players;
    }

    public List<AIPlayer> getPlayers() {
        return players;
    }


    public abstract void run();
    public abstract boolean isEnded();
    public abstract HashSet<Integer> winners();

    public void setup(String dataPath)
    {
        load(dataPath);

        gameState.setup(this);
        forwardModel.setup(gameState);
    }


    public void load(String dataPath)
    {
        boards = Board.loadBoards(dataPath + "boards.json");
        decks = Deck.loadDecks(dataPath + "decks.json");
        tokens = Token.loadTokens(dataPath + "tokens.json");
        counters = Counter.loadCounters(dataPath + "counters.json");
//        dice  = Dice.loadDice(dataPath + "dice.json");
    }


    public Board findBoard(String name) {
        for (Board c: boards) {
            if (name.equalsIgnoreCase(c.getNameID())) {
                return c;
            }
        }
        return null;
    }


    public Counter findCounter(String name) {
        for (Counter c: counters) {
            if (name.equalsIgnoreCase(c.getID())) {
                return c;
            }
        }
        return null;
    }

    public Token findToken(String name) {
        for (Token t: tokens) {
            if (name.equalsIgnoreCase(t.getNameID())) {
                return t;
            }
        }
        return null;
    }

}