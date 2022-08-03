/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vo;

import po.*;

/**
 * View Object of Book
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

    public Book(po.Book book) {
        isbn = book.getIsbn();
        title = book.getTitle();
        authors = book.getAuthors();
        publisher = book.getPublisher();
        year = book.getYear();
        stock = book.getStock();
        price = book.getPrice();
    }

    public po.Book po() {
        return new po.Book(this);
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

    public void setAuthors(String authors) {
        this.authors = authors;
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

    @Override
    public String toString() {
        return "{\"isbn\":\"" + isbn + "\",\"title\":\"" + title + "\",\"authors\":" + authors + ",\"publisher\":\"" + publisher + "\",\"year\":\"" + year + "\",\"stock\":" + stock + ",\"price\":" + price + "}";
    }
    
}
