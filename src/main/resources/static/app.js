// Aquí guardamos los datos que cargamos del servidor
let clientes = [];
let animales = [];
let consultas = [];
let tratamientos = [];


// Muestra una sección y oculta las demás
function mostrarSeccion(id, enlace) {
    document.querySelectorAll('.seccion').forEach(s => s.classList.remove('activa'));
    document.querySelectorAll('#sidebar a').forEach(a => a.classList.remove('activo'));
    document.getElementById(id).classList.add('activa');
    enlace.classList.add('activo');

    if (id === 'dashboard') cargarDashboard();
    if (id === 'clientes') cargarClientes();
    if (id === 'animales') cargarAnimales();
    if (id === 'consultas') cargarConsultas();
    if (id === 'tratamientos') cargarTratamientos();
}


// Muestra un mensaje verde o rojo durante 3 segundos
function mostrarMensaje(texto, color) {
    let div = document.getElementById('mensaje');
    div.textContent = texto;
    div.className = 'alert alert-' + color;
    div.style.display = 'block';
    setTimeout(function () {
        div.style.display = 'none';
    }, 3000);
}


// DASHBOARD

async function cargarDashboard() {
    clientes = await fetch('/clientes').then(r => r.json());
    animales = await fetch('/animales').then(r => r.json());
    consultas = await fetch('/consultas').then(r => r.json());
    tratamientos = await fetch('/tratamientos').then(r => r.json());

    document.getElementById('stat-clientes').textContent = clientes.length;
    document.getElementById('stat-animales').textContent = animales.length;
    document.getElementById('stat-consultas').textContent = consultas.length;
    document.getElementById('stat-tratamientos').textContent = tratamientos.length;
}


// CLIENTES

async function cargarClientes() {
    clientes = await fetch('/clientes').then(r => r.json());

    let filas = '';
    for (let c of clientes) {
        filas += '<tr>' +
            '<td>' + c.id + '</td>' +
            '<td>' + c.nombre + '</td>' +
            '<td>' + c.telefono + '</td>' +
            '<td>' + c.email + '</td>' +
            '<td>' +
            '<button class="btn btn-warning btn-sm" onclick="editarCliente(' + c.id + ')">Editar</button> ' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarCliente(' + c.id + ')">Eliminar</button>' +
            '</td>' +
            '</tr>';
    }
    document.getElementById('tabla-clientes').innerHTML = filas;
}

function abrirModalCliente() {
    document.getElementById('titulo-cliente').textContent = 'Nuevo cliente';
    document.getElementById('cliente-id').value = '';
    document.getElementById('cliente-nombre').value = '';
    document.getElementById('cliente-telefono').value = '';
    document.getElementById('cliente-email').value = '';
    new bootstrap.Modal(document.getElementById('modal-cliente')).show();
}

function editarCliente(id) {
    let c = clientes.find(function (x) {
        return x.id === id;
    });
    document.getElementById('titulo-cliente').textContent = 'Editar cliente';
    document.getElementById('cliente-id').value = c.id;
    document.getElementById('cliente-nombre').value = c.nombre;
    document.getElementById('cliente-telefono').value = c.telefono;
    document.getElementById('cliente-email').value = c.email;
    new bootstrap.Modal(document.getElementById('modal-cliente')).show();
}

async function guardarCliente() {
    let id = document.getElementById('cliente-id').value;
    let datos = {
        id: id ? parseInt(id) : null,
        nombre: document.getElementById('cliente-nombre').value,
        telefono: document.getElementById('cliente-telefono').value,
        email: document.getElementById('cliente-email').value
    };

    // si tiene id modificamos, si no insertamos
    let metodo = id ? 'PUT' : 'POST';

    let ok = await fetch('/clientes', {
        method: metodo,
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(datos)
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Cliente guardado', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modal-cliente')).hide();
        cargarClientes();
    } else {
        mostrarMensaje('Error al guardar', 'danger');
    }
}

async function eliminarCliente(id) {
    if (!confirm('¿Eliminar este cliente?')) return;
    let ok = await fetch('/clientes/' + id, {method: 'DELETE'}).then(r => r.json());
    if (ok) {
        mostrarMensaje('Cliente eliminado', 'success');
        cargarClientes();
    } else {
        mostrarMensaje('Error al eliminar', 'danger');
    }
}


// ANIMALES

async function cargarAnimales() {
    if (clientes.length === 0) await cargarClientes();
    animales = await fetch('/animales').then(r => r.json());

    let filas = '';
    for (let a of animales) {
        let propietario = clientes.find(function (c) {
            return c.id === a.id_cliente;
        });
        let nombrePropietario = propietario ? propietario.nombre : '-';

        filas += '<tr>' +
            '<td><img src="/animales/' + a.id + '/imagen" class="foto-animal" ' +
            'onerror="this.outerHTML=\'<span style=font-size:24px>🐾</span>\'"></td>' +
            '<td>' + a.id + '</td>' +
            '<td>' + a.nombre + '</td>' +
            '<td>' + a.especie + '</td>' +
            '<td>' + a.raza + '</td>' +
            '<td>' + a.edad + ' años</td>' +
            '<td>' + a.peso + ' kg</td>' +
            '<td>' + nombrePropietario + '</td>' +
            '<td>' +
            '<button class="btn btn-warning btn-sm" onclick="editarAnimal(' + a.id + ')">Editar</button> ' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarAnimal(' + a.id + ')">Eliminar</button>' +
            '</td>' +
            '</tr>';
    }
    document.getElementById('tabla-animales').innerHTML = filas;
}

// muestra la foto antes de guardarla
function mostrarPreview(input) {
    let lector = new FileReader();
    lector.onload = function (e) {
        let preview = document.getElementById('preview-imagen');
        preview.src = e.target.result;
        preview.style.display = 'block';
    };
    lector.readAsDataURL(input.files[0]);
}

async function abrirModalAnimal() {
    if (clientes.length === 0) await cargarClientes();
    document.getElementById('titulo-animal').textContent = 'Nuevo animal';
    document.getElementById('animal-id').value = '';
    document.getElementById('animal-nombre').value = '';
    document.getElementById('animal-especie').value = '';
    document.getElementById('animal-raza').value = '';
    document.getElementById('animal-edad').value = '';
    document.getElementById('animal-peso').value = '';
    document.getElementById('animal-imagen').value = '';
    document.getElementById('preview-imagen').style.display = 'none';

    let opciones = '';
    for (let c of clientes) {
        opciones += '<option value="' + c.id + '">' + c.nombre + '</option>';
    }
    document.getElementById('animal-cliente').innerHTML = opciones;
    new bootstrap.Modal(document.getElementById('modal-animal')).show();
}

async function editarAnimal(id) {
    if (clientes.length === 0) await cargarClientes();
    let a = animales.find(function (x) {
        return x.id === id;
    });

    document.getElementById('titulo-animal').textContent = 'Editar animal';
    document.getElementById('animal-id').value = a.id;
    document.getElementById('animal-nombre').value = a.nombre;
    document.getElementById('animal-especie').value = a.especie;
    document.getElementById('animal-raza').value = a.raza;
    document.getElementById('animal-edad').value = a.edad;
    document.getElementById('animal-peso').value = a.peso;
    document.getElementById('animal-imagen').value = '';

    let preview = document.getElementById('preview-imagen');
    preview.src = '/animales/' + a.id + '/imagen';
    preview.style.display = 'block';
    preview.onerror = function () {
        preview.style.display = 'none';
    };

    let opciones = '';
    for (let c of clientes) {
        let sel = c.id === a.id_cliente ? 'selected' : '';
        opciones += '<option value="' + c.id + '" ' + sel + '>' + c.nombre + '</option>';
    }
    document.getElementById('animal-cliente').innerHTML = opciones;
    new bootstrap.Modal(document.getElementById('modal-animal')).show();
}

async function guardarAnimal() {
    let id = document.getElementById('animal-id').value;

    // usamos FormData porque hay que enviar la imagen como archivo
    let fd = new FormData();
    if (id) fd.append('id', id);
    fd.append('nombre', document.getElementById('animal-nombre').value);
    fd.append('especie', document.getElementById('animal-especie').value);
    fd.append('raza', document.getElementById('animal-raza').value);
    fd.append('edad', document.getElementById('animal-edad').value);
    fd.append('peso', document.getElementById('animal-peso').value);
    fd.append('id_cliente', document.getElementById('animal-cliente').value);

    let imagenFile = document.getElementById('animal-imagen').files[0];
    if (imagenFile) fd.append('imagen', imagenFile);

    let ok = await fetch('/animales', {
        method: id ? 'PUT' : 'POST',
        body: fd
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Animal guardado', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modal-animal')).hide();
        cargarAnimales();
    } else {
        mostrarMensaje('Error al guardar', 'danger');
    }
}

async function eliminarAnimal(id) {
    if (!confirm('¿Eliminar este animal?')) return;
    let ok = await fetch('/animales/' + id, {method: 'DELETE'}).then(r => r.json());
    if (ok) {
        mostrarMensaje('Animal eliminado', 'success');
        cargarAnimales();
    } else {
        mostrarMensaje('Error al eliminar', 'danger');
    }
}


// CONSULTAS

async function cargarConsultas() {
    if (animales.length === 0) await cargarAnimales();
    consultas = await fetch('/consultas').then(r => r.json());

    let filas = '';
    for (let c of consultas) {
        let animal = animales.find(function (a) {
            return a.id === c.id_animal;
        });
        let nombreAnimal = animal ? animal.nombre : '-';

        filas += '<tr>' +
            '<td>' + c.id + '</td>' +
            '<td>' + c.fecha + '</td>' +
            '<td>' + c.motivo + '</td>' +
            '<td>' + c.precio + ' €</td>' +
            '<td>' + nombreAnimal + '</td>' +
            '<td><button class="btn btn-info btn-sm" onclick="abrirModalTratamientosConsulta(' + c.id + ')">Tratamientos</button></td>' +
            '<td>' +
            '<button class="btn btn-warning btn-sm" onclick="editarConsulta(' + c.id + ')">Editar</button> ' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarConsulta(' + c.id + ')">Eliminar</button>' +
            '</td>' +
            '</tr>';
    }
    document.getElementById('tabla-consultas').innerHTML = filas;
}

function abrirModalConsulta() {
    document.getElementById('titulo-consulta').textContent = 'Nueva consulta';
    document.getElementById('consulta-id').value = '';
    document.getElementById('consulta-fecha').value = new Date().toISOString().split('T')[0];
    document.getElementById('consulta-motivo').value = '';
    document.getElementById('consulta-precio').value = '';

    let opciones = '';
    for (let a of animales) {
        opciones += '<option value="' + a.id + '">' + a.nombre + ' (' + a.especie + ')</option>';
    }
    document.getElementById('consulta-animal').innerHTML = opciones;
    new bootstrap.Modal(document.getElementById('modal-consulta')).show();
}

function editarConsulta(id) {
    let c = consultas.find(function (x) {
        return x.id === id;
    });
    document.getElementById('titulo-consulta').textContent = 'Editar consulta';
    document.getElementById('consulta-id').value = c.id;
    document.getElementById('consulta-fecha').value = c.fecha;
    document.getElementById('consulta-motivo').value = c.motivo;
    document.getElementById('consulta-precio').value = c.precio;

    let opciones = '';
    for (let a of animales) {
        let sel = a.id === c.id_animal ? 'selected' : '';
        opciones += '<option value="' + a.id + '" ' + sel + '>' + a.nombre + ' (' + a.especie + ')</option>';
    }
    document.getElementById('consulta-animal').innerHTML = opciones;
    new bootstrap.Modal(document.getElementById('modal-consulta')).show();
}

async function guardarConsulta() {
    let id = document.getElementById('consulta-id').value;
    let datos = {
        id: id ? parseInt(id) : null,
        fecha: document.getElementById('consulta-fecha').value,
        motivo: document.getElementById('consulta-motivo').value,
        precio: parseFloat(document.getElementById('consulta-precio').value),
        id_animal: parseInt(document.getElementById('consulta-animal').value)
    };

    let ok = await fetch('/consultas', {
        method: id ? 'PUT' : 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(datos)
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Consulta guardada', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modal-consulta')).hide();
        cargarConsultas();
    } else {
        mostrarMensaje('Error al guardar', 'danger');
    }
}

async function eliminarConsulta(id) {
    if (!confirm('¿Eliminar esta consulta?')) return;
    let ok = await fetch('/consultas/' + id, {method: 'DELETE'}).then(r => r.json());
    if (ok) {
        mostrarMensaje('Consulta eliminada', 'success');
        cargarConsultas();
    } else {
        mostrarMensaje('Error al eliminar', 'danger');
    }
}


// TRATAMIENTOS

async function cargarTratamientos() {
    tratamientos = await fetch('/tratamientos').then(r => r.json());

    let filas = '';
    for (let t of tratamientos) {
        filas += '<tr>' +
            '<td>' + t.id + '</td>' +
            '<td>' + t.nombre + '</td>' +
            '<td>' + t.descripcion + '</td>' +
            '<td>' + t.duracion_dias + ' días</td>' +
            '<td>' + t.precio + ' €</td>' +
            '<td>' +
            '<button class="btn btn-warning btn-sm" onclick="editarTratamiento(' + t.id + ')">Editar</button> ' +
            '<button class="btn btn-danger btn-sm" onclick="eliminarTratamiento(' + t.id + ')">Eliminar</button>' +
            '</td>' +
            '</tr>';
    }
    document.getElementById('tabla-tratamientos').innerHTML = filas;
}

function abrirModalTratamiento() {
    document.getElementById('titulo-tratamiento').textContent = 'Nuevo tratamiento';
    document.getElementById('tratamiento-id').value = '';
    document.getElementById('tratamiento-nombre').value = '';
    document.getElementById('tratamiento-descripcion').value = '';
    document.getElementById('tratamiento-duracion').value = '';
    document.getElementById('tratamiento-precio').value = '';
    new bootstrap.Modal(document.getElementById('modal-tratamiento')).show();
}

function editarTratamiento(id) {
    let t = tratamientos.find(function (x) {
        return x.id === id;
    });
    document.getElementById('titulo-tratamiento').textContent = 'Editar tratamiento';
    document.getElementById('tratamiento-id').value = t.id;
    document.getElementById('tratamiento-nombre').value = t.nombre;
    document.getElementById('tratamiento-descripcion').value = t.descripcion;
    document.getElementById('tratamiento-duracion').value = t.duracion_dias;
    document.getElementById('tratamiento-precio').value = t.precio;
    new bootstrap.Modal(document.getElementById('modal-tratamiento')).show();
}

async function guardarTratamiento() {
    let id = document.getElementById('tratamiento-id').value;
    let datos = {
        id: id ? parseInt(id) : null,
        nombre: document.getElementById('tratamiento-nombre').value,
        descripcion: document.getElementById('tratamiento-descripcion').value,
        duracion_dias: parseInt(document.getElementById('tratamiento-duracion').value),
        precio: parseFloat(document.getElementById('tratamiento-precio').value)
    };

    let ok = await fetch('/tratamientos', {
        method: id ? 'PUT' : 'POST',
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify(datos)
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Tratamiento guardado', 'success');
        bootstrap.Modal.getInstance(document.getElementById('modal-tratamiento')).hide();
        cargarTratamientos();
    } else {
        mostrarMensaje('Error al guardar', 'danger');
    }
}

async function eliminarTratamiento(id) {
    if (!confirm('¿Eliminar este tratamiento?')) return;
    let ok = await fetch('/tratamientos/' + id, {method: 'DELETE'}).then(r => r.json());
    if (ok) {
        mostrarMensaje('Tratamiento eliminado', 'success');
        cargarTratamientos();
    } else {
        mostrarMensaje('Error al eliminar', 'danger');
    }
}


// CSV

async function importarCsv(tipo, input) {
    let fd = new FormData();
    fd.append('file', input.files[0]);
    let respuesta = await fetch('/csv/' + tipo + '/importar', {method: 'POST', body: fd});
    let texto = await respuesta.text();
    mostrarMensaje(texto, respuesta.ok ? 'success' : 'danger');
    if (tipo === 'clientes') cargarClientes();
    else cargarAnimales();
    input.value = '';
}

function exportarCsv(tipo) {
    window.location.href = '/csv/' + tipo + '/exportar';
}


// al abrir la pagina cargamos todo
window.addEventListener('DOMContentLoaded', function () {
    cargarDashboard();
    cargarClientes();
    cargarAnimales();
});


// TRATAMIENTOS DE UNA CONSULTA

let consultaSeleccionada = null;

async function abrirModalTratamientosConsulta(idConsulta) {
    consultaSeleccionada = idConsulta;
    if (tratamientos.length === 0) await cargarTratamientos();
    await cargarTratamientosDeConsulta();

    let opciones = '';
    for (let t of tratamientos) {
        opciones += '<option value="' + t.id + '">' + t.nombre + '</option>';
    }
    document.getElementById('select-tratamiento-asignar').innerHTML = opciones;

    new bootstrap.Modal(document.getElementById('modal-tratamientos-consulta')).show();
}

async function cargarTratamientosDeConsulta() {
    let lista = await fetch('/consulta-tratamiento/' + consultaSeleccionada).then(r => r.json());
    let html = '';

    if (lista.length === 0) {
        html = '<tr><td colspan="4" class="text-center text-muted">Sin tratamientos asignados</td></tr>';
    } else {
        for (let t of lista) {
            html += '<tr>' +
                '<td>' + t.nombre + '</td>' +
                '<td>' + t.duracion_dias + ' días</td>' +
                '<td>' + t.precio + ' €</td>' +
                '<td><button class="btn btn-danger btn-sm" onclick="quitarTratamiento(' + t.id + ')">Quitar</button></td>' +
                '</tr>';
        }
    }
    document.getElementById('tabla-tratamientos-consulta').innerHTML = html;
}

async function asignarTratamiento() {
    let idTratamiento = document.getElementById('select-tratamiento-asignar').value;
    let ok = await fetch('/consulta-tratamiento/' + consultaSeleccionada + '/' + idTratamiento, {
        method: 'POST'
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Tratamiento asignado', 'success');
        cargarTratamientosDeConsulta();
    } else {
        mostrarMensaje('Error al asignar', 'danger');
    }
}

async function quitarTratamiento(idTratamiento) {
    if (!confirm('¿Quitar este tratamiento de la consulta?')) return;
    let ok = await fetch('/consulta-tratamiento/' + consultaSeleccionada + '/' + idTratamiento, {
        method: 'DELETE'
    }).then(r => r.json());

    if (ok) {
        mostrarMensaje('Tratamiento quitado', 'success');
        cargarTratamientosDeConsulta();
    } else {
        mostrarMensaje('Error al quitar', 'danger');
    }
}
