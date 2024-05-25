package ru.itmo.server.managers.collections;

import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itmo.general.managers.CollectionManager;
import ru.itmo.general.models.Route;
import ru.itmo.server.dao.RouteDAO;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Управляет коллекцией маршрутов.
 *
 *
 */
public class RouteCollectionManager implements CollectionManager<Route> {
    private final Logger logger = LoggerFactory.getLogger("RouteCollectionManager");
    @Getter
    private final LinkedList<Route> collection = new LinkedList<>();
    private final ReentrantLock lock = new ReentrantLock(); // Замок для синхронизации доступа
    private final RouteDAO dao;
    private int currentId = 1;
    @Getter
    private LocalDateTime lastSaveTime;

    /**
     * Создает менеджер коллекции маршрута.
     */
    public RouteCollectionManager() {
        this.lastSaveTime = null;
        this.dao = new RouteDAO();
        this.loadCollection();
        update();
    }

    public RouteCollectionManager(RouteDAO routeDAO) {
        this.lastSaveTime = null;
        this.dao = routeDAO;
        this.loadCollection();
        update();
    }

    /**
     * Получить Route по ID
     */
    @Override
    public Route byId(int id) {
        try {
            lock.lock();
            if (collection.isEmpty()) return null;
            return collection.stream()
                    .filter(route -> route.getId() == id)
                    .findFirst()
                    .orElse(null);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Добавляет Route
     */
//    @Override
//    public boolean add(Route route, int userID) {
//        try {
//            lock.lock();
//            int newID = dao.addRoute(route, userID);
//            if (newID < 0) return false;
//            route.setId(newID);
//            collection.add(route);
//            update();
//            return true;
//        } finally {
//            lock.unlock();
//        }
//    }

    @Override
    public Integer add(Route route, int userID) {
        try {
            lock.lock();
            int newID = dao.addRoute(route, userID);
            if (newID < 0) return 0;
            route.setId(newID);
            route.setUserId(userID); // Установите userId для маршрута
            collection.add(route);
            update();
            return 1;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Обновляет Route
     */
    @Override
    public boolean update(Route route) {
        try {
            lock.lock();
            if (!contains(route)) {
                return false;
            }
            if (!dao.removeRouteById(route.getId())) return false;
            collection.remove(route);
            if (!dao.updateRoute(route)) return false;
            collection.add(route);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Удаляет Route по ID
     */
    @Override
    public boolean remove(Integer id) {
        try {
            lock.lock();
            Route route = byId(id);
            if (route == null) {
                return false;
            }
            if (!dao.removeRouteById(route.getId())) return false;
            collection.remove(route);
            update();
            return true;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Фиксирует изменения коллекции
     */
    public void update() {
        Collections.sort(collection);
    }

    /**
     * Содержит ли коллекции Route
     */
    public boolean contains(Route route) {
        try {
            lock.lock();
            for (Route t : collection) {
                if (t.getId() == route.getId()) {
                    return true;
                }
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Получить свободный ID
     */
    @Override
    public synchronized int getFreeId() {
        while (byId(currentId) != null) {
            currentId++;
        }
        return currentId;
    }

    public String collectionType() {
        return collection.getClass().getName();
    }

    @Override
    public Route getLast() {
        return collection.getLast();
    }

//    @Override
//    public boolean clear(int userId) {
//        return false;
//    }


    @Override
    public boolean remove(Route route) {
        try {
            lock.lock();
            if (!dao.removeRouteById(route.getId())) return false;
            collection.remove(route);
            return true;
        } finally {
            lock.unlock();
        }
    }


    public void clearCollection() {
        try {
            lock.lock();
            collection.clear();
        } finally {
            lock.unlock();
        }
    }

    public int collectionSize() {
        return collection.size();
    }

    @Override
    public String toString() {
        if (collection.isEmpty()) return "Коллекция пуста!";

        StringBuilder info = new StringBuilder();
        for (var Route : collection) {
            info.append(Route).append("\n\n");
        }
        return info.toString().trim();
    }

    @Override
    public boolean loadCollection() {
        try {
            lock.lock();
            Collection<Route> loadedRoutes = dao.getAllRoutes();
            if (loadedRoutes.isEmpty()) {
                collection.clear();
            } else {
                boolean success = collection.addAll(loadedRoutes);
                if (success) {
                    logger.info("Routes added successfully.");
                }
            }
            validateAll();
            return true;
        } finally {
            lock.unlock();
        }
    }


    public Route getFirst() {
        if (collection.isEmpty()) return null;
        return collection.getFirst();
    }

    public void validateAll() {
        AtomicBoolean flag = new AtomicBoolean(true);
        collection.forEach(route -> {
            if (!route.validate()) {
                logger.error("Маршрут с id={} имеет недопустимые поля.", route.getId());
                flag.set(false);
            }
        });
        if (flag.get()) {
            logger.info("! Загруженные маршруты валидны.");
        }
    }



    @Override
    public boolean removeAllByUserId(int userId) {
        boolean removed = false;
        try {
            lock.lock();
            Iterator<Route> iterator = collection.iterator();
            while (iterator.hasNext()) {
                Route route = iterator.next();
                if (route.getUserId() == userId) {
                    iterator.remove();
                    removed = true;
                }
            }
            update();
        } finally {
            lock.unlock();
        }
        return removed;
    }


    @Override
    public boolean clear(int userId) {
        try {
            lock.lock();
            boolean result = dao.removeRoutesByUserId(userId);
            if (result) {
                collection.removeIf(ticket -> ticket.getUserId() == userId);
            }
            return result;
        } finally {
            lock.unlock();
        }
    }
}