package com.baseline.dao;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface Action {
    /**
     * 요청을 처리하고, 뷰 페이지 경로를 반환하거나 리다이렉트 수행
     */
	void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;
}