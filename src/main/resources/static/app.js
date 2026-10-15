// QRuta · Trazabilidad – interfaz web que consume la API REST.

const ETAPAS = ["EN_ORIGEN", "EN_TRANSITO", "EN_DESTINO", "ENTREGADO"];

const CATEGORIA = { POLLO: "PECUARIO", PALTA: "AGRICOLA", BANANO: "AGRICOLA" };

const VISTAS = {
    PRODUCTOR: {
        titulo: "Mis productos",
        descripcion: "Registra tus lotes de pollo, palta o banano y publícalos para que un transportista los tome."
    },
    TRANSPORTISTA: {
        titulo: "Mis traslados",
        descripcion: "Acepta una publicación disponible y haz avanzar el producto por las etapas del recorrido."
    },
    INTERMEDIARIO: {
        titulo: "Productos asignados",
        descripcion: "Haz avanzar tus productos asignados por las etapas de la cadena."
    },
    MERCADO: {
        titulo: "Productos con destino a este mercado",
        descripcion: "Cuando un producto llegue (EN_DESTINO), registra su temperatura: la plataforma lo evalúa y, si es crítico, lo bloquea."
    },
    CENTRO_DE_ACOPIO: {
        titulo: "Productos con destino a este centro",
        descripcion: "Cuando un producto llegue (EN_DESTINO), registra su temperatura: la plataforma lo evalúa y, si es crítico, lo bloquea."
    },
    REGULADOR: {
        titulo: "Todos los productos",
        descripcion: "Aprueba cuentas y revisa todos los productos con su historial."
    }
};

let sesion = null;   // { token, usuario }

let destinosPorTipo = {};   // tipo de producto -> destinos que lo necesitan (para el transportista)

const esPuntoDeRecepcion = (tipo) => tipo === "MERCADO" || tipo === "CENTRO_DE_ACOPIO";

const $ = (id) => document.getElementById(id);


// ---------- Sesión (se recuerda solo en esta pestaña) ----------

function guardarSesion() {
    try { sessionStorage.setItem("qruta-sesion", JSON.stringify(sesion)); } catch (e) { /* sin almacenamiento */ }
}

function leerSesion() {
    try { return JSON.parse(sessionStorage.getItem("qruta-sesion")); } catch (e) { return null; }
}

function borrarSesion() {
    try { sessionStorage.removeItem("qruta-sesion"); } catch (e) { /* sin almacenamiento */ }
}


// ---------- API ----------

async function api(metodo, url, cuerpo) {

    const cabeceras = { "Content-Type": "application/json" };

    if (sesion) {
        cabeceras["Authorization"] = "Bearer " + sesion.token;
    }

    const respuesta = await fetch(url, {
        method: metodo,
        headers: cabeceras,
        body: cuerpo ? JSON.stringify(cuerpo) : undefined
    });

    const datos = await respuesta.json().catch(() => null);

    if (respuesta.status === 401 && sesion) {
        mostrarLogin();
    }

    if (!respuesta.ok) {
        throw new Error((datos && datos.mensaje) || "Error " + respuesta.status);
    }

    return datos;
}


// ---------- Avisos ----------

function avisar(mensaje, tipo = "exito") {

    const aviso = document.createElement("div");
    aviso.className = "aviso aviso--" + tipo;
    aviso.textContent = mensaje;

    $("avisos").appendChild(aviso);

    setTimeout(() => aviso.remove(), tipo === "error" ? 6000 : 3500);
}


// ---------- Vistas ----------

function mostrarLogin() {

    sesion = null;
    borrarSesion();

    $("vista-login").hidden = false;
    $("vista-panel").hidden = true;
    $("sesion").hidden = true;
}

function tipoCuenta() {
    return sesion.usuario.tipoCuenta;
}

async function mostrarPanel() {

    $("vista-login").hidden = true;
    $("vista-panel").hidden = false;
    $("sesion").hidden = false;

    const usuario = sesion.usuario;
    const tipo = tipoCuenta();
    const vista = VISTAS[tipo] || VISTAS.INTERMEDIARIO;

    $("sesion-nombre").textContent = usuario.nombreUsuario;
    $("sesion-rol").textContent = tipo.replaceAll("_", " ");

    $("titulo-panel").textContent = vista.titulo;
    $("descripcion-panel").textContent = vista.descripcion;

    $("panel-crear").hidden = tipo !== "PRODUCTOR";
    $("panel-transportista").hidden = tipo !== "TRANSPORTISTA";
    $("panel-usuarios").hidden = tipo !== "REGULADOR";
    $("panel-punto").hidden = !esPuntoDeRecepcion(tipo);

    await actualizar();
}

async function actualizar() {

    const tipo = tipoCuenta();

    try {
        if (tipo === "TRANSPORTISTA") await cargarTransportista();
        if (tipo === "REGULADOR") await cargarUsuarios();
        if (esPuntoDeRecepcion(tipo)) await cargarPuntoDeRecepcion();
        await cargarProductos();
    } catch (error) {
        avisar(error.message, "error");
    }
}


// ---------- Datos ----------

async function cargarProductos() {

    const productos = await api("GET", "/api/productos");

    if (tipoCuenta() === "TRANSPORTISTA") {
        await cargarDestinos(productos);
    }

    pintarProductos(productos);
}

/** El transportista solo puede elegir destinos activos que necesiten el tipo de su producto (RE-25). */
async function cargarDestinos(productos) {

    destinosPorTipo = {};

    const tipos = [...new Set(productos.filter(puedeElegirDestino).map(p => p.tipoProducto))];

    for (const tipo of tipos) {
        destinosPorTipo[tipo] = await api("GET", "/api/destinos?tipo=" + tipo);
    }
}

function puedeElegirDestino(p) {
    return p.intermediario === sesion.usuario.nombreUsuario
        && p.tipoProducto
        && !p.destino
        && (p.estado === "EN_ORIGEN" || p.estado === "EN_TRANSITO");
}

async function cargarPuntoDeRecepcion() {

    sesion.usuario = await api("GET", "/api/usuarios/yo");
    guardarSesion();

    const necesidades = sesion.usuario.necesidades || [];

    document.querySelectorAll("#formulario-necesidades input[type=checkbox]").forEach(casilla => {
        casilla.checked = necesidades.includes(casilla.value);
    });
}

async function cargarTransportista() {

    sesion.usuario = await api("GET", "/api/usuarios/yo");
    guardarSesion();

    const libre = sesion.usuario.estadoCarga === "LIBRE";

    $("estado-carga").textContent = sesion.usuario.estadoCarga;
    $("ubicacion").value = sesion.usuario.ubicacionActual || "";
    $("estado-carga").className = "estado-carga estado-carga--" + sesion.usuario.estadoCarga;

    const publicaciones = await api("GET", "/api/publicaciones");

    $("lista-publicaciones").innerHTML = !libre
        ? `<li class="texto-suave">Estás OCUPADO: termina tu traslado para ver nuevas publicaciones.</li>`
        : publicaciones.length === 0
            ? `<li class="texto-suave">No hay publicaciones disponibles.</li>`
            : publicaciones.map(p => `
                <li>
                    <div>
                        <strong>${escapar(p.nombre)}</strong>
                        <span class="texto-suave">${p.tipoProducto || ""} · lote ${escapar(p.lote || "—")} · ${p.productor}</span>
                    </div>
                    <button class="boton boton--primario boton--pequeno" data-aceptar="${p.id}">Aceptar</button>
                </li>`).join("");
}

async function cargarUsuarios() {

    const usuarios = await api("GET", "/api/usuarios");

    $("lista-usuarios").innerHTML = usuarios
        .map(u => `
            <li>
                <div>
                    <strong>${escapar(u.nombreUsuario)}</strong>
                    <span class="texto-suave">${u.tipoCuenta.replaceAll("_", " ")}${u.region ? " · " + escapar(u.region) : ""}</span>
                </div>
                ${u.estadoCuenta === "PENDIENTE"
                    ? `<button class="boton boton--primario boton--pequeno" data-aprobar="${u.nombreUsuario}">Aprobar</button>`
                    : `<span class="insignia insignia--${u.estadoCuenta}">${u.estadoCuenta}</span>`}
            </li>`)
        .join("");
}


// ---------- Pintado ----------

function pintarProductos(productos) {

    $("sin-productos").hidden = productos.length > 0;

    $("lista-productos").innerHTML = productos
        .map(pintarProducto)
        .join("");
}

function pintarProducto(p) {

    const indiceActual = ETAPAS.indexOf(p.estado);

    const etapas = ETAPAS.map((etapa, i) => `
        <div class="etapa ${i <= indiceActual ? "etapa--hecha" : ""} ${i === indiceActual ? "etapa--actual" : ""}">
            <span class="etapa__marca"></span>
            <span>${etapa.replace("_", " ")}</span>
        </div>`).join("");

    const esMio = p.intermediario === sesion.usuario.nombreUsuario;
    const esDelProductor = tipoCuenta() === "PRODUCTOR";
    const enOrigenSinTransportista = p.estado === "EN_ORIGEN" && !p.intermediario;
    const soyElDestino = p.destino === sesion.usuario.nombreUsuario;

    const acciones = [];

    if (puedeElegirDestino(p)) {
        const opciones = (destinosPorTipo[p.tipoProducto] || [])
            .map(d => `<option value="${d.nombreUsuario}">${escapar(d.razonSocial)} (${escapar(d.region || "")})</option>`)
            .join("");
        acciones.push(opciones
            ? `<span class="accion-en-linea"><select id="destino-${p.id}">${opciones}</select>
               <button class="boton boton--primario boton--pequeno" data-elegir-destino="${p.id}">Elegir destino</button></span>`
            : `<span class="texto-suave">No hay destinos activos que necesiten ${p.tipoProducto}.</span>`);
    }

    if (soyElDestino && p.estado === "EN_DESTINO" && !p.recepcionRegistrada) {
        acciones.push(`<span class="accion-en-linea">
            <input id="temperatura-${p.id}" type="number" step="0.1" placeholder="°C">
            <input id="humedad-${p.id}" type="number" step="1" min="0" max="100" placeholder="% humedad">
            <button class="boton boton--primario boton--pequeno" data-recepcion="${p.id}">Registrar recepción</button>
        </span>`);
    }

    if (esMio && p.siguienteEstado) {
        acciones.push(`<button class="boton boton--primario boton--pequeno" data-avanzar="${p.id}" data-estado="${p.siguienteEstado}">Pasar a ${p.siguienteEstado}</button>`);
    }

    if (esDelProductor && enOrigenSinTransportista && p.codigo) {
        acciones.push(p.publicado
            ? `<button class="boton boton--fantasma boton--pequeno" data-retirar="${p.id}">Retirar publicación</button>`
            : `<button class="boton boton--primario boton--pequeno" data-publicar="${p.id}">Publicar</button>`);
    }

    if (p.codigo) {
        acciones.push(`<button class="boton boton--fantasma boton--pequeno" data-qr="${p.codigo}" data-nombre="${escapar(p.nombre)}">Ver QR</button>`);
    }

    acciones.push(`<button class="boton boton--fantasma boton--pequeno" data-historial="${p.id}" data-nombre="${escapar(p.nombre)}">Ver historial</button>`);

    const detalle = [p.tipoProducto, p.variedadOEspecie, p.lote ? "lote " + p.lote : null]
        .filter(Boolean).map(escapar).join(" · ");

    return `
    <article class="tarjeta producto">
        <div class="producto__cabecera">
            <div>
                <p class="producto__nombre">${escapar(p.nombre)} <span class="producto__id">#${p.id}</span></p>
                ${detalle ? `<p class="producto__actores">${detalle}</p>` : ""}
                <p class="producto__actores">Productor: ${p.productor} · Transportista: ${p.intermediario || "sin asignar"}${p.destinoRazonSocial ? " · Destino: " + escapar(p.destinoRazonSocial) : ""}</p>
                ${p.codigo ? `<p class="producto__actores">Código QR: <code>${p.codigo}</code>${p.publicado && !p.intermediario ? ` <span class="insignia">PUBLICADO</span>` : ""}</p>` : ""}
            </div>
            <span class="estado estado--${p.estado}">${p.estado}</span>
        </div>

        ${p.estado === "BLOQUEADO" ? `<p class="alerta-bloqueo">Bloqueado: ${escapar(p.motivoBloqueo || "sin motivo")}</p>` : ""}

        <div class="etapas">${etapas}</div>

        <dl class="datos">
            <div class="dato"><dt>Inicio del transporte</dt><dd>${p.horaInicioTransporte || "—"}</dd></div>
            <div class="dato"><dt>Llegada</dt><dd>${p.horaLlegada || "—"}</dd></div>
            <div class="dato dato--destacado"><dt>Duración</dt><dd>${p.duracionTransporte || "—"}</dd></div>
        </dl>

        ${p.recepcionRegistrada || p.horaEntrega ? `<dl class="datos">
            <div class="dato"><dt>Recepción</dt><dd>${p.recepcionRegistrada ? p.temperaturaRecepcion + " °C" : "—"}</dd></div>
            <div class="dato"><dt>Entrega</dt><dd>${p.horaEntrega || "—"}</dd></div>
            <div class="dato"><dt>Destino</dt><dd>${escapar(p.destinoRazonSocial || "—")}</dd></div>
        </dl>` : ""}

        <div class="producto__acciones">${acciones.join("")}</div>
    </article>`;
}

function escapar(texto) {
    const div = document.createElement("div");
    div.textContent = texto == null ? "" : String(texto);
    return div.innerHTML;
}


// ---------- Acciones ----------

async function ejecutar(accion, mensaje) {

    try {
        await accion();
        avisar(mensaje);
        await actualizar();
    } catch (error) {
        avisar(error.message, "error");
    }
}

async function verHistorial(id, nombre) {

    try {
        const historial = await api("GET", `/api/productos/${id}/historial`);

        $("titulo-historial").textContent = `Historial · ${nombre} #${id}`;

        $("contenido-historial").innerHTML = historial.length === 0
            ? `<p class="texto-suave">Todavía no hay cambios de estado: el producto sigue en EN_ORIGEN.</p>`
            : `<ol class="linea-tiempo">${historial.map(c => `
                <li>
                    <strong>${c.estadoAnterior} → ${c.estadoNuevo}</strong>
                    <span>${c.fechaHora} · por ${c.usuario}</span>
                </li>`).join("")}</ol>`;

        $("dialogo-historial").showModal();
    } catch (error) {
        avisar(error.message, "error");
    }
}


function verQr(codigo, nombre) {

    const enlace = `${window.location.origin}/consulta.html?codigo=${encodeURIComponent(codigo)}`;

    $("titulo-qr").textContent = `QR · ${nombre} (${codigo})`;
    $("contenido-qr").innerHTML = "";
    $("enlace-qr").href = enlace;
    $("enlace-qr").textContent = enlace;

    if (window.QRCode) {
        new QRCode($("contenido-qr"), { text: enlace, width: 220, height: 220 });
    } else {
        $("contenido-qr").textContent = "No se pudo cargar la librería del QR (sin conexión a internet). Usa el enlace.";
    }

    $("dialogo-qr").showModal();
}

function textoEvaluacion(e) {
    return `${e.aptoConsumo ? "APTO" : "NO APTO PARA CONSUMO"} · riesgo ${e.nivelRiesgo}`
        + ` · vida útil restante ${e.vidaUtilRestanteHoras} h`
        + (e.rupturaCadenaFrio ? " · ruptura de la cadena de frío" : "");
}


// ---------- Formularios dinámicos ----------

function mostrarCamposDeRegistro() {

    const tipo = $("registro-tipo").value;
    const esProductor = tipo === "PRODUCTOR";

    document.querySelectorAll("#formulario-registro [data-solo]").forEach(campo => {
        campo.hidden = campo.dataset.solo !== tipo;
    });

    const documento = esProductor ? $("registro-tipo-documento").value : "RUC";

    $("etiqueta-documento").textContent =
        documento === "DNI" ? "DNI (8 dígitos)" : "RUC (11 dígitos)";
}

function mostrarCamposDeProducto() {

    const categoria = CATEGORIA[$("crear-tipo").value];

    document.querySelectorAll("#formulario-crear [data-categoria]").forEach(campo => {
        campo.hidden = campo.dataset.categoria !== categoria;
    });
}


// ---------- Eventos ----------

$("pestana-login").addEventListener("click", () => {
    $("bloque-login").hidden = false;
    $("bloque-registro").hidden = true;
    $("pestana-login").classList.add("pestana--activa");
    $("pestana-registro").classList.remove("pestana--activa");
});

$("pestana-registro").addEventListener("click", () => {
    $("bloque-login").hidden = true;
    $("bloque-registro").hidden = false;
    $("pestana-registro").classList.add("pestana--activa");
    $("pestana-login").classList.remove("pestana--activa");
});

$("formulario-login").addEventListener("submit", async (evento) => {

    evento.preventDefault();

    try {
        sesion = await api("POST", "/api/auth/login", {
            nombreUsuario: $("login-usuario").value.trim(),
            contrasena: $("login-contrasena").value
        });

        guardarSesion();
        $("formulario-login").reset();

        await mostrarPanel();
    } catch (error) {
        sesion = null;
        avisar(error.message, "error");
    }
});

$("registro-tipo").addEventListener("change", mostrarCamposDeRegistro);
$("registro-tipo-documento").addEventListener("change", mostrarCamposDeRegistro);

$("formulario-registro").addEventListener("submit", async (evento) => {

    evento.preventDefault();

    const tipo = $("registro-tipo").value;
    const esProductor = tipo === "PRODUCTOR";

    try {
        const cuenta = await api("POST", "/api/auth/registro", {
            tipoCuenta: tipo,
            nombreUsuario: $("registro-usuario").value.trim(),
            nombreCompleto: $("registro-nombre").value.trim(),
            correo: $("registro-correo").value.trim(),
            contrasena: $("registro-contrasena").value,
            razonSocial: $("registro-razon").value.trim(),
            tipoDocumento: esProductor ? $("registro-tipo-documento").value : "RUC",
            numeroDocumento: $("registro-documento").value.trim(),
            tipoProductor: esProductor ? $("registro-tipo-productor").value : null,
            placaVehiculo: tipo === "TRANSPORTISTA" ? $("registro-placa").value.trim() : null,
            telefono: $("registro-telefono").value.trim(),
            region: $("registro-region").value.trim()
        });

        avisar(`Cuenta ${cuenta.nombreUsuario} creada: queda ${cuenta.estadoCuenta} hasta su aprobación`);
        $("formulario-registro").reset();
        mostrarCamposDeRegistro();
        $("pestana-login").click();
        $("login-usuario").value = cuenta.nombreUsuario;
    } catch (error) {
        avisar(error.message, "error");
    }
});

document.querySelectorAll("[data-usuario]").forEach(chip => {
    chip.addEventListener("click", () => {
        $("login-usuario").value = chip.dataset.usuario;
        $("login-contrasena").value = "clave123";
        $("login-contrasena").focus();
    });
});

$("boton-salir").addEventListener("click", async () => {
    try { await api("POST", "/api/auth/logout"); } catch (e) { /* la sesión ya no era válida */ }
    mostrarLogin();
});

$("boton-actualizar").addEventListener("click", actualizar);

$("crear-tipo").addEventListener("change", mostrarCamposDeProducto);

$("formulario-crear").addEventListener("submit", async (evento) => {

    evento.preventDefault();

    const tipo = $("crear-tipo").value;
    const pecuario = CATEGORIA[tipo] === "PECUARIO";

    await ejecutar(async () => {
        await api("POST", "/api/productos", {
            nombre: $("crear-nombre").value.trim(),
            tipoProducto: tipo,
            tipoProduccion: $("crear-produccion").value,
            lote: $("crear-lote").value.trim(),
            fechaProduccion: $("crear-fecha").value,
            especie: pecuario ? $("crear-especie").value.trim() : null,
            codigoPlanta: pecuario ? $("crear-planta").value.trim() : null,
            certificadoSanitarioSenasa: pecuario ? $("crear-senasa").value.trim() : null,
            variedad: pecuario ? null : $("crear-variedad").value.trim(),
            estadoMaduracion: pecuario ? null : $("crear-maduracion").value,
            calidad: pecuario ? null : Number($("crear-calidad").value),
            certificadoFitosanitario: pecuario ? null : $("crear-fito").value.trim()
        });
        $("formulario-crear").reset();
        mostrarCamposDeProducto();
    }, "Producto registrado en EN_ORIGEN con su código QR");
});

$("lista-productos").addEventListener("click", (evento) => {

    const avanzar = evento.target.closest("[data-avanzar]");
    const publicar = evento.target.closest("[data-publicar]");
    const retirar = evento.target.closest("[data-retirar]");
    const historial = evento.target.closest("[data-historial]");

    if (avanzar) {
        ejecutar(
            () => api("PUT", `/api/productos/${avanzar.dataset.avanzar}/estado`, { estado: avanzar.dataset.estado }),
            `Producto #${avanzar.dataset.avanzar} pasó a ${avanzar.dataset.estado}`
        );
    }

    if (publicar) {
        ejecutar(() => api("POST", `/api/productos/${publicar.dataset.publicar}/publicar`), "Producto publicado para los transportistas");
    }

    if (retirar) {
        ejecutar(() => api("POST", `/api/productos/${retirar.dataset.retirar}/retirar-publicacion`), "Publicación retirada");
    }

    if (historial) verHistorial(historial.dataset.historial, historial.dataset.nombre);

    const destino = evento.target.closest("[data-elegir-destino]");
    const recepcion = evento.target.closest("[data-recepcion]");
    const qr = evento.target.closest("[data-qr]");

    if (destino) {
        const id = destino.dataset.elegirDestino;
        ejecutar(
            () => api("PUT", `/api/productos/${id}/destino`, { destino: $("destino-" + id).value }),
            "Destino elegido"
        );
    }

    if (recepcion) {
        const id = recepcion.dataset.recepcion;
        registrarRecepcion(id, $("temperatura-" + id).value, $("humedad-" + id).value);
    }

    if (qr) verQr(qr.dataset.qr, qr.dataset.nombre);
});

async function registrarRecepcion(id, temperatura, humedad) {

    if (temperatura === "") {
        avisar("Ingresa la temperatura medida", "error");
        return;
    }

    try {
        const evaluacion = await api("POST", `/api/productos/${id}/recepcion`, {
            temperatura: Number(temperatura),
            humedad: humedad === "" ? null : Number(humedad)
        });

        avisar(textoEvaluacion(evaluacion), evaluacion.aptoConsumo ? "exito" : "error");
        await actualizar();
    } catch (error) {
        avisar(error.message, "error");
    }
}

$("formulario-ubicacion").addEventListener("submit", (evento) => {
    evento.preventDefault();
    ejecutar(
        () => api("PUT", "/api/transportistas/yo/ubicacion", { ubicacion: $("ubicacion").value.trim() }),
        "Ubicación publicada"
    );
});

$("formulario-necesidades").addEventListener("submit", (evento) => {

    evento.preventDefault();

    const necesidades = [...document.querySelectorAll("#formulario-necesidades input:checked")].map(c => c.value);

    ejecutar(() => api("PUT", "/api/destinos/yo", { necesidades }), "Necesidades guardadas");
});

$("cerrar-qr").addEventListener("click", () => $("dialogo-qr").close());

$("lista-publicaciones").addEventListener("click", (evento) => {

    const aceptar = evento.target.closest("[data-aceptar]");

    if (aceptar) {
        ejecutar(() => api("POST", `/api/productos/${aceptar.dataset.aceptar}/aceptar`), "Producto aceptado: ahora estás OCUPADO");
    }
});

$("lista-usuarios").addEventListener("click", (evento) => {

    const aprobar = evento.target.closest("[data-aprobar]");

    if (aprobar) {
        ejecutar(() => api("POST", `/api/usuarios/${aprobar.dataset.aprobar}/aprobar`), `Cuenta ${aprobar.dataset.aprobar} aprobada`);
    }
});

$("cerrar-historial").addEventListener("click", () => $("dialogo-historial").close());


// ---------- Inicio ----------

mostrarCamposDeRegistro();
mostrarCamposDeProducto();

sesion = leerSesion();

if (sesion) {
    mostrarPanel().catch(mostrarLogin);
} else {
    mostrarLogin();
}
