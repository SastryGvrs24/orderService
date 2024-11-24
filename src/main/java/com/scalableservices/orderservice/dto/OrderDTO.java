package com.scalableservices.orderservice.dto;

import java.util.List;

public class OrderDTO {

    RestaurantDTO restaurantDTO;
    List<MenuItemDTO> menuItemDTO;
    String orderstatus;

    public RestaurantDTO getRestaurantDTO() {
        return restaurantDTO;
    }

    public void setRestaurantDTO(RestaurantDTO restaurantDTO) {
        this.restaurantDTO = restaurantDTO;
    }

    public List<MenuItemDTO> getMenuItemDTO() {
        return menuItemDTO;
    }

    public void setMenuItemDTO(List<MenuItemDTO> menuItemDTO) {
        this.menuItemDTO = menuItemDTO;
    }

    public String getOrderstatus() {
        return orderstatus;
    }

    public void setOrderstatus(String orderstatus) {
        this.orderstatus = orderstatus;
    }
}
