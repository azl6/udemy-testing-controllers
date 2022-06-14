package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerService;
import guru.springframework.brewery.web.model.BeerDto;
import guru.springframework.brewery.web.model.BeerPagedList;
import guru.springframework.brewery.web.model.BeerStyleEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerController.class)
class BeerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Mock
    BeerDto beerDto;

    @MockBean
    BeerService beerService;

    @Mock
    BeerPagedList beersPaged;


    @BeforeEach
    void setUp() {
        beerDto = BeerDto.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Brahma")
                .beerStyle(BeerStyleEnum.ALE)
                .price(new BigDecimal("10"))
                .build();

        List<BeerDto> beers = List.of(beerDto);
        beersPaged = new BeerPagedList(beers, PageRequest.of(1, 1), 2);
    }

    @Test
    void getBeerByIdTest() throws Exception {
        given(beerService.findBeerById(any())).willReturn(beerDto);

        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/beer/" + beerDto.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(jsonPath("$.id").value(beerDto.getId().toString()))
                .andExpect(jsonPath("$.beerName").value("Brahma"));


        then(beerService).should(times(1)).findBeerById(any());
    }

    @Test
    void listBeersTest() throws Exception {
        given(beerService.listBeers(any(), any(), any())).willReturn(beersPaged);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/beer"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].beerName", is("Brahma")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8));
    }
}