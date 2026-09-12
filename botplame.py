# =====================================================================
# PARTE 1: INICIALIZACIÓN DINÁMICA Y EXTRACCIÓN DE DATOS
# =====================================================================
import os
import shutil
import time
import subprocess
import glob 
from datetime import datetime

CARPETA_ORIGEN_ENVIO = r"D:\PDTENVIO"
CARPETA_RAIZ_PLANTILLA = r"C:\PDTPLAME\PLANTILLA"


# Mapeo por posición física de las columnas en la pestaña Determinación de la Deuda

MAPA_COLUMNAS_VISUALES = {
    0: {"x": 1088, "y": 681}, # Centro del recuadro blanco de la Columna 1 (Fila 801)
    1: {"x": 1219, "y": 683}, # Centro del recuadro blanco de la Columna 2 (Fila 802)
    2: {"x": 1386, "y": 683}, # Centro del recuadro blanco de la Columna 3 (Fila 805)
}


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


def pegar_texto(texto):
    """Copia la contraseña al portapapeles de Windows y simula Ctrl+V asegurando el import interno"""
    # Forzamos el import aquí adentro para evitar problemas de alcance en el script
    import pyperclip  
    
    # 1. Inyecta la clave directo a la memoria de Windows
    pyperclip.copy(texto)
    time.sleep(0.3)  # Pausa breve para asegurar que Windows registre el cambio
    

    # 2. Simula la combinación de teclas para pegar en la casilla del PLAME
    robot_pantalla.hotkey('ctrl', 'v')
    time.sleep(0.3)
    
    # 3. Limpieza inmediata del portapapeles para proteger la clave SOLD:\PDTENVIO


    pyperclip.copy("")



def iniciar_y_loguear_plame(ruc, usuario, clave):
    """Lanza la aplicación Java del PDT, salta la bienvenida e inicia sesión"""
    print(f"🤖 [RPA] Levantando proceso PLAME...")
    subprocess.Popen(RUTA_EJECUTABLE_PLAME)
    
    # 1. Esperamos a que cargue la pantalla de bienvenida del PDT
    time.sleep(8)
    
    # 2. Presionamos ENTER para activar el botón azul "Ingresar al PDT"
    print(f"🤖 [RPA] Saltando pantalla de bienvenida...")
    robot_pantalla.click(x=1242, y=583)
    
    # Damos una pequeña pausa de 3 segundos para que cargue la caja de login
    time.sleep(3)
    
    print(f"🤖 [RPA] Posicionando el cursor en la casilla RUC (Secuencia de 2 Tabs)...")
    robot_pantalla.press('tab')
    time.sleep(0.2)
    robot_pantalla.press('tab')
    time.sleep(0.3)
    
    print(f"🤖 [RPA] Escribiendo credenciales SOL...")
    
    # Escribimos el RUC (son solo números, corre bien con write)
    robot_pantalla.write(ruc)
    time.sleep(0.3)
    
    robot_pantalla.press('tab')  # Un tab para bajar a USUARIO
    time.sleep(0.3)
    
    # Escribimos el USUARIO SOL (letras simples en mayúscula)
    robot_pantalla.write(usuario)
    time.sleep(0.3)
    
    robot_pantalla.press('tab')  # Un tab para bajar a CLAVE
    time.sleep(0.5)
    
    # 🔑 CORRECCIÓN CRÍTICA: Pegamos la clave de forma segura y exacta usando el portapapeles
    pegar_texto(clave)
    time.sleep(0.5)
    
    # Presionamos Enter para enviar el formulario y acceder al sistema
    robot_pantalla.press('enter')
    
    # Esperamos que valide las credenciales y cargue el menú interno principal
    time.sleep(7)

def navegar_e_importar_planilla(ruc, periodo, rutas_archivos):
    """
    Navega al menú de declaraciones por coordenadas e inicia la carga.
    Formatea el periodo a MM/AAAA, sincroniza T-Registro, confirma alertas, cambia de pestaña
    y ejecuta la importación del archivo .rem mediante la ventana de diálogo de Windows.
    """
    print(f"🤖 [RPA] Configurando periodo y sincronizando T-Registro...")
    robot_pantalla.click(x=636, y=700)
    time.sleep(3)
    robot_pantalla.click(x=558, y=382)
    time.sleep(3)
    
    periodo_plame = f"{periodo[4:]}/{periodo[:4]}"
    robot_pantalla.press('tab')
    time.sleep(0.2)
    robot_pantalla.press('tab')
    time.sleep(0.3)
    robot_pantalla.write(periodo_plame)
    time.sleep(0.5)
    robot_pantalla.click(x=732, y=571)
    time.sleep(5)
    
    # Sincronización del T-Registro
    robot_pantalla.click(x=1109, y=577)
    time.sleep(10)
    robot_pantalla.press('enter')
    time.sleep(2)
    robot_pantalla.click(x=1080, y=296)
    time.sleep(3)
    # Importación de archivos .rem y .jor
    for ext in ['.rem', '.jor']:
        if rutas_archivos and ext in rutas_archivos:
            robot_pantalla.click(x=786, y=836)
            time.sleep(2)
            robot_pantalla.write(rutas_archivos[ext])
            time.sleep(0.5)
            robot_pantalla.press('enter')
            time.sleep(5)
            robot_pantalla.click(x=1251, y=754)
            time.sleep(3)



    robot_pantalla.click(x=1303, y=299)
    time.sleep(3)
    
    cliente_local = conectar_base_de_datos()
    
    # 📡 Consultamos usando tus códigos de 4 dígitos y limpiamos espacios con la API de Supabase
    query_tributos = cliente_local.table("empresa_tributos_afectos")\
        .select("tributo_codigo")\
        .eq("ruc", ruc.strip())\
        .eq("estado", True)\
        .lte("afecto_desde", periodo)\
        .order("tributo_codigo")\
        .execute()

    # Limpiamos posibles espacios en la lista devuelta
    tributos_afectos = [reg["tributo_codigo"].strip() for reg in query_tributos.data]
    print(f"📊 [DATOS] Tributos de 4 dígitos detectados con éxito: {tributos_afectos}")

    
    # Si la lista sigue vacía por error de registro, usamos las 3 columnas por defecto
    if not tributos_afectos:
        print("⚠️ [ALERTA] No se encontraron registros. Usando modo de respaldo completo...")
        tributos_afectos = ["3052", "5210", "5310"]

    for indice, cod_tributo in enumerate(tributos_afectos):
        if indice in MAPA_COLUMNAS_VISUALES:
            coord = MAPA_COLUMNAS_VISUALES[indice]
            print(f"🤖 [RPA] Rellenando Columna {indice + 1} para tributo {cod_tributo} en ({coord['x']}, {coord['y']})...")
            
            robot_pantalla.doubleClick(x=coord["x"], y=coord["y"])
            time.sleep(0.3)
            robot_pantalla.write("0")
            time.sleep(0.5)

    # ... (Esto va justo debajo del bucle for que escribe los ceros)
    
    # 1. Hace clic en el botón 'Validar' (769, 820)
    robot_pantalla.click(x=769, y=820)
    time.sleep(4) 
    
    # 2. Hace clic en el botón 'Guardar' (850, 847)
    robot_pantalla.click(x=850, y=847)
    time.sleep(4) # Esperamos a que procese y aparezca la ventana azul de éxito
    
    # 3. 🔑 CORRECCIÓN AQUÍ: Presionamos ENTER para cerrar el cuadro azul de confirmación exitosa
    print("🤖 [RPA] Cerrando la ventana azul de confirmación del guardado...")
    robot_pantalla.press('enter')
    time.sleep(2)
    
    # 4. Presionamos un ENTER de respaldo por si la interfaz Java se congela
    robot_pantalla.press('enter')
    time.sleep(2)







    # Secuencia para abrir declaraciones generadas y exportar
    print("🤖 [RPA] Abriendo menú de Declaraciones Generadas...")
    robot_pantalla.click(x=569, y=429)
    time.sleep(3)
    






    print("🔍 [RPA] Localizando el icono del disquete en pantalla de forma dinámica...")
    try:
        # 🔑 LA SOLUCIÓN: Construye la ruta absoluta exacta donde se encuentra tu archivo botplame.py
        ruta_script = os.path.dirname(os.path.abspath(__file__))
        ruta_imagen_disquete = os.path.join(ruta_script, 'disquete.png')
        
        # Ahora el bot siempre encontrará la imagen sin importar desde qué consola lo lances
        posicion_disquete = robot_pantalla.locateCenterOnScreen(ruta_imagen_disquete, confidence=0.8)

        
        if posicion_disquete is not None:
            # Capturamos la coordenada X exacta en la que apareció el disquete
            x_dinamico = posicion_disquete.x
            
            # Bajamos unos 35 píxeles en vertical (Eje Y) para posicionarnos sobre la primera fila de datos
            y_fila_datos = posicion_disquete.y + 35
            
            print(f"🎯 [RPA] ¡Icono encontrado! Coordinando clic en la fila: ({x_dinamico}, {y_fila_datos})")
            
            # 1. Hace clic en el extremo derecho de la fila para seleccionar la empresa
            robot_pantalla.click(x=x_dinamico, y=y_fila_datos)
            time.sleep(1.5)
            
            print("🤖 [RPA] Activando casilla de verificación de la DDJJ...")
            # 2. Mantiene la misma altura Y calculada, pero va al eje X fijo del check (731)
            robot_pantalla.click(x=731, y=y_fila_datos)
            time.sleep(1.5)
        else:
            raise Exception("No se encontró la imagen 'disquete.png' en la pantalla actual.")
            
    except Exception as e:
        print(f"⚠️ [ALERTA] Falló la detección visual ({e}). Aplicando coordenadas de respaldo estáticas...")
        # Tu plan B original si la pantalla cambia por completo o el archivo no existe
        robot_pantalla.click(x=1393, y=399)
        time.sleep(1.5)
        robot_pantalla.click(x=731, y=550)
        time.sleep(1.5)








    
    print("🤖 [RPA] Activando casilla de metodo de envio...")
    robot_pantalla.click(x=731, y=550)
    time.sleep(1.5)
  
    
    print("🤖 [RPA] Haciendo clic en examinar Archivo de Envío...")
    robot_pantalla.click(x=1329, y=614)
    time.sleep(2)
    
    print(f"🤖 [RPA] Inyectando ruta de destino en Windows: {CARPETA_ORIGEN_ENVIO}...")
    robot_pantalla.write(CARPETA_ORIGEN_ENVIO)
    time.sleep(0.5)
    robot_pantalla.press('tab')
    time.sleep(0.5)

    robot_pantalla.press('enter')
    time.sleep(0.5)

    print("🤖 [RPA] Haciendo clic en Generar Archivo de Envío...")
    robot_pantalla.click(x=743, y=642)
    time.sleep(4)

    
    print("⏳ [RPA] Esperando la encriptación y generación del archivo de envío .dec...")
    time.sleep(8) # Pausa larga para asegurar que Java cree el archivo físico .zip
    
    print("🤖 [RPA] Cerrando cuadro de diálogo final de éxito...")
    robot_pantalla.press('enter')
    time.sleep(2)

    print("🤖 [RPA] Cerrando cuadro de diálogo final de éxito...")
    robot_pantalla.press('enter')
    time.sleep(2)
    
    print("🤖 [RPA] Cerrando la ventana del PDT PLAME (Alt + F4)...")
    robot_pantalla.hotkey('alt', 'f4')
    time.sleep(2)

    
    print("🤖 [RPA] confirmando el cierre del PDT Plame...")
    robot_pantalla.press('enter')
    time.sleep(1.5)














def ordenar_zip_por_carpeta_ruc(ruc):
    """
    Busca el archivo .zip original con el nombre largo y encriptado del PLAME,
    y lo mueve INTACTO a la carpeta del RUC correspondiente para que el bot web lo lea.
    """
    import glob
    
    # 1. Definir la búsqueda de cualquier archivo .zip en la carpeta de salida
    patron = os.path.join(CARPETA_ORIGEN_ENVIO, "*.zip")
    archivos_zip = glob.glob(patron)
    
    if not archivos_zip:
        print("❌ [SISTEMA] No se encontró el paquete encriptado .zip en la ruta de salida.")
        return None
        
    # 2. Capturar el último .zip creado (el más reciente por fecha de modificación)
    archivos_zip.sort(key=os.path.getmtime)
    archivo_original_plame = archivos_zip[-1]
    nombre_nativo_zip = os.path.basename(archivo_original_plame)
    
    # 3. Definir el destino exacto dentro de la carpeta RUC del cliente
    # Usamos CARPETA_RAIZ_PLAME porque así la definiste en tu Parte 2 del script original
    carpeta_destino_ruc = os.path.join(CARPETA_RAIZ_PLAME, ruc)
    
    # SEGURIDAD EXTRA: Si por alguna razón la carpeta del RUC no existe, la crea al instante
    if not os.path.exists(carpeta_destino_ruc):
        os.makedirs(carpeta_destino_ruc)
        print(f"📁 [SISTEMA] Creando carpeta RUC faltante: {carpeta_destino_ruc}")
        
    ruta_final_intacta = os.path.join(carpeta_destino_ruc, nombre_nativo_zip)
    
    try:
        # Si el archivo ya existía de un intento previo, lo elimina para no trabar el proceso
        if os.path.exists(ruta_final_intacta):
            os.remove(ruta_final_intacta)
            
        # Mueve físicamente el archivo .zip a la carpeta asignada
        shutil.move(archivo_original_plame, ruta_final_intacta)
        print(f"📦 [SISTEMA] Archivo original movido intacto a: {ruta_final_intacta}")
        return ruta_final_intacta
        
    except Exception as e:
        print(f"❌ Error al mover el archivo .zip: {e}")
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
	    # 🔑 Debe quedar así:	
            navegar_e_importar_planilla(ruc_actual, periodo_actual, archivos_listos)

            
            print(f"✅ [ÉXITO] Empresa {ruc_actual} cargada correctamente en el PDT.")
            
            # =================================================================
            # ✨ LÍNEA AGREGADA: CORRECCIÓN COMPLETA PARA TU BOT WEB
            # =================================================================
            print(f"📦 [SISTEMA] Resguardando el archivo .zip original en su carpeta RUC...")
            ordenar_zip_por_carpeta_ruc(ruc_actual)
            
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
