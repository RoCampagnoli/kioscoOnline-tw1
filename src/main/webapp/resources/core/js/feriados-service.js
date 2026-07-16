// /js/feriados-service.js

/**
 * Obtiene los feriados de Argentina para un año específico desde la API de ArgentinaDatos.
 * Retorna un Array de objetos [{fecha: "YYYY-MM-DD", nombre: "Nombre del Feriado"}]
 */
// eslint-disable-next-line no-unused-vars -- se usa desde distribucion-funciones.js
async function obtenerFeriados(anio) {
  try {
    const respuesta = await fetch(
      `https://api.argentinadatos.com/v1/feriados/`
    );
    if (!respuesta.ok) throw new Error("Error al consultar la API de feriados");

    const datos = await respuesta.json();

    return datos.map((feriado) => ({
      fecha: feriado.fecha,
      nombre: feriado.nombre
    }));
  } catch (error) {
    console.error("No se pudieron cargar los feriados dinámicamente:", error);
    return [
      { fecha: `-01-01`, nombre: "Año Nuevo" },
      { fecha: `-03-24`, nombre: "Día de la Memoria" },
      { fecha: `-04-02`, nombre: "Día de las Malvinas" },
      { fecha: `-04-03`, nombre: "Viernes Santo" },
      { fecha: `-05-01`, nombre: "Día del Trabajador" },
      { fecha: `-05-25`, nombre: "Revolución de Mayo" },
      { fecha: `-06-20`, nombre: "Paso a la Inmortalidad de Belgrano" },
      { fecha: `-07-09`, nombre: "Día de la Independencia" },
      { fecha: `-12-08`, nombre: "Inmaculada Concepción" },
      { fecha: `-12-25`, nombre: "Navidad" }
    ];
  }
}
