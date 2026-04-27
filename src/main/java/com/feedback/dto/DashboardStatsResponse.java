package com.feedback.dto;

public class DashboardStatsResponse {
    private long totalForms;
    private long totalSubmissions;
    private long totalUsers;
    private long publishedForms;

    public DashboardStatsResponse(long totalForms, long totalSubmissions, long totalUsers, long publishedForms) {
        this.totalForms = totalForms;
        this.totalSubmissions = totalSubmissions;
        this.totalUsers = totalUsers;
        this.publishedForms = publishedForms;
    }

    public long getTotalForms() { return totalForms; }
    public void setTotalForms(long totalForms) { this.totalForms = totalForms; }
    public long getTotalSubmissions() { return totalSubmissions; }
    public void setTotalSubmissions(long totalSubmissions) { this.totalSubmissions = totalSubmissions; }
    public long getTotalUsers() { return totalUsers; }
    public void setTotalUsers(long totalUsers) { this.totalUsers = totalUsers; }
    public long getPublishedForms() { return publishedForms; }
    public void setPublishedForms(long publishedForms) { this.publishedForms = publishedForms; }
}
