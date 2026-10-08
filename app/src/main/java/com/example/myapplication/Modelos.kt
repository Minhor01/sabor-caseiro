package com.example.myapplication

/*
 * AULA 4 - Classes e Objetos
 * REQUISITO 3.2: "No mínimo 2 data classes diferentes"
 *
 * Aqui ficam os "modelos" do app, isto é, a forma dos dados:
 *   - Receita      -> 1º tipo de item (tem a lista, o formulário e o detalhe próprios)
 *   - Ingrediente  -> 2º tipo de item (a "despensa", também com lista, formulário e detalhe)
 *
 * data class: o Kotlin gera sozinho equals(), toString() e, principalmente,
 * copy(). O copy() é o que usamos para "alterar" um item de uma lista reativa:
 * como os campos são val (imutáveis), criamos uma cópia com o campo trocado e
 * colocamos a cópia no lugar do item antigo. É assim que a tela é avisada de que
 * algo mudou.
 */

/**
 * `object` (AULA 4): um objeto único, sem precisar de "Dificuldades()" para criar.
 * Usamos para guardar constantes do app. `const val` só pode ser usado dentro
 * de object/top-level e vira constante em tempo de compilação.
 */
object Dificuldades {
    const val FACIL = "Fácil"
    const val MEDIA = "Média"
    const val DIFICIL = "Difícil"

    // Lista com todas as opções. Usada nos chips do formulário e no gráfico da tela Início.
    val todas = listOf(FACIL, MEDIA, DIFICIL)
}

/**
 * 1ª data class: uma receita.
 *
 * `ingredientes` guarda só os NOMES dos ingredientes que a receita pede. Esse
 * é o "gancho" que liga as duas listas: a tela de detalhes compara esses nomes
 * com a despensa para dizer o que você já tem e o que falta.
 */
data class Receita(
    val id: Int,                       // identificador único: é o que viaja na rota até a tela de Detalhes
    val nome: String,
    val descricao: String,
    val tempoPreparoMin: Int,
    val porcoes: Int,
    val dificuldade: String,           // um dos valores de Dificuldades
    val ingredientes: List<String>,
    val passos: List<String>,
    val favorita: Boolean = false      // valor padrão: toda receita nova começa sem ser favorita
)

/**
 * 2ª data class: um ingrediente guardado na despensa.
 */
data class Ingrediente(
    val id: Int,
    val nome: String,
    val quantidade: Int,
    val unidade: String                // "g", "ml", "un", "pct"...
) {
    // Propriedade calculada (AULA 4): não guarda nada, só responde com base nos outros campos.
    val acabou: Boolean
        get() = quantidade <= 0
}
