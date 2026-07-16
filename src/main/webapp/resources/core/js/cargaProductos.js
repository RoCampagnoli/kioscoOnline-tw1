/* global listaCategorias, bootstrap */

// --- LÓGICA DE EDICIÓN EXCEL (TABLA) ---
document.querySelectorAll(".celda-editable").forEach((celda) => {
  celda.addEventListener("click", function () {
    if (this.querySelector("input") || this.querySelector("select")) return;

    const id = this.getAttribute("data-id");
    const campo = this.getAttribute("data-campo");

    // Obtenemos el valor real. Si es categoría, usamos el ID que guardamos en la celda
    let valorOriginal = this.innerText.trim();
    if (campo === "categoria") {
      valorOriginal = this.getAttribute("data-categoria-id") || "";
    }

    if (campo === "categoria") {
      const select = document.createElement("select");
      select.className = "select-edicion-tabla";

      const idCategoriaActual = this.getAttribute("data-categoria-id");

      if (listaCategorias.length === 0) {
        mostrarToast(
            "❌ No hay categorías disponibles para seleccionar",
            "#e53e3e"
        );
        return;
      }

      listaCategorias.forEach((cat) => {
        const opt = document.createElement("option");
        opt.value = cat.id;
        opt.text = cat.nombreCategoria;
        if (String(cat.id) === idCategoriaActual) {
          opt.selected = true;
        }
        select.appendChild(opt);
      });

      this.innerHTML = "";
      this.appendChild(select);
      select.focus();

      // Usamos un semáforo (flag) para que no se ejecute dos veces seguidas (blur y change)
      let guardadoEnProgreso = false;

      const guardarCategoria = () => {
        if (guardadoEnProgreso) return;
        guardadoEnProgreso = true;

        const nuevoId = select.value;

        if (!nuevoId) {
          this.innerHTML = `<span class="badge-categoria"></span>`;
          return;
        }

        enviarCambioAlServidor(
            id,
            campo,
            nuevoId,
            this,
            () => {
              this.innerHTML = `<span class="badge-categoria"></span>`;
              this.setAttribute("data-categoria-id", nuevoId);
            },
            () => {
              this.innerHTML = `<span class="badge-categoria"></span>`;
            }
        );
      };

      // Para evitar conflictos, priorizamos el cambio y si se pierde el foco sin cambiar, ejecutamos blur
      select.addEventListener("change", guardarCategoria);
      select.addEventListener("blur", () => {
        setTimeout(() => {
          if (!guardadoEnProgreso) {
            guardarCategoria();
          }
        }, 100);
      });
    } else {
      const input = document.createElement("input");
      input.type =
          campo === "precio" || campo === "cantidad" ? "number" : "text";
      if (campo === "precio") input.step = "0.01";

      input.className = "input-edicion-tabla";
      input.value = valorOriginal;

      this.innerHTML = "";
      this.appendChild(input);
      input.focus();

      let guardadoEnProgreso = false;

      const terminarEdicion = () => {
        if (guardadoEnProgreso) return;
        guardadoEnProgreso = true;

        const nuevoValor = input.value.trim();

        if (
            (campo === "nombre" || campo === "precio" || campo === "cantidad") &&
            nuevoValor === ""
        ) {
          mostrarToast("❌ Este campo es obligatorio", "#e53e3e");
          this.innerText = valorOriginal;
          return;
        }

        if (nuevoValor === valorOriginal) {
          this.innerText = valorOriginal;
          return;
        }

        enviarCambioAlServidor(
            id,
            campo,
            nuevoValor,
            this,
            () => {
              this.innerText = nuevoValor;
            },
            () => {
              this.innerText = valorOriginal;
            }
        );
      };

      input.addEventListener("blur", terminarEdicion);
      input.addEventListener("keydown", function (e) {
        if (e.key === "Enter") {
          input.blur();
        }
      });
    }
  });
});

function enviarCambioAlServidor(
    id,
    campo,
    valor,
    celdaHtml,
    exitoCallback,
    errorCallback
) {
  const formData = new FormData();
  formData.append("id", id);
  formData.append("campo", campo);
  formData.append("valor", valor);

  // Si tu Spring Controller usa rutas limpias (/productosKiosquero/editar-rapido), cámbiala aquí:
  fetch("productosKiosquero/editar-rapido", {
    method: "POST",
    body: formData
  })
      .then((response) => {
        if (!response.ok) {
          return response.json().then((err) => {
            throw new Error(err.mensaje || "Error al procesar la solicitud");
          });
        }
        return response.json();
      })
      .then((data) => {
        if (data.exito) {
          exitoCallback();
          mostrarToast("✔️ Guardado exitosamente", "#2f855a");
        } else {
          errorCallback();
          mostrarToast("❌ " + data.mensaje, "#e53e3e");
        }
      })
      .catch((error) => {
        errorCallback();
        mostrarToast(
            error.message.includes("Error")
                ? error.message
                : "❌ Error de conexión con el servidor",
            "#e53e3e"
        );
      });
}

// --- LÓGICA DE SUBIDA DE IMAGEN INDIVIDUAL ---
let modalImagenInstancia = null;

// eslint-disable-next-line no-unused-vars -- se invoca desde el atributo onclick del HTML
function abrirModalImagen(productoId) {
  document.getElementById("cambiarImagenProductoId").value = productoId;
  document.getElementById("nuevaImagenFile").value = "";
  document.getElementById("quitarFondoImgRapida").checked = false;

  modalImagenInstancia = new bootstrap.Modal(
      document.getElementById("cambiarImagenModal")
  );
  modalImagenInstancia.show();
}

document
    .getElementById("btnGuardarNuevaImagen")
    .addEventListener("click", function () {
      const id = document.getElementById("cambiarImagenProductoId").value;
      const inputImg = document.getElementById("nuevaImagenFile");
      const quitarFondo = document.getElementById("quitarFondoImgRapida").checked;

      if (inputImg.files.length === 0) {
        mostrarToast("❌ Por favor, selecciona una imagen", "#e53e3e");
        return;
      }

      const formData = new FormData();
      formData.append("id", id);
      formData.append("imagen", inputImg.files[0]);
      formData.append("quitarFondo", quitarFondo);

      this.disabled = true;
      this.innerText = "Subiendo...";

      fetch("productosKiosquero/cambiar-imagen", {
        method: "POST",
        body: formData
      })
          .then((res) => {
            if (!res.ok) {
              return res.json().then((err) => {
                throw new Error(err.mensaje || "Error al subir la imagen");
              });
            }
            return res.json();
          })
          .then((data) => {
            if (data.exito) {
              const previewImg = document.getElementById("img-preview-" + id);
              if (previewImg) {
                previewImg.src = data.nuevaUrl;
              }
              modalImagenInstancia.hide();
              mostrarToast("✔️ Imagen actualizada", "#2f855a");
            } else {
              mostrarToast("❌ " + data.mensaje, "#e53e3e");
            }
          })
          .catch((error) => {
            mostrarToast("❌ " + error.message, "#e53e3e");
          })
          .finally(() => {
            this.disabled = false;
            this.innerText = "Subir";
          });
    });

/*
// --- LÓGICA PARA ELIMINAR PRODUCTO (MODAL INTERACTIVO) ---
let modalEliminarInstancia = null;

// Esta función se ejecuta al hacer clic en el tachito de la tabla
function confirmarEliminarProducto(id) {
    // Seteamos el ID en el input oculto del modal
    document.getElementById('eliminarProductoId').value = id;

    // Inicializamos y abrimos el modal interactivo
    modalEliminarInstancia = new bootstrap.Modal(document.getElementById('confirmarEliminarModal'));
    modalEliminarInstancia.show();
}

// Escuchamos el clic en el botón "Eliminar" definitivo dentro de nuestro modal
document.getElementById('btnConfirmarEliminarDefinitivo').addEventListener('click', function() {
    const id = document.getElementById('eliminarProductoId').value;
    const formData = new FormData();
    formData.append('id', id);

    // Deshabilitamos el botón para evitar doble clic accidental
    this.disabled = true;
    this.innerText = "Eliminando...";

    fetch('productosKiosquero/eliminar', { // Ruta relativa limpia
        method: 'POST',
        body: formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(err => { throw new Error(err.mensaje || "Error al eliminar"); });
            }
            return response.json();
        })
        .then(data => {
            if (data.exito) {
                // Animación suave para desvanecer la fila borrada
                const fila = document.getElementById('fila-producto-' + id);
                if (fila) {
                    fila.style.transition = 'all 0.5s ease';
                    fila.style.opacity = '0';
                    fila.style.transform = 'scale(0.95)';
                    setTimeout(() => {
                        fila.remove();
                    }, 500);
                }
                modalEliminarInstancia.hide();
                mostrarToast("✔️ Producto eliminado correctamente", "#2f855a");
            } else {
                mostrarToast("❌ " + data.mensaje, "#e53e3e");
            }
        })
        .catch(error => {
            mostrarToast("❌ " + error.message, "#e53e3e");
        })
        .finally(() => {
            // Restablecemos el estado del botón
            this.disabled = false;
            this.innerText = "Eliminar";
        });
});

*/

function mostrarToast(mensaje, color) {
  const toast = document.getElementById("toast-guardado");
  toast.style.backgroundColor = color;
  toast.innerText = mensaje;
  toast.style.display = "flex";
  setTimeout(() => {
    toast.style.display = "none";
  }, 4000);
}