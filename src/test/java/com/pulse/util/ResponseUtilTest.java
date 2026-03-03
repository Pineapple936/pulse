package com.pulse.util;

import com.pulse.entity.dto.response.ResponseMessageDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ResponseUtilTest {

    @Test
    void createdMessage_shouldReturnExpectedText() {
        ResponseMessageDto response = ResponseUtil.createdMessage();
        assertNotNull(response);
        assertEquals("Created successfully", response.message());
    }

    @Test
    void updatedMessage_shouldReturnExpectedText() {
        ResponseMessageDto response = ResponseUtil.updatedMessage();
        assertNotNull(response);
        assertEquals("Updated successfully", response.message());
    }

    @Test
    void deletedMessage_shouldReturnExpectedText() {
        ResponseMessageDto response = ResponseUtil.deletedMessage();
        assertNotNull(response);
        assertEquals("Deleted successfully", response.message());
    }

    @Test
    void registeredMessage_shouldReturnExpectedText() {
        ResponseMessageDto response = ResponseUtil.registeredMessage();
        assertNotNull(response);
        assertEquals("User registered successfully", response.message());
    }

    @Test
    void unauthorizedUser_shouldReturnExpectedText() {
        ResponseMessageDto response = ResponseUtil.unauthorizedUser();
        assertNotNull(response);
        assertEquals("You are not authorized", response.message());
    }
}
