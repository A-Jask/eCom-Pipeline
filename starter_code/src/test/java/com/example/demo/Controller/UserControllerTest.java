package com.example.demo.Controller;

import com.example.demo.TestUtils;
import com.example.demo.controller.UserController;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserControllerTest {

    @Autowired
    private UserController userController;

    private UserRepository userRepository = mock(UserRepository.class);

    private CartRepository cartRepository = mock(CartRepository.class);

    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);


    @Before
    public void setUp(){
        userController = new UserController();
        TestUtils.injectObjects(userController, "userRepository", userRepository);
        TestUtils.injectObjects(userController, "cartRepository", cartRepository);
        TestUtils.injectObjects(userController, "bCryptPasswordEncoder", encoder);
    }

    @Test
    public void createUser() throws Exception{
        when(encoder.encode("admin")).thenReturn("thisIsHashed");
        CreateUserRequest user= new CreateUserRequest();
        user.setUsername("Grasshopper");
        user.setPassword("admin");
        user.setConfirmPassword("admin");

        final ResponseEntity<User> userResponseEntity = userController.createUser(user);
        assertNotNull(userResponseEntity);
        assertEquals(200, userResponseEntity.getStatusCodeValue());

        User tempUser = userResponseEntity.getBody();
        assertNotNull(tempUser);
        assertEquals(0, tempUser.getId());
        assertEquals("Grasshopper", tempUser.getUsername());
        assertEquals("thisIsHashed", tempUser.getPassword());
    }

    @Test
    public void createUserFail() throws Exception{
        when(encoder.encode("ad")).thenReturn("thisIsHashed");
        CreateUserRequest user= new CreateUserRequest();
        user.setUsername("Grasshopper");
        user.setPassword("ad");
        user.setConfirmPassword("ad");

        final ResponseEntity<User> userResponseEntity = userController.createUser(user);
        assertNotNull(userResponseEntity);
        assertEquals(400, userResponseEntity.getStatusCodeValue());
    }

    @Test
    public void findUserByName() throws Exception{
        User user = new User();
        user.setUsername("Grasshopper");
        when(userRepository.findByUsername("Grasshopper")).thenReturn(user);
        ResponseEntity<User> userResponseEntity = userController.findByUserName("Grasshopper");

        assertNotNull(userResponseEntity.getBody());
        assertEquals(userResponseEntity.getStatusCodeValue(), 200);
        assertEquals(userResponseEntity.getBody().getUsername(), "Grasshopper");
    }

    @Test
    public void findUserById() throws Exception{
        User user = new User();
        user.setUsername("Grasshopper");
        when(userRepository.findById(0L)).thenReturn(java.util.Optional.of(user));
        ResponseEntity<User> userResponseEntity = userController.findById(0L);

        assertNotNull(userResponseEntity.getBody());
        assertEquals(userResponseEntity.getStatusCodeValue(), 200);
        assertEquals(userResponseEntity.getBody().getId(), 0L);

    }
}
