package com.pulse.service.pagination;

public record Page(int limit, int offset) {
    private static final int MAX_SIZE = 100;

    public static Page of(int page, int size) {
        int limit = Math.clamp(size, 1, MAX_SIZE);
        int offset = Math.max(page, 0) * limit;
        return new Page(limit, offset);
    }
}
