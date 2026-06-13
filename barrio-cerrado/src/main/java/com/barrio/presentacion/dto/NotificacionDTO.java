package com.barrio.presentacion.dto;

import lombok.Data;

@Data
public class NotificacionDTO {

    private String dniDestinatario;
    private String mensaje;
    private String canal;
}
