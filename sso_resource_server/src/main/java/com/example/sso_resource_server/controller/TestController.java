package com.example.sso_resource_server.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.sso_resource_server.dto.*;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@SessionAttributes("authorizationRequest")
public class TestController {

    @PreAuthorize("hasAnyAuthority('api:hello')")
    @RequestMapping(value = "/hello")
    @ResponseBody
    public Message hello() {
        return new Message("hello world!");
    }




}
