package ru.gb.springbootmarket.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@JsonTest
class ProductDtoTest {

    @Autowired
    private JacksonTester<ProductDto> tester;

    @Test
    void toJsonTest() throws IOException {
        ProductDto productDto = new ProductDto();
        productDto.setId(3L);
        productDto.setTitle("test title");
        productDto.setPrice("123");
        productDto.setCategory("test category");

        JsonContent<ProductDto> jsonContent = tester.write(productDto);

        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.title")
                .isEqualTo("test title");

        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.category")
                .isEqualTo("test category");

        assertThat(jsonContent)
                .extractingJsonPathStringValue("@.price")
                .isEqualTo("123");
    }

}