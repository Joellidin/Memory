// Joel Lidin and Filip Ahlman, Group 39

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;


public class Memory extends JFrame implements ActionListener {
    
    //Instance variables.
    private static double DEFAULT_TIME = 1.5;
    private static int DEFAULT_WIDTH  = 4;
    private static int DEFAULT_HEIGHT = 4;
    private int playerIndex = 1;
    private Player[] players;
    private boolean timerOn = false;
    private static boolean soundsOn = true;
    private Timer timer = new Timer();
    private String[] names;
    String[] empty = new String[2];
    private Card allCards[] = new Card[51];
    private Card upsideCard, gameCards[];
    private JMenuBar menuBar;
    private JMenu game, settings;
    private JMenuItem newGame, quit;
    private JMenuItem playerSetting, savings, sizeOfGame, timeSetting, soundSetting, playerName, playerAmount;
    private JPanel mainGame, westPanel;
    private int nbrOfPairs, rows, columns, rememberRows, rememberColumns;
    private int timeOnTimer = (int)(DEFAULT_TIME*1000);
    private static boolean gameOver, happySound;
    
    //The main constructor.
    public Memory() throws FileNotFoundException {
        //Take the pictures from folder and put them in an array with the type Card.
        File folder = new File("mypictures");
        File[] pictures = folder.listFiles();
        for (int i = 0; i < pictures.length ; i++) {
            ImageIcon icon = new ImageIcon(pictures[i].getPath());
            allCards[i] = new Card(icon, Card.Status.HIDDEN);
        }
        
        //Decides dimensions and diverse things for the Panel.
        setLayout(new BorderLayout());
        setSize(new Dimension(125, 125));
        setMinimumSize(new Dimension(500, 500));
//        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        menuBar = new JMenuBar();
        game = new JMenu("Game");
        menuBar.add(game);
        
        newGame = new JMenuItem("New game");
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int hej = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to start a new game? This will restart the current play.",
                        "",
                        JOptionPane.YES_NO_OPTION);
                if (hej == JOptionPane.NO_OPTION) {
                    return;
                }
                else {
                    newGame(rememberRows, rememberColumns);
                }
            }
        });
        game.add(newGame);
        
        quit = new JMenuItem("Quit");
        quit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int reply = JOptionPane.showConfirmDialog(
                        null,
                        "Are you sure you want to quit?",
                        "",
                        JOptionPane.YES_NO_OPTION);
                if(reply == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        game.add(quit);
        
        settings = new JMenu("Settings");
        menuBar.add(settings);
        
        playerSetting = new JMenu("Player");
        settings.add(playerSetting);
        
        playerAmount = new JMenuItem("Number of players");
        playerAmount.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int p = showDialog("Choose the number of players: ", 2, 1, 6);
                String[] tmpArr = new String[p];
                remove(westPanel);
                if (p == 0) {
                    return;
                }
                playerCount(tmpArr, p);
                newGame(rememberRows, rememberColumns);
            }
        });
        playerSetting.add(playerAmount);
        
        playerName = new JMenuItem("Player names");
        playerName.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int nbrOfPlayers = players.length;
                String[] ans = new String[nbrOfPlayers];
                int[] scoreOfPlayers = new int[nbrOfPlayers];
                names = new String[nbrOfPlayers];
                int i;
                for (i = 0; i < nbrOfPlayers; i++) {
                    scoreOfPlayers[i] = players[i].getScore();
                }
                    for(i = 0; i < nbrOfPlayers; i++) {
                        int nbr = i+1;
                        ans[i] = JOptionPane.showInputDialog("Please enter the name of player " + nbr +  ": " , players[i].getName());
                        names[i] = ans[i];
                        if (ans[i] == null) {
                            return;
                        }
                    }
                    remove(westPanel);
                    playerCount(names, nbrOfPlayers);
                    playerIndex = playerIndex - 1;
                    togglePlayer();
                    for (i = 0; i < nbrOfPlayers; i++) {
                        players[i].setScore(scoreOfPlayers[i]);
                    }
                    setVisible(false);
                    setVisible(true);
            }
        });
        playerSetting.add(playerName);
        
        sizeOfGame = new JMenuItem("Size");
        sizeOfGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                chooseData();
            }
        });
        settings.add(sizeOfGame);
        
        timeSetting = new JMenuItem("Timer");
        timeSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String ans;
                while (true) {
                    ans = JOptionPane.showInputDialog("Choose time in seconds before a pair flips", DEFAULT_TIME);
                    if (ans == null)
                        return;
                    try {
                        double res = Double.parseDouble(ans);
                        if (res <= 5.0 && res >= 0.2) {
                            timeOnTimer = (int) (res*1000);
                            break;
                        }
                        JOptionPane.showMessageDialog(null, "Value must be between 0.2 and 5.");
                    } catch(NumberFormatException err) {
                        JOptionPane.showMessageDialog(null, "Invalid value.");
                    }
                }
            }
        });
        settings.add(timeSetting);
        
        String sounds;
        if (soundsOn == true) {
            sounds = "Sounds on";
        }
        else {
            sounds = "Sounds off";
        }
        soundSetting = new JMenuItem(sounds);
        soundSetting.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if(soundsOn) {
                    soundsOn = false;
                    soundSetting.setText("Sounds on");
                }
                else {
                    soundsOn = true;
                    soundSetting.setText("Sounds off");
                }
            }
        });
        settings.add(soundSetting);
        
        savings = new JMenuItem("Save");
        savings.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try{
                    // Create file 
                    FileWriter fstream = new FileWriter("Options.txt");
                    BufferedWriter out = new BufferedWriter(fstream);
                    out.write(rememberRows + "\n" + rememberColumns + "\n" +
                    players.length + "\n"  + timeOnTimer + "\n" + 
                             soundsOn);
                    //Close the output stream
                    out.close();
                    }catch (Exception err){//Catch exception if any
                      System.err.println("Error: " + err.getMessage());
                    }
            }
        });
        settings.add(savings);
        this.add(menuBar, BorderLayout.NORTH);
        
        try {
        savedSettings();
        } catch (FileNotFoundException e) {
            JOptionPane.showMessageDialog(null, "No saving file found!");
            playerCount(empty, 2);
            newGame(4,4);
            e.printStackTrace();
        }
        setVisible(true);
    }
    
    //Method that creates a new game with all different cards with the status HIDDEN
    public void newGame(int n, int m) {
        upsideCard = null;
        gameOver = false;
        nbrOfPairs = 0;
        for (int i = 0; i < players.length; i++) {
            players[i].setScore(0);
        }
        playerIndex = players.length - 1;
        togglePlayer();
        if (mainGame != null)
            remove(mainGame);
        int nbrOfCards = (n*m/2)*2;
        gameCards = new Card[nbrOfCards];
        Tools.randomOrder(allCards);
        int MAX = nbrOfCards/2;
        int i;
        for (i = 0; i < MAX; i++) {
            gameCards[i] = allCards[i].copy();
            gameCards[MAX + i] = gameCards[i].copy();
        }
        Tools.randomOrder(gameCards);
               
        mainGame = new JPanel(new GridLayout(n, m, 2, 2));
        for (i = 0; i < nbrOfCards; i++) {
            mainGame.add(gameCards[i]);
            gameCards[i].addActionListener(this);
        }
        setSize(new Dimension(n*125, m*125));
        setMinimumSize(new Dimension(n*100, m*100));
        setMaximumSize(new Dimension(n*150, m*150));
        add(mainGame, BorderLayout.CENTER);
        mainGame.setSize(new Dimension(n*150, m*150));
        setVisible(true);
        rememberRows = n;
        rememberColumns = m;
    }
    
    //Load the saved settings.
    public void savedSettings() throws FileNotFoundException {
        int loadSave = JOptionPane.showConfirmDialog(
                null,
                "Do you want to load the saved settings?",
                "",
                JOptionPane.YES_NO_OPTION);
        
        if(loadSave == JOptionPane.NO_OPTION) {
            playerCount(empty, 2);
            chooseData();
        }
        else {
            Scanner scan = new Scanner(new File("Options.txt"));
            int[] values = new int[4];
            int i = 0;
            while(scan.hasNextLine()) {
                String line = scan.nextLine();
                if( scan.hasNextLine() ) {
                    values[i] = Integer.parseInt(line);
                }
                else {
                    if (line.equals("true")) {
                        soundsOn = true;
                        soundSetting.setText("Sounds off");
                    }
                    else {
                        soundsOn = false;
                        soundSetting.setText("Sounds on");
                    }
                }
                i++;
            }
            scan.close();
            rememberRows = values[0];
            rememberColumns = values[1];
            timeOnTimer = values[3];
            String[] newPlayers = new String[values[2]];
            playerCount(newPlayers, values[2]);
            newGame(rememberRows, rememberColumns);
        }
    }
    
    //Toggles the player
    public void togglePlayer() {
        //Playerindex to decide whose turn it is
        playerIndex = (playerIndex + 1) % players.length;
        for (int i = 0; i < players.length; i++) {
            if (i == playerIndex) {
                players[i].setBackground(Color.YELLOW);
            }
            else {
                players[i].setBackground(Color.LIGHT_GRAY);
            }
        }
    }
    
    //Method to print out the names.
    public void playerCount(String[] names, int p) {
        players = new Player[p];
        westPanel = new JPanel(new GridLayout(p,1,1,1));
        for (int i = 0; i < p; i++) {
            int number = i+1;
            if (names[i] == null || names[i] == "") {
                names[i] = "Player " + number; 
            }
            players[i] = new Player(names[i]);
            players[i].setBorder(BorderFactory.createLineBorder(Color.black));
            westPanel.add(players[i]);
        }
        add(westPanel, BorderLayout.WEST);
    }
    
    //Checks how many rows and columns the player wants.
    public void chooseData() {
        rows = showDialog("Choose the number of rows: ", DEFAULT_WIDTH, 2, 7);
        if (rows == 0 && rememberRows == 0 ) {
            newGame(DEFAULT_HEIGHT, DEFAULT_WIDTH);
            return;
        }
        else if (rows == 0) {
            return;
        }
        columns = showDialog("Choose the number of columns: ", DEFAULT_HEIGHT, 2, 7);
        if (columns == 0 && rememberColumns == 0 ) {
            newGame(DEFAULT_HEIGHT, DEFAULT_WIDTH);
            return;
        }
        if (rows > 0 && columns > 0) {
            newGame(rows, columns);
        }
    }
    
    //A simple method for the dialog box when player choose rows and columns.
    public static int showDialog(String s, int i, int min, int max) {
        String ans;
        while (true) {
            ans = JOptionPane.showInputDialog(s, i);
            if (ans == null)
                return 0;
            try {
                int res = Integer.parseInt(ans);
                if (res < max+1 && res > min-1) {
                    return res;
                }
                JOptionPane.showMessageDialog(null, "Value must be between " + min + " and " + max + ".");
            } catch(NumberFormatException err) {
                JOptionPane.showMessageDialog(null, "Invalid value.");
            }
        }
        
    }
    
    //Method to play the different sounds.
    public static void playSound() {
        String happy = "1_person_cheering-Jett_Rifkin-1851518140.wav";
        String sad = "Pain-SoundBible.com-1883168362.wav";
        String end = "Ta Da-SoundBible.com-1884170640.wav";
        String activeSound = null;
        if (soundsOn) {
            if (happySound && !gameOver) {
                activeSound = happy;
            }
            else if (!happySound && !gameOver) {
                activeSound = sad;
            }
            else {
                activeSound = end;
            }
            AudioInputStream audioInputStream = null;
            try {
            audioInputStream = AudioSystem.getAudioInputStream(new File(activeSound).getAbsoluteFile());
            } catch (UnsupportedAudioFileException | IOException e1) {
             e1.printStackTrace();
            }
            Clip clip = null;
            try {
             clip = AudioSystem.getClip();
            } catch (LineUnavailableException e1) {
             e1.printStackTrace();
            }
            try {
             clip.open(audioInputStream);
            } catch (LineUnavailableException | IOException e1) {
             e1.printStackTrace();
            }
            clip.start();
            }
    }
    
    public static void main(String[] args) throws FileNotFoundException {
            new Memory();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       if(e.getSource() instanceof Card) {
           Card chosenCard = (Card) e.getSource();
           if (!timerOn && chosenCard.getStatus() == Card.Status.HIDDEN) {
               chosenCard.setStatus(Card.Status.VISIBLE);
               if (upsideCard == null) {
                   upsideCard = chosenCard;
               } else {
//                   for (int i = 0; i < gameCards.length; i++) { ////*Antoher way of doing so you can't click buttons when timerOn.
//                       if (gameCards[i].getStatus() == Card.Status.HIDDEN) {
//                           gameCards[i].setEnabled(false);
//                       }
//                   }
                   timerOn = true;
                   timer.schedule(new TimerTask() {
                       @Override
                       public void run() {
//                           for (int i = 0; i < gameCards.length; i++) { ////*
//                               gameCards[i].setEnabled(true);
//                           }
                           if (players.length > 1) {
                               if (upsideCard.equalIcon(chosenCard)) {
                                   upsideCard.setStatus(Card.Status.MISSING);
                                   chosenCard.setStatus(Card.Status.MISSING);
                                   upsideCard.setEnabled(false);
                                   chosenCard.setEnabled(false);
                                   upsideCard = null;
                                   Player activePlayer = players[playerIndex];
                                   activePlayer.setScore(activePlayer.getScore() + 1);
                                   nbrOfPairs = nbrOfPairs + 1;
                               }
                               else {
                                   upsideCard.setStatus(Card.Status.HIDDEN);
                                   chosenCard.setStatus(Card.Status.HIDDEN);
                                   upsideCard = null;
                                   togglePlayer();
                               }
                               if (nbrOfPairs*2 == gameCards.length) {
                                   gameOver = true;
                               }
                               int highestScorer = players[0].getScore();
                               String winner = players[0].getName();
                               if (gameOver) {
                                   playSound();
                                   for (int i = 1; i < players.length; i++) {
                                       int scoreOfNext = players[i].getScore();
                                       if (highestScorer < scoreOfNext) {
                                           highestScorer = scoreOfNext;
                                           winner = players[i].getName();
                                       }
                                       else if (scoreOfNext == highestScorer) {
                                           winner = winner + " and " + players[i].getName();
                                       }
                                   }
                               }
                               if (gameOver) {
                                   nbrOfPairs = 0;
                                   for (int i = 0; i < gameCards.length; i++) {
                                       gameCards[i].setStatus(Card.Status.VISIBLE);
                                   }
                                   int newgame = JOptionPane.showConfirmDialog(
                                           null,
                                           winner +" won with amazing " + highestScorer + " points!" + 
                                           " Do you want to play another game?",
                                           "",
                                           JOptionPane.YES_NO_OPTION);
                                   if (newgame == JOptionPane.NO_OPTION) {
                                       System.exit(0);
                                   }
                                   else {
                                       newGame(rememberRows, rememberColumns);
                                   }
                               }
                           }
                           else {
                               players[0].setScore(players[0].getScore() + 1);
                               if (upsideCard.equalIcon(chosenCard)) {
                                   upsideCard.setStatus(Card.Status.MISSING);
                                   chosenCard.setStatus(Card.Status.MISSING);
                                   upsideCard.setEnabled(false);
                                   chosenCard.setEnabled(false);
                                   upsideCard = null;
                                   nbrOfPairs = nbrOfPairs + 1;
                               }
                               else {
                                   upsideCard.setStatus(Card.Status.HIDDEN);
                                   chosenCard.setStatus(Card.Status.HIDDEN);
                                   upsideCard = null;
                               }
                               if (nbrOfPairs*2 == gameCards.length) {
                                   gameOver = true;
                               }
                               if (gameOver) {
                                   playSound();
                                   nbrOfPairs = 0;
                                   for (int i = 0; i < gameCards.length; i++) {
                                       gameCards[i].setStatus(Card.Status.VISIBLE);
                                   }
                                   double playerScore = (double)(players[0].getScore());
                                   double rightPercent = ((gameCards.length/2)/playerScore)*100;
                                   int newgame = JOptionPane.showConfirmDialog(
                                           null,
                                           "You picked the right pairs " + new DecimalFormat("#0.0").format(rightPercent) + 
                                           "% of the time. Do you want to try to beat that?",
                                           "",
                                           JOptionPane.YES_NO_OPTION);
                                   if (newgame == JOptionPane.NO_OPTION) {
                                       System.exit(0);
                                   }
                                   else {
                                       newGame(rememberRows, rememberColumns);
                                   }
                               }
                           }
                               timerOn = false;
                           }
                     }, timeOnTimer);
                   if (upsideCard.equalIcon(chosenCard)) {
                       happySound = true;
                   }
                   else {
                       happySound = false;
                   }
                   playSound();
               }
           }
       }   
    }
}

//A class Player that make it eaiser to add players in game.
class Player extends JPanel {
    private String name;
    private int score = 0;
    private JLabel scoreLabel;
    
    public Player(String name) {
        this.name = name;
        scoreLabel = new JLabel(""+this.score);
        JLabel nameLabel = new JLabel(this.name);
        
        scoreLabel.setHorizontalAlignment(JLabel.CENTER);
        scoreLabel.setFont(new Font("Courier", Font.BOLD, 15));
        nameLabel.setHorizontalAlignment(JLabel.CENTER);
        
        setPreferredSize(new Dimension(nameLabel.getPreferredSize().width+20, 0));
        setBackground(Color.LIGHT_GRAY);
        setLayout(new GridLayout(2, 1));
        add(nameLabel);
        add(scoreLabel);
    }
    
    public void setScore(int s) {
        this.score = s;
        scoreLabel.setText(""+this.score);
    }
    
    public int getScore() {
        return this.score;
    }
    
    public String getName() {
        return this.name;
    }
}

