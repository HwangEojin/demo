package com.baseline.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.baseline.config.DBConnection;
import com.baseline.dto.FileVO;

public class FileDAO {
    private static FileDAO instance = new FileDAO();

    private FileDAO() {}

    public static FileDAO getInstance() {
        return instance;
    }

    public void insertFile(FileVO fileVO) {
        String sql = "INSERT INTO TB_ATTACH (BOARD_SEQ, ORIGINAL_FILE_NM, SAVED_FILE_NM) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileVO.getBoardSeq());
            pstmt.setString(2, fileVO.getOriginalFileName());
            pstmt.setString(3, fileVO.getSavedFileName());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<FileVO> getFilesByBoardSeq(int boardSeq) {
        List<FileVO> fileList = new ArrayList<>();
        String sql = "SELECT FILE_SEQ, BOARD_SEQ, ORIGINAL_FILE_NM, SAVED_FILE_NM FROM TB_ATTACH WHERE BOARD_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardSeq);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    FileVO fileVO = new FileVO();
                    fileVO.setFileSeq(rs.getInt("FILE_SEQ"));
                    fileVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                    fileVO.setOriginalFileName(rs.getString("ORIGINAL_FILE_NM"));
                    fileVO.setSavedFileName(rs.getString("SAVED_FILE_NM"));
                    fileList.add(fileVO);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    public FileVO getFileByFileSeq(int fileSeq) {
        FileVO fileVO = null;
        String sql = "SELECT FILE_SEQ, BOARD_SEQ, ORIGINAL_FILE_NM, SAVED_FILE_NM FROM TB_ATTACH WHERE FILE_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, fileSeq);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fileVO = new FileVO();
                    fileVO.setFileSeq(rs.getInt("FILE_SEQ"));
                    fileVO.setBoardSeq(rs.getInt("BOARD_SEQ"));
                    fileVO.setOriginalFileName(rs.getString("ORIGINAL_FILE_NM"));
                    fileVO.setSavedFileName(rs.getString("SAVED_FILE_NM"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileVO;
    }

    public void deleteFilesByBoardSeq(int boardSeq) {
        String sql = "DELETE FROM TB_ATTACH WHERE BOARD_SEQ = ?";
        try (Connection conn = DBConnection.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, boardSeq);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}