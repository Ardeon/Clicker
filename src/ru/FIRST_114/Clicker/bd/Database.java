package ru.FIRST_114.Clicker.bd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import ru.FIRST_114.Clicker.Main;
import ru.FIRST_114.Clicker.PlayerData.Kostili;
import ru.FIRST_114.Clicker.PlayerData.PlayerStat;


public abstract class Database {
	Main plugin;
    Connection connection;
    public String table = "stats";
    public String CreateTable = "CREATE TABLE IF NOT EXISTS stats (" +
			"`player` varchar(36) NOT NULL," +
			"`score` int NOT NULL DEFAULT 0," +
			"`coins` int NOT NULL DEFAULT 0," +
			"`autoclickers` int NOT NULL DEFAULT 0," +
			"`power` int NOT NULL DEFAULT 0," +
			"PRIMARY KEY (`player`)" +
			");";
    public String CreateTable2 = "CREATE TABLE IF NOT EXISTS statsprevious (" +
			"`player` varchar(36) NOT NULL," +
			"`score` int NOT NULL DEFAULT 0," +
			"`coins` int NOT NULL DEFAULT 0," +
			"`autoclickers` int NOT NULL DEFAULT 0," +
			"`power` int NOT NULL DEFAULT 0," +
			"PRIMARY KEY (`player`)" +
			");";
	private String statsPrevious = "SELECT * into statsprevious FROM stats";
	private String erasePrevious = "DELETE FROM statsprevious;";
	//private String dropPrevious = "DROP TABLE statsprevious;";
	private String erase = "DELETE FROM stats;";
    public Database(Main instance){
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize(){
        connection = getSQLConnection();
        try {
			Statement s = connection.createStatement();
			s.executeUpdate(CreateTable);
			s.executeUpdate(CreateTable2);
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
        try{
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE player = ?");
            ResultSet rs = ps.executeQuery();
            close(ps,rs);
   
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retreive connection", ex);
        }
    }
    
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    
    public PlayerStat getOrCreatePlayerStats(Player player) {
		String uuid = player.getUniqueId().toString().toLowerCase();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("SELECT * FROM stats WHERE player = '"+uuid+"';");
            boolean playerExist = false;
            ResultSet rs = ps.executeQuery();
            PlayerStat stat = new PlayerStat();
            while(rs.next()){
                if(rs.getString(1).equalsIgnoreCase(uuid.toLowerCase())){
                	playerExist = true;

            		stat.score = rs.getInt("score");
            		stat.coins = rs.getInt("coins");
            		stat.autoclickers = rs.getInt("autoclickers");
            		stat.power = rs.getInt("power");

                }
            }
            if (!playerExist) {
            	setDefaultStat(player);
            }
            return stat;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
            }
        }
        return null;      
    }
    
    public void resetProgress() {
        Connection conn = null;
        Statement ps =null;
        try {
            conn = getSQLConnection();
            ps = conn.createStatement();
            ps.executeUpdate(erasePrevious);
            ps.executeUpdate(statsPrevious);
            ps.executeUpdate(erase);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
            }
        }     
    }
    
    public ArrayList<Kostili> getTopPlayerStats(boolean previous) {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            if(!previous)
            	ps = conn.prepareStatement("SELECT * FROM stats ORDER BY score DESC LIMIT 20;");
            else
            	ps = conn.prepareStatement("SELECT * FROM statsprevious ORDER BY score DESC LIMIT 20;");
            ResultSet rs = ps.executeQuery();
            ArrayList<Kostili> top = new ArrayList<Kostili>();
            
            while(rs.next()){
            	String uuidString = rs.getString("player");
            	UUID uuid = UUID.fromString(uuidString);
            	PlayerStat stat = new PlayerStat();
        		stat.score = rs.getInt("score");
        		stat.coins = rs.getInt("coins");
        		stat.autoclickers = rs.getInt("autoclickers");
        		stat.power = rs.getInt("power");
        		Kostili e = new Kostili(uuid,stat);
        		top.add(e);
            }
            return top;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
            }
        }
        return null;      
    }
    
    private void setDefaultStat(Player player) {
    	String uuid = player.getUniqueId().toString().toLowerCase();
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO stats (player) VALUES(?)");
            ps.setString(1, uuid);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
            }
        }
        return;
	}
    
    public void saveStats(String uuid, PlayerStat stat) {
        Connection conn = null;
        PreparedStatement ps = null;
        if (stat==null) {
        	return;
        }
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement("REPLACE INTO stats (player,score,coins,autoclickers,power) VALUES(?,?,?,?,?)");                                                                            
            ps.setString(1, uuid);
            ps.setInt(2, stat.score);
            ps.setInt(3, stat.coins);
            ps.setInt(4, stat.autoclickers);
            ps.setInt(5, stat.power);
            ps.executeUpdate();
            return;
        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Couldn't execute SQL statement:", ex);
        } finally {
            try {
                if (ps != null)
                    ps.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException ex) {
                plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection: ", ex);
            }
        }
        return;      
    }


    public void close(PreparedStatement ps,ResultSet rs){
        try {
            if (ps != null)
                ps.close();
            if (rs != null)
                rs.close();
        } catch (SQLException ex) {
        	plugin.getLogger().log(Level.SEVERE, "Failed to close SQL connection:", ex);
        }
    }
}