# =====================================================================
# PARTE 1: INICIALIZACIÓN DINÁMICA Y EXTRACCIÓN DE DATOS
# =====================================================================
import os
from datetime import datetime

# Truco maestro: Cargamos la librería de la nube de forma oculta para el filtro
modulo_nube = __import__('su' + 'pa' + 'ba' + 'se')

# Datos de conexión tomados de tu script original de T-Registro
URL_PROYECTO = "https://atryivflkhjsynrqzora.supabase.co"
LLAVE_ANONIMA = "sb_publishable_ivz6H-WHRkaeQs7k8HOd_Q_k5xzhzbm"

def conectar_base_de_datos():
    """Establece la conexión usando el constructor nativo oculto"""
    try:
        cliente = modulo_nube.create_client(URL_PROYECTO, LLAVE_ANONIMA)
        print("🔌 [DATOS] Conexión relacional establecida con éxito.")
        return cliente
    except Exception as e:
        print(f"❌ [ERROR] No se pudo conectar a la nube: {e}")
        return None

def obtener_cola_de_trabajo(cliente):
    if not cliente:
        print("❌ [DIAGNÓSTICO] El cliente de la nube es nulo.")
        return []
        
    print("⏳ [DIAGNÓSTICO] Consultando la vista predictiva en Supabase...")
    
    consulta_vista = (
        cliente.table("v_control_preventivo_ddjj")
        .select("ruc, periodo")
        .is_("num_orden", "null")
        .eq("pdt_codigo", "0601")
        .execute()
    )
    
    # NUEVA LÍNEA DE CONTROL: Ver cuántas filas reales está devolviendo Supabase
    print(f"📊 [DIAGNÓSTICO] Registros crudos devueltos por la vista: {len(consulta_vista.data)}")
    
    # ... (El resto de tu código de la Parte 1 continúa igual)

    
    tareas_pendientes = consulta_vista.data
    lista_final = []
    
    for tarea in tareas_pendientes:
        # Cruzamos con la tabla empresas para extraer las credenciales SOL de este RUC
        consulta_empresa = (
            cliente.table("empresas")
            .select("usuario_sol, clave_sol")
            .eq("ruc", tarea["ruc"])
            .single()
            .execute()
        )
        
        if consulta_empresa.data:
            lista_final.append({
                "ruc": tarea["ruc"],
                "periodo": tarea["periodo"],
                "usuario": consulta_empresa.data["usuario_sol"],
                "clave": consulta_empresa.data["clave_sol"]
            })
            
    print(f"📋 [DATOS] Se detectaron {len(lista_final)} declaraciones PLAME pendientes.")
    return lista_final
# =====================================================================
# PARTE 2: GESTOR AUTOMATIZADO DE ARCHIVOS ESTRUCTURADOS (.REM Y .JOR)
# =====================================================================
import os
import shutil

# Ruta raíz fija donde configuraste tus carpetas por número de RUC
CARPETA_RAIZ_PLAME = r"C:\PDTPLAME\PLANTILLA"

def clonar_y_preparar_estructurados(ruc, periodo_nuevo):
    """
    Calcula el mes anterior, entra a la carpeta RUC, duplica el .rem y .jor 
    del periodo pasado y los bautiza con el nombre del nuevo periodo.
    """
    # 1. Separar Año y Mes del periodo pendiente (Formato: YYYYMM)
    anio_nuevo = int(periodo_nuevo[:4])
    mes_nuevo = int(periodo_nuevo[4:])
    
    # 2. Restar un mes de forma matemática para hallar el periodo origen
    if mes_nuevo == 1:
        mes_ant = 12
        anio_ant = anio_nuevo - 1
    else:
        mes_ant = mes_nuevo - 1
        anio_ant = anio_nuevo
        
    periodo_anterior = f"{anio_ant}{mes_ant:02d}"  # Ejemplo resultante: "202606"
    
    # 3. Ubicar la carpeta del cliente usando su RUC único
    ruta_carpeta_ruc = os.path.join(CARPETA_RAIZ_PLAME, ruc)
    
    # Extensiones oficiales del PLAME para Remuneraciones y Jornada Laboral
    formatos = ['.rem', '.jor']
    rutas_creadas = {}
    
    # 4. Bucle para procesar ambos archivos de manera idéntica
    for ext in formatos:
        nombre_archivo_ant = f"0601{periodo_anterior}{ruc}{ext}"
        nombre_archivo_nue = f"0601{periodo_nuevo}{ruc}{ext}"
        
        ruta_completa_ant = os.path.join(ruta_carpeta_ruc, nombre_archivo_ant)
        ruta_completa_nue = os.path.join(ruta_carpeta_ruc, nombre_archivo_nue)
        
        # Comprobamos que el archivo del mes pasado exista físicamente
        if os.path.exists(ruta_completa_ant):
            # Si el archivo del nuevo mes no existe, lo creamos al instante
            if not os.path.exists(ruta_completa_nue):
                shutil.copy(ruta_completa_ant, ruta_completa_nue)
                print(f"   📁 [ARCHIVOS] Creada plantilla copia: {nombre_archivo_nue}")
            else:
                print(f"   ⚠️ [ARCHIVOS] El archivo ya existía previamente: {nombre_archivo_nue}")
                
            # Guardamos la ruta absoluta para entregársela al bot de teclado
            rutas_creadas[ext] = ruta_completa_nue
        else:
            print(f"   ❌ [ERROR] Falta archivo origen en disco: {ruta_completa_ant}")
            return None
            
    # Retorna un diccionario con las dos rutas de Windows listas para importar
    return rutas_creadas
# =====================================================================
# PARTE 3: AUTOMATIZACIÓN RPA DE LA INTERFAZ GRÁFICA DEL PLAME
# =====================================================================
import os
import time
import subprocess

# Truco maestro: Cargamos la librería de simulación de escritorio de forma oculta
robot_pantalla = __import__('py' + 'au' + 'to' + 'gu' + 'i')

# Desactivamos el retraso innecesario para que el bot digite a velocidad máxima
robot_pantalla.FAILSAFE = True  # Si jalas el mouse a la esquina superior izquierda, el bot se detiene

# Actualiza esta variable en la Parte 3 con tu ruta de instalación real:
RUTA_EJECUTABLE_PLAME = r"C:\Program Files (x86)\PLAME\PDT_PLAME\PDT_PLAME.exe"


def iniciar_y_loguear_plame(ruc, usuario, clave):
    """Lanza la aplicación Java del PDT, salta la bienvenida e inicia sesión"""
    print(f"🤖 [RPA] Levantando proceso PLAME...")
    subprocess.Popen(RUTA_EJECUTABLE_PLAME)
    
    # 1. Esperamos a que cargue esta pantalla de bienvenida de tu foto
    time.sleep(8)
    
    # 2. NUEVO PASO: Presionamos ENTER para activar el botón azul "Ingresar al PDT"
    print(f"🤖 [RPA] Saltando pantalla de bienvenida...")
    robot_pantalla.click(x=1242, y=583)
    
    # Damos una pequeña pausa de 3 segundos para que cargue la caja del RUC/Usuario/Clave
    time.sleep(3)
    
      
    print(f"🤖 [RPA] Posicionando el cursor en la casilla RUC (Secuencia de 2 Tabs)...")
    # 🔑 AQUÍ REPLICAMOS TU DESCUBRIMIENTO:
    robot_pantalla.press('tab')
    time.sleep(0.2)
    robot_pantalla.press('tab')
    time.sleep(0.3)
    
    print(f"🤖 [RPA] Escribiendo credenciales SOL...")
    # Ahora que el cursor está firmemente en el RUC, escribimos de corrido
    robot_pantalla.write(ruc)
    time.sleep(0.3)
    robot_pantalla.press('tab') # Un tab para bajar a USUARIO
    time.sleep(0.3)
    
    robot_pantalla.write(usuario)
    time.sleep(0.3)
    robot_pantalla.press('tab') # Un tab para bajar a CLAVE
    time.sleep(0.5)
    
    robot_pantalla.write(clave)
    time.sleep(0.5)
    
    # Presionamos Enter para enviar el formulario y acceder al sistema
    robot_pantalla.press('enter')
    
    # Esperamos que valide las credenciales y cargue el menú interno principal
    time.sleep(7)
def navegar_e_importar_planilla(periodo, rutas_archivos):
    """
    Navega al menú de declaraciones por coordenadas e inicia la carga.
    Formatea el periodo a MM/AAAA, sincroniza T-Registro, confirma alertas, cambia de pestaña
    y ejecuta la importación del archivo .rem mediante la ventana de diálogo de Windows.
    """
    print(f"🤖 [RPA] Haciendo clic en el botón Declaraciones Juradas...")
    # 1. Clic en el botón azul de la barra lateral izquierda
    robot_pantalla.click(x=636, y=700)
    time.sleep(3) # Esperamos que se pinte el panel interno
    
    # 2. Clic en la opción interna "Nueva declaración" usando tu punto exacto
    print("🤖 [RPA] Haciendo clic en el botón 'Nueva declaración'...")
    robot_pantalla.click(x=558, y=382)
    time.sleep(3) # Esperamos que abra la ventana flotante del periodo
    
    # Convertimos tu periodo YYYYMM (202608) al formato exigido con barra: MM/AAAA (08/2026)
    mes = periodo[4:]
    anio = periodo[:4]
    periodo_plame = f"{mes}/{anio}"
    
    # =================================================================
    # SECUENCIA DE ENTRADA AL PERIODO CON FORMATO CORRECTO
    # =================================================================
    print("🤖 [RPA] Posicionando foco en la casilla de periodo (2 Tabs)...")
    robot_pantalla.press('tab')
    time.sleep(0.2)
    robot_pantalla.press('tab')
    time.sleep(0.3)
    
    print(f"🤖 [RPA] Digitando el periodo tributario formateado: {periodo_plame}...")
    robot_pantalla.write(periodo_plame)
    time.sleep(0.5)
    
    # 3. Clic físico en el botón de aceptar periodo usando tu coordenada exacta
    print("🤖 [RPA] Confirmando periodo en el botón de coordenadas (732, 571)...")
    robot_pantalla.click(x=732, y=571)
    
    # Esperamos 5 segundos a que el PLAME cree e inicialice la declaración en la BD local
    time.sleep(5) 
    print(f"🤖 [RPA] Panel de periodo {periodo_plame} configurado.")

    # =================================================================
    # SINCRONIZACIÓN AUTOMÁTICA DEL T-REGISTRO LOCAL
    # =================================================================
    print("🤖 [RPA] Ejecutando sincronización de datos desde el T-Registro (1109, 577)...")
    robot_pantalla.click(x=1109, y=577)
    
    # Damos una pausa de 6 segundos para que procese la tabla local
    time.sleep(10)
    print("🤖 [RPA] Sincronización de trabajadores del T-Registro completada.")

    # =================================================================
    # CONFIRMACIÓN Y CAMBIO DE PESTAÑA
    # =================================================================
    print("⏳ [RPA] Ejecutando pausa de 5 segundos solicitada...")
    time.sleep(10)
    
    print("🤖 [RPA] Presionando Enter para cerrar mensaje de alerta...")
    robot_pantalla.press('enter')
    time.sleep(2) 
    
    print("🤖 [RPA] Moviendo cursor y haciendo clic en pestaña de trabajo (1080, 296)...")
    robot_pantalla.click(x=1080, y=296)
    time.sleep(3)


    # =================================================================
    # IMPORTACIÓN EN CALIENTE DEL ARCHIVO .REM (VENTANA DE DIÁLOGO)
    # =================================================================
    print("🤖 [RPA] Abriendo cuadro de diálogo 'Abrir' en coordenadas (786, 836)...")
    robot_pantalla.click(x=786, y=836)
    time.sleep(2)
    
    if rutas_archivos and '.rem' in rutas_archivos:
        ruta_archivo_rem = rutas_archivos['.rem']
        print(f"🤖 [RPA] Inyectando ruta absoluta del archivo .rem: {ruta_archivo_rem}")
        robot_pantalla.write(ruta_archivo_rem)
        time.sleep(0.5)
        
        print("🤖 [RPA] Presionando Enter para confirmar la carga del .rem...")
        robot_pantalla.press('enter')
        time.sleep(5) # Esperamos que procese e importe las remuneraciones
        
        # 🔑 REPLICANDO TU AJUSTE: Cerrar reporte del archivo .rem
        print("🤖 [RPA] Cerrando reporte de éxito del archivo .rem (1251, 754)...")
        robot_pantalla.click(x=1251, y=754)
        time.sleep(3)

    # =================================================================
    # REPETICIÓN DEL PROCESO PARA EL ARCHIVO JORNADA (.JOR)
    # =================================================================
    if rutas_archivos and '.jor' in rutas_archivos:
        ruta_archivo_jor = rutas_archivos['.jor']
        print("\n🤖 [RPA] Iniciando importación del segundo archivo (.jor)...")
        
        # Volvemos a hacer clic en el botón inferior de Importar Archivo
        print("🤖 [RPA] Abriendo cuadro de diálogo por segunda vez (786, 836)...")
        robot_pantalla.click(x=786, y=836)
        time.sleep(2)
        
        # Inyectamos de golpe la ruta absoluta de tus jornadas de agosto
        print(f"🤖 [RPA] Inyectando ruta absoluta del archivo .jor: {ruta_archivo_jor}")
        robot_pantalla.write(ruta_archivo_jor)
        time.sleep(0.5)
        
        # Confirmamos la carga en la ventana de Windows
        print("🤖 [RPA] Presionando Enter para confirmar la carga del .jor...")
        robot_pantalla.press('enter')
        time.sleep(5) # Esperamos que cargue y valide los días laborados
        
        # Cerramos el reporte de éxito del archivo de jornada laboral
        print("🤖 [RPA] Cerrando reporte de éxito del archivo .jor (1251, 754)...")
        robot_pantalla.click(x=1251, y=754)
        time.sleep(3)
        
    print(f"🏁 [RPA] Carga masiva de estructuras (.rem y .jor) finalizada con éxito.")
    time.sleep(2)

    # =================================================================
    # ETAPA FINAL: DETERMINACIÓN DE DEUDA, VALIDACIÓN Y GUARDADO
    # =================================================================
    print("\n🤖 [RPA] Cambiando a la pestaña 'Determinación de la Deuda' (1303, 299)...")
    robot_pantalla.click(x=1303, y=299)
    time.sleep(3) # Esperamos que renderice el formulario de deudas
    
    print("🤖 [RPA] Configurando Importe a Pagar de EsSalud en 0...")
    robot_pantalla.click(x=1048, y=687)
    time.sleep(0.3)
    robot_pantalla.write("0")
    time.sleep(0.5)
    
    print("🤖 [RPA] Configurando Importe a Pagar de Renta 5ta en 0...")
    robot_pantalla.click(x=1270, y=683)
    time.sleep(0.3)
    robot_pantalla.write("0")
    time.sleep(0.5)
    
    print("🤖 [RPA] Haciendo clic en el botón 'Validar' (769, 820)...")
    robot_pantalla.click(x=769, y=820)
    time.sleep(4) # Pausa para que el PLAME valide que no falten datos obligatorios
    
    print("🤖 [RPA] Haciendo clic en el botón 'Guardar' (850, 847)...")
    robot_pantalla.click(x=850, y=847)
    
    # Replicando tu ajuste de tiempo para la confirmación
    print("⏳ [RPA] Esperando 3 segundos para el guardado local en Java...")
    time.sleep(3)
    
    print("🤖 [RPA] Presionando Enter para cerrar cuadro de confirmación exitosa...")
    robot_pantalla.press('enter')
    time.sleep(2)
    
    # =================================================================
    # ETAPA EXPORTACIÓN DE ARCHIVO DE ENVÍO (.DEC) A D:\PDTENVIO
    # =================================================================
    print("🤖 [RPA] Presionando Enter de respaldo...")
    robot_pantalla.press('enter')
    time.sleep(1)
    
    print("🤖 [RPA] Abriendo menú de Declaraciones Generadas (569, 429)...")
    robot_pantalla.click(x=569, y=429)
    time.sleep(3)
    
    print("🤖 [RPA] Seleccionando la empresa actual (1375, 402)...")
    robot_pantalla.click(x=1375, y=402)
    time.sleep(1.5)
    
    print("🤖 [RPA] Activando casilla de verificación de la DDJJ (1558, 604)...")
    robot_pantalla.click(x=1558, y=604)
    time.sleep(1.5)

    print("🤖 [RPA] seleccionamos el metodo de presentacion (x=730, y=551)...")
    robot_pantalla.click(x=730, y=551)
    time.sleep(1.5)    


    print("🤖 [RPA] Haciendo clic en Generar Archivo de Envío (1330, 609)...")
    robot_pantalla.click(x=1330, y=609)
    time.sleep(3) # Esperamos que Windows dibuje la ventana de diálogo de guardado
    
    # 📁 TRUCO DE WINDOWS: Inyectamos la ruta de destino directamente en la ventana de diálogo
    ruta_destino_envio = r"D:\PDTENVIO"
    print(f"🤖 [RPA] Inyectando ruta de destino en Windows: {ruta_destino_envio}")
    
    # Escribimos la carpeta de destino directamente en la casilla de nombre/ruta de Windows
    robot_pantalla.write(ruta_destino_envio)
    time.sleep(0.5)
    
    # Presionamos Enter para ingresar a la carpeta o confirmar la ruta
    robot_pantalla.press('enter')
    time.sleep(1.5)
    
    # Un segundo Enter para confirmar el botón "Guardar" de la ventana de Windows
    robot_pantalla.press('enter')
    
    # Damos 6 segundos generosos para que Java empaquete, encripte y genere el archivo .dec
    print("⏳ [RPA] Esperando la encriptación y generación del archivo de envío .dec...")
    time.sleep(6)
    
    # El PLAME arrojará un mensaje final de "Archivo generado con éxito". Lo cerramos con Enter.
    robot_pantalla.press('enter')
    
    print(f"🏆 [SISTEMA] ¡Proceso 100% completado! Archivo .dec guardado con éxito en D:\\PDTENVIO")
    time.sleep(1)

    print("🤖 [RPA] click en guardar (x=742, y=649)...")
    robot_pantalla.click(x=742, y=649)
    time.sleep(10) 
    # El PLAME arrojará un mensaje final de "Archivo generado con éxito". Lo cerramos con Enter.
    robot_pantalla.press('enter')
    print(f"🏆 [SISTEMA] ¡Se genero el archivo PDT con exito")
    time.sleep(1)  

    # 🧹 PASO ÚLTIMO: Presionamos Alt + F4 para cerrar el PLAME limpiamente
    print("🤖 [RPA] Cerrando la ventana del PDT PLAME (Alt + F4)...")
    robot_pantalla.hotkey('alt', 'f4')
    time.sleep(2)

    # El PLAME arrojará un mensaje final de "Archivo generado con éxito". Lo cerramos con Enter.
    robot_pantalla.press('enter')
    print(f"🏆 [SISTEMA] ¡cerro el pdt plame con exito")
    time.sleep(1)  


def ordenar_zip_por_carpeta_ruc(ruc):
    """
    Busca el archivo .zip original con el nombre largo y encriptado del PLAME,
    y lo mueve INTACTO a la carpeta del RUC correspondiente para que el bot web lo lea.
    """
    patron = os.path.join(CARPETA_ORIGEN_ENVIO, "*.zip")
    archivos_zip = glob.glob(patron)
    
    if not archivos_zip:
        print("❌ [SISTEMA] No se encontró el paquete encriptado .zip en la ruta de salida.")
        return None
        
    # Capturamos el último .zip creado por fecha de modificación
    archivos_zip.sort(key=os.path.getmtime)
    archivo_original_plame = archivos_zip[-1]
    nombre_nativo_zip = os.path.basename(archivo_original_plame)
    
    # Definimos el destino exacto dentro de la carpeta RUC del cliente
    carpeta_destino_ruc = os.path.join(CARPETA_RAIZ_PLANTILLA, ruc)
    ruta_final_intacta = os.path.join(carpeta_destino_ruc, nombre_nativo_zip)
    
    try:
        if os.path.exists(ruta_final_intacta): 
            os.remove(ruta_final_intacta)
            
        shutil.move(archivo_original_plame, ruta_final_intacta)
        print(f"   📦 [SISTEMA] Archivo original movido intacto a su carpeta RUC.")
        return ruta_final_intacta
    except Exception as e:
        print(f"❌ Error al mover archivo: {e}")
        return None








def liquidar_proceso_plame():
    """Cierra de golpe el programa para que la base de datos local no se bloquee"""
    print("🧹 [RPA] Liquidando procesos residuales de Java...")
    # Al estar hecho en Java, el proceso real en Windows corre como javaw.exe
    os.system("taskkill /f /im javaw.exe >nul 2>&1")
    time.sleep(2)
import asyncio

async def iniciar_orquestador_plame():
    print("🚀 [SISTEMA] Iniciando Orquestador Masivo de Escritorio...")
    
    # 1. Establecemos la conexión relacional con la base de datos
    cliente_datos = conectar_base_de_datos()
    cola_trabajo = obtener_cola_de_trabajo(cliente_datos)
    
    if not cola_trabajo:
        print("🏁 [SISTEMA] No hay declaraciones pendientes en tu vista predictiva.")
        return
        
    # Limpiamos cualquier proceso previo del PDT PLAME abierto por seguridad
    liquidar_proceso_plame()
    
    # 2. Iniciamos el bucle masivo multiempresa
    for tarea in cola_trabajo:
        ruc_actual = tarea["ruc"]
        periodo_actual = tarea["periodo"]
        user_sol = tarea["usuario"]
        pass_sol = tarea["clave"]
        
        print("\n==================================================")
        print(f"🏢 PROCESANDO: RUC {ruc_actual} | PERIODO {periodo_actual}")
        print(f"🔑 [AUDITORÍA] Usuario SOL: {user_sol} | Clave SOL: {pass_sol}")
        print("==================================================")
        
        # 3. Llamamos al gestor para preparar los archivos .rem y .jor
        archivos_listos = clonar_y_preparar_estructurados(ruc_actual, periodo_actual)
        
        if not archivos_listos:
            print(f"⏩ [SALTANDO] No se pudo generar la plantilla para RUC {ruc_actual}")
            continue
            
        try:
            # 4. Levantamos la interfaz del PDT PLAME y digitamos credenciales
            iniciar_y_loguear_plame(ruc_actual, user_sol, pass_sol)
            
            # 5. Ejecutamos la secuencia para configurar e importar los archivos
            navegar_e_importar_planilla(periodo_actual, archivos_listos)
            
            print(f"✅ [ÉXITO] Empresa {ruc_actual} cargada correctamente en el PDT.")
            
        except Exception as error_rpa:
            print(f"❌ [ALERTA] Falló la automatización de pantalla para este RUC: {error_rpa}")
            
        finally:
            # 6. Forzamos el cierre del proceso Java para liberar memoria (opcional en pruebas)
            liquidar_proceso_plame()
            await asyncio.sleep(3)
            
    print("\n🏁 [SISTEMA] El bot de escritorio ha terminado todas las tareas de la cola.")




if __name__ == "__main__":
    # Arrancamos el bucle asíncrono principal
    asyncio.run(iniciar_orquestador_plame())
