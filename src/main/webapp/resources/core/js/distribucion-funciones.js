/* global obtenerFeriados, flatpickr */

// eslint-disable-next-line no-unused-vars -- se invoca desde el atributo onclick del HTML
function cambiar(btn, delta) {
  const control = btn.parentElement;
  const producto = btn.closest("[data-producto-id]");
  const span = control.querySelector(".qty-num");
  const input = control.querySelector(".qty-input");
  const stock = parseInt(control.dataset.stock);
  const totalActual = Array.from(producto.querySelectorAll(".qty-num")).reduce(
      (sum, s) => sum + parseInt(s.textContent),
      0
  );
  const valorActual = parseInt(span.textContent);
  const nuevoValor = valorActual + delta;
  if (nuevoValor < 0) return;
  if (delta > 0 && totalActual >= stock) return;
  span.textContent = nuevoValor;
  input.value = nuevoValor;
  actualizarEstadoBotonConfirmar();
}

// eslint-disable-next-line no-unused-vars -- se invoca desde el atributo onclick del HTML
function eliminarFila(btn) {
  const producto = btn.closest("[data-producto-id]");
  const productoId = producto.dataset.productoId;
  fetch("/spring/carrito/eliminar", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "productoId=" + productoId
  }).then(() => {
    producto.remove();
    actualizarEstadoBotonConfirmar();
  });
}

function actualizarEstadoBotonConfirmar() {
  const btnConfirmar = document.getElementById("btnConfirmarPedido");
  if (!btnConfirmar) return;
  btnConfirmar.disabled = false;
}

function configurarFechaRetiro() {
  /*const fecha = document.getElementById("fechaRetiro");
  if (!fecha) return;

  const manana = new Date();
  manana.setDate(manana.getDate() + 1);

  const anio = manana.getFullYear();
  const mes = String(manana.getMonth() + 1).padStart(2, "0");
  const dia = String(manana.getDate()).padStart(2, "0");

  fecha.min = `--`;

  fecha.addEventListener("keydown", (e) => e.preventDefault());
  fecha.addEventListener("paste", (e) => e.preventDefault());
  fecha.addEventListener("drop", (e) => e.preventDefault());*/
  inicializarFlatpickr();
}

function configurarEnvioFormulario() {
  const form = document.getElementById("formDistribucion");
  if (!form) return;

  form.addEventListener("submit", function (e) {
    const fechaInput = document.getElementById("fechaRetiro");

    const tieneFecha = fechaInput && fechaInput.value.trim() !== "";

    const productosCards = document.querySelectorAll("[data-producto-id]");
    let todosLosProductosTienenCantidad = true;
    let productosSinAsignar = [];

    productosCards.forEach((card) => {
      const inputs = card.querySelectorAll(".qty-input");

      const totalPorProducto = Array.from(inputs).reduce(
          (sum, input) => sum + parseInt(input.value || 0),
          0
      );

      if (totalPorProducto === 0) {
        todosLosProductosTienenCantidad = false;

        const nombreProd = card
            .querySelector(".producto-nombre")
            .textContent.trim();
        productosSinAsignar.push(nombreProd);
      }
    });

    if (!tieneFecha || !todosLosProductosTienenCantidad) {
      e.preventDefault();

      const alertasViejas = document.querySelectorAll(".alerta-mensaje");
      alertasViejas.forEach((alerta) => alerta.remove());

      const nuevaAlerta = document.createElement("div");
      nuevaAlerta.className =
          "alerta-mensaje alerta-error d-flex align-items-center gap-2";

      let mensajeTexto = "";

      if (!tieneFecha && !todosLosProductosTienenCantidad) {
        mensajeTexto =
            "Debe seleccionar una fecha de retiro y asignar cantidades para todos los productos de la lista.";
      } else if (!tieneFecha) {
        mensajeTexto =
            "Por favor, seleccione la fecha de retiro en el calendario antes de continuar.";
        if (fechaInput) {
          fechaInput.focus();
        }
      } else {
        if (productosSinAsignar.length === 1) {
          mensajeTexto = `Debe asignar al menos una unidad para: "${productosSinAsignar[0]}". Si no lo quiere, use el botón "Eliminar producto".`;
        } else {
          mensajeTexto =
              "Todos los productos del carrito deben tener al menos una unidad distribuida. Elimine los que no desee llevar.";
        }
      }

      nuevaAlerta.innerHTML = `
        <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" stroke-width="2" stroke="currentColor" width="20" height="20">
            <path stroke-linecap="round" stroke-linejoin="round" d="M12 9v3.75m9-.75a9 9 0 11-18 0 9 9 0 0118 0zm-9 3.75h.008v.008H12v-.008z" />
        </svg>
        <span>${mensajeTexto}</span>
      `;

      const banner = document.querySelector(".banner-titulos");
      if (banner) {
        banner.insertAdjacentElement("afterend", nuevaAlerta);
      }

      window.scrollTo({ top: 0, behavior: "smooth" });
    }
  });
}

// eslint-disable-next-line no-unused-vars -- se invoca desde el atributo onclick del HTML
function guardarYVolverAlHome() {
  const form = document.getElementById("formDistribucion");
  if (!form) {
    window.location.href = "/spring/home";
    return;
  }

  const params = new URLSearchParams();
  const inputs = form.querySelectorAll(".qty-input");

  inputs.forEach((input) => {
    params.append(input.name, input.value);
  });

  fetch("/spring/distribucion/guardar-borrador", {
    method: "POST",
    body: params
  })
      .then(() => {
        window.location.href = "/spring/home";
      })
      .catch((err) => {
        console.error("Error al guardar el borrador:", err);
        window.location.href = "/spring/home";
      });
}

function inicializarPagina() {
  actualizarEstadoBotonConfirmar();
  configurarFechaRetiro();
  configurarEnvioFormulario();
}

document.addEventListener("DOMContentLoaded", inicializarPagina);

async function inicializarFlatpickr() {
  const fechaInput = document.getElementById("fechaRetiro");
  if (!fechaInput) return;

  const anioActual = new Date().getFullYear();
  const feriadosArgentina = await obtenerFeriados(anioActual);

  const fechasFeriados = feriadosArgentina.map((f) => f.fecha);

  flatpickr(fechaInput, {
    locale: "es",
    dateFormat: "Y-m-d",
    minDate: "today",
    disable: [
      function (date) {
        return date.getDay() === 0 || date.getDay() === 6;
      },
      function (date) {
        const offset = date.getTimezoneOffset();
        const localDate = new Date(date.getTime() - offset * 60 * 1000);
        const fechaString = localDate.toISOString().split("T")[0];
        return fechasFeriados.includes(fechaString);
      }
    ],
    onDayCreate: function (dObj, dStr, fp, dayElem) {
      const offset = dayElem.dateObj.getTimezoneOffset();
      const localDate = new Date(
          dayElem.dateObj.getTime() - offset * 60 * 1000
      );
      const fechaString = localDate.toISOString().split("T")[0];

      const feriado = feriadosArgentina.find((f) => f.fecha === fechaString);
      if (feriado) {
        dayElem.setAttribute("title", feriado.nombre);
        dayElem.classList.add("dia-feriado-tooltip");
      }
    }
  });
}