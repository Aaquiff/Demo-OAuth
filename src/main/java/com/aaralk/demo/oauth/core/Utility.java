/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.core;

import javax.servlet.http.Cookie;

/**
 *
 * @author aaralk
 */
public class Utility {
    public static Cookie getSessionToken(Cookie[] cookies) {
        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals("SESSIONID")) {
                    return cookies[i];
                }
            }
        }
        return null;
    }
}
