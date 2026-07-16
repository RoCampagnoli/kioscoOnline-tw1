package com.tallerwebi.dominio.SubidaDeImgs;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import java.io.IOException;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ServicioImagenesImpl implements ServicioImagenes {

  private final Cloudinary cloudinary;
  private static final String FOLDER_LITERAL = "folder";

  @Autowired
  public ServicioImagenesImpl(Cloudinary cloudinary) {
    this.cloudinary = cloudinary;
  }

  @Override
  public String subirImagen(MultipartFile archivo, String carpeta) {
    try {
      Map<String, Object> resultado = (Map<String, Object>) cloudinary
        .uploader()
        .upload(archivo.getBytes(), ObjectUtils.asMap(FOLDER_LITERAL, carpeta));

      return resultado.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Error al subir imagen", e);
    }
  }

  @Override
  public String subirImagenHijo(MultipartFile archivo, String carpeta) {
    try {
      Map<String, Object> resultado = (Map<String, Object>) cloudinary
        .uploader()
        .upload(
          archivo.getBytes(),
          ObjectUtils.asMap(
            FOLDER_LITERAL,
            carpeta,
            "transformation",
            new Transformation<>()
              .width(400) // Tamaño carnet
              .height(400)
              .crop("thumb") // Recorte inteligente
              .gravity("face") //  Busca y centra el rostro del nene
          )
        );

      return resultado.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Error al subir imagen del hijo", e);
    }
  }

  @Override
  public String subirImagenProducto(MultipartFile archivo, String carpeta, boolean quitarFondo) {
    try {
      Map<String, Object> opciones = quitarFondo
        ? ObjectUtils.asMap(
          FOLDER_LITERAL,
          carpeta,
          "transformation",
          new Transformation<>().effect("background_removal")
        )
        : ObjectUtils.asMap(FOLDER_LITERAL, carpeta);

      Map<String, Object> resultado = (Map<String, Object>) cloudinary
        .uploader()
        .upload(archivo.getBytes(), opciones);

      return resultado.get("secure_url").toString();
    } catch (IOException e) {
      throw new RuntimeException("Error al subir la imagen del producto", e);
    }
  }
}
