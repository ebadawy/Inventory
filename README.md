Inventory Project
================================

- inventory project is still in the alpha version that made to help you with the database and invetory stuff, if you need to add any feauture please feel free to contact me.
- Created by: Ehab AlBadawy
- E-Mail : ehalbadawy93@gmail.com


Features
=========

- manage your items, customers and vendors database.
- create selling/purchasing invoice with easy & few steps.
- keep updated with your cash and other reports status.

Technologies
============

Server-side
- MySQL : Relational database, used to store items, customers, vendors, invoices and cash.


Client-side and design
- Java : gives a beautiful and easy to use GUI.


Installation Instructions
=========================


Relational Database(MySQL)
You will want to have your database set up before you begin installation.

- Installing on Linux / Ubuntu.

To install MySQL, run the following command from a terminal prompt:

    sudo apt-get install mysql-server

During the installation process you will be prompted to enter a password for the MySQL root user.
Once the installation is complete, the MySQL server should be started automatically. You can run the following command from a terminal prompt to check whether the MySQL server is running:

    sudo netstat -tap | grep mysql

When you run this command, you should see the following line or something similar:...

    tcp        0      0 localhost:mysql         *:*                LISTEN      2556/mysqld

If the server is not running correctly, you can type the following command to start it:

    sudo service mysql restart

- Installing on Windows.
kindly use these instructions : http://dev.mysql.com/doc/refman/5.1/en/windows-installation.html



Java jdk:

- Installing on Linux / Ubuntu.

Step 1: Open Application>> Accessories>> Terminal

Step 2: Type commandline as below...

    sudo apt-get install openjdk-7-jdk
    
Test the installation at the Sun Java test webpage and using the command: 

    java -version
     
you should see something like this

    java version "1.7.0_25"
    OpenJDK Runtime Environment (IcedTea 2.3.10) (7u25-2.3.10-1ubuntu0.13.04.2)
    OpenJDK Server VM (build 23.7-b01, mixed mode)


- Installing on Windows.
download the JDK from oracle main site and install it.
http://www.oracle.com/technetwork/java/javase/downloads/index.html

Author
======


- Created by: Ehab AlBadawy.

- Email address: ehalbadawy93@gmail.com

License
=======
(See LICENSE)
