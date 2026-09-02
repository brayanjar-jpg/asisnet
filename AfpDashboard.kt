package com.example.asisnet_contable

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

// Modelo analítico definitivo sincronizado con el PostgresDriver proactivo expandido
data class PlanillaAfpEntidad(
    val periodo: String,
    val planilla: String,
    val fecha: String,
    val total: String,
    val fondo: String,
    val ryr: String,
    val trabajadores: String,
    val estadoTicket: String,
    val omitidos: String,
    val ticket: String,
    val vencimiento: String,
    val fechaPago: String,
    val urlPdf: String,
    val urlPdfTicket: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AfpDashboard(
    rucUsuario: String,
    listaPlanillasAfp: List<PlanillaAfpEntidad>,
    cargandoAfp: Boolean,
    onVolver: () -> Unit
) {
    val context = LocalContext.current
    var mostrarMenuAdjuntosAfp by remember { mutableStateOf(false) }
    var urlPlanillaSeleccionada by remember { mutableStateOf("") }
    var urlTicketSeleccionado by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Aportes Previsionales - AFPnet", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    TextButton(onClick = onVolver) {
                        Text("< Volver", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(Color(0xFFF4F6F9))
        ) {
            if (cargandoAfp) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Color(0xFF6750A4))
            } else if (listaPlanillasAfp.isEmpty()) {
                Text(
                    text = "No se encontraron planillas o tickets de AFP configurados para este cliente.",
                    modifier = Modifier.align(Alignment.Center).padding(24.dp),
                    style = MaterialTheme.typography.bodyLarge, color = Color.Gray
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(listaPlanillasAfp) { afp ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth().height(IntrinsicSize.Min)
                                .background(Color(0xFFFAFAFA))
                                .padding(vertical = 12.dp, horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // =====================================================================
                            // 🧠 SEMÁFORO DE AUDITORÍA CALIBRADO A RIESGO REAL (ROJO SI ESTÁ PENDIENTE)
                            // =====================================================================
                            val estadoTicket = afp.estadoTicket
                            val colorSemafotoAfp = when {
                                // 🟢 REGLA 1: Si ya fue pagada en banco o figura como 'Planilla Declarada' -> VERDE
                                estadoTicket.contains("Pagad", ignoreCase = true) ||
                                        estadoTicket.contains("Declarada", ignoreCase = true) -> Color(0xFF00B27E)

                                // 🔴 REGLA 2: Si el ticket está vencido o la planilla está PENDIENTE DE DECLARAR -> ROJO
                                estadoTicket == "Pendiente" ||
                                        estadoTicket.contains("Declarar", ignoreCase = true) -> Color(0xFFCE0D0E)

                                else -> Color(0xFFFF9200) // Naranja de advertencia por defecto
                            }

                            Box(modifier = Modifier.width(5.dp).fillMaxHeight().background(colorSemafotoAfp))
                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f).wrapContentHeight()) {
                                val rawPlanilla = afp.planilla
                                val afpLimpia = rawPlanilla.substringBefore(" (").trim()
                                val numeroPlanillaLimpio = rawPlanilla.substringAfter("(", "").substringBefore(")", "").trim()

                                Text(
                                    text = "Planilla AFPnet: $afpLimpia",
                                    fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color(0xFF1D1B20)
                                )
                                Spacer(modifier = Modifier.height(2.dp))

                                // 🎯 CORREGIDO: El texto del estado 'Por Declarar' ahora hereda dinámicamente el Color Rojo de la variable colorSemafotoAfp
                                Text(
                                    text = if (numeroPlanillaLimpio.isNotEmpty() && numeroPlanillaLimpio != "POR DECLARAR") {
                                        "N° Planilla: $numeroPlanillaLimpio"
                                    } else {
                                        "Estado: Por Declarar"
                                    },
                                    fontWeight = FontWeight.Bold, fontSize = 13.sp,
                                    color = if (estadoTicket.contains("Declarar", ignoreCase = true)) colorSemafotoAfp else Color.Gray
                                )

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(text = "Período: ${afp.periodo}", fontSize = 12.sp, color = Color.Gray)
                                    Text(text = "Declarados: ${afp.trabajadores} trab.", fontSize = 12.sp, color = Color.Gray)
                                }

                                Spacer(modifier = Modifier.height(2.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    Text(text = "Fondo: S/. ${afp.fondo}", fontSize = 11.sp, color = Color.Gray)
                                    Text(text = "RyR: S/. ${afp.ryr}", fontSize = 11.sp, color = Color.Gray)
                                }

                                val omitidos = afp.omitidos.toIntOrNull() ?: 0
                                if (omitidos > 0) {
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = "⚠️ Alerta: Falta incluir $omitidos trabajador(es) según Maestro laboral",
                                        color = Color(0xFFCE0D0E), fontSize = 11.sp, fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                val ticketVigente = afp.ticket.ifEmpty { "Sin Ticket Emitido" }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(text = "Ticket Vigente: ", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        text = ticketVigente, fontSize = 12.sp, fontWeight = FontWeight.Bold,
                                        color = if (ticketVigente.contains("Sin Ticket", ignoreCase = true)) Color(0xFFCE0D0E) else Color(0xFF555555)
                                    )
                                }

                                val fechaPagoReal = afp.fechaPago
                                val esPagadoReal = !fechaPagoReal.contains("Sin Pago", ignoreCase = true) && estadoTicket.contains("Pagad", ignoreCase = true)

                                Spacer(modifier = Modifier.height(2.dp))
                                if (esPagadoReal) {
                                    Text(text = "✅ Pagado el: $fechaPagoReal", fontSize = 11.sp, color = Color(0xFF00B27E), fontWeight = FontWeight.Bold)
                                } else {
                                    Text(text = "❌ No Pagado: Pendiente en Banco", fontSize = 11.sp, color = Color(0xFFCE0D0E), fontWeight = FontWeight.Bold)
                                }

                                if (!esPagadoReal && afp.vencimiento != "S/V" && !estadoTicket.contains("Declarar", ignoreCase = true)) {
                                    Text(text = "Vence el: ${afp.vencimiento}", fontSize = 11.sp, color = Color(0xFFCE0D0E), fontWeight = FontWeight.Medium)
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                Box(
                                    modifier = Modifier.background(color = colorSemafotoAfp, shape = RoundedCornerShape(3.dp)).padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(text = estadoTicket.uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Column(horizontalAlignment = Alignment.End, modifier = Modifier.padding(start = 8.dp)) {
                                Text(text = "S/. ${afp.total}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF333333))
                                Spacer(modifier = Modifier.height(14.dp))

                                val urlPlanilla = afp.urlPdf
                                val urlTicket = afp.urlPdfTicket

                                if (urlPlanilla.isNotEmpty() || urlTicket.isNotEmpty()) {
                                    Row(
                                        modifier = Modifier
                                            .background(color = colorSemafotoAfp.copy(alpha = 0.08f), shape = RoundedCornerShape(6.dp))
                                            .clickable {
                                                urlPlanillaSeleccionada = urlPlanilla
                                                urlTicketSeleccionado = urlTicket
                                                mostrarMenuAdjuntosAfp = true
                                            }
                                            .padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "📎 AFP PDF", fontSize = 12.sp, color = colorSemafotoAfp, fontWeight = FontWeight.Bold)
                                    }
                                } else {
                                    Text(text = "Sin adjuntos", fontSize = 11.sp, color = Color.LightGray)
                                }
                            }
                        }
                        HorizontalDivider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                    }
                }
            }
        }
    }

    if (mostrarMenuAdjuntosAfp) {
        AlertDialog(
            onDismissRequest = { mostrarMenuAdjuntosAfp = false },
            title = { Text(text = "Documentos de AFPnet", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    if (urlPlanillaSeleccionada.isNotEmpty() && urlPlanillaSeleccionada != "null") {
                        Card(
                            onClick = {
                                abrirUrlEnNavegadorAfp(urlPlanillaSeleccionada, context)
                                mostrarMenuAdjuntosAfp = false
                            },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6F9)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "📄", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(text = "Constancia de Declaración", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF6750A4))
                                    Text(text = "Detalle de planilla oficial de AFPnet", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                    if (urlTicketSeleccionado.isNotEmpty() && urlTicketSeleccionado != "null") {
                        Card(
                            onClick = {
                                abrirUrlEnNavegadorAfp(urlTicketSeleccionado, context)
                                mostrarMenuAdjuntosAfp = false
                            },
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F6F9)),
                            shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "💰", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(14.dp))
                                Column {
                                    Text(text = "Ticket de Pago Vigente", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF6750A4))
                                    Text(text = "Código de barras y orden para el banco", fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { mostrarMenuAdjuntosAfp = false }) {
                    Text("Cancelar", color = Color.Gray, fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp), containerColor = Color.White
        )
    }
}

fun abrirUrlEnNavegadorAfp(url: String, context: Context) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url.trim())).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "No se pudo abrir el documento desde la nube", Toast.LENGTH_SHORT).show()
    }
}
