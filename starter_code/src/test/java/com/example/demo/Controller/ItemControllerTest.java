package com.example.demo.Controller;

import com.example.demo.TestUtils;
import com.example.demo.controller.ItemController;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ItemControllerTest {

    @Autowired
    private ItemController itemController;

    private ItemRepository itemRepository = mock(ItemRepository.class);

    Item item1 = createNewItem(1L, "First Item");
    Item item7 = createNewItem(7L, "Seventh Item");
    Item item3 = createNewItem(3L, "Third Item");
    Item item2 = createNewItem(2L, "Second Item");

    @Before
    public void setup() {
        itemController = new ItemController();
        TestUtils.injectObjects(itemController, "itemRepository", itemRepository);

        }


    @Test
    public void testItemShowAll(){
        when(itemRepository.findAll()).thenReturn(Arrays.asList(item1, item7, item3, item2));
        List<Item> foundAll = itemController.getItems().getBody();

        assertEquals(200, itemController.getItems().getStatusCodeValue());
        assertNotNull(foundAll);
        assertEquals(foundAll.size(), 4);
    }

    @Test
    public void testItemFindById(){
        when(itemRepository.findById(7L)).thenReturn(Optional.of(item7));
        Item item = itemController.getItemById(7L).getBody();

        assertEquals(200, itemController.getItemById(7L).getStatusCodeValue());
        assertNotNull(item);
        assertEquals((item.getId()).longValue(), 7L);
    }

    @Test
    public void testItemFindByName(){
        when(itemRepository.findByName(any())).thenReturn(Arrays.asList(item1, item2, item3, item7));
        List<Item> item = itemController.getItemsByName("Seventh Item").getBody();

        assertEquals(200, itemController.getItemsByName("Seventh Item").getStatusCodeValue());
        assertNotNull(item);
        assertEquals(item.size(), 4);
    }


    private Item createNewItem(Long id, String name){
        Item item = new Item();
        item.setId(id);
        item.setName(name);
        return item;
    }
}
