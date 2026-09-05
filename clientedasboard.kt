package com.example.asisnet_contable
import com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

var diagnosticoCelular = ""

// Modelo de datos expandido para el Buzón SOL
data class NotificacionSunat(
    val asunto: String,
    val fechaHora: String,
    val estado: String,
    val numResolucion: String,
    val etiqueta: String,
    val leidoApp: Boolean,
    val contenidoCuerpo: String,
    val urlPdfCabecera: String,
    val urlPdfCuerpo: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClienteDashboard(rucUsuario: String, onLogout: () -> Unit) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var descargando by rememberSaveable { mutableStateOf(false) }
    var verBuzonNativo by rememberSaveable { mutableStateOf(false) }
    var cargandoBuzon by rememberSaveable { mutableStateOf(false) }
    // 🔑 VARIABLES DE ESTADO PARA EL MÓDULO DE T-REGISTRO
    var verTregistroNativo by rememberSaveable { mutableStateOf(false) }
    var cargandoTregistro by rememberSaveable { mutableStateOf(false) }
    var listaTrabajadores by remember { mutableStateOf<List<EmpleadoLaboral>>(emptyList()) }

    // --- BANDERAS DE CONTROL Y LISTAS MUTABLES FUERTEMENTE TIPADAS ---
    var verDdjjNativo by rememberSaveable { mutableStateOf(false) }
    var cargandoDdjj by rememberSaveable { mutableStateOf(false) }
    val listaDdjj = remember { mutableStateListOf<ObligacionSunat>() }

    // Banderas de control independientes para el aislamiento modular de la AFP
    var verAfpNativo by rememberSaveable { mutableStateOf(false) }
    var cargandoAfp by rememberSaveable { mutableStateOf(false) }
    val listaAfp = remember { mutableStateListOf<PlanillaAfpEntidad>() }

    var nombreCliente by rememberSaveable { mutableStateOf("Cargando...") }

    // Hilo asíncrono inicial para capturar la Razón Social desde Supabase
    LaunchedEffect(rucUsuario) {
        coroutineScope.launch {
            val detalles = PostgresDriver.obtenerDetallesCliente(rucUsuario)
            nombreCliente = detalles?.nombre ?: "Cliente No Registrado"
        }
    }

    // Guardador físico del listSaver del Buzón SOL (Corregido y libre de avisos de Cast)
    val listaNotificaciones = rememberSaveable(
        saver = androidx.compose.runtime.saveable.listSaver(
            save = { lista ->
                lista.map { n ->
                    listOf(
                        n.asunto, n.fechaHora, n.estado, n.numResolucion, n.etiqueta,
                        n.leidoApp.toString(), n.contenidoCuerpo, n.urlPdfCabecera, n.urlPdfCuerpo
                    )
                }
            },
            restore = { guardado ->
                val restaurada = mutableStateListOf<NotificacionSunat>()
                (guardado as? List<*>)?.forEach { item ->
                    val d = item as? List<*>
                    if (d != null && d.size >= 9) {
                        restaurada.add(
                            NotificacionSunat(
                                asunto = d[0] as String, fechaHora = d[1] as String, estado = d[2] as String,
                                numResolucion = d[3] as String, etiqueta = d[4] as String, leidoApp = (d[5] as String).toBoolean(),
                                contenidoCuerpo = d[6] as String, urlPdfCabecera = d[7] as String, urlPdfCuerpo = d[8] as String
                            )
                        )
                    }
                }
                restaurada
            }
        )
    ) { mutableStateListOf<NotificacionSunat>() }
    // =====================================================================
    // 🧭 ENRUTADOR REACTIVO MULTI-MODULAR DE PANTALLAS
    // =====================================================================
    if (verBuzonNativo) {
        BuzonDashboard(
            rucUsuario = rucUsuario,
            listaNotificaciones = listaNotificaciones,
            cargandoBuzon = cargandoBuzon,
            onNotifLeida = { notif ->
                val index = listaNotificaciones.indexOf(notif)
                if (index != -1) {
                    listaNotificaciones[index] = notif.copy(leidoApp = true)
                }
            },
            onVolver = { verBuzonNativo = false }
        )
    }
    else if (verDdjjNativo) {
        DdjjDashboard(
            rucUsuario = rucUsuario,
            listaDdjj = listaDdjj,
            cargandoDdjj = cargandoDdjj,
            onVolver = { verDdjjNativo = false }
        )
    }
    else if (verTregistroNativo) {
        TregistroDashboard(
            listaTrabajadores = listaTrabajadores, // 🚀 Aquí le pasamos la variable que ya llenó Supabase
            onBackClick = {
                verTregistroNativo = false
            }
        )
    }
    else if (verAfpNativo) {
        // 🚀 CONEXIÓN ATÓMICA: Invoca tu nueva pantalla modular pasándole los datos de la AFP
        AfpDashboard(
            rucUsuario = rucUsuario,
            listaPlanillasAfp = listaAfp,
            cargandoAfp = cargandoAfp,
            onVolver = { verAfpNativo = false }
        )
    }
    else {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido, $nombreCliente",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D1B20),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(text = "RUC: ", fontSize = 14.sp, color = Color.Gray)
                Text(text = rucUsuario, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                Spacer(modifier = Modifier.width(16.dp))
                Text(text = "Domicilio: ", fontSize = 14.sp, color = Color.Gray)
                Text(text = "Habido", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00B27E))
            }

            Spacer(modifier = Modifier.weight(1f))

            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth().wrapContentHeight().padding(horizontal = 4.dp)
            ) {
                // 📄 CARD: FICHA RUC
                item {
                    Card(
                        onClick = {
                            descargando = true
                            diagnosticoCelular = "Conectando..."
                            coroutineScope.launch {
                                val exito = descargarFichaRucPrivada(context, rucUsuario)
                                descargando = false
                                if (exito) {
                                    Toast.makeText(context, "¡Ficha RUC descargada con éxito!", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, diagnosticoCelular, Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        enabled = !descargando,
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "📄", fontSize = 32.sp)
                            Text(text = "Ficha RUC", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                    }
                }

                // 📊 CARD: REPORTES TRIBUTARIOS
                item {
                    Card(
                        onClick = { Toast.makeText(context, "Generando Reporte Tributario...", Toast.LENGTH_SHORT).show() },
                        modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "📊", fontSize = 32.sp)
                            Text(text = "Reporte Trib.", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                    }
                }

                // 📬 CARD: BUZÓN SOL
                item {
                    Card(
                        onClick = {
                            cargandoBuzon = true
                            verBuzonNativo = true
                            coroutineScope.launch {
                                try {
                                    listaNotificaciones.clear()
                                    val baseResultados = PostgresDriver.obtenerBuzonPorCliente(rucUsuario)
                                    listaNotificaciones.addAll(baseResultados)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Falla al conectar con Buzón SOL", Toast.LENGTH_SHORT).show()
                                } finally {
                                    cargandoBuzon = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "📬", fontSize = 32.sp)
                            Text(text = "Buzón SOL", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                    }
                }

                // 👥 CARD: T-REGISTRO MÓDULO LABORAL (Conectado al 100%)
                item {
                    Card(
                        onClick = {
                            // 1. Encendemos el indicador de carga en la interfaz
                            cargandoTregistro = true

                            // 2. Disparamos una corrutina asíncrona para consultar la base de datos sin colgar la app
                            coroutineScope.launch {
                                try {
                                    // Llamamos a tu nuevo método JDBC que configuramos en el PostgresDriver
                                    listaTrabajadores = PostgresDriver.obtenerTrabajadoresPorCliente(rucUsuario)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error al cargar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    // 3. Apagamos el cargando y activamos la pantalla del Dashboard
                                    cargandoTregistro = false
                                    verTregistroNativo = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp),
                        shape = RoundedCornerShape(size = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(all = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "👥", fontSize = 32.sp)
                            Text(
                                text = "T-Registro",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF6750A4)
                            )
                        }
                    }
                }
                // 💰 CARD: DDJJ Y PAGOS (MÓDULO CONTABLE PREDICTIVO DE SEMÁFOROS)
                item {
                    Card(
                        onClick = {
                            cargandoDdjj = true
                            verDdjjNativo = true
                            coroutineScope.launch {
                                try {
                                    listaDdjj.clear()
                                    val resultadosBd = PostgresDriver.obtenerDashboardPredictivoDDJJ(rucUsuario)
                                    listaDdjj.addAll(resultadosBd)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error en obligaciones SUNAT", Toast.LENGTH_SHORT).show()
                                } finally {
                                    cargandoDdjj = false
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "💰", fontSize = 32.sp)
                            Text(text = "DDJJ y Pagos", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                    }
                }

                // 🏦 CARD: APORTES MULTI-AFP (EJECUTA LA CARGA ASÍNCRONA EN EL ENTORNO PRINCIPAL)
                item {
                    Card(
                        onClick = {
                            cargandoAfp = true
                            verAfpNativo = true // Enciende el enrutador condicional de la AFP
                            coroutineScope.launch {
                                try {
                                    listaAfp.clear()
                                    // 🚀 CARGA EN SEGUNDO PLANO DESDE EL DRIVER MODERNO JDBC STRONGLY TYPED
                                    val resultadosAfp = PostgresDriver.obtenerPlanillasAfpPorCliente(rucUsuario)
                                    listaAfp.addAll(resultadosAfp)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error en datos de AFPnet", Toast.LENGTH_SHORT).show()
                                } finally {
                                    cargandoAfp = false // Apaga la barra de progreso siempre liberando la visualización
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(120.dp), shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "🏦", fontSize = 32.sp)
                            Text(text = "Aportes AFP", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6750A4))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // BOTÓN DE DESCONEXIÓN LOCAL SEGURO
            Button(
                onClick = {
                    LocalStorageManager.borrarCredenciales(context)
                    onLogout()
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6750A4), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth().height(48.dp).padding(horizontal = 4.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Salir", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
} // 🔐 CIERRE TOTAL DEL ENTORNO PRINCIPAL
