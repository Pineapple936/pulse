package com.example.demo.util;

import com.example.demo.entity.dto.ResponseMessageDto;

public class ResponseUtil {
    public static ResponseMessageDto createdMessage() {
        return new ResponseMessageDto("Created successfully");
    }

    public static ResponseMessageDto updatedMessage() {
        return new ResponseMessageDto("Updated successfully");
    }

    public static ResponseMessageDto deletedMessage() {
        return new ResponseMessageDto("Deleted successfully");
    }

    public static ResponseMessageDto registeredMessage() {
        return new ResponseMessageDto("User registered successfully");
    }

    public static  ResponseMessageDto unauthorizedUser() {
        return new ResponseMessageDto("You are not authorized");
    }
}
