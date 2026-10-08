@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.example.myapplication.telas

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Ingrediente
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas

/*
 * TELA 6 de 8 — DESPENSA (aba) = LISTA DE INGREDIENTES
 *
 * AULA 7 + lista de mercado feita em aula: LazyColumn + Card + formulário + remover
 * REQUISITOS 3.2 (lista 2): LazyColumn + Card, ADICIONAR, REMOVER, clicar no item abre Detalhes
 *
 * - ADICIONAR : formulário NA PRÓPRIA TELA (nome + quantidade numérica + unidade + botão).
 *               (a lista de Receitas usa uma tela separada para o formulário; aqui é inline,
 *               para mostrar as duas formas de fazer)
 * - REMOVER   : ícone de lixeira no cartão
 * - MARCAR    : clique LONGO no cartão marca o ingrediente como "acabou" (combinedClickable)
 * - DETALHES  : clique no cartão -> navController.navigate(Rotas.detalheIngrediente(id))
 *
 * Toda a tela é UM LazyColumn: o formulário é o primeiro item e os ingredientes vêm depois.
 * Assim tudo rola junto e o formulário não ocupa a tela toda quando o teclado abre.
 */
@Composable
fun DespensaScreen(navController: NavController) {
    val contexto = LocalContext.current

    // Estados do formulário
    var nome by remember { mutableStateOf("") }
    var quantidade by remember { mutableStateOf("") }
    var unidade by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // ---------- Item 0: o formulário de ADICIONAR ----------
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Adicionar ingrediente",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = nome,
                        onValueChange = { nome = it },
                        label = { Text("Nome (ex.: Cenoura)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = quantidade,
                            onValueChange = { quantidade = it.filter { c -> c.isDigit() } },
                            label = { Text("Quantidade") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = unidade,
                            onValueChange = { unidade = it },
                            label = { Text("Unidade") },
                            placeholder = { Text("g, ml, un") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    if (erro != null) {
                        Text(
                            text = erro!!,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Button(
                        onClick = {
                            val qtd = quantidade.toIntOrNull()
                            if (nome.isBlank()) {
                                erro = "Informe o nome do ingrediente."
                            } else if (qtd == null) {
                                erro = "Informe a quantidade."
                            } else {
                                erro = null
                                // ADICIONAR de verdade na lista reativa
                                Repositorio.adicionarIngrediente(
                                    nome = nome.trim(),
                                    quantidade = qtd,
                                    unidade = unidade.trim().ifBlank { "un" }
                                )
                                // limpa o formulário
                                nome = ""
                                quantidade = ""
                                unidade = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Adicionar à despensa")
                    }
                }
            }
        }

        // ---------- Item 1: dica de uso ----------
        item {
            Text(
                text = "Toque para ver detalhes. Segure um item para marcar que acabou.",
                style = MaterialTheme.typography.bodySmall
            )
        }

        // ---------- Os ingredientes (LazyColumn + items + Card) ----------
        if (Repositorio.ingredientes.isEmpty()) {
            item {
                Text("Sua despensa está vazia. Adicione o primeiro ingrediente acima.")
            }
        }
        items(Repositorio.ingredientes, key = { it.id }) { ingrediente ->
            CartaoIngrediente(
                ingrediente = ingrediente,
                onAbrir = { navController.navigate(Rotas.detalheIngrediente(ingrediente.id)) },
                onAcabou = {
                    Repositorio.marcarAcabou(ingrediente.id)
                    Toast.makeText(contexto, "${ingrediente.nome}: marcado como acabou", Toast.LENGTH_SHORT).show()
                },
                onRemover = { Repositorio.removerIngrediente(ingrediente) }
            )
        }

        // Espaço final para o último cartão não colar na barra de navegação
        item { Spacer(Modifier.height(8.dp)) }
    }
}

/**
 * Cartão de um ingrediente.
 *
 * combinedClickable (AULA de gestos/Compose): permite reagir a CLIQUE e a CLIQUE LONGO
 * no mesmo elemento. Ele fica no Row (dentro do Card) para o efeito de toque respeitar
 * os cantos arredondados. A lixeira é um IconButton, que captura o próprio clique —
 * por isso tocar nela remove o item e NÃO abre os Detalhes.
 */
@Composable
private fun CartaoIngrediente(
    ingrediente: Ingrediente,
    onAbrir: () -> Unit,
    onAcabou: () -> Unit,
    onRemover: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (ingrediente.acabou)
                MaterialTheme.colorScheme.errorContainer
            else
                MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onAbrir,
                    onLongClick = onAcabou
                )
                .padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ingrediente.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (ingrediente.acabou) "Acabou" else "${ingrediente.quantidade} ${ingrediente.unidade}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (ingrediente.acabou) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onRemover) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Remover ingrediente",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
