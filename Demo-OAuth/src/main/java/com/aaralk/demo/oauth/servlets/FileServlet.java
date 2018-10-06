/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.servlets;

import com.aaralk.demo.oauth.MyCredentialsStore;
import com.aaralk.demo.oauth.core.Utility;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import org.apache.commons.io.IOUtils;
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
                //uploadFileToDrive(token);
                getFile(request, response, token);
                response.setStatus(200);
            }
        
            //response.sendRedirect("/");
        }
        else {
            
        }

        response.sendRedirect("/Demo-OAuth");

    }
    
    private void getFile(HttpServletRequest request, HttpServletResponse response, String token) throws IOException, ServletException {
        response.setContentType("text/html;charset=UTF-8");

        final Part filePart = request.getPart("multiPartServlet");
        final String fileName = getFileName(filePart);

        OutputStream out = null;
        InputStream filecontent = null;
        final PrintWriter writer = response.getWriter();

        try {
            filecontent = filePart.getInputStream();

            String result = IOUtils.toString(filecontent, StandardCharsets.UTF_8);

            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("https://www.googleapis.com/upload/drive/v3/files?uploadType=media");

            post.setHeader("Host", "www.googleapis.com");
            post.setHeader("Authorization", String.format("Bearer %s", token));
            post.setHeader("Content-Type", "application/xml");

            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.RFC6532)
                    .setContentType(ContentType.MULTIPART_FORM_DATA)
                    .addBinaryBody("upfile", filecontent, ContentType.DEFAULT_BINARY, fileName);

            HttpEntity entity = builder.build();
            post.setEntity(entity);
            HttpResponse res = client.execute(post);
            System.out.println(res);

            /*int read = 0;
            final byte[] bytes = new byte[1024];

            while ((read = filecontent.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }*/
            writer.println("New file " + fileName + " created at ");
        } catch (FileNotFoundException fne) {
            writer.println("You either did not specify a file to upload or are "
                    + "trying to upload a file to a protected or nonexistent "
                    + "location.");
            writer.println("<br/> ERROR: " + fne.getMessage());

        } finally {
            if (out != null) {
                out.close();
            }
            if (filecontent != null) {
                filecontent.close();
            }
            if (writer != null) {
                writer.close();
            }
        }
    }

    private String getFileName(final Part part) {
        final String partHeader = part.getHeader("content-disposition");
        for (String content : part.getHeader("content-disposition").split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(
                        content.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
}
