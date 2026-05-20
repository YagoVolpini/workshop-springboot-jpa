package com.example.demo.config;

import com.example.demo.entities.*;
import com.example.demo.enums.OrderStatus;
import com.example.demo.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Instant;
import java.util.Arrays;

@Configuration
@Profile("test")
public class TestConfig implements CommandLineRunner {


    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private PaymentRepository paymentRepository;


    @Override
    public void run(String... args) throws Exception {

        Category cat1 = new Category(null, "Electronics");
        Category cat2 = new Category(null, "Computers");
        Category cat3 = new Category(null, "Books");

        categoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3));

        Product p1 = new Product(null, "Smart TV 50\"", "Smart TV 50 Polegadas Full HD", 2190.0, "");
        Product p2 = new Product(null, "Macbook Pro", "Macbook Pro M2 13 Polegadas", 6500.0, "");
        Product p3 = new Product(null, "PC Gamer", "PC Gamer RTX 4060 Ryzen 7 32GB RAM", 8900.0, "");
        Product p4 = new Product(null, "Harry Potter and the Philosopher's Stone", "Fantasy novel by J.K. Rowling. ", 79.90, "");
        Product p5 = new Product(null, "The Lord of the Rings", "Epic fantasy adventure by J.R.R. Tolkien. ", 90.5, "");

        p1.getCategories().add(cat1);
        p1.getCategories().add(cat2);
        p2.getCategories().add(cat2);
        p3.getCategories().add(cat2);
        p4.getCategories().add(cat3);
        p5.getCategories().add(cat3);

        productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5));

        User user1 = new User(null, "John Smith", "john@gmail.com", "11999990001", "123456");
        User user2 = new User(null, "Jane Doe", "jane@gmail.com", "11999990002", "123456");
        User user3 = new User(null, "Bob Johnson", "bob@gmail.com", "11999990003", "123456");
        User user4 = new User(null, "Alice Brown", "alice@gmail.com", "11999990004", "123456");

        userRepository.saveAll(Arrays.asList(user1, user2, user3, user4));

        Order o1 = new Order(null, Instant.now(), OrderStatus.PAID, user1);
        Order o2 = new Order(null, Instant.now(), OrderStatus.CANCELED, user2);
        Order o3 = new Order(null, Instant.now(), OrderStatus.WAITING_PAYMENT, user3);
        Order o4 = new Order(null, Instant.now(), OrderStatus.SHIPPED, user3);
        Order o5 = new Order(null, Instant.now(), OrderStatus.DELIVERED, user4);

        o1.addItem(p3, 1);
        o2.addItem(p1, 2);
        o3.addItem(p2, 1);
        o4.addItem(p5, 2);
        o5.addItem(p4, 1);

        orderRepository.saveAll(Arrays.asList(o1, o2, o3, o4, o5));

        Payment pay1 = new Payment(Instant.now(), o1);
        o1.setPayment(pay1);

        orderRepository.save(o1);


    }
}
