// QRuta · Consulta pública: lo que ve el consumidor al escanear el código QR (CU-15).

const $ = (id) => document.getElementById(id);

const ETAPAS = {
    EN_ORIGEN: "En origen",
    EN_TRANSITO: "En tránsito",
    EN_DESTINO: "En el mercado",
    ENTREGADO: "Recibido por el mercado",
    BLOQUEADO: "Bloqueado"
};


function texto(id, valor) {
    $(id).textContent = valor == null || valor === "" ? "—" : valor;
}

function horas(valor) {
    if (valor == null) return null;
    return valor === 0 ? "menos de 1 h" : `${valor} h`;
}


async function consultar(codigo) {

    $("formulario-codigo").hidden = true;
    $("no-encontrado").hidden = true;
    $("ficha").hidden = true;
    $("cargando").hidden = false;

    try {
        const respuesta = await fetch(`/api/publico/productos/${encodeURIComponent(codigo)}`);
        const datos = await respuesta.json().catch(() => null);

        if (!respuesta.ok) {
            throw new Error((datos && datos.mensaje) || "No se pudo consultar el producto");
        }

        pintar(datos);
    } catch (error) {
        $("no-encontrado").textContent = error.message;
        $("no-encontrado").hidden = false;
        $("formulario-codigo").hidden = false;
    } finally {
        $("cargando").hidden = true;
    }
}


function pintar(f) {

    $("veredicto").className = "veredicto " + (f.apto ? "veredicto--apto" : "veredicto--no-apto");
    $("veredicto-texto").textContent = f.apto ? "✓ " + f.veredicto : "✕ " + f.veredicto;
    $("veredicto-motivo").textContent = f.motivo ? "Motivo: " + f.motivo : "";

    $("ficha-nombre").textContent = f.nombre;
    $("ficha-subtitulo").textContent =
        [f.tipoProducto, f.variedadOEspecie, "código " + f.codigo].filter(Boolean).join(" · ");

    texto("ficha-productor", f.productor);
    texto("ficha-region", f.regionDeOrigen);
    texto("ficha-lote", f.lote);
    texto("ficha-produccion", f.fechaProduccion);
    texto("ficha-etapa", ETAPAS[f.etapa] || f.etapa);
    texto("ficha-transporte", horas(f.duracionTransporteHoras));
    texto("ficha-mercado", horas(f.horasEnElMercado));
    texto("ficha-certificados", (f.certificados || []).join(", "));

    const e = f.ultimaEvaluacion;
    $("bloque-evaluacion").hidden = !e;

    if (e) {
        texto("eval-temperatura", e.temperatura + " °C");
        texto("eval-vida", e.vidaUtilRestanteHoras + " h");
        texto("eval-ruptura", e.rupturaCadenaFrio ? "Se rompió" : "Se mantuvo");
        texto("eval-riesgo", e.nivelRiesgo);
    }

    $("ficha-recorrido").innerHTML = f.recorrido.length === 0
        ? `<li><strong>Registrado por el productor</strong><span>Todavía no inicia su recorrido.</span></li>`
        : f.recorrido.map(paso => `
            <li class="${paso.etapa === "BLOQUEADO" ? "linea-tiempo__bloqueo" : ""}">
                <strong>${ETAPAS[paso.etapa] || paso.etapa}</strong>
                <span>${paso.fechaHora}${paso.observacion ? " · " + escapar(paso.observacion) : ""}</span>
            </li>`).join("");

    document.title = `QRuta · ${f.nombre} · ${f.veredicto}`;
    $("ficha").hidden = false;
}


function escapar(valor) {
    const div = document.createElement("div");
    div.textContent = valor;
    return div.innerHTML;
}


$("formulario-codigo").addEventListener("submit", (evento) => {

    evento.preventDefault();

    const codigo = $("codigo").value.trim().toUpperCase();

    history.replaceState(null, "", "?codigo=" + encodeURIComponent(codigo));
    consultar(codigo);
});


const codigoInicial = new URLSearchParams(window.location.search).get("codigo");

if (codigoInicial) {
    consultar(codigoInicial);
} else {
    $("formulario-codigo").hidden = false;
}
