package com.example.myapplication.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas

/*
 * TELA 2 de 8 — LISTA DE RECEITAS (aba)
 *
 * AULA 7 + lista de tarefas/mercado feita em aula: LazyColumn + Card + mutableStateListOf
 * REQUISITOS 3.2 (lista 1): LazyColumn + Card, ADICIONAR, REMOVER, clicar no item abre Detalhes
 *
 * - LazyColumn + items(lista): só desenha os itens que cabem na tela (eficiente) e
 *   se atualiza sozinha porque Repositorio.receitas é uma mutableStateListOf.
 * - ADICIONAR : botão "Nova" -> navController.navigate(Rotas.NOVA_RECEITA) (formulário na tela 3)
 * - REMOVER   : ícone de lixeira no cartão -> Repositorio.removerReceita(...)
 * - DETALHES  : clicar no cartão -> navController.navigate(Rotas.detalheReceita(receita.id))
 *               (o id do item certo viaja na rota; não é um valor fixo)
 * - Extra     : campo de busca (remember + mutableStateOf) que filtra a lista ao digitar.
 */
@Composable
fun ReceitasScreen(navController: NavController) {
    // remember + mutableStateOf (AULA 7): guarda o texto digitado e redesenha ao mudar.
    var busca by remember { mutableStateOf("") }

    // Esta linha lê a lista reativa; se algo for adicionado/removido, a tela recompõe.
    val filtradas = Repositorio.receitas.filter { it.nome.contains(busca, ignoreCase = true) }

    Column(modifier = Modifier.fillMaxSize()) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = busca,
                onValueChange = { busca = it },
                label = { Text("Buscar receita") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            Button(onClick = { navController.navigate(Rotas.NOVA_RECEITA) }) {
                Icon(Icons.Default.Add, contentDescription = null)
                Text("Nova")
            }
        }

        if (filtradas.isEmpty()) {
            TelaVazia(
                mensagem = if (Repositorio.receitas.isEmpty())
                    "Você ainda não tem receitas. Que tal criar a primeira?"
                else
                    "Nenhuma receita encontrada para \"$busca\".",
                textoBotao = if (Repositorio.receitas.isEmpty()) "Criar receita" else null,
                onBotao = { navController.navigate(Rotas.NOVA_RECEITA) }
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // key = id: ajuda o Compose a saber QUAL item mudou/saiu ao remover da lista.
                items(filtradas, key = { it.id }) { receita ->
                    CartaoReceita(
                        receita = receita,
                        onAbrir = { navController.navigate(Rotas.detalheReceita(receita.id)) },
                        onFavoritar = { Repositorio.alternarFavorita(receita.id) },
                        onRemover = { Repositorio.removerReceita(receita) }
                    )
                }
            }
        }
    }
}
