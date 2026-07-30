package com.baseline.controller.playground;

import java.io.IOException;

import com.baseline.controller.Action;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PlaygroundMainAction implements Action {

    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("/playground/index.jsp").forward(request, response);
    }
}