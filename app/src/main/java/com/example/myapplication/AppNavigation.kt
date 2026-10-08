package com.example.myapplication

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapplication.telas.CozinharScreen
import com.example.myapplication.telas.DespensaScreen
import com.example.myapplication.telas.DetalheIngredienteScreen
import com.example.myapplication.telas.DetalheReceitaScreen
import com.example.myapplication.telas.FavoritasScreen
import com.example.myapplication.telas.InicioScreen
import com.example.myapplication.telas.NovaReceitaScreen
import com.example.myapplication.telas.ReceitasScreen

/*
 * AULA 13 e 14 - Navigation Compose  +  AULA 8 - Mudar Tela
 * REQUISITOS 3.1: NavHost central, NavigationBar, TopAppBar com voltar, Scaffold
 *
 * DECISÃO DE ORGANIZAÇÃO (para a documentação):
 * existe UM único Scaffold, aqui. Ele monta:
 *   - topBar    : título que muda conforme a tela + botão de voltar nas telas secundárias
 *   - bottomBar : NavigationBar, que só aparece nas 4 telas principais (abas)
 *   - conteúdo  : o NavHost, que troca a tela exibida
 * Assim as telas em si ficam simples (só o conteúdo) e não precisam repetir
 * TopAppBar/NavigationBar cada uma.
 *
 * Mapa das 8 telas:
 *   Abas      : INICIO, RECEITAS, DESPENSA, FAVORITAS
 *   Secundárias: NOVA_RECEITA, DETALHE_RECEITA/{id}, COZINHAR/{id}, DETALHE_INGREDIENTE/{id}
 */

// Um item da barra de baixo: para onde vai, o texto e o ícone.
private data class ItemMenu(val rota: String, val rotulo: String, val icone: ImageVector)

@OptIn(ExperimentalMaterial3Api::class) // CenterAlignedTopAppBar ainda é marcada como experimental
@Composable
fun AppNavigation() {
    // NavController: o "controle remoto" da navegação. Criado uma vez e lembrado (remember).
    val navController = rememberNavController()

    // Observa em qual tela estamos. Quando a tela muda, esta variável muda e o Scaffold se redesenha.
    val entradaAtual by navController.currentBackStackEntryAsState()
    val rotaAtual = entradaAtual?.destination?.route

    val abas = listOf(
        ItemMenu(Rotas.INICIO, "Início", Icons.Default.Home),
        ItemMenu(Rotas.RECEITAS, "Receitas", Icons.AutoMirrored.Filled.List),
        ItemMenu(Rotas.DESPENSA, "Despensa", Icons.Default.ShoppingCart),
        ItemMenu(Rotas.FAVORITAS, "Favoritas", Icons.Default.Favorite)
    )

    // Nas abas: mostra a barra de baixo e esconde o botão de voltar.
    // (rotaAtual é null só no primeiro instante; tratamos como aba para não piscar.)
    val estaEmAba = rotaAtual == null || abas.any { it.rota == rotaAtual }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = tituloDaRota(rotaAtual),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    if (!estaEmAba) {
                        // REQUISITO: botão de voltar funcional com popBackStack()
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Voltar"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            if (estaEmAba) {
                // REQUISITO: BottomNavigation/NavigationBar funcionando de verdade
                NavigationBar {
                    abas.forEach { item ->
                        NavigationBarItem(
                            selected = rotaAtual == item.rota,
                            onClick = { navController.irParaAba(item.rota) },
                            icon = { Icon(item.icone, contentDescription = item.rotulo) },
                            label = { Text(item.rotulo) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // innerPadding é o espaço ocupado pela topBar/bottomBar; sem ele o conteúdo ficaria escondido atrás delas.
        NavHost(
            navController = navController,
            startDestination = Rotas.INICIO,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
        ) {
            // --- Abas ---
            composable(Rotas.INICIO) { InicioScreen(navController) }
            composable(Rotas.RECEITAS) { ReceitasScreen(navController) }
            composable(Rotas.DESPENSA) { DespensaScreen(navController) }
            composable(Rotas.FAVORITAS) { FavoritasScreen(navController) }

            // --- Telas secundárias ---
            composable(Rotas.NOVA_RECEITA) { NovaReceitaScreen(navController) }

            // Rotas com ARGUMENTO: o id do item viaja na rota e é lido aqui, no `entrada.arguments`.
            // A tela de Detalhes recebe só o id e usa Repositorio.buscarReceita(id) para achar o item certo.
            composable(
                route = Rotas.DETALHE_RECEITA,
                arguments = listOf(navArgument(Rotas.ARG_RECEITA_ID) { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt(Rotas.ARG_RECEITA_ID) ?: -1
                DetalheReceitaScreen(navController, id)
            }
            composable(
                route = Rotas.COZINHAR,
                arguments = listOf(navArgument(Rotas.ARG_RECEITA_ID) { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt(Rotas.ARG_RECEITA_ID) ?: -1
                CozinharScreen(navController, id)
            }
            composable(
                route = Rotas.DETALHE_INGREDIENTE,
                arguments = listOf(navArgument(Rotas.ARG_INGREDIENTE_ID) { type = NavType.IntType })
            ) { entrada ->
                val id = entrada.arguments?.getInt(Rotas.ARG_INGREDIENTE_ID) ?: -1
                DetalheIngredienteScreen(navController, id)
            }
        }
    }
}

/**
 * Navegação entre ABAS. Usada pela barra de baixo e pelos atalhos da tela Início.
 *
 * - popUpTo(INICIO) { saveState }: ao trocar de aba, volta o histórico até o Início
 *   (assim o botão "voltar" do celular não fica andando por 20 telas) e guarda o estado da aba.
 * - launchSingleTop: tocar na aba em que já estamos não empilha uma cópia da mesma tela.
 * - restoreState: ao voltar para uma aba, ela reaparece como foi deixada.
 */
fun NavController.irParaAba(rota: String) {
    navigate(rota) {
        popUpTo(Rotas.INICIO) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

/** Título mostrado na TopAppBar para cada rota. */
private fun tituloDaRota(rota: String?): String = when (rota) {
    Rotas.INICIO -> "Sabor Caseiro"
    Rotas.RECEITAS -> "Receitas"
    Rotas.DESPENSA -> "Despensa"
    Rotas.FAVORITAS -> "Favoritas"
    Rotas.NOVA_RECEITA -> "Nova receita"
    Rotas.DETALHE_RECEITA -> "Detalhes da receita"
    Rotas.COZINHAR -> "Modo cozinhar"
    Rotas.DETALHE_INGREDIENTE -> "Detalhes do ingrediente"
    else -> "Sabor Caseiro"
}
