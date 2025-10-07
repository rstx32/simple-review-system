package com.review_system.backend.service;

import com.review_system.backend.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Product> getAllProducts() {
        String sql = "SELECT * FROM products";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class));
    }

    public Product getProductById(UUID id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new BeanPropertyRowMapper<>(Product.class), id);
    }

    public Product createProduct(Product product, UUID ownerId) {
        String sql = "INSERT INTO products (id, owner_id, name, description, created_at) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING *";
        UUID productId = UUID.randomUUID();
        OffsetDateTime now = OffsetDateTime.now();

        return jdbcTemplate.queryForObject(
                sql,
                new BeanPropertyRowMapper<>(Product.class),
                productId,
                ownerId,
                product.getName(),
                product.getDescription(),
                Timestamp.from(now.toInstant()));
    }

    public void deleteProduct(UUID id) {
        String sql = "DELETE FROM products WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }

    public List<Product> getProductsByOwner(UUID ownerId) {
        String sql = "SELECT * FROM products WHERE owner_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(Product.class), ownerId);
    }
}
