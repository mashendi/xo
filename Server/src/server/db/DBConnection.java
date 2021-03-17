package server.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBConnection {

    private Connection con;
    private static DBConnection databaseObject;
    private PreparedStatement pst;
    private PreparedStatement pst1;
    private Statement stmt;
    private ResultSet rs;
    public static Vector<String> playerList;

    private DBConnection() {
        playerList = new Vector<>();
    }

    public static DBConnection getDatabaseInstance() {
        if (databaseObject == null) {
            databaseObject = new DBConnection();
        }
        return databaseObject;
    }

    public void openConnection() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/tictactoe", "root", "");
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void addPlayer(String username, String password) {
        try {
            pst = con.prepareStatement("INSERT INTO players (username, password, status, score, playstatus) VALUES (?, ?, 0, 0, 0)");
            pst.setString(1, username);
            pst.setString(2, password);
            pst.execute();
            pst.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isPlayerExists(String username) {
        try {
            pst = con.prepareStatement("SELECT username FROM players WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, username);
            rs = pst.executeQuery();
            if (!rs.next()) {
                pst.close();
                rs.close();
                return false;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    public boolean checkPlayerPassword(String username, String password) {
        try {
            String databasePass;
            pst = con.prepareStatement("SELECT password FROM players WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, username);
            rs = pst.executeQuery();
            rs.next();
            databasePass = rs.getString(1);

            if (databasePass.equals(password)) {
                pst.close();
                rs.close();
                return true;
            }

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public void signInPlayer(String username) {
        try {
            pst = con.prepareStatement("UPDATE players SET status=? where username = ?", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            pst.setBoolean(1, true);
            pst.setString(2, username);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public int numOfOnlinePlayers() {
        int online = 0;
        try {
            pst = con.prepareStatement("SELECT * FROM players WHERE status = true", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs = pst.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                ++online;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return online;
    }

    public int numOfflinePlayers() {
        int offline = 0;
        try {

            pst = con.prepareStatement("SELECT * FROM players WHERE status = false", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            rs = pst.executeQuery();
            rs.beforeFirst();
            while (rs.next()) {
                ++offline;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return offline;
    }

    public int getScore(String username) {
        int score = 0;
        try {
            pst = con.prepareStatement("SELECT score FROM players WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, username);
            rs = pst.executeQuery();
            if (rs.next()) {
                score = rs.getInt(1);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return score;
    }


    public void setPlaying(String username) {
        try {
            pst = con.prepareStatement("UPDATE players SET playstatus = ? WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setBoolean(1, true);
            pst.setString(2, username);
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setNotPlaying(String username) {
        try {
            pst = con.prepareStatement("UPDATE players SET playstatus=? WHERE username=?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setBoolean(1, false);
            pst.setString(2, username);
            pst.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setScore(String username, int score) {
        try {
            pst1 = con.prepareStatement("UPDATE players SET score = ? WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst1.setInt(1, score);
            pst1.setString(2, username);
            pst1.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isOnline(String username) {
        boolean onlineStatus = false;
        try {
            pst = con.prepareStatement("SELECT status FROM players WHERE username = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            pst.setString(1, username);
            rs = pst.executeQuery();
            rs.next();
            onlineStatus = rs.getBoolean(1);
            rs.close();
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        return onlineStatus;
    }

    public String getOnlinePlayersList() {
        String players = null;
        playerList.clear();
        try {
            pst = con.prepareStatement("SELECT username FROM players WHERE status = true AND playstatus = false", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);    //solved rollback exception and delay time
            players = getPlayerListString(players);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println(players);
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return players;
    }

    public String getPlayingPlayersList() {
        String players = null;
        playerList.clear();
        try {
            pst = con.prepareStatement("SELECT username FROM players WHERE status = true AND playstatus = true", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);    //solved rollback exception and delay time
            players = getPlayerListString(players);
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            pst.close();
            rs.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return players;
    }

    private String getPlayerListString(String players) throws SQLException {
        rs = pst.executeQuery();
        rs.beforeFirst();
        while (rs.next()) {
            playerList.add(rs.getString(1));
        }

        for (String s : playerList) {
            if (players == null) {
                players = "PLIST#" + s;
            } else {
                players = players + ("#" + s);
            }

        }
        return players;
    }

    public void signOutPlayer(String username) {
        try {
            pst = con.prepareStatement("UPDATE players SET status = ?, playstatus = ? WHERE username = ?", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            pst.setBoolean(1, false);
            pst.setBoolean(2, false);
            pst.setString(3, username);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setOpeningServer() {
        try {
            pst = con.prepareStatement("UPDATE players SET status = ?, playstatus = ?", ResultSet.TYPE_SCROLL_SENSITIVE,
                    ResultSet.CONCUR_UPDATABLE);
            pst.setBoolean(1, false);
            pst.setBoolean(2, false);
            pst.executeUpdate();
            pst.close();
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    public void setRecord(String sender, String playerOne, String playerTwo, String gameMoves) {
        try {
            pst = con.prepareStatement("INSERT INTO records (playerone, playertwo, record, sender) VALUES (?, ?, ?, ?)");
            pst.setString(1, playerOne);
            pst.setString(2, playerTwo);
            pst.setString(3, gameMoves);
            pst.setString(4, sender);
            pst.executeUpdate();
            //pst.close();

        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getRecord(String sender) {
        boolean firstTimeFlag = true;
        String gameMoves = "GETREC#";
        ArrayList<String> recList = new ArrayList<>();
        int i = 0;
        try {
            pst = con.prepareStatement("SELECT playerone, playertwo, record, sender FROM records WHERE sender = ?", ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);    //solved rollback exception and delay time
            pst.setString(1, sender);
            rs = pst.executeQuery();
            rs.afterLast();
            while (rs.previous() && i < 5) {
                if (firstTimeFlag) {
                    gameMoves += rs.getString(4) + "@";
                    firstTimeFlag = false;
                }
                gameMoves += rs.getString(1) + "#" + rs.getString(2) + "#" + rs.getString(3) + "!";
                i++;
            }
        } catch (SQLException ex) {
            Logger.getLogger(DBConnection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return gameMoves;
    }
}