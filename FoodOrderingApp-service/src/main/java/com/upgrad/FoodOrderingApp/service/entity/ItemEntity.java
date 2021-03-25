package com.upgrad.FoodOrderingApp.service.entity;

import com.upgrad.FoodOrderingApp.service.common.ItemType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Entity
@Table(name = "item")
@NamedQueries(
        {
                @NamedQuery(name = "getItems", query = "SELECT i FROM ItemEntity i WHERE i.uuid = :itemUuid order by i.itemName asc "),
                @NamedQuery(name = "getItemByUuid", query = "SELECT i FROM ItemEntity i WHERE i.uuid = :itemUuid"),
        }
)
public class ItemEntity implements Serializable {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    @Size(max = 200)
    @NotNull
    private String uuid;

    @Column(name = "item_name")
    @Size(max = 30)
    @NotNull
    private String itemName;

    @Column(name = "price")
    @NotNull
    private int price;

    @Column(name = "type")
    @Size(max = 10)
    @NotNull
    private ItemType type;

    @ManyToMany
    @JoinTable(name = "category_item",
            joinColumns = @JoinColumn(name = "item_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id"))
    private List<CategoryEntity> categories = new LinkedList<CategoryEntity>();

//    @OneToMany(mappedBy = "order")
//    private List<OrderItemEntity> orders = new LinkedList<OrderItemEntity>();

    public List<CategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryEntity> categories) {
        this.categories = categories;
    }

//    public List<OrderItemEntity> getOrders() {
//        return orders;
//    }
//
//    public void setOrders(List<OrderItemEntity> orders) {
//        this.orders = orders;
//    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public ItemType getType() {
        return type;
    }

    public void setType(ItemType type) {
        this.type = type;
    }
}
