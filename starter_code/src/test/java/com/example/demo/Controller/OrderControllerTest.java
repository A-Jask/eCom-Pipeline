package com.example.demo.Controller;

import com.example.demo.TestUtils;
import com.example.demo.controller.OrderController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.assertj.core.util.Lists;
import org.hibernate.criterion.Order;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class OrderControllerTest {

    @Autowired
    private OrderController orderController;

    private OrderRepository orderRepository = mock(OrderRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);

    private String username = "Grasshopper";

    @Before
    public void setup(){
        orderController = new OrderController();
        TestUtils.injectObjects(orderController, "orderRepository", orderRepository);
        TestUtils.injectObjects(orderController, "userRepository", userRepository);
    }

    @Test
    public void testSubmitNoUser(){
        when(userRepository.findByUsername(username)).thenReturn(null);

        assertNotNull(orderController.submit(username));
        assertEquals(404, orderController.getOrdersForUser(username).getStatusCodeValue());
    }

    @Test
    public void testSubmitOrder(){
        when(userRepository.findByUsername(username)).thenReturn(userSetup());
        when(orderRepository.findByUser(any())).thenReturn(userOrderList());

        List<UserOrder> userOrders = orderController.getOrdersForUser(username).getBody();

        assertEquals(200, orderController.getOrdersForUser(username).getStatusCodeValue());
        assertNotNull(userOrders);

        UserOrder userOrder = userOrders.get(0);
        assertEquals(username, userOrder.getUser().getUsername());
        assertEquals(userOrder.getItems().size(), 1);
    }


    private List<UserOrder> userOrderList() {
        UserOrder userOrder = UserOrder.createFromCart(userSetup().getCart());
        return Lists.list(userOrder);
    }

    private User userSetup(){
        User user = new User();
        user.setUsername(username);
        user.setCart(userSetupCart(user));
        return user;
    }

    private Cart userSetupCart(User user){
        Cart cart = new Cart();
        cart.setUser(user);
        cart.addItem(cartItems().orElse(null));
        cart.setTotal(cartItems().stream().map(temp -> temp.getPrice()).reduce(BigDecimal::add).get());
        return cart;
    }

    private Optional<Item> cartItems(){
        Item item = new Item();
        item.setId(1L);
        item.setPrice(new BigDecimal(20));
        return Optional.of(item);
    }
}
