package org.example.expert.client;

import org.example.expert.client.dto.WeatherDto;
import org.example.expert.domain.common.exception.ServerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class WeatherClientTest {

    @Mock
    private RestTemplate restTemplate;

    private WeatherClient weatherClient;

    @BeforeEach
    void setUp() {
        weatherClient = new WeatherClient(new RestTemplateBuilder());
        ReflectionTestUtils.setField(weatherClient, "restTemplate", restTemplate);
    }

    private String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("MM-dd"));
    }

    @Test
    void 오늘_날씨_정상_조회() {
        // given
        String today = getToday();
        WeatherDto[] weatherArray = {new WeatherDto(today, "Sunny")};
        ResponseEntity<WeatherDto[]> response = ResponseEntity.ok(weatherArray);
        given(restTemplate.getForEntity(any(), eq(WeatherDto[].class))).willReturn(response);

        // when
        String weather = weatherClient.getTodayWeather();

        // then
        assertEquals("Sunny", weather);
    }

    @Test
    void 날씨_데이터가_없으면_예외가_발생한다() {
        // given
        ResponseEntity<WeatherDto[]> response = ResponseEntity.ok(new WeatherDto[]{});
        given(restTemplate.getForEntity(any(), eq(WeatherDto[].class))).willReturn(response);

        // when & then
        ServerException exception = assertThrows(ServerException.class,
                () -> weatherClient.getTodayWeather());
        assertEquals("날씨 데이터가 없습니다.", exception.getMessage());
    }

    @Test
    void 날씨_데이터가_null이면_예외가_발생한다() {
        // given
        ResponseEntity<WeatherDto[]> response = ResponseEntity.ok(null);
        given(restTemplate.getForEntity(any(), eq(WeatherDto[].class))).willReturn(response);

        // when & then
        ServerException exception = assertThrows(ServerException.class,
                () -> weatherClient.getTodayWeather());
        assertEquals("날씨 데이터가 없습니다.", exception.getMessage());
    }

    @Test
    void 상태코드가_200이_아니면_예외가_발생한다() {
        // given
        WeatherDto[] weatherArray = {new WeatherDto(getToday(), "Sunny")};
        ResponseEntity<WeatherDto[]> response = ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(weatherArray);
        given(restTemplate.getForEntity(any(), eq(WeatherDto[].class))).willReturn(response);

        // when & then
        ServerException exception = assertThrows(ServerException.class,
                () -> weatherClient.getTodayWeather());
        assertTrue(exception.getMessage().contains("날씨 데이터를 가져오는데 실패했습니다."));
    }

    @Test
    void 오늘_날씨가_없으면_예외가_발생한다() {
        // given
        WeatherDto[] weatherArray = {new WeatherDto("01-01", "Sunny")};
        ResponseEntity<WeatherDto[]> response = ResponseEntity.ok(weatherArray);
        given(restTemplate.getForEntity(any(), eq(WeatherDto[].class))).willReturn(response);

        // when & then
        String today = getToday();
        if (!today.equals("01-01")) {
            ServerException exception = assertThrows(ServerException.class,
                    () -> weatherClient.getTodayWeather());
            assertEquals("오늘에 해당하는 날씨 데이터를 찾을 수 없습니다.", exception.getMessage());
        }
    }
}