package com.ru55o.luckypalm.preguntas.pojo;

/**
 * Created by toledop on 18/05/2018.
 */
public class Answer {
    private String solucion;
    private String respuesta;
    private String nivel;

    public String getRespuesta() {
        return respuesta;
    }

    public void setRespuesta(String respuesta) {
        this.respuesta = respuesta;
    }

    public String getNivel() {
        return nivel;
    }

    public void setNivel(String nivel) {
        this.nivel = nivel;
    }

    public String getSolucion() {

        return solucion;
    }

    public void setSolucion(String solucion) {
        this.solucion = solucion;
    }
}
