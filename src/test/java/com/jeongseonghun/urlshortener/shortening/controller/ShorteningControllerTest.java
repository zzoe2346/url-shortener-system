package com.jeongseonghun.urlshortener.shortening.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongseonghun.urlshortener.common.config.AppProperties;
import com.jeongseonghun.urlshortener.shortening.model.dto.ShortenRequest;
import com.jeongseonghun.urlshortener.shortening.model.entity.UrlMapping;
import com.jeongseonghun.urlshortener.shortening.repository.UrlMappingRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ShorteningControllerTest {
    @Autowired
    private UrlMappingRepository urlMappingRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AppProperties appProperties;

    @Nested
    @DisplayName("URL 단축 API (POST: /urls)")
    class ShortenUrlApi {
        @ParameterizedTest
        @CsvSource({
                "https://originalURL, 1",
                "http://originalURL, 2",
                "https://www.jeongseonghun.com, 3"
        })
        void SHORTEN_SUCCESS(String originalUrl, String shortCode) throws Exception {
            //given
            ShortenRequest request = new ShortenRequest(originalUrl);

            //when
            ResultActions resultActions = mockMvc.perform(post("/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("shortUrl").value(appProperties.getDomain() + "/" + shortCode)
            );
        }

        @ParameterizedTest
        @ValueSource(strings = {"originalURL", "", "www.jeongseonghun.com"})
        void SHORTEN_FAIL_WHEN_WRONG_ORIGINAL_URL(String wrongOriginalUrl) throws Exception {
            //given
            ShortenRequest request = new ShortenRequest(wrongOriginalUrl);

            //when
            ResultActions resultActions = mockMvc.perform(post("/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //then
            resultActions.andExpectAll(
                    status().isBadRequest()
            );
        }
    }


    @Nested
    @DisplayName("URL 리다이렉트 API (GET: /{shortUrl})")
    class RedirectApi {
        @Test
        void REDIRECT_SUCCESS() throws Exception {
            //given
            String originalUrl = "https://jeongseonghun.com";
            String shortUrl = "aaaaaab"; // 테스트용으로 사용할 고유한 값

            urlMappingRepository.save(new UrlMapping(originalUrl, shortUrl));

            // when
            ResultActions resultActions = mockMvc.perform(get("/{shortUrl}", shortUrl));

            // then
            resultActions.andExpectAll(
                    status().isFound(),
                    header().string(HttpHeaders.LOCATION, originalUrl)
            );
        }

        @Test
        void RETURN_404_NOT_FOUND_WHEN_URL_DOES_NOT_EXIST() throws Exception {
            // given
            String nonExistentShortUrl = "zzzzzzz";

            // when
            ResultActions resultActions = mockMvc.perform(get("/{shortUrl}", nonExistentShortUrl));

            // then
            resultActions.andExpectAll(
                    status().isNotFound()
            );
        }
    }
}