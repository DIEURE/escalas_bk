package com.hope.escala.dto.response;

 

import java.time.LocalDateTime;

public class ErrorResponseDTO {

    private Integer status;
    private String erro;
    private LocalDateTime dataHora;

    public ErrorResponseDTO() {
    }

    public ErrorResponseDTO(Integer status, String erro) {
        this.status = status;
        this.erro = erro;
        this.dataHora = LocalDateTime.now();
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }
}