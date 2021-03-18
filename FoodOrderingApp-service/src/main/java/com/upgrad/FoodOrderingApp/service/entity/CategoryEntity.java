package com.upgrad.FoodOrderingApp.service.entity;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.UUID;

@Entity
@Table(name = "category")
@NamedQueries(
        {
                @NamedQuery(name = "allCategories", query = "select c from CategoryEntity c ORDER BY c.categoryName"),
                @NamedQuery(name = "categoryDetails", query = "select c from CategoryEntity c where c.uuid=:categoryId"),
                @NamedQuery(name = "categoryListOfRestaurant", query = "SELECT c FROM CategoryEntity c WHERE c.id IN (SELECT rc.categoryId FROM RestaurantCategoryEntity rc where rc.restaurantId =:restaurantId) order by c.categoryName")

        }
)
public class CategoryEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "uuid")
    @Size(max = 200)
    private String uuid;

    @Column(name = "category_name")
    @Size(max = 255)
    private String categoryName;

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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

}
