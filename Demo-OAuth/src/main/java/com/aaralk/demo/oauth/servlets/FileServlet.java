/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.servlets;

import com.aaralk.demo.oauth.MyCredentialsStore;
import com.aaralk.demo.oauth.core.Utility;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author aaralk
 */
@WebServlet(name = "File", urlPatterns = {"/File"})
@MultipartConfig(fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 5,
        maxRequestSize = 1024 * 1024 * 5 * 5)
public class FileServlet extends HttpServlet {

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Cookie sessionCookie = Utility.getSessionToken(request.getCookies());
        System.out.println("Got session ID: " + sessionCookie.getValue());
        if(sessionCookie != null) {
            String token = MyCredentialsStore.TokenStore.get(sessionCookie.getValue());
            if(token != null) {
                uploadFileToDrive(token);
                response.setStatus(200);
            }
        
            //response.sendRedirect("/");
        }
        else {
            
        }

        response.sendRedirect("/Demo-OAuth");

    }
    
    private void uploadFileToDrive(String token) throws IOException {
        String fileName = "C:\\Users\\aaralk\\OneDrive - IFS\\Documents\\SLIIT\\Secure Software Development\\Assignment 2\\IT15048738.xml";
        String message = "This is a multipart post";
        File file = new File(fileName);
        
        HttpClient client = new DefaultHttpClient();
        HttpPost post = new HttpPost("https://www.googleapis.com/upload/drive/v3/files?uploadType=media");
        
        post.setHeader("Host", "www.googleapis.com");
        post.setHeader("Authorization", String.format("Bearer %s", token));
//        post.setHeader("Content-Type", "multipart/related");
        post.setHeader("Content-Type", "text/plain");
//        post.setHeader("Content-Length", Integer.toString(message.length()));
        
//        InputStream inputStream = new FileInputStream(zipFileName);
//        FileServlet file = new FileServlet(imageFileName);
        
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();         
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addBinaryBody("upfile", file, ContentType.DEFAULT_BINARY, "IT15048738.xml");
        builder.addTextBody("text", message, ContentType.DEFAULT_BINARY);
        
        HttpEntity entity = builder.build();
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        System.out.println(response);
    }
    
}
