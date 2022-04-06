package ru.gb.springbootmarket.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Service;
import ru.gb.springbootmarket.converter.ProductMapper;
import ru.gb.springbootmarket.dto.ProductDto;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProductElasticSearchService {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final String INDEX_NAME = "products";

    private final RestHighLevelClient client;
    private final ProductService productService;
    private final ProductMapper productMapper;

    public ProductElasticSearchService(RestHighLevelClient client,
                                       ProductService productService,
                                       ProductMapper productMapper) {
        this.client = client;
        this.productService = productService;
        this.productMapper = productMapper;
    }

    @PostConstruct
    void init() {
        List<ProductDto> productDtos =  productService.getAll().stream()
                .map(productMapper::productToProductDto).collect(Collectors.toList());
        productDtos.forEach(productDto -> {
            try {
                indexProduct(productDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void indexProduct(ProductDto productDto) throws IOException {
        SearchRequest searchRequest = new SearchRequest();
        SearchResponse response = client.search(searchRequest, RequestOptions.DEFAULT);
        SearchHit[] searchHits = response.getHits().getHits();
        if (searchHits.length == 0) {
            IndexRequest request = new IndexRequest(INDEX_NAME);
            request.source(mapper.writeValueAsString(productDto), XContentType.JSON);
            client.index(request, RequestOptions.DEFAULT);
        }
    }

    public List<ProductDto> search(String searchString) throws IOException {
        SearchRequest request = new SearchRequest(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder()
                .query(QueryBuilders.queryStringQuery(searchString));
        request.source(searchSourceBuilder);
        SearchResponse searchResponse = client.search(request, RequestOptions.DEFAULT);
        return Arrays.stream(searchResponse.getHits().getHits())
                .map(hit -> {
                    Map<String, Object> map = hit.getSourceAsMap();
                    ProductDto productDto = new ProductDto();
                    productDto.setTitle((String) map.get("title"));
                    productDto.setPrice((String) map.get("price"));
                    productDto.setCategory((String) map.get("category"));
                    productDto.setImageUrl((String) map.get("imageUrl"));
                    productDto.setId(Objects.requireNonNull(productService.findByTitle(productDto.getTitle()).orElse(null)).getId());
                    return productDto;
                }).collect(Collectors.toList());
    }
}
