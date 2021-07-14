package gui;

import core.AbstractParameters;
import core.AbstractPlayer;
import core.Game;
import evaluation.TunableParameters;
import games.GameType;
import players.PlayerParameters;
import players.PlayerType;
import players.human.ActionController;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Frontend extends GUI {
    private final int nMaxPlayers = 20;
    private final int defaultNPlayers = 2;

    public Frontend() {

        // Game type and parameters selection

        JPanel gameSelect = new JPanel();
        gameSelect.add(new JLabel("Game type:"));
        String[] gameNames = new String[GameType.values().length];
        TunableParameters[] gameParameters = new TunableParameters[GameType.values().length];
        JFrame[] gameParameterEditWindow = new JFrame[GameType.values().length];
        for (int i = 0; i < gameNames.length; i++) {
            gameNames[i] = GameType.values()[i].name();
            AbstractParameters params = GameType.values()[i].createParameterSet(0);
            if (params instanceof TunableParameters) {
                gameParameters[i] = (TunableParameters) params;
                gameParameterEditWindow[i] = new JFrame();
                gameParameterEditWindow[i].getContentPane().setLayout(new BoxLayout(gameParameterEditWindow[i].getContentPane(), BoxLayout.Y_AXIS));

                List<String> paramNames = gameParameters[i].getParameterNames();
                HashMap<String, JComboBox<Object>> paramValueOptions = createParameterWindow(paramNames, gameParameters[i], gameParameterEditWindow[i]);

                int idx = i;
                JButton submit = new JButton("Submit");
                submit.addActionListener(e -> {
                    for (String param: paramNames) {
                        gameParameters[idx].setParameterValue(param, paramValueOptions.get(param).getSelectedItem());
                    }
                    gameParameterEditWindow[idx].dispose();
                });
                JButton reset = new JButton("Reset");
                reset.addActionListener(e -> {
                    gameParameters[idx].reset();
                    for (String param: paramNames) {
                        paramValueOptions.get(param).setSelectedItem(gameParameters[idx].getDefaultParameterValue(param));
                    }
                });
                JPanel buttons = new JPanel();
                buttons.add(submit);
                buttons.add(reset);

                gameParameterEditWindow[i].getContentPane().add(buttons);
            }
        }
        JComboBox<String> gameOptions = new JComboBox<>(gameNames);  // index of this selection is game
        gameSelect.add(gameOptions);
        JButton gameParameterEdit = new JButton("Edit");
        gameParameterEdit.setVisible(false);
        gameOptions.addActionListener(e -> {
            int idx = gameOptions.getSelectedIndex();
            gameParameterEdit.setVisible(gameParameterEditWindow[idx] != null);
        });
        gameSelect.add(gameParameterEdit);
        gameParameterEdit.addActionListener(e -> {
            int idx = gameOptions.getSelectedIndex();
            if (gameParameterEditWindow[idx] != null) {
                gameParameterEditWindow[idx].pack();
                gameParameterEditWindow[idx].setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                gameParameterEditWindow[idx].setTitle("Edit parameters " + gameOptions.getSelectedItem());
                gameParameterEditWindow[idx].setVisible(true);
            }
        });

        // Number of players selection

        JPanel playerSelect = new JPanel();
        playerSelect.setLayout(new BoxLayout(playerSelect, BoxLayout.Y_AXIS));
        JPanel nPlayers = new JPanel();
        playerSelect.add(nPlayers);
        nPlayers.add(new JLabel("# players (max " + nMaxPlayers + "):"));
        JTextField nPlayerField = new JTextField(""+defaultNPlayers, 10);  // integer of this is n players
        nPlayers.add(nPlayerField);

        // For each player, select type and parameters

        JPanel[] playerOptions = new JPanel[nMaxPlayers];
        JComboBox<String>[] playerOptionsChoice = new JComboBox[nMaxPlayers];  // player is index of this selection
        String[] playerOptionsString = new String[PlayerType.values().length];
        PlayerParameters[] playerParameters = new PlayerParameters[PlayerType.values().length];
        JFrame[] playerParameterEditWindow = new JFrame[PlayerType.values().length];
        for (int i = 0; i < playerOptionsString.length; i++) {
            playerOptionsString[i] = PlayerType.values()[i].name();
            playerParameters[i] = PlayerType.values()[i].createParameterSet(0);
            playerParameterEditWindow[i] = new JFrame();
            playerParameterEditWindow[i].getContentPane().setLayout(new BoxLayout(playerParameterEditWindow[i].getContentPane(), BoxLayout.Y_AXIS));

            if (playerParameters[i] != null) {
                List<String> paramNames = playerParameters[i].getParameterNames();
                HashMap<String, JComboBox<Object>> paramValueOptions = createParameterWindow(paramNames, playerParameters[i], playerParameterEditWindow[i]);

                int idx = i;
                JButton submit = new JButton("Submit");
                submit.addActionListener(e -> {
                    for (String param: paramNames) {
                        playerParameters[idx].setParameterValue(param, paramValueOptions.get(param).getSelectedItem());
                    }
                    playerParameterEditWindow[idx].dispose();
                });
                JButton reset = new JButton("Reset");
                reset.addActionListener(e -> {
                    playerParameters[idx].reset();
                    for (String param: paramNames) {
                        paramValueOptions.get(param).setSelectedItem(playerParameters[idx].getDefaultParameterValue(param));
                    }
                });
                JPanel buttons = new JPanel();
                buttons.add(submit);
                buttons.add(reset);

                playerParameterEditWindow[i].getContentPane().add(buttons);
            }
        }
        for (int i = 0; i < nMaxPlayers; i++) {
            playerOptions[i] = new JPanel();
            if (i >= defaultNPlayers) {
                playerOptions[i].setVisible(false);
            }
            playerOptions[i].add(new JLabel("Player " + i + ":"));
            JButton paramButton = new JButton("Edit");
            paramButton.setVisible(false);
            playerOptionsChoice[i] = new JComboBox<>(playerOptionsString);
            playerOptionsChoice[i].setSelectedItem("Random");
            int playerIdx = i;
            playerOptionsChoice[i].addActionListener(e -> {
                int idx = playerOptionsChoice[playerIdx].getSelectedIndex();
                PlayerParameters pp = playerParameters[idx];
                paramButton.setVisible(pp != null);
                paramButton.removeAll();
                paramButton.addActionListener(f -> {
                    playerParameterEditWindow[idx].setTitle("Edit parameters " + playerOptionsChoice[playerIdx].getSelectedItem());
                    playerParameterEditWindow[idx].pack();
                    playerParameterEditWindow[idx].setVisible(true);
                    playerParameterEditWindow[idx].setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                });
                pack();
            });
            playerOptions[i].add(playerOptionsChoice[i]);
            playerOptions[i].add(paramButton);
            playerSelect.add(playerOptions[i]);
        }
        JButton updateNPlayers = new JButton("Update");
        updateNPlayers.addActionListener(e -> {
            if (!nPlayerField.getText().equals("")) {
                int nP = Integer.parseInt(nPlayerField.getText());
                if (nP > 0 && nP < nMaxPlayers) {
                    for (int i = 0; i < nP; i++) {
                        playerOptions[i].setVisible(true);
                    }
                    for (int i = nP; i < nMaxPlayers; i++) {
                        playerOptions[i].setVisible(false);
                    }
                    pack();
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Error: Please enter number bigger than 0 and less than " + nMaxPlayers, "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        nPlayers.add(updateNPlayers);

        // Select visuals on/off
        
        JPanel visualSelect = new JPanel();
        visualSelect.add(new JLabel("Visuals ON/OFF:"));
        JComboBox<Boolean> visualOptions = new JComboBox<>(new Boolean[]{false, true}); // index here is visuals on/off
        visualOptions.setSelectedItem(true);
        visualSelect.add(visualOptions);

        // Select random seed

        JPanel seedSelect = new JPanel();
        seedSelect.add(new JLabel("Seed:"));
        JTextField seedOption = new JTextField(""+System.currentTimeMillis());  // integer of this is seed
        JButton seedRefresh = new JButton("Refresh");
        seedRefresh.addActionListener(e -> seedOption.setText("" + System.currentTimeMillis()));
        seedSelect.add(seedOption);
        seedSelect.add(seedRefresh);

        // Put all together

        JPanel gameOptionFullPanel = new JPanel();
        gameOptionFullPanel.setLayout(new BoxLayout(gameOptionFullPanel, BoxLayout.Y_AXIS));
        gameOptionFullPanel.add(gameSelect);
        gameOptionFullPanel.add(playerSelect);
        gameOptionFullPanel.add(visualSelect);
        gameOptionFullPanel.add(seedSelect);

        // Empty panel to hold game when play button is pressed

        JPanel gamePanel = new JPanel();
        gamePanel.setVisible(false);

        // Play button, runs game in separate thread to allow for proper updates

        JPanel gameControlButtons = new JPanel();
        JButton startGame = new JButton("Play!");
        startGame.addActionListener(e -> {

            GUI frame = this;
            Runnable runnable = () -> {

                ActionController ac = new ActionController();
                if (visualOptions.getSelectedIndex() == 0) ac = null;
                long seed = Long.parseLong(seedOption.getText());
                ArrayList<AbstractPlayer> players = new ArrayList<>();
                int nP = Integer.parseInt(nPlayerField.getText());
                String[] playerNames = new String[nP];
                for (int i = 0; i < nP; i++) {
                    AbstractPlayer player = PlayerType.valueOf(playerOptionsChoice[i].getItemAt(playerOptionsChoice[i].getSelectedIndex())).createPlayerInstance(seed, ac, playerParameters[i]);
                    playerNames[i] = player.toString();
                    players.add(player);
                }
                GameType gameType = GameType.valueOf(gameOptions.getItemAt(gameOptions.getSelectedIndex()));
                System.out.println("Playing `" + gameType.name() + "` with players: " + Arrays.toString(playerNames));

                gamePanel.removeAll();
                TunableParameters params = gameParameters[gameOptions.getSelectedIndex()];
                if (params != null) {
                    params.setRandomSeed(seed);
                }
                Game game = gameType.createGameInstance(players.size(), params);
                if (game != null) {

                    // Reset game instance, passing the players for this game
                    game.reset(players);

                    AbstractGUIManager gui = null;
                    if (ac != null) {
                        // Create GUI (null if not implemented; running without visuals)
                        gui = gameType.createGUIManager(gamePanel, game, ac);
                    }
                    revalidate();
                    pack();
                    game.run(gui, frame);
                    System.out.println("Game over: " + Arrays.toString(game.getGameState().getPlayerResults()));

                }
            };

            Thread thread = new Thread(runnable);
            thread.start();
        });
        gameControlButtons.add(startGame);
        // todo stop game
        // todo tournaments, game report, player report etc

        // Collapse run settings panel

        gameOptionFullPanel.add(gameControlButtons);
        JButton toggleButton = new JButton("<<");
        toggleButton.addActionListener(e -> {
            boolean visible = gameOptionFullPanel.isVisible();
            if (visible) {
                gameOptionFullPanel.setVisible(false);
                toggleButton.setText(">>");
            } else {
                gameOptionFullPanel.setVisible(true);
                toggleButton.setText("<<");
            }
            pack();
        });

        // Wrap all together

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.X_AXIS));
        wrapper.add(gameOptionFullPanel);
        wrapper.add(toggleButton);
        wrapper.add(gamePanel);

        getContentPane().add(wrapper, BorderLayout.CENTER);

        // Frame properties
        setFrameProperties();
    }

    private HashMap<String, JComboBox<Object>> createParameterWindow(List<String> paramNames, TunableParameters pp, JFrame frame) {
        HashMap<String, JComboBox<Object>> paramValueOptions = new HashMap<>();
        frame.getContentPane().removeAll();
        for (String param: paramNames) {
            JPanel paramPanel = new JPanel();
            paramPanel.add(new JLabel(param));
            List<Object> values = pp.getPossibleValues(param);
            JComboBox<Object> valueOptions = new JComboBox<>(values.toArray());
            paramValueOptions.put(param, valueOptions);
            paramPanel.add(valueOptions);
            frame.getContentPane().add(paramPanel);
        }
        return paramValueOptions;
    }

    public static void main(String[] args) {
        new Frontend();
    }

}