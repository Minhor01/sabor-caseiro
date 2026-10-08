package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.myapplication.ui.theme.MyApplicationTheme

/*
 * AULA 5 e 6 - Intro Compose
 *
 * A MainActivity é a "porta de entrada" do app. Ela quase não faz nada:
 * liga o tema e entrega o resto para o AppNavigation(), que é quem decide
 * qual tela aparece. Toda a interface vive em funções @Composable.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // o app desenha também embaixo da barra de status/navegação
        setContent {
            MyApplicationTheme {
                AppNavigation()
            }
        }
    }
}