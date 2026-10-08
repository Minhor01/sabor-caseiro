@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.myapplication.telas

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.myapplication.Dificuldades
import com.example.myapplication.Repositorio
import com.example.myapplication.Rotas
import com.example.myapplication.irParaAba

/*
 * TELA 1 de 8 — INÍCIO (aba)
 *
 * AULA 9 a 12 : Row / Column / Box, Card, estilização
 * AULA 7      : Compose (Canvas e estado)
 * REQUISITOS  : Trabalho 1 -> CANVAS (o prato e o gráfico de rosca são desenhados com Canvas)
 *               3.1        -> todo botão chama navController.navigate(...) de verdade
 *
 * O que a tela faz: resume o app (quantas receitas, ingredientes, favoritas),
 * mostra um gráfico das receitas por dificuldade e sugere o que dá para cozinhar
 * agora com o que há na despensa. Os números e o gráfico leem as listas reativas,
 * então mudam sozinhos quando você adiciona ou remove algo em outra tela.
 */
@Composable
fun InicioScreen(navController: NavController) {
    val receitas = Repositorio.receitas
    val prontas = receitas.filter { Repositorio.podeCozinhar(it) }
    // Lista de pares (dificuldade, quantidade): ex.: ("Fácil", 3), ("Média", 1), ("Difícil", 1)
    val contagens = Dificuldades.todas.map { d -> d to receitas.count { it.dificuldade == d } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // ---------- Cartão de boas-vindas (com o desenho do prato em Canvas) ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PratoCanvas(Modifier.size(100.dp))
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Bem-vindo(a)!",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Planeje, cozinhe e acompanhe suas receitas.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(Modifier.height(8.dp))
                    Button(onClick = { navController.navigate(Rotas.NOVA_RECEITA) }) {
                        Text("Nova receita")
                    }
                }
            }
        }

        // ---------- Três atalhos com números (cada um leva para uma aba) ----------
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            CartaoNumero("Receitas", receitas.size, Modifier.weight(1f)) {
                navController.irParaAba(Rotas.RECEITAS)
            }
            CartaoNumero("Despensa", Repositorio.ingredientes.size, Modifier.weight(1f)) {
                navController.irParaAba(Rotas.DESPENSA)
            }
            CartaoNumero("Favoritas", receitas.count { it.favorita }, Modifier.weight(1f)) {
                navController.irParaAba(Rotas.FAVORITAS)
            }
        }

        // ---------- Gráfico de rosca desenhado com Canvas ----------
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TituloSecao("Receitas por dificuldade")
                Spacer(Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        GraficoDificuldade(contagens, Modifier.fillMaxSize())
                        Text(
                            text = "${receitas.size}\nreceitas",
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                    Spacer(Modifier.width(24.dp))
                    // Legenda
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        contagens.forEach { (dificuldade, quantidade) ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .clip(CircleShape)
                                        .background(corDaDificuldade(dificuldade))
                                )
                                Spacer(Modifier.width(8.dp))
                                Text("$dificuldade: $quantidade")
                            }
                        }
                    }
                }
            }
        }

        // ---------- Sugestões: receitas que já dá para fazer ----------
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TituloSecao("Você já pode cozinhar")
                Spacer(Modifier.height(8.dp))
                if (prontas.isEmpty()) {
                    Text("Nenhuma receita está completa com a sua despensa agora. Abra uma receita para ver o que falta.")
                } else {
                    prontas.forEach { receita ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = receita.nome,
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium
                            )
                            TextButton(onClick = { navController.navigate(Rotas.detalheReceita(receita.id)) }) {
                                Text("Ver receita")
                            }
                        }
                    }
                }
            }
        }
    }
}

/** Cartão pequeno com um número grande e um rótulo; clicável. */
@Composable
private fun CartaoNumero(
    rotulo: String,
    valor: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(text = rotulo, style = MaterialTheme.typography.labelLarge)
        }
    }
}

/*
 * CANVAS (Trabalho 1)
 *
 * O Canvas é uma "folha em branco" onde desenhamos com comandos: drawCircle,
 * drawLine, drawArc, drawOval... As posições são em pixels dentro do espaço
 * que o Canvas ocupa; por isso usamos proporções de `size` (largura/altura)
 * em vez de números fixos — o desenho escala junto com o tamanho do Canvas.
 */

/** Desenho de um prato com garfo e colher. */
@Composable
fun PratoCanvas(modifier: Modifier = Modifier) {
    val corBorda = MaterialTheme.colorScheme.primary
    val corPrato = MaterialTheme.colorScheme.surface
    val corTalher = MaterialTheme.colorScheme.onTertiaryContainer

    Canvas(modifier = modifier) {
        val raio = size.minDimension / 3f
        val espessura = size.minDimension * 0.045f

        // Prato: círculo externo, círculo interno claro e um "fundo" mais suave
        drawCircle(color = corBorda, radius = raio, center = center)
        drawCircle(color = corPrato, radius = raio * 0.82f, center = center)
        drawCircle(color = corBorda.copy(alpha = 0.25f), radius = raio * 0.55f, center = center)

        // Garfo (à esquerda): cabo + base + 3 dentes
        val xGarfo = size.width * 0.07f
        drawLine(
            color = corTalher,
            start = Offset(xGarfo, size.height * 0.45f),
            end = Offset(xGarfo, size.height * 0.85f),
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )
        drawLine(
            color = corTalher,
            start = Offset(xGarfo - size.width * 0.04f, size.height * 0.45f),
            end = Offset(xGarfo + size.width * 0.04f, size.height * 0.45f),
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )
        for (deslocamento in listOf(-0.04f, 0f, 0.04f)) {
            drawLine(
                color = corTalher,
                start = Offset(xGarfo + size.width * deslocamento, size.height * 0.15f),
                end = Offset(xGarfo + size.width * deslocamento, size.height * 0.45f),
                strokeWidth = espessura * 0.8f,
                cap = StrokeCap.Round
            )
        }

        // Colher (à direita): concha oval + cabo
        val xColher = size.width * 0.93f
        drawOval(
            color = corTalher,
            topLeft = Offset(xColher - size.width * 0.05f, size.height * 0.15f),
            size = Size(size.width * 0.10f, size.height * 0.28f)
        )
        drawLine(
            color = corTalher,
            start = Offset(xColher, size.height * 0.43f),
            end = Offset(xColher, size.height * 0.85f),
            strokeWidth = espessura,
            cap = StrokeCap.Round
        )
    }
}

/**
 * Gráfico de rosca (donut): cada fatia é um drawArc cujo ângulo é proporcional
 * à quantidade de receitas daquela dificuldade. Começa em -90° (topo do círculo).
 */
@Composable
fun GraficoDificuldade(contagens: List<Pair<String, Int>>, modifier: Modifier = Modifier) {
    val total = contagens.sumOf { it.second }

    Canvas(modifier = modifier) {
        val espessura = size.minDimension * 0.22f
        val diametro = size.minDimension - espessura
        val canto = Offset((size.width - diametro) / 2f, (size.height - diametro) / 2f)
        val tamanho = Size(diametro, diametro)

        if (total == 0) {
            // Sem receitas: anel cinza
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = canto,
                size = tamanho,
                style = Stroke(width = espessura)
            )
        } else {
            var anguloInicial = -90f
            contagens.forEach { (dificuldade, quantidade) ->
                val varredura = 360f * quantidade / total
                drawArc(
                    color = corDaDificuldade(dificuldade),
                    startAngle = anguloInicial,
                    sweepAngle = varredura,
                    useCenter = false,
                    topLeft = canto,
                    size = tamanho,
                    style = Stroke(width = espessura)
                )
                anguloInicial += varredura
            }
        }
    }
}
