package com.library.dto;

public record MemberDto(String memberId, String fullName, String email, String phone, String address) {
    @Override
    public String toString() {
        return memberId + " - " + fullName;
    }
}
