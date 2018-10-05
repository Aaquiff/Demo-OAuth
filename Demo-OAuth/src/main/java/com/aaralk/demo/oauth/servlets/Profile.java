/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.servlets;

import com.aaralk.demo.oauth.MyCredentialsStore;
import com.aaralk.demo.oauth.core.Utility;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
@WebServlet(name = "Profile", urlPatterns = {"/Profile"})
public class Profile extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet Profile</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet Profile at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

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
        System.out.println("Got session ID: " + sessionCookie.getValue());
        if(sessionCookie != null) {
            String token = MyCredentialsStore.TokenStore.get(sessionCookie.getValue());
            if(token != null) {
                String res = RequestUserInformation(token);
                response.getWriter().write(res);
                response.setStatus(200);
            }
            
//response.sendRedirect("/");
        }
        else {
            
        }
        
    }

    private String RequestUserInformation(String token) {
        String profileUrl = "https://www.googleapis.com/plus/v1/people/me";
        
        StringBuilder strBuf = new StringBuilder();  
        HttpURLConnection conn=null;
        BufferedReader reader=null;
        try {  
            URL url = new URL(profileUrl);  
            conn = (HttpURLConnection)url.openConnection();  
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "GET" );
            conn.setRequestProperty("Host", "www.googleapis.com");
            conn.setRequestProperty("Authorization", String.format("Bearer %s", token));
            
            conn.connect();
            
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("HTTP GET Request Failed with Error code : "
                              + conn.getResponseCode());
            }
            
            //Read the content from the defined connection
            //Using IO Stream with Buffer raise highly the efficiency of IO
	        reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
            String output = null;  
            while ((output = reader.readLine()) != null)  
                strBuf.append(output);  
        }catch(MalformedURLException e) {  
            e.printStackTrace();   
        }catch(IOException e){  
            e.printStackTrace();   
        }
        finally
        {
            if(reader!=null)
            {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null)
            {
                conn.disconnect();
            }
            return strBuf.toString();
        }
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
