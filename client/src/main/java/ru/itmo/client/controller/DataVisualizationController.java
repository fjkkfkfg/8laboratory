//
//
//package ru.itmo.client.controller;
//
//import javafx.animation.KeyFrame;
//import javafx.animation.Timeline;
//import javafx.fxml.FXML;
//import javafx.scene.canvas.Canvas;
//import javafx.scene.canvas.GraphicsContext;
//import javafx.scene.input.MouseEvent;
//import javafx.scene.paint.Color;
//import javafx.util.Duration;
//import ru.itmo.client.MainApp;
//import ru.itmo.client.utility.runtime.Runner;
//import ru.itmo.general.models.Route;
//
//import java.util.*;
//
//public class DataVisualizationController {
//    private MainApp mainApp;
//    private Runner runner;
//    private MainController mainController;
//
//    @FXML
//    private Canvas canvas;
//
//    private List<Route> routes;
//    private List<CircleAnimation> circles;
//    private Map<Integer, Color> userColors;
//    private Random random;
//
//    public void setMainApp(MainApp mainApp) {
//        this.mainApp = mainApp;
//    }
//
//    public void setRunner(Runner runner) {
//        this.runner = runner;
//    }
//
//    public void setMainController(MainController mainController) {
//        this.mainController = mainController;
//    }
//
//    @FXML
//    private void initialize() {
//        routes = new ArrayList<>();
//        circles = new ArrayList<>();
//        userColors = new HashMap<>();
//        random = new Random();
//
//        // Запуск анимации
//        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> draw()));
//        timeline.setCycleCount(Timeline.INDEFINITE);
//        timeline.play();
//
//        canvas.setOnMouseClicked(this::handleCanvasClick);
//    }
//
//    public void initializeRoutes(List<Route> routes) {
//        this.routes.clear();
//        this.circles.clear();
//        for (Route route : routes) {
//            addRoute(route);
//        }
//    }
//
//    public void addRoute(Route route) {
//        routes.add(route);
//        if (!userColors.containsKey(route.getUserId())) {
//            userColors.put(route.getUserId(), generateRandomColor());
//        }
//        circles.add(new CircleAnimation(route, userColors.get(route.getUserId())));
//    }
//
//    public void removeRoute(Route route) {
//        routes.remove(route);
//        Iterator<CircleAnimation> iterator = circles.iterator();
//        while (iterator.hasNext()) {
//            CircleAnimation circle = iterator.next();
//            if (circle.getRoute().equals(route)) {
//                iterator.remove();
//                break;
//            }
//        }
//    }
//
//    public void updateRoute(Route route) {
//        removeRoute(route);
//        addRoute(route);
//    }
//
//    public void clearAllRoutes() {
//        routes.clear();
//        circles.clear();
//    }
//
//    private void draw() {
//        GraphicsContext gc = canvas.getGraphicsContext2D();
//        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
//
//        for (CircleAnimation circle : circles) {
//            circle.update();
//            circle.draw(gc);
//        }
//    }
//
//    private void handleCanvasClick(MouseEvent event) {
//        for (CircleAnimation circle : circles) {
//            if (circle.contains(event.getX(), event.getY())) {
//                mainController.selectRoute(circle.getRoute());
//                break;
//            }
//        }
//    }
//
//    private class CircleAnimation {
//        private Route route;
//        private double x, y;
//        private double dx, dy;
//        private double radius;
//        private Color color;
//
//        public CircleAnimation(Route route, Color color) {
//            this.route = route;
//            this.color = color;
//            Random random = new Random();
//            this.x = random.nextDouble() * canvas.getWidth();
//            this.y = random.nextDouble() * canvas.getHeight();
//            this.dx = random.nextDouble() * 4 - 2;
//            this.dy = random.nextDouble() * 4 - 2;
//            this.radius = calculateRadius(route);
//        }
//
//        public Route getRoute() {
//            return route;
//        }
//
//        private double calculateRadius(Route route) {
//            return 10 + route.getDistance() * 2;
//        }
//
//        public void update() {
//            x += dx;
//            y += dy;
//
//            // Отскакивать от стен
//            if (x - radius < 0 || x + radius > canvas.getWidth()) {
//                dx = -dx;
//            }
//            if (y - radius < 0 || y + radius > canvas.getHeight()) {
//                dy = -dy;
//            }
//        }
//
//        public void draw(GraphicsContext gc) {
//            gc.setFill(color);
//            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
//        }
//
//        public boolean contains(double mx, double my) {
//            double dx = mx - x;
//            double dy = my - y;
//            return dx * dx + dy * dy <= radius * radius;
//        }
//    }
//
//    private Color generateRandomColor() {
//        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
//    }
//}
//

package ru.itmo.client.controller;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import ru.itmo.client.MainApp;
import ru.itmo.client.utility.runtime.Runner;
import ru.itmo.general.models.Route;

import java.util.*;

public class DataVisualizationController {
    private MainApp mainApp;
    private Runner runner;
    private MainController mainController;

    @FXML
    private Canvas canvas;

    private List<Route> routes;
    private List<CircleAnimation> circles;
    private Map<Integer, Color> userColors;
    private Random random;

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    public void setRunner(Runner runner) {
        this.runner = runner;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void initialize() {
        routes = new ArrayList<>();
        circles = new ArrayList<>();
        userColors = new HashMap<>();
        random = new Random();

        // Запуск анимации
        Timeline timeline = new Timeline(new KeyFrame(Duration.millis(50), e -> draw()));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();

        canvas.setOnMouseClicked(this::handleCanvasClick);
    }

    public void initializeRoutes(List<Route> routes) {
        this.routes.clear();
        this.circles.clear();
        for (Route route : routes) {
            addRoute(route);
        }
    }

    public void addRoute(Route route) {
        routes.add(route);
        if (!userColors.containsKey(route.getUserId())) {
            userColors.put(route.getUserId(), generateRandomColor());
        }
        circles.add(new CircleAnimation(route, userColors.get(route.getUserId())));
    }

    public void removeRoute(Route route) {
        routes.remove(route);
        Iterator<CircleAnimation> iterator = circles.iterator();
        while (iterator.hasNext()) {
            CircleAnimation circle = iterator.next();
            if (circle.getRoute().equals(route)) {
                iterator.remove();
                break;
            }
        }
    }

    public void updateRoute(Route route) {
        removeRoute(route);
        addRoute(route);
    }

    public void clearAllRoutes() {
        routes.clear();
        circles.clear();
    }

    private void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (CircleAnimation circle : circles) {
            circle.update();
            circle.draw(gc);
        }
    }

    private void handleCanvasClick(MouseEvent event) {
        for (CircleAnimation circle : circles) {
            if (circle.contains(event.getX(), event.getY())) {
                mainController.selectRoute(circle.getRoute());
                break;
            }
        }
    }

    private class CircleAnimation {
        private Route route;
        private double x, y;
        private double dx, dy;
        private double radius;
        private Color color;

        public CircleAnimation(Route route, Color color) {
            this.route = route;
            this.color = color;
            Random random = new Random();
            this.x = random.nextDouble() * canvas.getWidth();
            this.y = random.nextDouble() * canvas.getHeight();
            this.dx = random.nextDouble() * 4 - 2;
            this.dy = random.nextDouble() * 4 - 2;
            this.radius = calculateRadius(route);
        }

        public Route getRoute() {
            return route;
        }

        private double calculateRadius(Route route) {
            return 10 + route.getDistance() * 2;
        }

        public void update() {
            x += dx;
            y += dy;

            // Отскакивать от стен
            if (x - radius < 0 || x + radius > canvas.getWidth()) {
                dx = -dx;
            }
            if (y - radius < 0 || y + radius > canvas.getHeight()) {
                dy = -dy;
            }
        }

        public void draw(GraphicsContext gc) {
            gc.setFill(color);
            gc.fillOval(x - radius, y - radius, radius * 2, radius * 2);
        }

        public boolean contains(double mx, double my) {
            double dx = mx - x;
            double dy = my - y;
            return dx * dx + dy * dy <= radius * radius;
        }
    }

    private Color generateRandomColor() {
        return Color.color(random.nextDouble(), random.nextDouble(), random.nextDouble());
    }
}

