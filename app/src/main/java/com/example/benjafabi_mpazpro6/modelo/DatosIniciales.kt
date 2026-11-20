package com.example.benjafabi_mpazpro6.modelo

const val CURSO_DEMO = "6.° A"

private const val MA = "MA06 OA 03"
private const val LE = "LE06 OA 03"
private const val CN = "CN06 OA 02"
private const val HI = "HI06 OA 02"

private val ACT = TipoPregunta.ACTIVIDAD
private val DES = TipoPregunta.DESAFIO
private val PF = TipoPregunta.PRUEBA_FINAL

private fun alt(vararg opciones: String) = opciones.joinToString("|")

private fun oa(codigo: String, asignatura: String, unidad: String, eje: String, descripcion: String) =
    ObjetivoAprendizaje(
        codigo, "6.º Básico", asignatura, unidad, eje, descripcion,
        "Texto del Estudiante 6.º Básico (Mineduc): pendiente de completar",
        EstadoContenido.PUBLICADO
    )

private fun contenido(id: Int, oa: String, titulo: String, explicacion: String, ejemplo: String) =
    Contenido(
        id = id, oaCodigo = oa, titulo = titulo, explicacion = explicacion, ejemplo = ejemplo,
        instrucciones = "Lee la explicación y el ejemplo. Luego resuelve las actividades y el desafío antes de rendir la prueba final.",
        origen = "propio"
    )

private fun p(
    id: Int, oa: String, tipo: TipoPregunta, dificultad: Int,
    enunciado: String, opciones: String, correcta: Int, retro: String = ""
) = Pregunta(id, oa, tipo, enunciado, opciones, correcta, dificultad, retro)

// ───────────── MATEMÁTICA ─────────────
private val preguntasMatematica = listOf(
    p(101, MA, ACT, 1, "En un estuche hay 3 lápices rojos y 5 lápices azules. ¿Cuál es la razón entre la cantidad de lápices rojos y el total de lápices?", alt("3 : 5", "5 : 3", "3 : 8", "5 : 8"), 2, "El total es 3 + 5 = 8. La razón es 3 : 8."),
    p(102, MA, ACT, 2, "Si la razón entre manzanas rojas y verdes es 2:3 y hay 6 manzanas rojas, ¿cuántas verdes hay?", alt("6", "9", "12", "15"), 1, "Multiplicas por 3: 3 x 3 = 9."),
    p(103, MA, ACT, 2, "Un vehículo recorre 120 km en 2 horas. ¿Cuál es la razón simplificada de km por hora?", alt("120 : 2", "60 : 1", "30 : 1", "2 : 120"), 1, "120 : 2 simplificado es 60 : 1."),
    p(104, MA, DES, 3, "En un curso de 40 estudiantes, la razón entre niños y niñas es 3:2. Si ingresan 5 niñas nuevas, ¿cuál es la nueva razón en su forma más simple?", alt("24 : 21", "3 : 3", "24 : 16", "8 : 7"), 3, "24 niños y 21 niñas -> 8 : 7."),
    p(1041, MA, DES, 3, "Si la razón entre hombres y mujeres en una reunión es 4:5 y hay 20 hombres, ¿cuántas personas hay en total?", alt("25", "45", "20", "50"), 1, "5 x 4 = 20 hombres; 5 x 5 = 25 mujeres. Total = 45."),
    p(1042, MA, DES, 3, "Un mapa tiene escala 1:500. Si la distancia en el papel es de 4 cm, ¿cuánto mide en la realidad en metros?", alt("20 m", "200 m", "2 m", "2000 m"), 0, "4 x 500 = 2000 cm = 20 metros."),
    p(105, MA, PF, 2, "¿Cuál expresión es equivalente a 4 : 12?", alt("1 : 3", "1 : 4", "2 : 5", "3 : 1"), 0, "Dividiendo por 4 resulta 1 : 3."),
    p(106, MA, PF, 2, "En una fiesta la razón entre globos inflados y desinflados es 5:1. Si hay 30 globos en total, ¿cuántos están inflados?", alt("5", "20", "25", "30"), 2, "Hay 6 partes iguales (5 + 1). Cada parte vale 30 : 6 = 5. Los inflados son 5 partes: 5 x 5 = 25."),
    p(107, MA, PF, 2, "Si un auto consume 1 litro de gasolina por cada 15 km recorridos, ¿cuántos litros necesitará para recorrer 75 km?", alt("3 litros", "5 litros", "6 litros", "15 litros"), 1, "La razón es 1 : 15. Para 75 km (15 x 5) se necesitan 1 x 5 = 5 litros."),
    p(108, MA, PF, 3, "En un plano a escala 1:100, el largo de una cancha mide 8 cm. ¿Cuál es el largo real de la cancha en metros?", alt("8 metros", "80 metros", "800 metros", "0,8 metros"), 0, "8 cm x 100 = 800 cm, que equivalen a 8 metros.")
)

// ───────────── LENGUAJE ─────────────
private val preguntasLenguaje = listOf(
    p(201, LE, ACT, 1, "¿Cuál de los siguientes textos corresponde a un texto NO literario?", alt("Un cuento sobre un dragón.", "Un poema a la primavera.", "Una noticia científica.", "Una leyenda."), 2, "Las noticias corresponden a hechos reales."),
    p(202, LE, ACT, 2, "¿Cuál es la función principal de un artículo informativo?", alt("Entretener.", "Explicar un tema objetivamente.", "Expresar rimas.", "Enseñar una moraleja."), 1, "Transmite conocimientos reales."),
    p(203, LE, ACT, 2, "En 'Los científicos afirman que la reforestación disminuye el cambio climático', ¿qué respalda la postura?", alt("Una opinión.", "Una fantasía.", "El conocimiento comprobado de especialistas.", "Un adjetivo."), 2, "La evidencia científica respaldada."),
    p(204, LE, DES, 3, "En una columna de opinión, ¿cómo se diferencia un hecho de una opinión?", alt("Hechos en verso, opinión en prosa.", "Los hechos son comprobables objetivamente; la opinión es un juicio personal.", "Los hechos ocurren en el futuro.", "Son lo mismo."), 1, "Hecho = dato comprobable."),
    p(2041, LE, DES, 3, "¿Por qué es importante identificar el propósito del autor en un texto explicativo?", alt("Para evaluar si la información es objetiva y confiable.", "Para saber si es largo.", "Para aprender de memoria las palabras.", "Para adivinar la moraleja."), 0, "Permite comprender la intención comunicativa."),
    p(2042, LE, DES, 3, "¿Qué recurso ayuda a comprender mejor un informe científico complejo?", alt("Los gráficos, diagramas y tablas de datos.", "Los diálogos dramáticos.", "Las rimas consonantes.", "Las opiniones de personajes ficticios."), 0, "Los elementos visuales complementan los datos."),
    p(205, LE, PF, 2, "¿En qué publicación predomina el texto no literario?", alt("Antología de cuentos.", "Diario o periódico nacional.", "Mitos y leyendas.", "Novela gráfica."), 1),
    p(206, LE, PF, 2, "¿Qué característica es indispensable en la redacción de un informe de investigación?", alt("Utilizar lenguaje ambiguo y figurado.", "Incluir personajes imaginarios.", "Utilizar un lenguaje claro, preciso y objetivo.", "Escribir en estrofas y versos."), 2),
    p(207, LE, PF, 2, "Si lees en una infografía que 'El 70% de la superficie terrestre está cubierta por agua', ¿a qué tipo de información corresponde?", alt("A un dato cuantitativo u objetivo.", "A la opinión del diseñador gráfico.", "A una expresión de deseo personal.", "A un recurso puramente poético."), 0),
    p(208, LE, PF, 3, "¿Cuál es el propósito de contrastar información en dos textos no literarios sobre un mismo tema?", alt("Memorizar el texto más largo.", "Evaluar la validez, diferentes perspectivas y amplitud del contenido.", "Decidir cuál texto tiene la tipografía más vistosa.", "Eliminar todas las diferencias para dejar un solo párrafo."), 1)
)

// ───────────── CIENCIAS NATURALES ─────────────
private val preguntasCiencias = listOf(
    p(301, CN, ACT, 1, "¿Cuál es la función principal del sistema circulatorio?", alt("Digerir comida.", "Filtrar aire.", "Transportar nutrientes y oxígeno.", "Eliminar heces."), 2, "Transporta sangre con oxígeno y nutrientes."),
    p(302, CN, ACT, 2, "¿Dónde ocurre la mayor absorción de nutrientes?", alt("En la boca.", "En el estómago.", "En el intestino delgado.", "En el intestino grueso."), 2, "En las vellosidades del intestino delgado."),
    p(303, CN, ACT, 2, "¿Qué gas intercambian los alvéolos pulmonares hacia la sangre?", alt("Dióxido de carbono.", "Oxígeno.", "Nitrógeno.", "Metano."), 1, "Capturan oxígeno para la sangre."),
    p(304, CN, DES, 3, "Durante el ejercicio, ¿cómo responden los sistemas respiratorio y circulatorio?", alt("Disminuyen el ritmo.", "Aumentan la respiración y el bombeo del corazón.", "Frenan la circulación.", "Se acumula dióxido de carbono."), 1, "Aceleran para abastecer la demanda muscular."),
    p(3041, CN, DES, 3, "Si el intestino delgado sufriera una lesión en sus vellosidades, ¿cuál sería la consecuencia directa?", alt("Dificultad para absorber los nutrientes de los alimentos hacia la sangre.", "Pérdida de la capacidad de respirar.", "Falla en los latidos del corazón.", "Falta de producción de saliva."), 0, "Las vellosidades son responsables de absorber nutrientes."),
    p(3042, CN, DES, 3, "¿Por qué se dice que el sistema respiratorio y excretor colaboran en la purificación del cuerpo?", alt("Porque ambos expulsan desechos metabólicos (CO2 y orina/sudor).", "Porque ambos digieren alimentos.", "Porque ambos bombean sangre.", "No colaboran entre sí."), 0, "Ambos eliminan sustancias de desecho."),
    p(305, CN, PF, 2, "¿Qué órgano filtra la sangre para formar la orina?", alt("El hígado.", "El estómago.", "Los riñones.", "Los pulmones."), 2),
    p(306, CN, PF, 2, "El dióxido de carbono es un desecho del metabolismo celular. ¿Qué sistema se encarga de expulsarlo fuera del cuerpo?", alt("El sistema respiratorio.", "El sistema digestivo.", "El sistema linfático.", "El sistema tegumentario."), 0),
    p(307, CN, PF, 2, "¿Cuál es el orden correcto del recorrido del alimento en el tubo digestivo?", alt("Boca → Intestino delgado → Estómago → Esófago.", "Boca → Esófago → Estómago → Intestino delgado → Intestino grueso.", "Esófago → Boca → Intestino grueso → Estómago.", "Estómago → Esófago → Boca → Intestino delgado."), 1),
    p(308, CN, PF, 3, "¿Qué ocurriría si el sistema circulatorio fallara en la entrega de oxígeno a las células?", alt("Las células producirían más energía de lo habitual.", "Las células no podrían realizar la respiración celular y morirían por falta de energía.", "El sistema digestivo reemplazaría completamente la función sanguínea.", "La absorción de calcio aumentaría automáticamente."), 1)
)

// ───────────── HISTORIA ─────────────
private val preguntasHistoria = listOf(
    p(401, HI, ACT, 1, "¿En qué hito las mujeres lograron votar en elecciones presidenciales en Chile?", alt("Constitución 1833.", "Ley de Sufragio Femenino 1949.", "Independencia 1810.", "Reforma agraria 1967."), 1, "En 1949 se aprobó el voto femenino pleno."),
    p(402, HI, ACT, 2, "¿Qué significó la Cédula Única Electoral en 1958?", alt("Voto solo para ricos.", "Transparencia y reducción del cohecho.", "Eliminación de partidos.", "Prohibición del voto campesino."), 1, "Aseguró el voto secreto e independiente."),
    p(403, HI, ACT, 2, "¿Cómo la educación pública ayudó a la democratización?", alt("Limitando la universidad.", "Permitiendo acceso a derechos a sectores populares.", "Prohibiendo liceos.", "Obligando a votar."), 1, "Integró y alfabetizó a más población."),
    p(404, HI, DES, 3, "¿Qué impacto tuvo el voto a analfabetos y a jóvenes desde 18 años en 1970?", alt("Restringió la participación.", "Logró un sufragio universal más inclusivo.", "Eliminó elecciones.", "Dio poder a FFAA."), 1, "Amplió la ciudadanía a toda la población adulta."),
    p(4041, HI, DES, 3, "¿Cuál fue el rol fundamental de las organizaciones sindicales en el siglo XX?", alt("Exigir derechos laborales e integración social.", "Impedir las elecciones.", "Eliminar la educación pública.", "Organizar censos."), 0, "Impulsaron demandas y derechos sociales."),
    p(4042, HI, DES, 3, "¿Por qué la libertad de prensa es clave en el proceso de democratización?", alt("Permite a la ciudadanía informarse sobre distintas posturas y fiscalizar al poder.", "Obliga a todos a pensar igual.", "Elimina los partidos políticos.", "Sirve solo para hacer propaganda oficial."), 0, "Garantiza el debate libre e informado."),
    p(405, HI, PF, 2, "¿A qué se refiere la 'democratización'?", alt("Inclusión ciudadana en decisiones públicas.", "Poder en una sola persona.", "Cierre del Congreso.", "Privatización de servicios."), 0),
    p(406, HI, PF, 2, "¿Cuál de los siguientes grupos sociales adquirió protagonismo político durante la primera mitad del siglo XX en Chile?", alt("Los encomenderos coloniales.", "La clase media y los obreros urbanos y mineros.", "La aristocracia terrateniente exclusivamente.", "Los comerciantes extranjeros únicamente."), 1),
    p(407, HI, PF, 2, "¿Qué rol cumplieron los sindicatos de trabajadores en el proceso de democratización?", alt("Exigir mejores condiciones laborales y el reconocimiento de sus derechos sociales.", "Impedir la votación de los obreros en elecciones municipales.", "Organizar los censos de población.", "Financiar exclusivamente las campañas de la elite terrateniente."), 0),
    p(408, HI, PF, 3, "¿Por qué la ampliación del sufragio es fundamental para fortalecer un sistema democrático?", alt("Porque garantiza que solo un grupo reducido de personas tome decisiones.", "Porque asegura que las autoridades representen la diversidad de la sociedad.", "Porque evita que existan elecciones libres.", "Porque elimina la división de los poderes del Estado."), 1)
)

suspend fun poblarBaseInicial(db: AppDatabase) {
    db.usuarioDao().insertarUsuarios(
        listOf(
            Usuario("est-01", "martina6a", "clave123", "Martina Torres", Rol.ESTUDIANTE, CURSO_DEMO),
            Usuario("doc-01", "prof.gonzalez", "clave123", "Prof. María González", Rol.DOCENTE),
            Usuario("utp-01", "utp.vera", "clave123", "Coord. Roberto Vera", Rol.UTP)
        )
    )

    val c = db.curricularDao()

    c.insertarOas(
        listOf(
            oa(MA, "Matemática", "Unidad 1: Razones", "Números y operaciones",
                "Demostrar que comprenden el concepto de razón de forma concreta, pictórica y simbólica."),
            oa(LE, "Lenguaje y Comunicación", "Unidad 1: Textos no literarios", "Lectura",
                "Leer de manera autónoma y comprender textos no literarios para ampliar su conocimiento del mundo y formar una opinión fundamentada."),
            oa(CN, "Ciencias Naturales", "Unidad 1: Sistemas que nutren el cuerpo", "Ciencias de la Vida",
                "Explicar la función del sistema digestivo, respiratorio, circulatorio y excretor en la nutrición del organismo."),
            oa(HI, "Historia, Geografía y Ciencias Sociales", "Unidad 1: Democratización en Chile", "Historia",
                "Explicar los principales elementos del proceso de democratización de la sociedad chilena durante el siglo XX.")
        )
    )

    c.insertarContenidos(
        listOf(
            contenido(1, MA, "Razones: comparar dos cantidades",
                "Una razón es una comparación entre dos cantidades mediante una división. Nos indica cuántas veces una cantidad contiene a otra o en qué proporción se relacionan dos grupos de elementos.",
                "Si en una receta se usan 2 tazas de agua por cada 1 taza de arroz, la razón entre el agua y el arroz es 2 es a 1 (se escribe 2 : 1 o 2/1)."),
            contenido(2, LE, "Textos no literarios",
                "Los textos no literarios tienen como propósito principal informar, explicar, instruir o persuadir sobre hechos reales o conceptos objetivos. A diferencia de la literatura (cuentos, mitos o poesías, que usan la ficción), los textos no literarios, como los artículos de divulgación, las noticias y las biografías, entregan datos concretos y permiten desarrollar un pensamiento crítico basado en evidencias.",
                "Una noticia sobre un nuevo récord deportivo informa hechos que se pueden comprobar; un cuento sobre un dragón es ficción."),
            contenido(3, CN, "Sistemas que nutren al cuerpo",
                "El cuerpo humano funciona como un sistema integrado donde varios órganos colaboran para mantener con vida las células. La nutrición celular depende de la acción conjunta de cuatro sistemas:\n- Sistema digestivo: transforma los alimentos en nutrientes.\n- Sistema respiratorio: captura oxígeno y elimina dióxido de carbono.\n- Sistema circulatorio: transporta oxígeno y nutrientes a todo el cuerpo.\n- Sistema excretor: elimina los desechos del metabolismo.",
                "Cuando comes una manzana, el sistema digestivo la transforma en nutrientes, la sangre los lleva a tus músculos y, gracias al oxígeno que respiras, tus células obtienen energía."),
            contenido(4, HI, "La democratización de Chile en el siglo XX",
                "Durante el siglo XX en Chile, la democracia se expandió significativamente. Este proceso implicó la ampliación del sufragio (voto de las mujeres en 1934 en elecciones municipales y en 1949 en todas las elecciones; en 1970, voto de personas analfabetas y edad para votar desde los 18 años), el fortalecimiento de las organizaciones sociales y sindicales, y una mayor participación de las personas en la vida política del país.",
                "Antes de 1949, las mujeres solo podían votar en elecciones municipales; después de esa ley, también pudieron votar por presidente y parlamentarios.")
        )
    )

    c.insertarPreguntas(preguntasMatematica + preguntasLenguaje + preguntasCiencias + preguntasHistoria)

    val todos = listOf(MA, LE, CN, HI)
    c.insertarAsignaciones(todos.map { Asignacion(oaCodigo = it, curso = CURSO_DEMO) })
    todos.forEach { c.insertarConfig(ConfigPrueba(oaCodigo = it, maxIntentos = 3, habilitada = true)) }
}