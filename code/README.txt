# CNSCC311 Distributed System README Document of Team 6

## File List

```
g1-t6-code
│
├── services
│   ├── BookService
│   │   └── ...
│   ├── CartService
│   │   └── ...
│   ├── DatabaseService
│   │   └── ...
│   ├── TransactionService
│   │   └── ...
│   ├── UserService
│   │   └── ...
│   └── UserVerifyService
│       └── ...
│
├── client
│   ├── css
│   │   ├── footer.css
│   │   ├── header.css
│   │   ├── index.css
│   │   ├── main.css
│   │   ├── snackbar.css
│   │   ├── top.css
│   │   └── window.css
│   ├── js
│   │   ├── ajax.js
│   │   ├── cart.js
│   │   ├── footer.js
│   │   ├── index.js
│   │   ├── modify.js
│   │   ├── search.js
│   │   ├── sign.js
│   │   ├── snackbar.js
│   │   ├── top.js
│   │   └── txn.js
│   └── bookshop.html
│
└── README.txt
```

## External Libraries

The following sections show the external libraries used in services.

### com.google.code.gson

This external library is used in our project to convert map structure to JSON string, or convert JSON string to a map
structure. Available on: https://mvnrepository.com/artifact/com.google.code.gson/gson/2.8.6

### mysql-connector-java

This external library is used to access database with JDBC. Available
on: https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.23

## Services

The following sections introduces all the services.

### DatabaseService

This is a SOAP service. This service directly interacts with the database. In DatabaseService:

- `dao` package includes classes that handle specific aspect of data. For example, `CartDao.java` handles all data about
  cart.
- `service` package includes classes constructing this SOAP service.
- `util` package includes class that support JDBC.

### UserService

This is a RESTful service. It handles request from client side when the user wants to register an account, login, or
update personal information. This service also serves as a client calling SOAP services including Database Service and
User Verify Service. In UserService:

- `UserService` package includes servlet that support this RESTful service.

### CartService

This is a RESTful service. It handles request from client side when the user wants to check books in the cart, add books
to cart, delete books in cart, or update number of books in cart. This service also serves as a client calling SOAP
services including Database Service and User Verify Service. In CartService:

- `CartService` package includes servlet that support this RESTful service.

### UserVerifyService

This is a SOAP service. It verifies the authenticity of the user. In UserVerifyService:

- `UserVerifyService` package includes classes constructing this SOAP service.

### TransactionService

This is a RESTful service. It handles request from client side when the user wants to submit order and make a payment,
acknowledge the receipt of books, return books, or check all the past transactions or ordering. This service also serves
as a client calling SOAP services including Database Service and User Verify Service. In TransactionService:

- `TransactionService` package includes servlet that support this RESTful service.

### BookService

This is a RESTful service. It handles access to book information. In BookService:

- `dao` package includes classes that directly access database.
- `po` package is Persistence Object.
- `service` package includes class that handles logic.
- `util` package includes class that support JDBC.
- `vo` package includes servlet that handle request from client to view available books.

## Client Source Code Guidance

### Introduction to Client

The client is written in HTML, CSS, and JavaScript. It can be deployed to a server,or directly opened in browser.
Moreover, it can also be built to a desktop application using Node.js and Electron. The client follows material design.

### HTML File Structure

In the `head` tag, the charset, viewport, and title are configured, it has also imported the stylesheets and JavaScript
files. It is remarkable that in the 17th line of bookshop.html, it writes:

> `<link href="https://fonts.googleapis.com/css2?family=Material+Icons" rel="stylesheet">`

This file is for displaying material design icons. The icons are performed as a font in the web pages. It is also
notable that in the 28th line, it writes:

> `<script rel="script" src="https://cdn.bootcss.com/blueimp-md5/2.10.0/js/md5.js" type="text/javascript"></script>`

It imports the MD5 algorithm from client, so that it can encrypt password using `md5()` function.

In the `body` tag, there are six elements:

1. The first one is a `div` tag for pop-up windows for sign up, sign in, modifying information, managing cart, and
   managing transactions. This element is consist of a half-transparent background, and some windows with default style
   of `display: none`. Some onclick events will call some JavaScript functions to modify the style of specific window as
   `display: inherit`.
2. The second one is the header of the client, displays as grid and always floats on the top of the window. The left
   side shows "Bookshop" text, searchbar lies in center, and provides sign up and sign in buttons when not sign in.
   After login, the buttons on the right will be changed to the name of user. It will also provide some buttons for
   manage personal information, cart, and transactions.
3. The third part is `main`, as its name shows, the search results of books will be displayed in this block.
4. Then the next part is the footer of the client. The initial style of it is `position: absolute; bottom: 0` since the
   height of main is not high to allow users to scroll. As the search action is called, no matter it succeeds or fails,
   the position will be set to `absolute` or `relative` to make sure that the footer is on the bottom of the client.
5. The next part is a floating button named "back-to-top", and displays as an upward arrow. The initial position of this
   button is `position: fixed; bottom: -100%`, only when the window has been scrolled lower, it will display. This
   button type is named FAB, also known as floating action button, which is an Android material design component.
6. The final part is snackbar, which is also a component in Android development. Snackbar is designed to display
   messages, comparing to `alert()` in JavaScript, the user experience of it is much better.

### Stylesheets

There are seven stylesheet files in CSS in this client, even though it can be gathered as one file or even integrated in
the HTML file, for better reading experience, they are separated according to their usages. By the way, the input effect
in the pop-up windows, such as the login window, modify window, etc., are completed using css, you can read it in
`window.css` from line 30 to 113 if you are interested in it.

### Scripts

There are ten JavaScript files in this client, the same as the stylesheets, these classes and functions are grouped by
their usages. The second line of the files declares the usage of them. In the following part, will introduce you the
details of each file.

1. `ajax.js` defines a `AjaxRequest` class. This class provides a convenience way to raise AJAX requests, and try
   different hosts iteratively and randomly (selectable). This client can be called as an "AJAX-based" client because
   AJAX are widely used in this client. Therefore, this file is the basement of this client.
2. `cart.js` provides functions for handling cart related actions, including add/decline amount, add/remove book(s)
   to/from cart, get cart information, and modify amount of books in cart.
3. `footer.js` sets the position of the footer according to the size of result list. By comparing `window.innerHeight`
   with `document.getElementById("main").offsetHeight + document.getElementById("footer").scrollHeight`, the footer is
   always on the bottom of the page.
4. `index.js` defines some constants and global variables, and set onload and onscroll events for window. It is
   remarkable that in the `window.onload` event, there is a `updateHosts()` function. Via this function, the client can
   request for latest IP addresses of all available addresses. It is named HDS - Host Distribution System and its
   invention is inspired from DNS (Domain Name System).
5. `modify.js` defines the modify function for modifying user information. The `modify()` function allows users to
   modify their credit card number and address after login.
6. `search.js` defines the search function for finding books by keyword and page. According to the response JSON, the
   results will be listed. It is call by the `oninput` and `onclick` event, and the page will be increased or reset
   according to how it is called.
7. `sign.js` defines functions for doing sign-related actions including sign in, sign up, sign out and check login by
   cookie. User information, including telephone number, name, password (encrypted by MD5), card number, address, and
   consumption, will be written in JSON in cookie by setting `document.cookie`. There are no sessions in servers, so it
   is a simplified JWT-like login and operate strategy for solving the stateless nature of HTTP.
8. `snackbar.js` defines the snackbar class, which can display different messages and do different actions when the
   button is pressed.
9. `top.js` defines the function for getting back to top. In this function, the current height decreases by one fifth of
   it every 10 milliseconds. Thus, the scroll to top animation is smoother than the native implementation in JavaScript
   due to the existence of accelerated speed this function achieves.
10. `txn.js` defines functions to operate transactions, including generate new transactions, retrieve transactions of
    users, return items, and acknowledge receipt of items.

## Database design

The database our team used is MySQL, and the creation SQLs for database `bookshop` are shown as below:

```
CREATE TABLE IF NOT EXISTS `book`
(
    `b_isbn`      CHAR(13) PRIMARY KEY COMMENT 'Book ISBN-13 code',
    `b_title`     VARCHAR(255)      NOT NULL COMMENT 'Book title',
    `b_author`    VARCHAR(255)      NOT NULL COMMENT 'Book author(s), stored in JSON',
    `b_publisher` VARCHAR(255)      NOT NULL COMMENT 'Book publisher',
    `b_year`      SMALLINT UNSIGNED NOT NULL COMMENT 'Book publish year',
    `b_stock`     INT UNSIGNED      NOT NULL COMMENT 'Book stock',
    `b_price`     DOUBLE UNSIGNED   NOT NULL COMMENT 'Book price (in USD)'
);

CREATE TABLE IF NOT EXISTS `user`
(
    `u_tel`         VARCHAR(16) PRIMARY KEY COMMENT 'User telephone number, unique, use as ID',
    `u_name`        VARCHAR(16)  NOT NULL COMMENT 'User name, not unique',
    `u_passwd`      CHAR(32)     NOT NULL COMMENT 'User password encrypted by MD5',
    `u_card`        VARCHAR(20)  NOT NULL COMMENT 'Credit card number',
    `u_addr`        VARCHAR(255) NOT NULL COMMENT 'User address',
    `u_consumption` DOUBLE       NOT NULL DEFAULT 0 COMMENT 'User consumption'
);

CREATE TABLE IF NOT EXISTS `cart`
(
    `u_tel`    VARCHAR(16)  NOT NULL COMMENT 'User telephone number',
    `b_isbn`   CHAR(13)     NOT NULL COMMENT 'Book ISBN',
    `c_amount` INT UNSIGNED NOT NULL COMMENT 'Book amount',
    `c_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Add timestamp, generates automatically',
    PRIMARY KEY (`u_tel`, `b_isbn`),
    FOREIGN KEY (`u_tel`) REFERENCES `user` (`u_tel`),
    FOREIGN KEY (`b_isbn`) REFERENCES `book` (`b_isbn`)
);

CREATE TABLE IF NOT EXISTS `txn`
(
    `u_tel`    VARCHAR(16)      NOT NULL COMMENT 'User telephone number',
    `t_id`     VARCHAR(32)      NOT NULL COMMENT 'Transaction ID encrypted by MD5',
    `t_books`  JSON             NOT NULL COMMENT 'Transaction books, stored in JSON',
    `t_amount` DOUBLE           NOT NULL COMMENT 'Transaction amount',
    `t_status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT 'Transaction status',
    `t_time`   TIMESTAMP        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'Transaction generate timestamp, generates automatically',
    PRIMARY KEY (`u_tel`, `t_id`),
    FOREIGN KEY (`u_tel`) REFERENCES `user` (`u_tel`)
);

CREATE USER 'admin'@'%' IDENTIFIED BY 'admin';
GRANT ALL PRIVILEGES ON `bookshop`.* TO 'admin'@'%';
```
