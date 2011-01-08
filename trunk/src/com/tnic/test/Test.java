package com.tnic.test;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.script.*;

import java.io.IOException;

public class Test extends HttpServlet {
    public void doGet (HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        A a = new A();
        resp.getWriter().println(a.ok());
    }
}
