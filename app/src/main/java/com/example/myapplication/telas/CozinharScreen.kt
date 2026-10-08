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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Repositorio

/*
 * TELA 5 de 8 — MODO COZINHAR  (ACOMPANHAMENTO / PROGRESSO — Trabalho 1)
 *
 * AULA 7 + lista de tarefas feita em aula: Checkbox, mutableStateListOf, remember
 * REQUISITO Trabalho 1: "Acompanhamento/Progresso"
 * REQUISITO 3.1       : botões chamando popBackStack() / navigate de verdade
 *
 * O que faz: acompanha o andamento da receita passo a passo. Cada passo tem um
 * Checkbox; a barra de progresso e o texto "Passo X de Y" são CALCULADOS a partir
 * de quantos passos já foram marcados. O botão "Concluir próximo passo" marca o
 * primeiro passo pendente (alternativa a tocar nas caixinhas).
 *
 * O progresso mora só nesta tela (remember): ao sair, a receita "reinicia".
 * Isso é de propósito — guardar o andamento é assunto de persistência (próximo trabalho).
 */
@Composable
fun CozinharScreen(navController: NavController, receitaId: Int) {
    val receita = Repositorio.buscarReceita(receitaId)
    if (receita == null) {
        TelaVazia("Receita não encontrada.", "Voltar") { navController.popBackStack() }
        return
    }

    // Uma lista reativa de true/false: concluidos[i] diz se o passo i já foi feito.
    // Começa com todos false. `remember(receitaId)` recria a lista se mudar de receita.
    val concluidos = remember(receitaId) {
        mutableStateListOf<Boolean>().apply { repeat(receita.passos.size) { add(false) } }
    }

    // Valores calculados (mudam sozinhos quando um Checkbox é marcado)
    val feitos = concluidos.count { it }
    val total = receita.passos.size
    val fracao = if (total == 0) 0f else feitos.toFloat() / total
    val percentual = (fracao * 100).toInt()
    val terminou = total > 0 && feitos == total

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        Text(
            text = receita.nome,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // ---------- Progresso ----------
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$feitos de $total passos concluídos ($percentual%)",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { fracao },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                )
            }
        }

        // ---------- Passos com Checkbox ----------
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            receita.passos.forEachIndexed { indice, passo ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (concluidos[indice])
                            MaterialTheme.colorScheme.secondaryContainer
                        else
                            MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = concluidos[indice],
                            onCheckedChange = { marcado -> concluidos[indice] = marcado }
                        )
                        Text(
                            text = "${indice + 1}. $passo",
                            modifier = Modifier.weight(1f),
                            // Passo feito fica riscado
                            textDecoration = if (concluidos[indice]) TextDecoration.LineThrough else TextDecoration.None
                        )
                    }
                }
            }
        }

        // ---------- Ações ----------
        if (terminou) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Receita concluída! Bom apetite!",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Voltar para a receita")
            }
            OutlinedButton(
                onClick = { for (i in concluidos.indices) concluidos[i] = false },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Recomeçar")
            }
        } else {
            Button(
                onClick = {
                    // Marca o primeiro passo que ainda está pendente
                    val proximo = concluidos.indexOfFirst { !it }
                    if (proximo >= 0) concluidos[proximo] = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Concluir próximo passo")
            }
        }
    }
}
