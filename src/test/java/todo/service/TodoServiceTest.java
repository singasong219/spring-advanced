package org.example.expert.domain.todo.service;

import org.example.expert.client.WeatherClient;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;
    @Mock
    private WeatherClient weatherClient;
    @InjectMocks
    private TodoService todoService;

    @Test
    void todo_저장_성공() {
        // given
        AuthUser authUser = new AuthUser(1L, "test@test.com", UserRole.USER);
        TodoSaveRequest request = new TodoSaveRequest("title", "contents");

        given(weatherClient.getTodayWeather()).willReturn("Sunny");

        User user = User.fromAuthUser(authUser);
        Todo savedTodo = new Todo("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(savedTodo, "id", 1L);

        given(todoRepository.save(any(Todo.class))).willReturn(savedTodo);

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, request);

        // then
        assertNotNull(response);
        assertEquals("title", response.getTitle());
        assertEquals("contents", response.getContents());
        assertEquals("Sunny", response.getWeather());
    }

    @Test
    void todo_목록_조회_성공() {
        // given
        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        Page<Todo> todoPage = new PageImpl<>(List.of(todo), PageRequest.of(0, 10), 1);
        given(todoRepository.findAllByOrderByModifiedAtDesc(any())).willReturn(todoPage);

        // when
        Page<TodoResponse> responses = todoService.getTodos(1, 10);

        // then
        assertNotNull(responses);
        assertEquals(1, responses.getTotalElements());
        assertEquals("title", responses.getContent().get(0).getTitle());
    }

    @Test
    void todo_단건_조회_성공() {
        // given
        User user = new User("test@test.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L);
        Todo todo = new Todo("title", "contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L);

        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(1L);

        // then
        assertNotNull(response);
        assertEquals("title", response.getTitle());
        assertEquals("contents", response.getContents());
    }

    @Test
    void todo_단건_조회_시_존재하지_않으면_예외가_발생한다() {
        // given
        given(todoRepository.findByIdWithUser(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class,
                () -> todoService.getTodo(1L));
        assertEquals("Todo not found", exception.getMessage());
    }
}