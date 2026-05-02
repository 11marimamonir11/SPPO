package server.manager;

import common.model.Car;
import common.model.Coordinates;
import common.model.HumanBeing;
import common.model.Mood;
import server.util.PasswordHasher;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private final Connection connection;

    public DatabaseManager(String user, String pass) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:postgresql://pg:5432/studs", user, pass);
    }

    //SHA-1 password hashing and Authentication.This method also registers the user if they don't exist yet.
    public synchronized boolean authenticate(String username, String password) throws SQLException {
        String hashedPass = PasswordHasher.hash(password);

        // Check if user exists
        String checkQuery = "SELECT id FROM users WHERE username = ?";
        PreparedStatement checkPs = connection.prepareStatement(checkQuery);
        checkPs.setString(1, username);
        ResultSet rs = checkPs.executeQuery();

        if (rs.next()) {
            // User exists, verify password
            String loginQuery = "SELECT id FROM users WHERE username = ? AND password_hash = ?";
            PreparedStatement loginPs = connection.prepareStatement(loginQuery);
            loginPs.setString(1, username);
            loginPs.setString(2, hashedPass);
            return loginPs.executeQuery().next();
        } else {
            // Registration. Auto-register on first attempt.
            String regQuery = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            PreparedStatement regPs = connection.prepareStatement(regQuery);
            regPs.setString(1, username);
            regPs.setString(2, hashedPass);
            regPs.executeUpdate();
            return true;
        }
    }

    //For add command, form an object with DB-generated ID.

    public synchronized HumanBeing insertHumanBeing(HumanBeing hb, String username) throws SQLException {
        String query = "INSERT INTO human_beings (name, x, y, creation_date, real_hero, has_toothpick, impact_speed, soundtrack_name, minutes_of_waiting, mood, car_name, car_cool, owner_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM users WHERE username = ?)) RETURNING id";

        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, hb.name());
        ps.setInt(2, hb.coordinates().x());
        ps.setInt(3, hb.coordinates().y());
        ps.setDate(4, Date.valueOf(LocalDate.now()));
        ps.setBoolean(5, hb.realHero());
        if (hb.hasToothpick() == null) ps.setNull(6, Types.BOOLEAN);
        else ps.setBoolean(6, hb.hasToothpick());
        ps.setDouble(7, hb.impactSpeed());
        ps.setString(8, hb.soundtrackName());
        if (hb.minutesOfWaiting() == null) ps.setNull(9, Types.INTEGER);
        else ps.setInt(9, hb.minutesOfWaiting());
        ps.setString(10, hb.mood().toString());
        if (hb.car() == null) {
            ps.setNull(11, Types.VARCHAR);
            ps.setNull(12, Types.BOOLEAN);
        } else {
            ps.setString(11, hb.car().name());
            if (hb.car().cool() == null) ps.setNull(12, Types.BOOLEAN);
            else ps.setBoolean(12, hb.car().cool());
        }
        ps.setString(13, username);

        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int generatedId = rs.getInt(1);
            return new HumanBeing(generatedId, hb.name(), hb.coordinates(), LocalDate.now(), hb.realHero(),
                    hb.hasToothpick(), hb.impactSpeed(), hb.soundtrackName(),
                    hb.minutesOfWaiting(), hb.mood(), hb.car());
        }
        throw new SQLException("Failed to insert HumanBeing.");
    }

    //Users can only modify their OWN objects.
    public synchronized boolean updateHumanBeing(int id, HumanBeing hb, String username) throws SQLException {
        String query = "UPDATE human_beings SET name=?, x=?, y=?, real_hero=?, has_toothpick=?, impact_speed=?, " +
                "soundtrack_name=?, minutes_of_waiting=?, mood=?, car_name=?, car_cool=? " +
                "WHERE id=? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, hb.name());
        ps.setInt(2, hb.coordinates().x());
        ps.setInt(3, hb.coordinates().y());
        ps.setBoolean(4, hb.realHero());
        if (hb.hasToothpick() == null) ps.setNull(5, Types.BOOLEAN);
        else ps.setBoolean(5, hb.hasToothpick());
        ps.setDouble(6, hb.impactSpeed());
        ps.setString(7, hb.soundtrackName());
        if (hb.minutesOfWaiting() == null) ps.setNull(8, Types.INTEGER);
        else ps.setInt(8, hb.minutesOfWaiting());
        ps.setString(9, hb.mood().toString());
        if (hb.car() == null) {
            ps.setNull(10, Types.VARCHAR);
            ps.setNull(11, Types.BOOLEAN);
        } else {
            ps.setString(10, hb.car().name());
            if (hb.car().cool() == null) ps.setNull(11, Types.BOOLEAN);
            else ps.setBoolean(11, hb.car().cool());
        }
        ps.setInt(12, id);
        ps.setString(13, username);

        return ps.executeUpdate() > 0;
    }

    public synchronized boolean deleteHumanBeing(int id, String username) throws SQLException {
        String query = "DELETE FROM human_beings WHERE id = ? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.setString(2, username);
        return ps.executeUpdate() > 0;
    }

    public synchronized int clearUserObjects(String username) throws SQLException {
        String query = "DELETE FROM human_beings WHERE owner_id = (SELECT id FROM users WHERE username = ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, username);
        return ps.executeUpdate();
    }

    public synchronized boolean isOwner(int id, String username) throws SQLException {
        String query = "SELECT id FROM human_beings WHERE id = ? AND owner_id = (SELECT id FROM users WHERE username = ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        ps.setString(2, username);
        return ps.executeQuery().next();
    }

    public synchronized List<HumanBeing> loadAll() throws SQLException {
        List<HumanBeing> list = new ArrayList<>();
        String query = "SELECT * FROM human_beings";
        Statement st = connection.createStatement();
        ResultSet rs = st.executeQuery(query);

        while (rs.next()) {
            // Build the HumanBeing object from the database row
            HumanBeing hb = new HumanBeing(
                    rs.getInt("id"),
                    rs.getString("name"),
                    new Coordinates(rs.getInt("x"), rs.getInt("y")),
                    rs.getDate("creation_date").toLocalDate(),
                    rs.getBoolean("real_hero"),
                    rs.getObject("has_toothpick") != null ? rs.getBoolean("has_toothpick") : null,
                    rs.getDouble("impact_speed"),
                    rs.getString("soundtrack_name"),
                    rs.getObject("minutes_of_waiting") != null ? rs.getInt("minutes_of_waiting") : null,
                    Mood.valueOf(rs.getString("mood")),
                    rs.getString("car_name") != null ? new Car(rs.getString("car_name"), rs.getBoolean("car_cool")) : null
            );
            list.add(hb);
        }
        return list;
    }
}