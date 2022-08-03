/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package po;

import vo.*;

/**
 * Persistence Object
 * 
 * @author Team 6
 */
public class Book {
    
    private String isbn;
    private String title;
    private String authors;
    private String publisher;
    private String year;
    private int stock;
    private double price;

    public Book(String isbn, String title, String authors, String publisher, String year, int stock, double price) {
        this.isbn = isbn;
        this.title = title;
        this.authors = authors;
        this.publisher = publisher;
        this.year = year;
        this.stock = stock;
        this.price = price;
    }

    public Book(vo.Book book) {
        isbn = book.getIsbn();
        title = book.getTitle();
        authors = book.getAuthors();
        publisher = book.getPublisher();
        year = book.getYear();
        stock = book.getStock();
        price = book.getPrice();
    }

    public vo.Book vo() {
        return new vo.Book(this);
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String author) {
        this.authors = author;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
}
