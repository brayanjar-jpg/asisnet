package com.example.asisnet_contable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Dispatchers
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TregistroDashboard(
    rucEmpresa: String,
    listaTrabajadores: List<com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral>,
    onBackClick: () -> Unit,
    onTrabajadorAgregado: () -> Unit
) {
    val scopeParaCorrutinas = rememberCoroutineScope()
    val contexto = androidx.compose.ui.platform.LocalContext.current

    var mostrarModalAgregar by remember { mutableStateOf(false) }
    var mostrarModalDetalle by remember { mutableStateOf(false) }
    var mostrarSelectorFecha by remember { mutableStateOf(false) }

    var trabajadorSeleccionado by remember { mutableStateOf<com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral?>(null) }
    val datePickerState = rememberDatePickerState()

    var dni by remember { mutableStateOf("") }
    var nombres by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var cargo by remember { mutableStateOf("SECRETARIA") }
    var sueldoStr by remember { mutableStateOf("1025.00") }
    var contrato by remember { mutableStateOf("POR NECES DEL MERCADO") }

    var errorDni by remember { mutableStateOf<String?>(null) }
    var cargandoApi by remember { mutableStateOf(false) }
    // 👈 AGREGA ESTA LÍNEA EXACTA EN LA PARTE SUPERIOR DE VARIABLES (Línea 40-50 aprox)
    var regimenPensionarioSeleccionado by remember { mutableStateOf("ONP") }

    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var primerNombre by remember { mutableStateOf("") }
    var segundoNombre by remember { mutableStateOf("") }
    var activarBotSbs by remember { mutableStateOf(false) }




    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(end = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Registro de Trabajadores",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        floatingActionButton = {
            Box(modifier = Modifier.padding(bottom = 60.dp)) {
                FloatingActionButton(
                    onClick = { mostrarModalAgregar = true },
                    containerColor = Color(0xFF6750A4),
                    contentColor = Color.White
                ) {
                    Text("+", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                listaTrabajadores.forEach { empleado ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                trabajadorSeleccionado = empleado
                                mostrarModalDetalle = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // LADO IZQUIERDO: Información del Trabajador
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = empleado.nombresCompletos, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "DNI: ${empleado.dni}", color = Color.Gray, fontSize = 14.sp)
                                Text(text = "Fec. Nac.: ${empleado.fechaNacimiento}", color = Color.Gray, fontSize = 14.sp)
                                Text(text = "Cargo: ${empleado.ocupacion}", color = Color.Gray, fontSize = 14.sp)
                            }

                            // LADO DERECHO: Estado SUNAT + Botones de Acción (Lápiz y Tachito)
                            Column(
                                horizontalAlignment = Alignment.End,
                                verticalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier.padding(start = 8.dp)
                            ) {
                                // Etiqueta SUNAT
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE0F7FA)
                                ) {
                                    Text(
                                        text = empleado.estadoSunat,
                                        color = Color(0xFF006064),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }

                                // Contenedor de los Íconos interactivos
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // 1. BOTÓN DEL LÁPIZ (EDITAR)
                                    IconButton(
                                        onClick = {
                                            trabajadorSeleccionado = empleado
                                            dni = empleado.dni
                                            nombres = empleado.nombresCompletos
                                            fechaNacimiento = empleado.fechaNacimiento
                                            cargo = empleado.ocupacion
                                            sueldoStr = empleado.remuneracionBasica.toString()
                                            contrato = empleado.tipoContrato
                                            regimenPensionarioSeleccionado = empleado.regimenPensionario
                                            mostrarModalAgregar = true
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text(
                                            text = "✏️",
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )



                                    }

                                    // 2. BOTÓN DEL TACHITO (ELIMINAR)
                                    IconButton(
                                        onClick = {
                                            scopeParaCorrutinas.launch(kotlinx.coroutines.Dispatchers.IO) {
                                                try {
                                                    val exitoBorrado = com.example.asisnet_contable.PostgresDriver.eliminarTrabajador(rucEmpresa, empleado.dni)
                                                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                        if (exitoBorrado) {
                                                            onTrabajadorAgregado()
                                                            android.widget.Toast.makeText(contexto, "Trabajador eliminado con éxito", android.widget.Toast.LENGTH_SHORT).show()
                                                        } else {
                                                            android.widget.Toast.makeText(contexto, "No se pudo eliminar de la BD", android.widget.Toast.LENGTH_SHORT).show()
                                                        }
                                                    }
                                                } catch (e: Exception) {
                                                    withContext(kotlinx.coroutines.Dispatchers.Main) {
                                                        android.widget.Toast.makeText(contexto, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Text(
                                            text = "🗑️",
                                            fontSize = 20.sp,
                                            modifier = Modifier.padding(8.dp)
                                        )



                                    }
                                }
                            }
                        }

                    }
                }
            }

            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A3780)),
                modifier = Modifier.fillMaxWidth().padding(top = 16.dp),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Volver al Menú", color = Color.White, fontSize = 16.sp)
            }
        }
    }
    // ==========================================
    // 3. DIÁLOGO FORMULARIO AGREGAR TRABAJADOR (PARTE 1)
    // ==========================================
    // =====================================================================
    // BLOQUE 5: FORMULARIO DE REGISTRO CON AUDITORÍA VISUAL DEL BOT SBS
    // =====================================================================
    if (mostrarModalAgregar) {
        // Variables locales para controlar los menús desplegables de abajo
        var mostrarContratos by remember { mutableStateOf(false) }
        val opcionesContrato = listOf("POR NECES DEL MERCADO", "PLAZO INDETERMINADO", "INTERMITENTE", "TEMPORAL")

        var mostrarPensiones by remember { mutableStateOf(false) }
        val opcionesPension = listOf("ONP", "AFP INTEGRA", "AFP PRIMA", "AFP PROFUTURO", "AFP HABITAT")

        AlertDialog(
            onDismissRequest = { mostrarModalAgregar = false },
            confirmButton = {
                Button(
                    onClick = {
                        if (dni.trim().length != 8 || !dni.all { it.isDigit() }) {
                            errorDni = "El DNI debe tener exactamente 8 números"
                        } else {
                            errorDni = null
                            scopeParaCorrutinas.launch(Dispatchers.IO) {
                                try {
                                    val sueldoNumerico = sueldoStr.toDoubleOrNull() ?: 1025.00
                                    val nuevoEmpleado = com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral(
                                        dni = dni.trim(),
                                        nombresCompletos = nombres.trim(),
                                        fechaNacimiento = fechaNacimiento.trim().ifEmpty { "1990-01-01" },
                                        sexo = "MASCULINO", telefono = "-", correo = "-", primeraDireccion = "-", paisEmisor = "PERU", nacionalidad = "PERUANA", estadoCivil = "SOLTERO", categoria = "EMPLEADO",
                                        ocupacion = cargo.trim(),
                                        tipoContrato = contrato.trim(),
                                        fechaInicioLabores = "2026-01-01", estadoSunat = "Activo", regimenLaboral = "REGIMEN GENERAL",
                                        regimenPensionario = regimenPensionarioSeleccionado,
                                        cuspp = "-", regimenSalud = "ESSALUD", entidadPrestadora = "-", fechaInicioPension = "2026-01-01", fechaInicioSalud = "2026-01-01",
                                        remuneracionBasica = sueldoNumerico, tipoPeriodicidadPago = "MENSUAL", jornadaLaboral = "JORNADA TRABAJO TIEMPO COMPLETO"
                                    )
                                    val exito = com.example.asisnet_contable.PostgresDriver.insertarNuevoTrabajador(rucEmpresa, nuevoEmpleado)
                                    withContext(Dispatchers.Main) {
                                        if (exito) {
                                            onTrabajadorAgregado()
                                            mostrarModalAgregar = false
                                            dni = ""; nombres = ""; fechaNacimiento = ""; regimenPensionarioSeleccionado = "ONP"
                                        } else {
                                            android.widget.Toast.makeText(contexto, "Error al guardar en BD", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                } catch (e: Exception) {
                                    withContext(Dispatchers.Main) {
                                        android.widget.Toast.makeText(contexto, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                }
                            }
                        }
                    }
                ) { Text("Guardar") }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        // Data fija para auditar visualmente el comportamiento de la SBS
                        dni = "70384614"
                        apellidoPaterno = "QUISPE"
                        apellidoMaterno = "SUAREZ"
                        primerNombre = "BRAYAN"


                        // Encendemos el bot para que empiece a cargar arriba en el formulario
                        activarBotSbs = true
                    }
                ) {
                    Text("Probar Bot SBS")
                }
            },

            title = { Text("Registrar Trabajador", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 👈 1. EL BOT SE DIBUJA AQUÍ: Es lo primero dentro de la Column del formulario
                    // Invocación del Bot Scraper de forma segura dentro de la jerarquía
                    if (activarBotSbs && dni.length == 8) {
                        BotScraperSBS(
                            dni = dni,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            primerNombre = primerNombre,
                            onResultadoEncontrado = { afpAsignada ->
                                regimenPensionarioSeleccionado = afpAsignada
                                activarBotSbs = false
                                android.widget.Toast.makeText(contexto, "Sincronizado: $afpAsignada", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            onDismissRequest = { activarBotSbs = false }
                        )
                    }


                    // 👈 2. AQUÍ CONECTAMOS EL CAMPO DNI INMEDIATAMENTE DEBAJO DEL BOT
                    OutlinedTextField(
                        value = dni,
                        onValueChange = { input ->
                            if (input.all { it.isDigit() } && input.length <= 8) {
                                dni = input
                                errorDni = null
                                if (input.length == 8 && !cargandoApi) {
                                    cargandoApi = true
                                    scopeParaCorrutinas.launch(Dispatchers.IO) {
                                        try {
                                            val datosApi = com.example.asisnet_contable.PostgresDriver.consultarDniApiPeruDesglosado(input)
                                            withContext(Dispatchers.Main) {
                                                if (datosApi != null) {
                                                    nombres = datosApi["completo"] ?: ""
                                                    apellidoPaterno = datosApi["paterno"] ?: ""
                                                    apellidoMaterno = datosApi["materno"] ?: ""
                                                    primerNombre = datosApi["nombres"] ?: ""
                                                    activarBotSbs = true
                                                }
                                            }
                                        } catch (e: Exception) {}
                                        finally { withContext(Dispatchers.Main) { cargandoApi = false } }
                                    }
                                }
                            }
                        },
                        label = { Text(if (cargandoApi) "Buscando en API Perú..." else "DNI") },
                        isError = errorDni != null,
                        supportingText = { errorDni?.let { Text(it, color = MaterialTheme.colorScheme.error) } },
                        keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres Completos") }, modifier = Modifier.fillMaxWidth())

                    Box(modifier = Modifier.fillMaxWidth().clickable { mostrarSelectorFecha = true }) {
                        OutlinedTextField(
                            value = fechaNacimiento, onValueChange = {}, label = { Text("Fecha Nacimiento (AAAA-MM-DD)") }, placeholder = { Text("Selecciona una fecha...") },
                            readOnly = true, enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant, disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    OutlinedTextField(value = cargo, onValueChange = { cargo = it }, label = { Text("Cargo u Ocupación") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = sueldoStr, onValueChange = { sueldoStr = it }, label = { Text("Sueldo Básico (S/)") }, modifier = Modifier.fillMaxWidth())

                    // Selector de Contratos nativo
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = contrato, onValueChange = {}, label = { Text("Tipo Contrato") }, readOnly = true, enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.fillMaxWidth().clickable { mostrarContratos = true }
                        )
                        DropdownMenu(expanded = mostrarContratos, onDismissRequest = { mostrarContratos = false }) {
                            opcionesContrato.forEach { opcion ->
                                DropdownMenuItem(text = { Text(opcion) }, onClick = { contrato = opcion; mostrarContratos = false })
                            }
                        }
                    }

                    // Selector de Régimen Pensionario (AFP / ONP)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = regimenPensionarioSeleccionado, onValueChange = {}, label = { Text("Régimen Pensionario") }, readOnly = true, enabled = false,
                            colors = OutlinedTextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface, disabledBorderColor = MaterialTheme.colorScheme.outline, disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant),
                            modifier = Modifier.fillMaxWidth().clickable { mostrarPensiones = true }
                        )
                        DropdownMenu(expanded = mostrarPensiones, onDismissRequest = { mostrarPensiones = false }) {
                            opcionesPension.forEach { opcion ->
                                DropdownMenuItem(text = { Text(opcion) }, onClick = { regimenPensionarioSeleccionado = opcion; mostrarPensiones = false })
                            }
                        }
                    }

                    // Invocación del Bot Scraper de forma segura dentro de la jerarquía
                    if (activarBotSbs && dni.length == 8) {
                        BotScraperSBS(
                            dni = dni,
                            apellidoPaterno = apellidoPaterno,
                            apellidoMaterno = apellidoMaterno,
                            primerNombre = primerNombre,
                            onResultadoEncontrado = { afpAsignada ->
                                regimenPensionarioSeleccionado = afpAsignada
                                activarBotSbs = false
                                android.widget.Toast.makeText(contexto, "Sincronizado: $afpAsignada", android.widget.Toast.LENGTH_SHORT).show()
                            },
                            onDismissRequest = { activarBotSbs = false }
                        )
                    }

                    // Tus campos normales van abajo...
                    OutlinedTextField(value = dni, onValueChange = { /* ... */ }, label = { Text("DNI") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = nombres, onValueChange = { nombres = it }, label = { Text("Nombres Completos") }, modifier = Modifier.fillMaxWidth())

                }
            }
        )
    }

    // =====================================================================
    // DETALLE DEL TRABAJADOR: FORMATO PREMIUM ESTRUCTURADO (RESTAURADO)
    // =====================================================================
    if (mostrarModalDetalle && trabajadorSeleccionado != null) {
        AlertDialog(
            onDismissRequest = { mostrarModalDetalle = false },
            confirmButton = {},
            dismissButton = {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Button(
                        onClick = { mostrarModalDetalle = false },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4)),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Cerrar", color = Color.White, fontWeight = FontWeight.Medium)
                    }
                }
            },
            title = {
                Text(
                    text = trabajadorSeleccionado!!.nombresCompletos.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Sección Datos de Contacto Básicos
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text("Correo electrónico:", color = Color.Gray, fontSize = 13.sp)
                        Text(text = trabajadorSeleccionado!!.correo.ifBlank { "-" }, color = Color.DarkGray, fontSize = 14.sp)

                        Spacer(modifier = Modifier.height(6.dp))

                        Text("Primera dirección:", color = Color.Gray, fontSize = 13.sp)
                        Text(text = trabajadorSeleccionado!!.primeraDireccion.ifBlank { "-" }.uppercase(), color = Color.DarkGray, fontSize = 14.sp)
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // 💼 Sección Datos Laborales
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💼 ", fontSize = 16.sp)
                        Text("Datos laborales", color = Color(0xFF4A3780), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row {
                            Text("Régimen laboral: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.regimenLaboral.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row {
                            Text("Categoría ocupacional: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.categoria.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row {
                            Text("Ocupación: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.ocupacion.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row {
                            Text("Tipo de contrato: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.tipoContrato.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row {
                            Text("Régimen pensionario: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.regimenPensionario.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)

                    // 🏥 Sección Aseguramiento de Salud
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("🏥 ", fontSize = 16.sp)
                        Text("Régimen de aseguramiento de salud", color = Color(0xFF4A3780), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row {
                            Text("Régimen de salud: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.regimenSalud.uppercase(), color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                        Row {
                            Text("Fecha de inicio: ", color = Color.Gray, fontSize = 14.sp)
                            Text(trabajadorSeleccionado!!.fechaInicioSalud, color = Color.DarkGray, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }





    if (mostrarSelectorFecha) {
        DatePickerDialog(
            onDismissRequest = { mostrarSelectorFecha = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val milisegundosSeleccionados = datePickerState.selectedDateMillis
                        if (milisegundosSeleccionados != null) {
                            val formatoIsof = SimpleDateFormat("yyyy-MM-dd", Locale.US)
                            formatoIsof.timeZone = TimeZone.getTimeZone("UTC")
                            fechaNacimiento = formatoIsof.format(Date(milisegundosSeleccionados))
                        }
                        mostrarSelectorFecha = false
                    }
                ) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarSelectorFecha = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BotScraperSBS(
    dni: String,
    apellidoPaterno: String,
    apellidoMaterno: String,
    primerNombre: String,
    onResultadoEncontrado: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    androidx.compose.ui.viewinterop.AndroidView(
        factory = { ctx ->
            android.webkit.WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.mixedContentMode = android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                settings.javaScriptCanOpenWindowsAutomatically = true

                // Forzamos al WebView a ignorar el modo oscuro del celular para que aclare la web
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                    settings.forceDark = android.webkit.WebSettings.FORCE_DARK_OFF
                }

                // Habilitamos el gestor de cookies para mantener viva la sesión de la SBS
                val cookieManager = android.webkit.CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.setAcceptThirdPartyCookies(this, true)

                settings.userAgentString = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

                addJavascriptInterface(object {
                    @android.webkit.JavascriptInterface
                    fun enviarAfp(htmlCuerpo: String) {
                        val afpDetectada = when {
                            htmlCuerpo.contains("PRIMA", ignoreCase = true) -> "AFP PRIMA"
                            htmlCuerpo.contains("INTEGRA", ignoreCase = true) -> "AFP INTEGRA"
                            htmlCuerpo.contains("PROFUTURO", ignoreCase = true) -> "AFP PROFUTURO"
                            htmlCuerpo.contains("HABITAT", ignoreCase = true) -> "AFP HABITAT"
                            else -> "ONP"
                        }
                        post { onResultadoEncontrado(afpDetectada) }
                    }
                }, "AndroidBot")

                webViewClient = object : android.webkit.WebViewClient() {
                    var ejecucionAutomatica = true


                    override fun onReceivedSslError(
                        view: android.webkit.WebView?,
                        handler: android.webkit.SslErrorHandler?,
                        error: android.net.http.SslError?
                    ) {
                        handler?.proceed()
                    }
                    override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                        super.onPageFinished(view, url)

                        if (ejecucionAutomatica) {
                            ejecucionAutomatica = false

                            // 👈 LE DAMOS 1.5 SEGUNDOS DE ESPERA PARA QUE LA PÁGINA ACTIVE SUS CAMPOS
                            postDelayed({
                                val scriptBot = """
                                    (function() {
                                        // 1. Selector del tipo de documento (DNI = '00')
                                        var comboDoc = document.getElementById('ctl00_ContentPlaceHolder1_cboTipoDoc');
                                        if(comboDoc) comboDoc.value = '00';
                                        
                                        // 2. Inyectar el número de DNI
                                        var inputDni = document.getElementById('ctl00_ContentPlaceHolder1_txtNumeroDoc');
                                        if(inputDni) inputDni.value = '$dni';
                                        
                                        // 3. Inyectar Apellido Paterno (ID corregido por Brayan)
                                        var inputPat = document.getElementById('ctl00_ContentPlaceHolder1_txtAp_pat');
                                        if(inputPat) inputPat.value = '$apellidoPaterno';
                                        
                                        // 4. Inyectar Apellido Materno (ID corregido)
                                        var inputMat = document.getElementById('ctl00_ContentPlaceHolder1_txtAp_mat');
                                        if(inputMat) inputMat.value = '$apellidoMaterno';
                                        
                                        // 5. Inyectar Primer Nombre (ID corregido)
                                        var inputNom = document.getElementById('ctl00_ContentPlaceHolder1_txtPri_nom');
                                        if(inputNom) inputNom.value = '$primerNombre';
                                        
                                        // 6. Simular el clic en el botón Buscar oficial
                                        var btnBuscar = document.getElementById('ctl00_ContentPlaceHolder1_btnBuscar');
                                        if(btnBuscar) btnBuscar.click();
                                    })();
                                """.trimIndent()

                                evaluateJavascript(scriptBot, null)
                            }, 1500) // 1500 milisegundos de delay estratégico

                        } else {
                            // FASE 2: LA PÁGINA YA PROCESÓ LA BÚSQUEDA, LEEMOS EL RESULTADO DE LA GRILLA
                            postDelayed({
                                val scriptLectura = "window.AndroidBot.enviarAfp(document.body.innerText);"
                                evaluateJavascript(scriptLectura, null)
                            }, 1000)
                        }
                    }

                }
                loadUrl("https://servicios.sbs.gob.pe/ReporteSituacionPrevisional/Afil_Consulta.aspx")
            }
        },
        modifier = Modifier
            .size(1.dp)
            .padding(0.dp)

    )
}



