/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.aaralk.demo.oauth.servlets;

import com.aaralk.demo.oauth.MyCredentialsStore;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author aaralk
 */
@WebServlet(name = "OAuth2Callback", urlPatterns = {"/oauth2callback"})
public class OAuth2Callback extends HttpServlet {

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
                
        String code = request.getParameter("code");
        String token = exchangeCodeForToken(code);
        if(token != null)
        {
            String sessionId = UUID.randomUUID().toString();
            MyCredentialsStore.TokenStore.put(sessionId, token);
            
            response.addHeader("Set-Cookie", "SESSIONID="+ sessionId + ";");
        }
        String res = SampleRequest(token);
        response.sendRedirect("http://localhost:8080/Demo-OAuth");
        
//        String x = String.format("https://www.googleapis.com/plus/v1/people/me?key=%s", token);
//        response.sendRedirect(x);
    }
    
    private String SampleRequest(String token) {
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
    
    private String exchangeCodeForToken(String code) throws RuntimeException{  
        String redirectUrl = "http://localhost:8080/Demo-OAuth/oauth2callback";
        //define a variable to store the weather api url and set beijing as it's default value
        String codeUrl = String.format("https://www.googleapis.com/oauth2/v4/token");
        StringBuilder strBuf = new StringBuilder();  
        
        HttpURLConnection conn=null;
        BufferedReader reader=null;
        try{  
            String urlParameters  = String.format(
                    "code=%s&" +
                    "client_id=%s&" +
                    "client_secret=%s&" +
                    "redirect_uri=%s&" +
                    "grant_type=authorization_code",
                    code, MyCredentialsStore.CLIENT_ID, MyCredentialsStore.CLIENT_SECRET, redirectUrl
                    );
            
            byte[] postData       = urlParameters.getBytes( StandardCharsets.UTF_8 );
            int    postDataLength = postData.length;
            
            URL url = new URL(codeUrl);  
            conn = (HttpURLConnection)url.openConnection();  
            conn.setDoOutput( true );
            conn.setInstanceFollowRedirects( false );
            conn.setRequestMethod( "POST" );
            conn.setRequestProperty("Host", "www.googleapis.com");
            conn.setRequestProperty( "Content-Type", "application/x-www-form-urlencoded"); 
            conn.setRequestProperty( "charset", "utf-8");
            conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
            conn.setUseCaches( false );
            try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
               wr.write( postData );
            }
            
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
        }

        String token = strBuf.toString();
        token = token.substring(token.indexOf("\"access_token\":") + 17);
        token = token.substring(0, token.indexOf("\""));
        return token;  
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
