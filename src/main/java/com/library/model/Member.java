package com.library.model;

public record Member(String memberId, String fullName, String email, String phone, String address) {
    @Override
    public String toString() {
        return memberId + " - " + fullName;
    }
}
