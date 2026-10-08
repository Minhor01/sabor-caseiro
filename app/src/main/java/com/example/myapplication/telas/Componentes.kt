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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myapplication.Dificuldades
import com.example.myapplication.Receita

/*
 * AULA 9 a 12 - Exercícios de Compose (Row/Column/Box, Card, Icon, estilização)
 *
 * Componentes pequenos que são usados por mais de uma tela. Em Compose, quando
 * um pedaço de interface se repete, a gente cria uma função @Composable própria
 * e reaproveita (por isso o CartaoReceita aparece tanto em Receitas quanto em Favoritas).
 */

/** Cor de cada dificuldade. Usada na etiqueta do cartão e no gráfico (Canvas) da tela Início. */
fun corDaDificuldade(dificuldade: String): Color = when (dificuldade) {
    Dificuldades.FACIL -> Color(0xFF2E7D32)
    Dificuldades.MEDIA -> Color(0xFFF9A825)
    else -> Color(0xFFC62828)
}

/** Etiqueta arredondada com a dificuldade ("Fácil", "Média", "Difícil"). */
@Composable
fun EtiquetaDificuldade(dificuldade: String) {
    Surface(
        color = corDaDificuldade(dificuldade),
        shape = RoundedCornerShape(50)
    ) {
        Text(
            text = dificuldade,
            color = if (dificuldade == Dificuldades.MEDIA) Color.Black else Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * Cartão de uma receita para as listas (LazyColumn + Card).
 *
 * Repare que o cartão NÃO sabe navegar nem mexer na lista: ele recebe funções
 * (onAbrir, onFavoritar, onRemover) de quem o usa. Isso se chama "elevar o evento"
 * (state hoisting) e deixa o componente reaproveitável.
 *
 * - Clicar no cartão          -> onAbrir (abre os Detalhes)
 * - Clicar no coração         -> onFavoritar
 * - Clicar na lixeira         -> onRemover (se for null, a lixeira nem aparece)
 * Cada IconButton captura o seu próprio clique, então tocar no ícone NÃO abre os Detalhes.
 */
@Composable
fun CartaoReceita(
    receita: Receita,
    onAbrir: () -> Unit,
    onFavoritar: () -> Unit,
    onRemover: (() -> Unit)? = null
) {
    Card(
        onClick = onAbrir,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = receita.nome,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${receita.tempoPreparoMin} min • ${receita.porcoes} porções",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.height(6.dp))
                EtiquetaDificuldade(receita.dificuldade)
            }
            IconButton(onClick = onFavoritar) {
                Icon(
                    imageVector = if (receita.favorita) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = if (receita.favorita) "Desfavoritar" else "Favoritar",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            if (onRemover != null) {
                IconButton(onClick = onRemover) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remover receita",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

/** Tela/área vazia: mensagem centralizada e, opcionalmente, um botão de ação. */
@Composable
fun TelaVazia(
    mensagem: String,
    textoBotao: String? = null,
    onBotao: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        if (textoBotao != null && onBotao != null) {
            Spacer(Modifier.height(16.dp))
            Button(onClick = onBotao) { Text(textoBotao) }
        }
    }
}

/** Título de seção (ex.: "Ingredientes", "Modo de preparo"). */
@Composable
fun TituloSecao(texto: String) {
    Text(
        text = texto,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}
