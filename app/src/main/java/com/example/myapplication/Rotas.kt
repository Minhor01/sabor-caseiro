package com.example.myapplication

/*
 * AULA 13 e 14 - Navigation Compose
 * REQUISITO 3.1: "objeto Rotas com as rotas nomeadas como const val String"
 *
 * Uma rota é o "endereço" de uma tela dentro do NavHost. Em vez de espalhar
 * strings soltas ("receitas", "detalhe/1"...) pelo código — onde um erro de
 * digitação só aparece quando o app já está rodando —, concentramos todas aqui.
 *
 * Rotas com argumento usam o formato "nome/{argumento}". O texto entre chaves é
 * um "buraco" que o NavHost preenche com o valor real na hora de navegar
 * (ex.: "detalhe_receita/3" -> receitaId = 3).
 */
object Rotas {

    // --- Telas principais (as 4 abas do BottomNavigation / NavigationBar) ---
    const val INICIO = "inicio"
    const val RECEITAS = "receitas"
    const val DESPENSA = "despensa"
    const val FAVORITAS = "favoritas"

    // --- Telas secundárias (abrem "por cima" das abas, com botão de voltar) ---
    const val NOVA_RECEITA = "nova_receita"
    const val DETALHE_RECEITA = "detalhe_receita/{receitaId}"
    const val COZINHAR = "cozinhar/{receitaId}"
    const val DETALHE_INGREDIENTE = "detalhe_ingrediente/{ingredienteId}"

    // Nomes dos argumentos (precisam ser iguais ao que está entre chaves acima)
    const val ARG_RECEITA_ID = "receitaId"
    const val ARG_INGREDIENTE_ID = "ingredienteId"

    // Funções auxiliares (AULA 3 - Funções): montam a rota já com o valor real.
    // Assim, quem chama escreve navController.navigate(Rotas.detalheReceita(receita.id))
    // e não precisa lembrar o formato da string.
    fun detalheReceita(id: Int): String = "detalhe_receita/$id"
    fun cozinhar(id: Int): String = "cozinhar/$id"
    fun detalheIngrediente(id: Int): String = "detalhe_ingrediente/$id"
}
