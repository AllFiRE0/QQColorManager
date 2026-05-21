package com.allfire.qqcolormanager.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MySQLStorage implements ColorStorage {
    private HikariDataSource dataSource;
    private final ConfigurationSection config;

    public MySQLStorage(ConfigurationSection config) {
        this.config = config;
    }

    @Override
    public void init() throws SQLException {
        String host = config.getString("host", "localhost");
        int port = config.getInt("port", 3306);
        String database = config.getString("database", "qqcolor");
        String user = config.getString("user", "root");
        String password = config.getString("password", "");
        boolean useSsl = config.getBoolean("use-ssl", false);
        int poolSize = config.getInt("pool-size", 10);
        
        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=" + useSsl + "&characterEncoding=UTF-8";
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(user);
        hikariConfig.setPassword(password);
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(poolSize);
        hikariConfig.setPoolName("QQColorManager-MySQL");
        
        dataSource = new HikariDataSource(hikariConfig);
        
        createTables();
    }

    private void createTables() throws SQLException {
        String colorsTable = """
            CREATE TABLE IF NOT EXISTS player_colors (
                uuid BINARY(16) NOT NULL,
                player_name VARCHAR(16) NOT NULL,
                template VARCHAR(64) NOT NULL,
                slot INT NOT NULL,
                color VARCHAR(6) NOT NULL,
                PRIMARY KEY (uuid, template, slot)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;
        
        String gradientsTable = """
            CREATE TABLE IF NOT EXISTS player_gradients (
                uuid BINARY(16) NOT NULL,
                player_name VARCHAR(16) NOT NULL,
                gradient VARCHAR(64) NOT NULL,
                slot INT NOT NULL,
                color VARCHAR(6) NOT NULL,
                PRIMARY KEY (uuid, gradient, slot)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4
            """;
        
        String playerIndex = "CREATE INDEX IF NOT EXISTS idx_colors_uuid ON player_colors(uuid)";
        String gradientIndex = "CREATE INDEX IF NOT EXISTS idx_gradients_uuid ON player_gradients(uuid)";
        
        try (Statement stmt = dataSource.getConnection().createStatement()) {
            stmt.execute(colorsTable);
            stmt.execute(gradientsTable);
            stmt.execute(playerIndex);
            stmt.execute(gradientIndex);
        }
    }

    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    private byte[] uuidToBytes(UUID uuid) {
        ByteBuffer bb = ByteBuffer.wrap(new byte[16]);
        bb.putLong(uuid.getMostSignificantBits());
        bb.putLong(uuid.getLeastSignificantBits());
        return bb.array();
    }

    @Override
    public void updatePlayerName(UUID uuid, String name) {
        // Names are stored with each row, no separate update needed
    }

    @Override
    public String getPlayerName(UUID uuid) {
        String sql = "SELECT player_name FROM player_colors WHERE uuid = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("player_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setColor(UUID uuid, String playerName, String template, int slot, String hex) {
        String sql = "INSERT INTO player_colors (uuid, player_name, template, slot, color) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE player_name = ?, color = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, playerName);
            stmt.setString(3, template);
            stmt.setInt(4, slot);
            stmt.setString(5, hex);
            stmt.setString(6, playerName);
            stmt.setString(7, hex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getColor(UUID uuid, String template, int slot) {
        String sql = "SELECT color FROM player_colors WHERE uuid = ? AND template = ? AND slot = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, template);
            stmt.setInt(3, slot);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("color");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<Integer, String> getAllColors(UUID uuid, String template) {
        Map<Integer, String> result = new HashMap<>();
        String sql = "SELECT slot, color FROM player_colors WHERE uuid = ? AND template = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, template);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("slot"), rs.getString("color"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, String>> getAllPlayerColors(UUID uuid) {
        Map<String, Map<Integer, String>> result = new HashMap<>();
        String sql = "SELECT template, slot, color FROM player_colors WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String template = rs.getString("template");
                int slot = rs.getInt("slot");
                String color = rs.getString("color");
                result.computeIfAbsent(template, k -> new HashMap<>()).put(slot, color);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void removeColor(UUID uuid, String template, int slot) {
        String sql = "DELETE FROM player_colors WHERE uuid = ? AND template = ? AND slot = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, template);
            stmt.setInt(3, slot);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllColors(UUID uuid) {
        String sql = "DELETE FROM player_colors WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setGradientColor(UUID uuid, String playerName, String gradient, int slot, String hex) {
        String sql = "INSERT INTO player_gradients (uuid, player_name, gradient, slot, color) VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE player_name = ?, color = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, playerName);
            stmt.setString(3, gradient);
            stmt.setInt(4, slot);
            stmt.setString(5, hex);
            stmt.setString(6, playerName);
            stmt.setString(7, hex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getGradientColor(UUID uuid, String gradient, int slot) {
        String sql = "SELECT color FROM player_gradients WHERE uuid = ? AND gradient = ? AND slot = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, gradient);
            stmt.setInt(3, slot);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("color");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<Integer, String> getAllGradientColors(UUID uuid, String gradient) {
        Map<Integer, String> result = new HashMap<>();
        String sql = "SELECT slot, color FROM player_gradients WHERE uuid = ? AND gradient = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, gradient);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                result.put(rs.getInt("slot"), rs.getString("color"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Map<String, Map<Integer, String>> getAllPlayerGradients(UUID uuid) {
        Map<String, Map<Integer, String>> result = new HashMap<>();
        String sql = "SELECT gradient, slot, color FROM player_gradients WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String gradient = rs.getString("gradient");
                int slot = rs.getInt("slot");
                String color = rs.getString("color");
                result.computeIfAbsent(gradient, k -> new HashMap<>()).put(slot, color);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public void removeGradient(UUID uuid, String gradient) {
        String sql = "DELETE FROM player_gradients WHERE uuid = ? AND gradient = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, gradient);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void removeAllGradients(UUID uuid) {
        String sql = "DELETE FROM player_gradients WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void clearPlayer(UUID uuid) {
        removeAllColors(uuid);
        removeAllGradients(uuid);
    }

    @Override
    public boolean hasAnyData(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM player_colors WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String sql2 = "SELECT COUNT(*) FROM player_gradients WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
