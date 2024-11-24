package com.scalableservices.orderservice.service;

import com.scalableservices.orderservice.domain.Orders;
import com.scalableservices.orderservice.dto.MenuItemDTO;
import com.scalableservices.orderservice.dto.OrderDTO;
import com.scalableservices.orderservice.dto.RestaurantDTO;
import com.scalableservices.orderservice.repository.OrderRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private OrderRepository orderRepository;

    @Value("${app-restaurantservice-url}")
    private String restaurantServiceUrl;

    public void createOrder(Orders order, HttpServletRequest request) {
        RestaurantDTO restaurantDTO = getRestaurant(order.getRestaurantId(), request);
        List<MenuItemDTO> menuItemsDTO = getMenuItems(order.getMenuItemIds(), request);
        orderRepository.save(order);
    }

    public OrderDTO getOrderById(Long id, HttpServletRequest request) {
        Optional<Orders> order = orderRepository.findById(id);

        if(order.isPresent()) {
            RestaurantDTO restaurantDTO = getRestaurant(order.get().getRestaurantId(), request);
            List<MenuItemDTO> menuItemsDTO = getMenuItems(order.get().getMenuItemIds(), request);
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setMenuItemDTO(menuItemsDTO);
            orderDTO.setRestaurantDTO(restaurantDTO);

            return orderDTO;
        }

        return new OrderDTO();
    }

    public List<MenuItemDTO> getMenuItems(List<Long> menuItemIds, HttpServletRequest request) {
        return menuItemIds.stream()
                .map(x -> this.getMenuItem(x, request))
                .collect(Collectors.toList());
    }

    private MenuItemDTO getMenuItem(Long menuItemId, HttpServletRequest request) {
        String url = restaurantServiceUrl + "/menuItem/" + menuItemId;

        HttpHeaders headers = new HttpHeaders();
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            return null;
        }

        headers.add("Cookie", "token=" + jwtToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<MenuItemDTO> response =  restTemplate.exchange(url, HttpMethod.GET, entity, MenuItemDTO.class);

        return response.getBody();
    }

    private RestaurantDTO getRestaurant(Long restaurantId, HttpServletRequest request) {


        String url = restaurantServiceUrl + "/restaurant/" + restaurantId;

        HttpHeaders headers = new HttpHeaders();
        String jwtToken = extractJwtFromCookies(request);
        if (jwtToken == null) {
            return null;
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);
        headers.add("Cookie", "token=" + jwtToken);

        ResponseEntity<RestaurantDTO> response = restTemplate.exchange(url, HttpMethod.GET, entity, RestaurantDTO.class);
        return response.getBody();
    }


    private String extractJwtFromCookies(HttpServletRequest request) {
        if (request.getCookies() == null) {
            return null;
        }
        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
