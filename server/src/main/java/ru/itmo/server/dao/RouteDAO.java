package ru.itmo.server.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.models.CooRDiNates1;
import ru.itmo.general.models.LocactioN;
import ru.itmo.general.models.Route;
import ru.itmo.general.utility.base.Accessible;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ru.itmo.server.managers.ConnectionManager.*;

/**
 * The RouteDAO class provides methods for interacting with the routes table in the database.
 * It handles route creation, retrieval, updating, and removal.
 */
public class RouteDAO implements Accessible {
    private static final Logger LOGGER = LoggerFactory.getLogger("RouteDAO");
    private static final String SELECT_ALL_TICKETS_SQL = "SELECT * FROM routes";



    private static final String CREATE_TICKETS_TABLE_SQL = "CREATE TABLE IF NOT EXISTS routes (" +
            "id SERIAL PRIMARY KEY," +
            "name VARCHAR NOT NULL," +
            "coordinates_x DOUBLE PRECISION NOT NULL," +
            "coordinates_y FLOAT NOT NULL," +
            "creation_date TIMESTAMP," +
            "from_x BIGINT NOT NULL," +
            "from_y INT NOT NULL," +
            "from_name TEXT NOT NULL," +  // Assuming you want to store the name of the location 'from'
            "to_x BIGINT NOT NULL," +
            "to_y INT NOT NULL," +
            "to_name TEXT NOT NULL," +  // Assuming you want to store the name of the location 'to'
            "distance_of_the_route FLOAT NOT NULL," +  // Adjusted type to FLOAT to match the Java type
            "user_id INT," +
            "FOREIGN KEY (user_id) REFERENCES users(id))";



    private static final String INSERT_TICKET_SQL = "INSERT INTO routes (" +
            "name, coordinates_x, coordinates_y, creation_date, " +
            "from_x, from_y, from_name, to_x, to_y, to_name, distance_of_the_route, user_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


    private static final String REMOVE_TICKET_SQL = "DELETE FROM routes WHERE id = ?";
    private static final String CHECK_TICKET_OWNERSHIP_SQL = "SELECT user_id FROM routes WHERE id = ?";
    private static final String UPDATE_TICKET_SQL = "UPDATE routes SET " + "name = ?, " + "coordinates_x = ?, " + "coordinates_y = ?, " + "creation_date = ?, " + "location_x = ?, " + "location_y = ? " + "WHERE id = ?";
    private static final String REMOVE_ROUTES_BY_USER_ID_SQL = "DELETE FROM routes WHERE user_id = ?";


    /**
     * Adds a new route to the database.
     *
     * @param route  The route to be added.
     * @param userId The ID of the user adding the route.
     * @return The ID of the newly added route if successful, otherwise -1.
     */

    public int addRoute(Route route, int userId) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_TICKET_SQL, Statement.RETURN_GENERATED_KEYS)) {

            set(userId, statement, route);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        route.setId(generatedId);  // Установите ID в объекте Route
                        return generatedId;
                    } else {
                        LOGGER.error("Failed to retrieve generated keys after adding route");
                        return -1;
                    }
                }
            } else {
                LOGGER.error("No rows were affected while adding route");
                return -1;
            }
        } catch (SQLException e) {
            LOGGER.error("Error while adding route: {}", e.getMessage());
            return -1;
        }
    }



    /**
     * Adds a collection of routes to the database.
     *
     * @param routes The collection of routes to be added.
     * @param userId The ID of the user adding the routes.
     */
    public void addRoutes(Collection<Route> routes, int userId) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(INSERT_TICKET_SQL)) {
            for (Route route : routes) {
                set(userId, statement, route);
                statement.addBatch();
            }
            int[] results = statement.executeBatch();
            // Check the results array to determine the success of each insertion
            for (int result : results) {
                if (result <= 0) {
                    return; // At least one insertion failed
                }
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while adding routes, continuing without adding routes");
        } catch (SQLException e) {
            LOGGER.error("Error while adding routes {}", e.getMessage());
        }
    }

    private void set(int userId, PreparedStatement statement, Route route) throws SQLException {
        set(statement, route);
        statement.setInt(12 ,userId); // User's ID who added the route
    }

    private void set(PreparedStatement statement, Route route) throws SQLException {
        statement.setString(1, route.getName());
        statement.setDouble(2, route.getCoordinates().x());
        statement.setFloat(3, route.getCoordinates().y());
        statement.setTimestamp(4, new Timestamp(route.getCreationDate().getTime()));
        statement.setLong(5, route.getFrom().getX());
        statement.setInt(6, route.getFrom().getY());
        statement.setString(7, route.getFrom().getName());
        statement.setLong(8, route.getTo().getX());
        statement.setInt(9, route.getTo().getY());
        statement.setString(10, route.getTo().getName());
        statement.setFloat(11, route.getDistance());
    }



//    private void set(PreparedStatement statement, Route route) throws SQLException {
//        statement.setString(1, route.getName());
//        statement.setDouble(2, route.getCoordinates().x());
//        statement.setFloat(3, route.getCoordinates().y());
//        statement.setTimestamp(4, Timestamp.from(route.getCreationDate().toInstant()));
//        statement.setLong(5, route.getFrom().getX());
//        statement.setInt(6, route.getFrom().getY());
//        statement.setString(7, route.getFrom().getName());
//        statement.setLong(8, route.getTo().getX());
//        statement.setInt(9, route.getTo().getY());
//        statement.setString(10, route.getTo().getName());
//        statement.setFloat(11, route.getDistance());
//    }
    /**
     * Retrieves all routes from the database.
     *
     * @return A list of all routes retrieved from the database.
     */

    public List<Route> getAllRoutes() {
        List<Route> routes = new ArrayList<>();
        try (Connection connection = getConnection()) {
            if (connection == null) {
                LOGGER.error("Connection is null");
                System.out.println("Connection is null");
                return routes;
            }
            System.out.println("Connection established");

            try (PreparedStatement statement = connection.prepareStatement(SELECT_ALL_TICKETS_SQL);
                 ResultSet resultSet = statement.executeQuery()) {
                System.out.println("Executing query: " + SELECT_ALL_TICKETS_SQL);
                while (resultSet.next()) {
                    Route route = extractRouteFromResultSet(resultSet);
                    System.out.println("Fetched route: " + route);
                    routes.add(route);
                    System.out.println("Added route to list");
                }
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while getting all routes: {}", exception.getMessage());
            exception.printStackTrace();
        } catch (SQLException e) {
            LOGGER.error("Error while retrieving routes from the database: {}", e.getMessage());
            e.printStackTrace();
        }
        return routes;
    }






    /**
     * Removes a route from the database by its ID.
     *
     * @param routeId The ID of the route to be removed.
     * @return true if the route was successfully removed, false otherwise.
     */
    public boolean removeRouteById(int routeId) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(REMOVE_TICKET_SQL)) {
            statement.setInt(1, routeId);
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while removing route, continuing without removing route");
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while deleting route with ID {}: {}", routeId, e.getMessage());
            return false;
        }
    }



    /**
     * Updates a route in the database.
     *
     * @param route The route with updated information.
     * @return true if the route was successfully updated, false otherwise.
     */
    public boolean updateRoute(Route route) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(UPDATE_TICKET_SQL)) {

            set(statement, route);
            statement.setInt(13, route.getId());
            return executePrepareUpdate(statement) > 0;
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while updating route, continuing without updating route");
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while updating route {}: {}", route.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Creates the routes table in the database if it does not already exist.
     */
    public void createTablesIfNotExist() {
        Connection connection = getConnection();
        executeUpdate(connection, CREATE_TICKETS_TABLE_SQL);
    }

    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String name = resultSet.getString("name");
        double coordinatesX = resultSet.getDouble("coordinates_x");
        float coordinatesY = resultSet.getFloat("coordinates_y");
        Timestamp creationDate = resultSet.getTimestamp("creation_date");

        Long fromX = resultSet.getLong("from_x");
        Integer fromY = resultSet.getInt("from_y");
        String fromName = resultSet.getString("from_name");
        LocactioN from = new LocactioN(fromX, fromY, fromName);

        Long toX = resultSet.getLong("to_x");
        Integer toY = resultSet.getInt("to_y");
        String toName = resultSet.getString("to_name");
        LocactioN to = new LocactioN(toX, toY, toName);

        float distance = resultSet.getFloat("distance_of_the_route");
        int userId = resultSet.getInt("user_id");

        Route route = new Route(id, name, new CooRDiNates1(coordinatesX, coordinatesY), creationDate, from, to, distance);
        route.setUserId(userId);

        return route;
    }



//    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String name = resultSet.getString("name");
//        double coordinatesX = resultSet.getDouble("coordinates_x");
//        float coordinatesY = resultSet.getFloat("coordinates_y");
//        Timestamp creationDateTimestamp = resultSet.getTimestamp("creation_date");
//        // Используем Timestamp напрямую, чтобы избежать проблем с преобразованием
//        Date creationDate = new Date(creationDateTimestamp.getTime());
//
//        Long fromX = resultSet.getLong("from_x");
//        Integer fromY = resultSet.getInt("from_y");
//        String fromName = resultSet.getString("from_name");
//        LocactioN from = new LocactioN(fromX, fromY, fromName);
//
//        Long toX = resultSet.getLong("to_x");
//        Integer toY = resultSet.getInt("to_y");
//        String toName = resultSet.getString("to_name");
//        LocactioN to = new LocactioN(toX, toY, toName);
//
//        float distance = resultSet.getFloat("distance_of_the_route");
//
//        int userId = resultSet.getInt("user_id");
//
//        Route route = new Route(id, name, new CooRDiNates1(coordinatesX, coordinatesY), creationDate, from, to, distance);
//        route.setUserId(userId);
//
//        return route;
//    }


//    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String name = resultSet.getString("name");
//        double coordinatesX = resultSet.getDouble("coordinates_x");
//        float coordinatesY = resultSet.getFloat("coordinates_y");
//        Timestamp creationDateTimestamp = resultSet.getTimestamp("creation_date");
//        Date creationDate = new Date(creationDateTimestamp.getTime());
//
//        // Извлечение информации о начальной и конечной локации
//        Long fromX = resultSet.getLong("from_x");
//        Integer fromY = resultSet.getInt("from_y");
//        String fromName = resultSet.getString("from_name");
//        LocactioN from = new LocactioN(fromX, fromY, fromName);
//
//        Long toX = resultSet.getLong("to_x");
//        Integer toY = resultSet.getInt("to_y");
//        String toName = resultSet.getString("to_name");
//        LocactioN to = new LocactioN(toX, toY, toName);
//
//        float distance = resultSet.getFloat("distance_of_the_route");
//
//        // Извлечение user_id
//        int userId = resultSet.getInt("user_id");
//
//        // Создание и возврат объекта Route
//        Route route = new Route(id, name, new CooRDiNates1(coordinatesX, coordinatesY), creationDate, from, to, distance);
//        route.setUserId(userId);  // Установим user_id в объект Route
//
//        return route;
//    }


//    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String name = resultSet.getString("name");
//        double coordinatesX = resultSet.getDouble("coordinates_x");
//        float coordinatesY = resultSet.getFloat("coordinates_y");
//        Timestamp creationDateTimestamp = resultSet.getTimestamp("creation_date");
//        Date creationDate = new Date(creationDateTimestamp.getTime());  // Преобразование Timestamp в Date
//
//        // Извлечение информации о начальной и конечной локации
//        Long fromX = resultSet.getLong("from_x");
//        Integer fromY = resultSet.getInt("from_y");
//        String fromName = resultSet.getString("from_name");
//        LocactioN from = new LocactioN(fromX, fromY, fromName);
//
//        Long toX = resultSet.getLong("to_x");
//        Integer toY = resultSet.getInt("to_y");
//        String toName = resultSet.getString("to_name");
//        LocactioN to = new LocactioN(toX, toY, toName);
//
//        float distance = resultSet.getFloat("distance_of_the_route");
//
//        return new Route(id, name, new CooRDiNates1(coordinatesX, coordinatesY), creationDate, from, to, distance);
//    }


//    private Route extractRouteFromResultSet(ResultSet resultSet) throws SQLException {
//        int id = resultSet.getInt("id");
//        String name = resultSet.getString("name");
//        double coordinatesX = resultSet.getDouble("coordinates_x");
//        float coordinatesY = resultSet.getFloat("coordinates_y");
//        Timestamp creationDateTimestamp = resultSet.getTimestamp("creation_date");
//        Date creationDate = new Date(creationDateTimestamp.getTime());  // Преобразование Timestamp в Date
//
//        // Извлечение информации о начальной и конечной локации
//        Long locationFromX = resultSet.getLong("location_from_x");
//        Integer locationFromY = resultSet.getInt("location_from_y");
//        String locationFromName = resultSet.getString("location_from_name");
//        LocactioN from = new LocactioN(locationFromX, locationFromY, locationFromName);
//
//        Long locationToX = resultSet.getLong("location_to_x");
//        Integer locationToY = resultSet.getInt("location_to_y");
//        String locationToName = resultSet.getString("location_to_name");
//        LocactioN to = new LocactioN(locationToX, locationToY, locationToName);
//
//        float distance = resultSet.getFloat("distance"); // Предположим, что такое поле есть в вашей таблице.
//
//        return new Route(id, name, new CooRDiNates1(coordinatesX, coordinatesY), creationDate, from, to, distance);
//    }


    /**
     * Checks if a route belongs to a specific user.
     *
     * @param routeId The ID of the route.
     * @param userId  The ID of the user.
     * @return true if the route belongs to the user, false otherwise.
     */
    @Override
    public boolean checkOwnership(int routeId, int userId) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(CHECK_TICKET_OWNERSHIP_SQL)) {
            statement.setInt(1, routeId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int ownerId = resultSet.getInt("user_id");
                return ownerId == userId;
            } else {
                return false;
            }
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while checking ownership of route with ID {}: {}", routeId, exception.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while checking ownership of route with ID {}: {}", routeId, e.getMessage());
            return false;
        }
    }

    public boolean removeRoutesByUserId(int userId) {
        try (Connection connection = getConnection(); PreparedStatement statement = connection.prepareStatement(REMOVE_ROUTES_BY_USER_ID_SQL)) {
            statement.setInt(1, userId);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (NullPointerException exception) {
            LOGGER.error("Null pointer exception while removing routes for user with ID {}: {}",
                    userId, exception.getMessage());
            return false;
        } catch (SQLException e) {
            LOGGER.error("Error while removing routes for user with ID {}: {}", userId, e.getMessage());
            return false;
        }
    }


}
