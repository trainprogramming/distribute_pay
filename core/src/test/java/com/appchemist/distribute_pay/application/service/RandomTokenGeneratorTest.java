package com.appchemist.distribute_pay.application.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RandomTokenGeneratorTest {
    private TokenGenerator tokenGenerator;

    @Test
    void 토큰생성_기본() {
        // given
        tokenGenerator = new RandomTokenGenerator();

        // when
        String token = tokenGenerator.generate();

        // then
        assertNotNull(token);
        assertEquals(3, token.length());
    }

    @Test
    void 토큰생성_길이_6() {
        // given
        tokenGenerator = new RandomTokenGenerator(6, "aaaaaaaaaaaaaaaaa");

        // when
        String token = tokenGenerator.generate();

        // then
        assertNotNull(token);
        assertEquals("aaaaaa", token);
    }

    @Test
    void 토큰생성_길이_0() {
        // given
        tokenGenerator = new RandomTokenGenerator(0, "aaaaaaaaaaaaaaaaa");

        // when
        String token = tokenGenerator.generate();

        // then
        assertNotNull(token);
        assertEquals("a", token);
    }
}