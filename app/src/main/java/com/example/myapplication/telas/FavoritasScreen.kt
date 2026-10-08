package com.example.myapplication.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas
import com.example.myapplication.irParaAba

/*
 * TELA 8 de 8 — FAVORITAS (aba)
 *
 * AULA 7 + lista de tarefas feita em aula: LazyColumn + Card e lista filtrada
 * REQUISITO 3.1: 4ª aba do BottomNavigation; todo item e botão tem propósito real.
 *
 * Mostra só as receitas marcadas com o coração. É a MESMA lista (Repositorio.receitas)
 * vista com um filtro: favoritar numa tela e desfavoritar nesta mexe no mesmo dado,
 * por isso uma aba reflete a outra na hora. Reaproveita o CartaoReceita (sem lixeira:
 * aqui o coração é a ação principal, e a remoção da receita fica na aba Receitas).
 */
@Composable
fun FavoritasScreen(navController: NavController) {
    val favoritas = Repositorio.receitas.filter { it.favorita }

    if (favoritas.isEmpty()) {
        TelaVazia(
            mensagem = "Nenhuma favorita ainda. Toque no coração de uma receita para guardá-la aqui.",
            textoBotao = "Ver receitas",
            onBotao = { navController.irParaAba(Rotas.RECEITAS) }
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(favoritas, key = { it.id }) { receita ->
                CartaoReceita(
                    receita = receita,
                    onAbrir = { navController.navigate(Rotas.detalheReceita(receita.id)) },
                    onFavoritar = { Repositorio.alternarFavorita(receita.id) }
                    // onRemover não é passado -> a lixeira não aparece nesta tela
                )
            }
        }
    }
}
