@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.telas

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas
import com.example.myapplication.irParaAba

/*
 * TELA 4 de 8 — DETALHES DA RECEITA
 *
 * AULA 13 e 14 (Navigation): recebe o argumento da rota (receitaId) e busca o item certo.
 * REQUISITOS 3.2: tela de Detalhes própria, mostrando o item ESPECÍFICO.
 *
 * ===== COMPLEXIDADE EXTRA (o "passo a mais" pedido na seção 3.2) =====
 * Esta tela faz bem mais do que reexibir os campos da receita:
 *   1. COMBINA DADOS DAS DUAS LISTAS: compara os ingredientes da receita com a
 *      Despensa e mostra, ingrediente por ingrediente, se você tem ou não.
 *   2. INFORMAÇÃO CALCULADA: "você tem X de Y ingredientes" + barra de progresso (percentual).
 *   3. AÇÃO QUE ALTERA A OUTRA LISTA: o botão "Comprei" coloca o ingrediente que faltava
 *      na Despensa, e a tela se atualiza na hora (a barra sobe, o item vira ✔).
 *   4. NAVEGAÇÃO SECUNDÁRIA: botão que abre o "Modo cozinhar" (tela 5) e outro que leva à Despensa.
 * Escolhemos isso porque é o que faz sentido num app de culinária: antes de cozinhar,
 * a pergunta real é "eu tenho tudo o que preciso?".
 */
@Composable
fun DetalheReceitaScreen(navController: NavController, receitaId: Int) {
    // Procura a receita pelo id que veio na rota. Pode ser null (ex.: id inválido).
    val receita = Repositorio.buscarReceita(receitaId)
    if (receita == null) {
        TelaVazia("Receita não encontrada.", "Voltar") { navController.popBackStack() }
        return
    }

    // Informações CALCULADAS a partir dos dados das duas listas
    val faltam = Repositorio.ingredientesQueFaltam(receita)
    val total = receita.ingredientes.size
    val tenho = total - faltam.size
    val fracao = if (total == 0) 0f else tenho.toFloat() / total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------- Cabeçalho com os dados da receita ----------
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = receita.nome,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    IconButton(onClick = { Repositorio.alternarFavorita(receita.id) }) {
                        Icon(
                            imageVector = if (receita.favorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favoritar",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                if (receita.descricao.isNotBlank()) {
                    Text(receita.descricao, style = MaterialTheme.typography.bodyMedium)
                    Spacer(Modifier.height(8.dp))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    EtiquetaDificuldade(receita.dificuldade)
                    Spacer(Modifier.width(12.dp))
                    Text("${receita.tempoPreparoMin} min • ${receita.porcoes} porções")
                }
            }
        }

        // ---------- Cruzamento com a Despensa (informação calculada) ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (faltam.isEmpty())
                    MaterialTheme.colorScheme.secondaryContainer
                else
                    MaterialTheme.colorScheme.surfaceContainerHigh
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Você tem $tenho de $total ingredientes",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { fracao },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (faltam.isEmpty())
                        "Tudo certo: dá para cozinhar agora!"
                    else
                        "Faltam: ${faltam.joinToString(", ")}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // ---------- Lista de ingredientes, cada um marcado como "tenho" ou "falta" ----------
        TituloSecao("Ingredientes")
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            receita.ingredientes.forEach { nomeIngrediente ->
                val temEste = Repositorio.temNaDespensa(nomeIngrediente)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (temEste) Icons.Default.Check else Icons.Default.Close,
                        contentDescription = if (temEste) "Tem na despensa" else "Falta",
                        tint = if (temEste) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(nomeIngrediente, modifier = Modifier.weight(1f))
                    if (!temEste) {
                        // Altera a OUTRA lista (despensa) a partir desta tela
                        TextButton(onClick = { Repositorio.comprarIngrediente(nomeIngrediente) }) {
                            Text("Comprei")
                        }
                    }
                }
            }
        }

        // ---------- Modo de preparo ----------
        TituloSecao("Modo de preparo")
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            receita.passos.forEachIndexed { indice, passo ->
                Text("${indice + 1}. $passo", style = MaterialTheme.typography.bodyLarge)
            }
        }

        // ---------- Botões de navegação ----------
        Button(
            onClick = { navController.navigate(Rotas.cozinhar(receita.id)) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Começar a cozinhar")
        }
        OutlinedButton(
            onClick = { navController.irParaAba(Rotas.DESPENSA) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Ver minha despensa")
        }
    }
}
