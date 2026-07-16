package com.tallerwebi.dominio.SubidaDeImgs;

import org.springframework.web.multipart.MultipartFile;

public interface ServicioImagenes {
  String subirImagen(MultipartFile archivo, String carpeta);
  String subirImagenHijo(MultipartFile archivo, String carpeta);
  String subirImagenProducto(MultipartFile archivo, String carpeta, boolean quitarFondo);
}
