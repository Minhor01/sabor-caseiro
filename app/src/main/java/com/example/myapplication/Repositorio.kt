package com.example.myapplication

import androidx.compose.runtime.mutableStateListOf

/*
 * AULA 7 - Compose (estado) / lista de tarefas e de mercado feitas em aula
 * REQUISITO 3.2: "listas guardadas numa lista reativa (mutableStateListOf)"
 * REQUISITO 5: a lista pode viver só na memória (some quando o app fecha)
 *
 * DECISÃO DE ORGANIZAÇÃO (vai para a documentação, seção 4):
 * as duas listas ficam neste `object` (um único objeto no app inteiro) em vez de
 * dentro de uma tela. Motivos:
 *   1. Várias telas precisam das MESMAS listas (Início, Receitas, Favoritas,
 *      Detalhes...). Se cada tela tivesse a sua, os dados não bateriam.
 *   2. Um object sobrevive quando a Activity é recriada (ex.: girar a tela), o que
 *      não acontece com um `remember { ... }` dentro de uma tela.
 *   3. As funções de adicionar/remover ficam todas num lugar só, e as telas apenas
 *      chamam Repositorio.adicionarReceita(...), Repositorio.removerIngrediente(...)...
 * No próximo trabalho (dados que sobrevivem ao fechar o app) é aqui que a
 * persistência entraria, sem precisar mexer nas telas.
 *
 * mutableStateListOf: é uma lista "observável". Quando um item é adicionado,
 * removido ou trocado, toda @Composable que LEU a lista é redesenhada sozinha.
 * Por isso a tela atualiza sem a gente mandar "atualizar".
 */
object Repositorio {

    // As duas listas reativas do app (uma para cada data class)
    val receitas = mutableStateListOf<Receita>()
    val ingredientes = mutableStateListOf<Ingrediente>()

    // Contadores para gerar ids únicos. Precisam ser declarados ANTES do bloco init.
    private var proximoIdReceita = 1
    private var proximoIdIngrediente = 1

    init {
        carregarDadosIniciais()
    }

    // ------------------------------------------------------------------
    // RECEITAS
    // ------------------------------------------------------------------

    /** ADICIONAR (3.2): cria a Receita com um id novo e coloca no fim da lista. */
    fun adicionarReceita(
        nome: String,
        descricao: String,
        tempoPreparoMin: Int,
        porcoes: Int,
        dificuldade: String,
        ingredientes: List<String>,
        passos: List<String>,
        favorita: Boolean = false
    ) {
        receitas.add(
            Receita(
                id = proximoIdReceita++,
                nome = nome,
                descricao = descricao,
                tempoPreparoMin = tempoPreparoMin,
                porcoes = porcoes,
                dificuldade = dificuldade,
                ingredientes = ingredientes,
                passos = passos,
                favorita = favorita
            )
        )
    }

    /** REMOVER (3.2). */
    fun removerReceita(receita: Receita) {
        receitas.remove(receita)
    }

    /** Usada pela tela de Detalhes: encontra a receita certa pelo id recebido na rota. */
    fun buscarReceita(id: Int): Receita? = receitas.firstOrNull { it.id == id }

    /** Liga/desliga o coração. Usa copy() porque os campos da data class são val. */
    fun alternarFavorita(id: Int) {
        val posicao = receitas.indexOfFirst { it.id == id }
        if (posicao >= 0) {
            val atual = receitas[posicao]
            receitas[posicao] = atual.copy(favorita = !atual.favorita)
        }
    }

    // ------------------------------------------------------------------
    // INGREDIENTES (despensa)
    // ------------------------------------------------------------------

    /** ADICIONAR (3.2). */
    fun adicionarIngrediente(nome: String, quantidade: Int, unidade: String) {
        ingredientes.add(
            Ingrediente(
                id = proximoIdIngrediente++,
                nome = nome,
                quantidade = quantidade,
                unidade = unidade
            )
        )
    }

    /** REMOVER (3.2). */
    fun removerIngrediente(ingrediente: Ingrediente) {
        ingredientes.remove(ingrediente)
    }

    fun buscarIngrediente(id: Int): Ingrediente? = ingredientes.firstOrNull { it.id == id }

    /** Soma (ou subtrai, com delta negativo) na quantidade, sem deixar ficar negativa. */
    fun alterarQuantidade(id: Int, delta: Int) {
        val posicao = ingredientes.indexOfFirst { it.id == id }
        if (posicao >= 0) {
            val atual = ingredientes[posicao]
            ingredientes[posicao] = atual.copy(quantidade = maxOf(0, atual.quantidade + delta))
        }
    }

    /** Marca que o ingrediente acabou (quantidade = 0). Usado no clique longo da Despensa. */
    fun marcarAcabou(id: Int) {
        val posicao = ingredientes.indexOfFirst { it.id == id }
        if (posicao >= 0) {
            ingredientes[posicao] = ingredientes[posicao].copy(quantidade = 0)
        }
    }

    /**
     * "Comprei": usado no Detalhe da Receita para um ingrediente que faltava.
     * Se já existe na despensa (mas acabou), repõe 1; se não existe, cria com 1 un.
     */
    fun comprarIngrediente(nome: String) {
        val existente = ingredientes.indexOfFirst { it.nome.equals(nome.trim(), ignoreCase = true) }
        if (existente >= 0) {
            val atual = ingredientes[existente]
            ingredientes[existente] = atual.copy(quantidade = maxOf(1, atual.quantidade + 1))
        } else {
            adicionarIngrediente(nome.trim(), 1, "un")
        }
    }

    // ------------------------------------------------------------------
    // CRUZAMENTO DAS DUAS LISTAS (a "complexidade extra" do Detalhe, seção 3.2)
    // ------------------------------------------------------------------

    /** O ingrediente está na despensa E ainda tem quantidade? */
    fun temNaDespensa(nomeIngrediente: String): Boolean =
        ingredientes.any {
            it.nome.equals(nomeIngrediente.trim(), ignoreCase = true) && it.quantidade > 0
        }

    /** Quais ingredientes da receita NÃO estão disponíveis na despensa. */
    fun ingredientesQueFaltam(receita: Receita): List<String> =
        receita.ingredientes.filter { !temNaDespensa(it) }

    /** Dá para cozinhar agora? (nenhum ingrediente faltando) */
    fun podeCozinhar(receita: Receita): Boolean = ingredientesQueFaltam(receita).isEmpty()

    /** Quais receitas usam um determinado ingrediente (usado no Detalhe do Ingrediente). */
    fun receitasQueUsam(nomeIngrediente: String): List<Receita> =
        receitas.filter { receita ->
            receita.ingredientes.any { it.equals(nomeIngrediente.trim(), ignoreCase = true) }
        }

    // ------------------------------------------------------------------
    // DADOS DE EXEMPLO (para o app não abrir vazio na apresentação)
    // ------------------------------------------------------------------
    private fun carregarDadosIniciais() {
        // Despensa. Repare: Manteiga com quantidade 0 (acabou) e sem Cenoura/Fermento/Azeite...
        adicionarIngrediente("Ovos", 6, "un")
        adicionarIngrediente("Farinha de trigo", 1000, "g")
        adicionarIngrediente("Açúcar", 500, "g")
        adicionarIngrediente("Óleo", 900, "ml")
        adicionarIngrediente("Queijo", 200, "g")
        adicionarIngrediente("Sal", 1, "pct")
        adicionarIngrediente("Alho", 4, "un")
        adicionarIngrediente("Macarrão", 500, "g")
        adicionarIngrediente("Leite", 1000, "ml")
        adicionarIngrediente("Manteiga", 0, "g")
        adicionarIngrediente("Cebola", 3, "un")

        // ...assim cada receita mostra um estado diferente na tela de Detalhes.
        adicionarReceita(
            nome = "Bolo de Cenoura",
            descricao = "Bolo fofinho e úmido, ótimo para o café da tarde.",
            tempoPreparoMin = 60,
            porcoes = 8,
            dificuldade = Dificuldades.FACIL,
            ingredientes = listOf("Cenoura", "Ovos", "Açúcar", "Farinha de trigo", "Óleo", "Fermento"),
            passos = listOf(
                "Pré-aqueça o forno a 180 °C e unte uma forma com óleo e farinha.",
                "Bata no liquidificador as cenouras picadas, os ovos e o óleo.",
                "Em uma tigela, misture o açúcar e a farinha e despeje a mistura do liquidificador.",
                "Adicione o fermento e misture delicadamente.",
                "Asse por cerca de 40 minutos.",
                "Deixe esfriar antes de desenformar."
            ),
            favorita = true
        )
        adicionarReceita(
            nome = "Macarrão ao Alho e Óleo",
            descricao = "Clássico italiano simples, pronto em poucos minutos.",
            tempoPreparoMin = 20,
            porcoes = 2,
            dificuldade = Dificuldades.FACIL,
            ingredientes = listOf("Macarrão", "Alho", "Azeite", "Sal", "Salsinha"),
            passos = listOf(
                "Cozinhe o macarrão em água com sal até ficar al dente.",
                "Frite o alho fatiado no azeite em fogo baixo.",
                "Misture o macarrão escorrido ao alho e ao azeite.",
                "Finalize com salsinha picada."
            )
        )
        adicionarReceita(
            nome = "Omelete de Queijo",
            descricao = "Café da manhã rápido e cheio de proteína.",
            tempoPreparoMin = 10,
            porcoes = 1,
            dificuldade = Dificuldades.FACIL,
            ingredientes = listOf("Ovos", "Queijo", "Sal", "Leite"),
            passos = listOf(
                "Bata os ovos com o leite e uma pitada de sal.",
                "Aqueça uma frigideira untada.",
                "Despeje os ovos e adicione o queijo.",
                "Dobre ao meio quando firmar e sirva."
            )
        )
        adicionarReceita(
            nome = "Risoto de Cogumelos",
            descricao = "Cremoso e aromático, pede paciência para mexer.",
            tempoPreparoMin = 45,
            porcoes = 4,
            dificuldade = Dificuldades.MEDIA,
            ingredientes = listOf("Arroz arbóreo", "Cogumelos", "Cebola", "Caldo de legumes", "Manteiga", "Queijo"),
            passos = listOf(
                "Refogue a cebola picada na manteiga.",
                "Adicione os cogumelos e deixe dourar.",
                "Junte o arroz e mexa por 2 minutos.",
                "Acrescente o caldo quente aos poucos, mexendo sempre.",
                "Finalize com queijo ralado e um pouco de manteiga."
            )
        )
        adicionarReceita(
            nome = "Lasanha de Frango",
            descricao = "Receita de domingo, com várias camadas e molho branco.",
            tempoPreparoMin = 90,
            porcoes = 6,
            dificuldade = Dificuldades.DIFICIL,
            ingredientes = listOf("Massa de lasanha", "Frango", "Molho de tomate", "Queijo", "Leite", "Farinha de trigo", "Manteiga"),
            passos = listOf(
                "Cozinhe e desfie o frango; refogue com o molho de tomate.",
                "Faça o molho branco: derreta a manteiga, junte a farinha e depois o leite.",
                "Cozinhe a massa conforme a embalagem.",
                "Monte camadas de massa, frango, molho branco e queijo.",
                "Asse a 200 °C por 30 minutos, até gratinar."
            )
        )
    }
}
