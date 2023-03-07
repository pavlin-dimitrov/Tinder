package com.volasoftware.tinder.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("api/v1/demo-controller")
@Api(value = "Demo controller")
public class DemoController {

    @ApiOperation(value = "Demo", response = String.class)
    @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Success"),
                    @ApiResponse(code = 401, message = "You are not authorized"),
                    @ApiResponse(code = 403, message = "Accessing is forbidden"),
                    @ApiResponse(code = 404, message = "The resource is not found")})
    @GetMapping
    public ResponseEntity<String> demoMessage() {
        log.info("Received request to get message after the LOGIN");
        return new ResponseEntity<>("Hello after the login!", HttpStatus.OK);
    }
}
