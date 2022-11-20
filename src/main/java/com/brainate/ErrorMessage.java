package com.brainate;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

public abstract class ErrorMessage {

    public static ResponseEntity<?> send(String error, HttpStatus httpStatus) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("error", error);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(obj.toString(), httpStatus);
    }

    public static ResponseEntity<?> send(BindingResult result, HttpStatus httpStatus) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("error", result.getFieldError().getField() + " " + result.getFieldError().getDefaultMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(obj.toString(), httpStatus);
    }
}
