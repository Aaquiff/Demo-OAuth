/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.servlets;

import com.aaralk.demo.oauth.MyCredentialsStore;
import com.aaralk.demo.oauth.core.Utility;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aaralk
 */
@WebServlet(name = "OAuthServlet", urlPatterns = {"/oauth2"})
public class OAuth extends HttpServlet {


    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cookie sessionCookie = Utility.getSessionToken(request.getCookies());
        if(sessionCookie == null || MyCredentialsStore.TokenStore.get(sessionCookie.getValue()) == null) {
            response.sendRedirect(getOAuthURL());
        }
        else {
            response.sendRedirect("http://localhost:8080/Demo-OAuth");
        }
    }
    
    private String getOAuthURL() throws UnsupportedEncodingException {
        String scope = URLEncoder.encode("https://www.googleapis.com/auth/drive profile", "UTF-8");
        String redirectUrl = URLEncoder.encode("http://localhost:8080/Demo-OAuth/oauth2callback", "UTF-8");
        String state = "12345";
        String authorizationUrl = 
                String.format("https://accounts.google.com/o/oauth2/v2/auth?"
                                + "scope=%s&"
                                + "access_type=offline&"
                                + "include_granted_scopes=true&"
                                + "state=%s&"
                                + "redirect_uri=%s&"
                                + "response_type=code&"
                                + "client_id=%s", 
                                scope, state, redirectUrl, MyCredentialsStore.CLIENT_ID);
        return authorizationUrl;
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
