package com.pavelgajdos.demo.exchangerateservice.rates.provider.remote.fixer.dto;

public record FixerErrorResponse(
    int code,
    String type,
    String info
) {
    @Override
    public String toString() {
        return "status=" + code + " type=" + type + " message=" + info;
    }
}
