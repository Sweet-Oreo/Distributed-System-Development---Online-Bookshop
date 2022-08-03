/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import service.impl.BookServiceImpl;
import vo.BookList;
import vo.RespObj;

/**
 * Handle request from client to view available books
 * 
 * @author Team 6
 */
public class BookServlet extends HttpServlet {

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.setHeader("Access-Control-Allow-Origin", "*");

        String keyword = req.getParameter("keyword");
        if (keyword == null) {
            keyword = "";
        }

        String pageString = req.getParameter("page");
        int page = 1;
        if (pageString != null) {
            try {
                page = Integer.parseInt(pageString);
            } catch (NumberFormatException ignore) {
            }
        }

        BookList books = new BookServiceImpl().findBooksByKeyword(keyword, page);
        resp.getWriter().write(new RespObj<>(books != null, books, "books").toString());
        
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
