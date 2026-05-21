package com.allfire.qqcolormanager.storage;

import com.allfire.qqcolormanager.QQColorManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import java.nio.ByteBuffer;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class H2Storage implements ColorStorage {
    private HikariDataSource dataSource;
    private final ConfigurationSection config;
    private final QQColorManager plugin;

    public H2Storage(ConfigurationSection config, QQColorManager plugin) {
        this.config = config;
        this.plugin = plugin;
    }

    @Override
    public void init() throws SQLException {
        String file = config != null ? config.getString("file", "data/colors") : "data/colors";
        String jdbcUrl = "jdbc:h2:./plugins/QQColorManager/" + file;
        
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setDriverClassName("org.h2.Driver");
        hikariConfig.setUsername("sa");
        hikariConfig.setPassword("");
        hikariConfig.setMaximumPoolSize(10);
        hikariConfig.setPoolName("QQColorManager-H2");
        
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
            )
            """;
        
        String gradientsTable = """
            CREATE TABLE IF NOT EXISTS player_gradients (
                uuid BINARY(16) NOT NULL,
                player_name VARCHAR(16) NOT NULL,
                gradient VARCHAR(64) NOT NULL,
                slot INT NOT NULL,
                color VARCHAR(6) NOT NULL,
                PRIMARY KEY (uuid, gradient, slot)
            )
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

    private UUID bytesToUuid(byte[] bytes) {
        ByteBuffer bb = ByteBuffer.wrap(bytes);
        return new UUID(bb.getLong(), bb.getLong());
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
            plugin.getLogger().severe("Failed to get player name: " + e.getMessage());
        }
        
        // Try gradients table
        String sql2 = "SELECT player_name FROM player_gradients WHERE uuid = ? LIMIT 1";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql2)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("player_name");
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get player name from gradients: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void setColor(UUID uuid, String playerName, String template, int slot, String hex) {
        String sql = "MERGE INTO player_colors (uuid, player_name, template, slot, color) KEY(uuid, template, slot) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, playerName);
            stmt.setString(3, template);
            stmt.setInt(4, slot);
            stmt.setString(5, hex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set color: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get color: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get all colors: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get all player colors: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to remove color: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to remove all colors: " + e.getMessage());
        }
    }

    @Override
    public void setGradientColor(UUID uuid, String playerName, String gradient, int slot, String hex) {
        String sql = "MERGE INTO player_gradients (uuid, player_name, gradient, slot, color) KEY(uuid, gradient, slot) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setString(2, playerName);
            stmt.setString(3, gradient);
            stmt.setInt(4, slot);
            stmt.setString(5, hex);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to set gradient color: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get gradient color: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get all gradient colors: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to get all player gradients: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to remove gradient: " + e.getMessage());
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
            plugin.getLogger().severe("Failed to remove all gradients: " + e.getMessage());
        }
    }

    @Override
    public void clearPlayer(UUID uuid) {
        removeAllColors(uuid);
        removeAllGradients(uuid);
    }

    @Override
    public boolean hasAnyData(UUID uuid) {
        String sql = "SELECT COUNT(*) FROM player_colors WHERE uuid = ? UNION ALL SELECT COUNT(*) FROM player_gradients WHERE uuid = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBytes(1, uuidToBytes(uuid));
            stmt.setBytes(2, uuidToBytes(uuid));
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                if (rs.getInt(1) > 0) return true;
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to check player data: " + e.getMessage());
        }
        return false;
    }
}
