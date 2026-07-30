package com.baseline.dto;

public class Pagination {
    private int currentPage;    // 현재 페이지 번호
    private int pageSize;       // 페이지당 게시글 수
    private int totalCount;     // 전체 게시글 수
    private int totalPages;     // 전체 페이지 수
    private int startPage;      // 시작 페이지 번호
    private int endPage;        // 끝 페이지 번호
    private boolean prev;       // 이전 페이지 존재 여부
    private boolean next;       // 다음 페이지 존재 여부

    public Pagination(int currentPage, int pageSize, int totalCount) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalCount = totalCount;

        this.totalPages = (int) Math.ceil((double) totalCount / pageSize);

        int pageBlock = 10;
        this.endPage = (int) (Math.ceil((double) currentPage / pageBlock)) * pageBlock;
        this.startPage = this.endPage - pageBlock + 1;

        if (this.endPage > this.totalPages) {
            this.endPage = this.totalPages;
        }

        this.prev = this.startPage > 1;
        this.next = this.endPage < this.totalPages;
    }

    public int getCurrentPage() { return currentPage; }
    public int getPageSize() { return pageSize; }
    public int getTotalCount() { return totalCount; }
    public int getTotalPages() { return totalPages; }
    public int getStartPage() { return startPage; }
    public int getEndPage() { return endPage; }
    public boolean isPrev() { return prev; }
    public boolean isNext() { return next; }
}