package com.upgrad.FoodOrderingApp.service.entity;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


@Entity
@Table(name = "restaurant_item")
@NamedQueries({
        @NamedQuery(name = "getItemsByRestaurant", query = "SELECT r FROM RestaurantItemEntity r WHERE r.restaurant = :restaurant ORDER BY lower(r.item.itemName)"),
})
public class RestaurantItemEntity implements Serializable {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "item_id")
    @NotNull
    private ItemEntity item;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "restaurant_id")
    @NotNull
    private RestaurantEntity restaurant;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public ItemEntity getItem() {
        return item;
    }

    public void setItem(ItemEntity item) {
        this.item = item;
    }

    public RestaurantEntity getRestaurant() {
        return restaurant;
    }

    public void setRestaurant(RestaurantEntity restaurant) {
        this.restaurant = restaurant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantItemEntity that = (RestaurantItemEntity) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(item, that.item) &&
                Objects.equals(restaurant, that.restaurant);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, item, restaurant);
    }

    @Override
    public String toString() {
        return "RestaurantItemEntity{" +
                "id=" + id +
                ", item=" + item +
                ", restaurant=" + restaurant +
                '}';
    }
}
