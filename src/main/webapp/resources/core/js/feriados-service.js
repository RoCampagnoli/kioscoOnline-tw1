// /js/feriados-service.js

/**
 * Obtiene los feriados de Argentina para un año específico desde la API de ArgentinaDatos.
 * Retorna un Array de objetos [{fecha: "YYYY-MM-DD", nombre: "Nombre del Feriado"}]
 */
async function obtenerFeriados(anio) {
  try {
    const respuesta = await fetch(`https://api.argentinadatos.com/v1/feriados/${anio}`);
    if (!respuesta.ok) throw new Error("Error al consultar la API de feriados");

    const datos = await respuesta.json();

    // Retornamos el array de objetos tal cual viene para conservar fecha y nombre
    return datos.map(feriado => ({
      fecha: feriado.fecha,
      nombre: feriado.nombre
    }));
  } catch (error) {
    console.error("No se pudieron cargar los feriados dinámicamente:", error);
    // Backup de emergencia con nombres si la API se cae
    return [
      {fecha: `${anio}-01-01`, nombre: "Año Nuevo"},
      {fecha: `${anio}-03-24`, nombre: "Día de la Memoria"},
      {fecha: `${anio}-04-02`, nombre: "Día de las Malvinas"},
      {fecha: `${anio}-04-03`, nombre: "Viernes Santo"},
      {fecha: `${anio}-05-01`, nombre: "Día del Trabajador"},
      {fecha: `${anio}-05-25`, nombre: "Revolución de Mayo"},
      {fecha: `${anio}-06-20`, nombre: "Paso a la Inmortalidad de Belgrano"},
      {fecha: `${anio}-07-09`, nombre: "Día de la Independencia"},
      {fecha: `${anio}-12-08`, nombre: "Inmaculada Concepción"},
      {fecha: `${anio}-12-25`, nombre: "Navidad"}
    ];
  }
}