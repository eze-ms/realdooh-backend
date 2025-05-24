package com.econocom.auth_backend.security;

import com.econocom.auth_backend.util.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    @Test
    void doFilter_sinCabeceraAuthorization_continuaSinAutenticar() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        HttpServletRequest request = new MockHttpServletRequest();
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getClaims(anyString());
    }

    @Test
    void doFilter_conCabeceraSinPrefijoBearer_continuaSinAutenticar() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Token faketoken123"); // sin "Bearer "
        HttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil, never()).validateToken(anyString());
        verify(jwtUtil, never()).getClaims(anyString());
    }

    @Test
    void doFilter_conTokenInvalido_continuaSinAutenticar() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtUtil.validateToken("faketoken")).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(jwtUtil).validateToken("faketoken");
        verify(jwtUtil, never()).getClaims(anyString());
    }

    @Test
    void doFilter_conTokenValido_autenticaYContinuaFiltro() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken("faketoken")).thenReturn(true);
        when(jwtUtil.getClaims("faketoken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("usuario1");

        filter.doFilterInternal(request, response, filterChain);

        assertEquals("usuario1", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_tokenValido_creaAutenticacionConSubject() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer faketoken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken("faketoken")).thenReturn(true);
        when(jwtUtil.getClaims("faketoken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("usuario123");

        filter.doFilterInternal(request, response, filterChain);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertInstanceOf(UsernamePasswordAuthenticationToken.class, authentication);
        assertEquals("usuario123", authentication.getPrincipal());
        assertTrue(authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_USER")));

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void doFilter_siempresEjecutaFilterChainDoFilter() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        // Ejecutar sin Authorization header
        filter.doFilterInternal(request, response, filterChain);

        request.addHeader("Authorization", "Bearer invalido");
        when(jwtUtil.validateToken("invalido")).thenReturn(false);
        filter.doFilterInternal(request, response, filterChain);

        request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer valido");
        Claims claims = mock(Claims.class);
        when(jwtUtil.validateToken("valido")).thenReturn(true);
        when(jwtUtil.getClaims("valido")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("usuario");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(3)).doFilter(any(), any());
    }

    @Test
    void doFilter_errorAlObtenerClaims_lanzaExcepcion() throws ServletException, IOException {
        JwtUtil jwtUtil = mock(JwtUtil.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtUtil);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer tokenConError");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain filterChain = mock(FilterChain.class);

        when(jwtUtil.validateToken("tokenConError")).thenReturn(true);
        when(jwtUtil.getClaims("tokenConError")).thenThrow(new RuntimeException("Error inesperado"));

        assertThrows(RuntimeException.class, () ->
                filter.doFilterInternal(request, response, filterChain));
    }


}
