package com.gamasoft.example.servlets;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class HelloWorld extends AbstractHandler
{

    private final static AtomicInteger counter = new AtomicInteger();

    public void handle(String target,
                       Request baseRequest,
                       HttpServletRequest request,
                       HttpServletResponse response)
            throws IOException, ServletException
    {
        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Hello World</h1>");
        response.getWriter().println("<p>thread name " + Thread.currentThread().getName() + "</p>");
        response.getWriter().println("<p>counter " + counter.getAndIncrement() + "</p>");
    }

    public static void main(String[] args) throws Exception
    {
        Server server = new Server(8080);

        server.setHandler(new HelloWorld());

        server.start();
        server.join();
    }
}