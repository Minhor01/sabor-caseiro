@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.telas

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Dificuldades
import com.example.myapplication.Repositorio

/*
 * TELA 3 de 8 — NOVA RECEITA (formulário)
 *
 * AULA 7 + lista de tarefas feita em aula: TextField/OutlinedTextField + Button
 * REQUISITOS 3.2 : ADICIONAR um item pela UI (formulário com OutlinedTextField + Button)
 * 3.3 (variedade): campo numérico, campo de múltiplas linhas, FilterChip, Toast
 *
 * Cada campo é um estado (remember + mutableStateOf). Ao digitar, onValueChange
 * guarda o novo texto e a tela se redesenha mostrando o que foi digitado.
 * Ao salvar, validamos os dados; se estiver tudo certo, chamamos
 * Repositorio.adicionarReceita(...) e voltamos com popBackStack().
 */
@Composable
fun NovaReceitaScreen(navController: NavController) {
    val contexto = LocalContext.current // necessário para mostrar o Toast

    var nome by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var tempo by remember { mutableStateOf("") }
    var porcoes by remember { mutableStateOf("") }
    var dificuldade by remember { mutableStateOf(Dificuldades.FACIL) }
    var ingredientesTexto by remember { mutableStateOf("") }
    var passosTexto by remember { mutableStateOf("") }
    var erro by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()                          // sobe o conteúdo quando o teclado abre
            .verticalScroll(rememberScrollState()) // formulário longo: a tela rola
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        OutlinedTextField(
            value = nome,
            onValueChange = { nome = it },
            label = { Text("Nome da receita") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        // Campo de MÚLTIPLAS LINHAS
        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            minLines = 2,
            maxLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Campos NUMÉRICOS: teclado de números + filtro para aceitar só dígitos
            OutlinedTextField(
                value = tempo,
                onValueChange = { tempo = it.filter { c -> c.isDigit() } },
                label = { Text("Tempo (min)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = porcoes,
                onValueChange = { porcoes = it.filter { c -> c.isDigit() } },
                label = { Text("Porções") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }

        Text("Dificuldade", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Dificuldades.todas.forEach { opcao ->
                FilterChip(
                    selected = dificuldade == opcao,
                    onClick = { dificuldade = opcao },
                    label = { Text(opcao) }
                )
            }
        }

        OutlinedTextField(
            value = ingredientesTexto,
            onValueChange = { ingredientesTexto = it },
            label = { Text("Ingredientes (um por linha)") },
            supportingText = { Text("Use os mesmos nomes da Despensa para o app saber o que você já tem.") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = passosTexto,
            onValueChange = { passosTexto = it },
            label = { Text("Modo de preparo (um passo por linha)") },
            minLines = 4,
            modifier = Modifier.fillMaxWidth()
        )

        if (erro != null) {
            Text(
                text = erro!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    // Transforma o texto digitado em listas: uma linha = um item (ignora linhas vazias)
                    val ingredientes = ingredientesTexto.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    val passos = passosTexto.lines().map { it.trim() }.filter { it.isNotEmpty() }
                    val tempoNumero = tempo.toIntOrNull()
                    val porcoesNumero = porcoes.toIntOrNull()

                    // VALIDAÇÃO (condicionais - AULA 2.2)
                    if (nome.isBlank()) {
                        erro = "Dê um nome para a receita."
                    } else if (tempoNumero == null || tempoNumero <= 0) {
                        erro = "Informe o tempo de preparo em minutos."
                    } else if (porcoesNumero == null || porcoesNumero <= 0) {
                        erro = "Informe quantas porções a receita rende."
                    } else if (ingredientes.isEmpty()) {
                        erro = "Informe pelo menos um ingrediente."
                    } else if (passos.isEmpty()) {
                        erro = "Informe pelo menos um passo do preparo."
                    } else {
                        erro = null
                        // ADICIONAR de verdade na lista reativa
                        Repositorio.adicionarReceita(
                            nome = nome.trim(),
                            descricao = descricao.trim(),
                            tempoPreparoMin = tempoNumero,
                            porcoes = porcoesNumero,
                            dificuldade = dificuldade,
                            ingredientes = ingredientes,
                            passos = passos
                        )
                        Toast.makeText(contexto, "Receita salva!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack() // volta para a tela anterior
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Salvar receita")
            }
        }
    }
}
