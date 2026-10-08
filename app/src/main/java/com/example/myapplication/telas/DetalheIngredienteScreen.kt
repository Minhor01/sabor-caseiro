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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas

/*
 * TELA 7 de 8 — DETALHES DO INGREDIENTE
 *
 * AULA 13 e 14 (Navigation): recebe o id pela rota e busca o item certo.
 * REQUISITOS 3.2: tela de Detalhes própria (a 2ª, uma para cada lista).
 *
 * Também faz mais que reexibir campos:
 *   - EDITA o item ali mesmo: botões -1 / +1 / "Acabou" alteram a quantidade
 *     (Repositorio.alterarQuantidade) e a tela atualiza na hora;
 *   - COMBINA DADOS DAS DUAS LISTAS: mostra quais RECEITAS usam este ingrediente
 *     e, para cada uma, se já dá para cozinhar;
 *   - NAVEGAÇÃO SECUNDÁRIA: clicar numa dessas receitas abre os Detalhes da receita.
 */
@Composable
fun DetalheIngredienteScreen(navController: NavController, ingredienteId: Int) {
    val ingrediente = Repositorio.buscarIngrediente(ingredienteId)
    if (ingrediente == null) {
        TelaVazia("Ingrediente não encontrado.", "Voltar") { navController.popBackStack() }
        return
    }

    // Cruzamento com a lista de receitas
    val receitasQueUsam = Repositorio.receitasQueUsam(ingrediente.nome)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------- Dados do ingrediente + edição da quantidade ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (ingrediente.acabou)
                    MaterialTheme.colorScheme.errorContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = ingrediente.nome,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (ingrediente.acabou) "Acabou" else "${ingrediente.quantidade} ${ingrediente.unidade}",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = { Repositorio.alterarQuantidade(ingrediente.id, -1) }) {
                        Text("−1")
                    }
                    Button(onClick = { Repositorio.alterarQuantidade(ingrediente.id, 1) }) {
                        Text("+1")
                    }
                    OutlinedButton(onClick = { Repositorio.alterarQuantidade(ingrediente.id, 10) }) {
                        Text("+10")
                    }
                    OutlinedButton(onClick = { Repositorio.marcarAcabou(ingrediente.id) }) {
                        Text("Acabou")
                    }
                }
            }
        }

        // ---------- Receitas que usam este ingrediente (combina as duas listas) ----------
        TituloSecao("Usado em ${receitasQueUsam.size} receita(s)")

        if (receitasQueUsam.isEmpty()) {
            Text("Nenhuma receita usa este ingrediente ainda.")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                receitasQueUsam.forEach { receita ->
                    val faltam = Repositorio.ingredientesQueFaltam(receita)
                    Card(
                        onClick = { navController.navigate(Rotas.detalheReceita(receita.id)) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = receita.nome,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (faltam.isEmpty())
                                    "Dá para cozinhar agora"
                                else
                                    "Faltam ${faltam.size} ingrediente(s)",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        // ---------- Remover + voltar ----------
        Button(
            onClick = {
                Repositorio.removerIngrediente(ingrediente)
                navController.popBackStack() // o item não existe mais: volta para a lista
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("Remover da despensa")
        }
        OutlinedButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Voltar")
        }
    }
}
