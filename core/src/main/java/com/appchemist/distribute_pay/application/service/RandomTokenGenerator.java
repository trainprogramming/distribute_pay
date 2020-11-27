package com.appchemist.distribute_pay.application.service;

import java.util.Random;

class RandomTokenGenerator implements TokenGenerator {
    private static final String DEFAULT_TOKEN_POOL = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int DEFAULT_LEN = 3;

    private String tokenPool;
    private int len;
    private Random rand = new Random();

    public RandomTokenGenerator() {
        this(DEFAULT_LEN, DEFAULT_TOKEN_POOL);
    }

    public RandomTokenGenerator(int len, String tokenPool) {
        if (len < 1) {
            len = 1;
        }
        if (tokenPool == null || tokenPool.isEmpty()) {
            tokenPool = DEFAULT_TOKEN_POOL;
        }

        this.len = len;
        this.tokenPool = tokenPool;
    }

    @Override
    public String generate() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i ++) {
            sb.append(tokenPool.charAt(rand.nextInt(tokenPool.length())));
        }

        return sb.toString();
    }
}
