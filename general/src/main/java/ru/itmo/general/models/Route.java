package ru.itmo.general.models;

import lombok.Getter;
import lombok.Setter;
import ru.itmo.general.utility.base.Element;


import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class Route extends Element {

    private Integer id; // Автоматически генерируемый уникальный идентификатор
    @Getter
    private Integer userId;
    private String name; // Имя маршрута
    private CooRDiNates1 coordinates; // Координаты начала маршрута
    private Date creationDate; // Дата создания
    private LocactioN from; // Начальная локация
    private LocactioN to; // Конечная локация (может быть null)
    private float distance; // Дистанция маршрута, должна быть больше 1

    // Конструктор с параметрами для полной инициализации объекта
    public Route(Integer nextID, String name, CooRDiNates1 coordinates, Date creationDate, LocactioN from, LocactioN to, float distance) {
        this.id = nextID;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.from = from;
        this.to = to;
        this.distance = distance;
    }

    public Route() {
        this.id = null; // или другой способ генерации ID
        this.name = "";
        this.coordinates = new CooRDiNates1(0.0, 0.0f);
        this.creationDate = new Date();
        this.from = new LocactioN(0L, 0, "");
        this.to = new LocactioN(0L, 0, "");
        this.distance = 1.0f; // Минимальная валидная дистанция
    }

    // Упрощенный конструктор с автоматической установкой даты создания
    public Route(Integer nextID, String name, CooRDiNates1 coordinates, LocactioN from, LocactioN to, float distance) {
        this(nextID, name, coordinates, new Date(), from, to, distance);
    }

    // Метод для валидации объекта Route
    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null) return false;
        if (creationDate == null) return false;
        if (from == null) return false;
        if (distance <= 1) return false;
        return true;
    }

    // Переопределение метода toString для вывода информации о маршруте
    @Override
    public String toString() {
        return "Route{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", from=" + from +
                ", to=" + (to != null ? to : "null") +
                ", distance=" + distance +
                '}';
    }

    // Переопределение методов equals и hashCode для корректной работы в коллекциях
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Route)) return false;
        Route route = (Route) o;
        Integer INT = id;
        return INT.equals(route.id);
    }

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Переопределение метода compareTo для сравнения маршрутов
    @Override
    public int compareTo(Element element) {
        if (!(element instanceof Route)) throw new IllegalArgumentException("Cannot compare Route with " + element.getClass());
        Route other = (Route) element;
        return this.name.compareTo(other.name);
    }

    // Метод для обновления объекта Route
    public void update(Route newRoute) {
        this.name = newRoute.name;
        this.coordinates = newRoute.coordinates;
        this.creationDate = newRoute.creationDate;
        this.from = newRoute.from;
        this.to = newRoute.to;
        this.distance = newRoute.distance;
    }
}
