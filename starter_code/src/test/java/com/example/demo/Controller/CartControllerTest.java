package com.example.demo.Controller;

import com.example.demo.TestUtils;
import com.example.demo.controller.CartController;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class CartControllerTest {

    @Autowired
    private CartController cartController;

    private ItemRepository itemRepository = mock(ItemRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);

    private Long itemId = 77L;
    private String username = "Grasshopper";


    @Before
    public void setUp(){
        cartController = new CartController();
        TestUtils.injectObjects(cartController, "userRepository", userRepository);
        TestUtils.injectObjects(cartController, "cartRepository", cartRepository);
        TestUtils.injectObjects(cartController, "itemRepository", itemRepository);
        when(userRepository.findByUsername(username)).thenReturn(userSetup());
        when(itemRepository.findById(itemId)).thenReturn(cartItems());
    }

    @Test
    public void testAddToCart(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);
        Cart cart = response.getBody();

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(cart);
        assertEquals(cart.getUser().getUsername(), username);
        assertEquals(2, cart.getItems().size()-1);
        assertEquals(BigDecimal.valueOf(60), cart.getTotal());
    }

    @Test
    public void testAddToCartNoUser(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setItemId(itemId);
        modifyCartRequest.setUsername(null);
        modifyCartRequest.setQuantity(3);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartNoItem() {
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);

        ResponseEntity<Cart> response = cartController.addTocart(modifyCartRequest);

        assertEquals(404, response.getStatusCodeValue());
    }


    @Test
    public void testRemoveFromCartInvalidItemCount(){
        ModifyCartRequest remove = new ModifyCartRequest();
        remove.setUsername(username);
        remove.setItemId(5L);
        remove.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(remove);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCart(){
        ModifyCartRequest remove = new ModifyCartRequest();
        remove.setUsername(username);
        remove.setItemId(itemId);
        remove.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(remove);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartInvalidItem(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(username);
        modifyCartRequest.setItemId(5L);
        modifyCartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartInvalidUsername(){
        ModifyCartRequest modifyCartRequest = new ModifyCartRequest();
        modifyCartRequest.setUsername(null);
        modifyCartRequest.setItemId(5L);
        modifyCartRequest.setQuantity(2);

        ResponseEntity<Cart> response = cartController.removeFromcart(modifyCartRequest);

        assertEquals(404, response.getStatusCodeValue());
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
        item.setId(itemId);
        item.setPrice(new BigDecimal(20));
        return Optional.of(item);
    }

}
