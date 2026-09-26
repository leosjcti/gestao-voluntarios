package br.com.ibaji.voluntarios.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class VoluntarioContextFilterTest {

    @Test
    void deveLimparContextoAoFinalDaRequisicao() throws ServletException, IOException {
        VoluntarioContextFilter filter = new VoluntarioContextFilter();
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        // Simulando que dentro da chain o ID é setado
        doAnswer(invocation -> {
            VoluntarioContextHolder.setId(123L);
            return null;
        }).when(chain).doFilter(request, response);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNull(VoluntarioContextHolder.getId(), "O ThreadLocal deve estar limpo após a execução do filtro");
    }
}
