package com.example.asisnet_contable

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TregistroDashboard(
    listaTrabajadores: List<com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral>,
    onBackClick: () -> Unit
) {
    var trabajadorSeleccionado by remember { mutableStateOf<com.example.asisnet_contable.PostgresDriver.EmpleadoLaboral?>(null) }
    var mostrarModalDetalle by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Dashboard T-Registro", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "Control de Trabajadores Activos",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (listaTrabajadores.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No se encontraron trabajadores registrados.",
                        color = Color.Gray,
                        fontSize = 16.sp
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(listaTrabajadores.size) { index ->
                        val empleado = listaTrabajadores[index]
                        val esActivo = empleado.estadoSunat.contains("Activo", ignoreCase = true)
                        val colorEstado = if (esActivo) Color(0xFF00B27E) else Color.Gray

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
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = empleado.nombresCompletos,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Surface(
                                        color = colorEstado.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = empleado.estadoSunat,
                                            color = colorEstado,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text(text = "DNI: ${empleado.dni}", fontSize = 14.sp, color = Color.Gray)
                                        Text(text = "Fec. Nac.: ${empleado.fechaNacimiento}", fontSize = 14.sp, color = Color.Gray)
                                        Text(text = "Sexo: ${empleado.sexo}", fontSize = 14.sp, color = Color.Gray)
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        IconButton(
                                            onClick = {
                                                trabajadorSeleccionado = empleado
                                                mostrarModalDetalle = true
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar", tint = Color(0xFF6750A4))
                                        }
                                        IconButton(onClick = { }, modifier = Modifier.size(36.dp)) {
                                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Eliminar", tint = Color.Red)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver al Menú", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (mostrarModalDetalle && trabajadorSeleccionado != null) {
        val empleado = trabajadorSeleccionado!!
        AlertDialog(
            onDismissRequest = { mostrarModalDetalle = false },
            confirmButton = {
                Button(onClick = { mostrarModalDetalle = false }) {
                    Text("Cerrar")
                }
            },
            title = {
                Text(
                    text = empleado.nombresCompletos,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 400.dp) // Reducimos ligeramente el alto máximo para dar aire al botón Cerrar
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider()

                    // 1. IDENTIFICACIÓN
                    Text(text = "👤 TRABAJADOR - Datos de identificación", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6750A4))
                    Text(text = "DNI: ${empleado.dni}", fontSize = 14.sp)
                    Text(text = "Fecha nacimiento: ${empleado.fechaNacimiento}", fontSize = 14.sp)
                    Text(text = "País Emisor: ${empleado.paisEmisor}", fontSize = 14.sp)
                    Text(text = "Sexo: ${empleado.sexo}", fontSize = 14.sp)
                    Text(text = "Estado Civil: ${empleado.estadoCivil}", fontSize = 14.sp)
                    Text(text = "Nacionalidad: ${empleado.nacionalidad}", fontSize = 14.sp)
                    Text(text = "Teléfono: ${empleado.telefono}", fontSize = 14.sp)
                    Text(text = "Correo electrónico: ${empleado.correo}", fontSize = 14.sp)
                    Text(text = "Primera dirección: ${empleado.primeraDireccion}", fontSize = 14.sp)

                    HorizontalDivider()

                    // 2. LABORALES
                    Text(text = "💼 Datos laborales", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6750A4))
                    Text(text = "Régimen laboral: ${empleado.regimenLaboral}", fontSize = 14.sp)
                    Text(text = "Categoría ocupacional: ${empleado.categoria}", fontSize = 14.sp)
                    Text(text = "Ocupación: ${empleado.ocupacion}", fontSize = 14.sp)
                    Text(text = "Tipo de contrato: ${empleado.tipoContrato}", fontSize = 14.sp)
                    Text(text = "Fecha inicio: ${empleado.fechaInicioLabores}", fontSize = 14.sp)
                    Text(text = "Jornada laboral: ${empleado.jornadaLaboral}", fontSize = 14.sp)
                    Text(text = "Tipo de pago y periodicidad: ${empleado.tipoPeriodicidadPago}", fontSize = 14.sp)
                    Text(text = "Remuneración básica inicial: S/ ${String.format("%.2f", empleado.remuneracionBasica)}", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)

                    HorizontalDivider()

                    // 3. SALUD
                    Text(text = "🏥 Régimen de aseguramiento de salud", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6750A4))
                    Text(text = "Régimen de salud: ${empleado.regimenSalud}", fontSize = 14.sp)
                    Text(text = "Entidad prestadora: ${empleado.entidadPrestadora}", fontSize = 14.sp)
                    Text(text = "Fecha de inicio: ${empleado.fechaInicioSalud}", fontSize = 14.sp)

                    HorizontalDivider()

                    // 4. PENSIÓN
                    Text(text = "💰 Régimen pensionario", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF6750A4))
                    Text(text = "Régimen pensionario: ${empleado.regimenPensionario}", fontSize = 14.sp)
                    Text(text = "CUSPP: ${empleado.cuspp}", fontSize = 14.sp)
                    Text(text = "Fecha de inicio: ${empleado.fechaInicioPension}", fontSize = 14.sp)
                }
            },

            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }
}
