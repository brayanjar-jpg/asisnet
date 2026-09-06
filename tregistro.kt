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
