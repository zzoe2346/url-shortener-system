package com.jeongseonghun.urlshortener.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jeongseonghun.urlshortener.domain.entity.URL;
import com.jeongseonghun.urlshortener.domain.repository.UrlRepository;
import com.jeongseonghun.urlshortener.dto.ShortenRequest;
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
class ShortenControllerTest {

    @Autowired
    private UrlRepository urlRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("URL 단축 API (POST: /urls)")
    class ShortenUrlApi {
        @ParameterizedTest
        @CsvSource({
                "https://originalURL, 1",
                "http://originalURL, 2",
                "https://www.jeongseonghun.com, 3"
        })
        void SHORTEN_SUCCESS(String originalUrl, String expectedShortUrl) throws Exception {
            //given
            ShortenRequest request = new ShortenRequest(originalUrl);

            //when
            ResultActions resultActions = mockMvc.perform(post("/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)));

            //then
            resultActions.andExpectAll(
                    status().isCreated(),
                    jsonPath("shortUrl").value(expectedShortUrl)
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

            urlRepository.save(new URL(originalUrl, shortUrl));

            // when
            ResultActions resultActions = mockMvc.perform(get("/{shortUrl}", shortUrl));

            // then
            resultActions.andExpectAll(
                    status().isFound(),
                    header().string("Location", originalUrl)
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