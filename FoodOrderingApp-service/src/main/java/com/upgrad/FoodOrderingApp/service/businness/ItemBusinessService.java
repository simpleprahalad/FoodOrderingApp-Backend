package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemBusinessService {

    @Autowired
    private ItemDao itemDao;

    /**
     *
     * @param categoryId
     * @return List of ItemEntity
     */
    public List<ItemEntity> getItemsForCategory(final String categoryId) {

        return itemDao.getItemsForCategory(categoryId);
    }

}
