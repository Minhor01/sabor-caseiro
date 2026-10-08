# Sabor Caseiro — app de culinária (Jetpack Compose)

Trabalho 2 — MAF (Mínimo Aplicativo Funcional) da disciplina Desenvolvimento de Aplicativos Móveis.
Inclui as 3 telas evoluídas do Trabalho 1 (com **Canvas** e **Acompanhamento/Progresso**) mais as telas novas do Trabalho 2.

## Como rodar o projeto

1. Crie (ou abra) o projeto no Android Studio como **Empty Activity** (Jetpack Compose), com:
   - Package name: `com.example.myapplication` (se usar outro, troque a linha `package` e os `import com.example.myapplication...` dos arquivos)
   - Minimum SDK: API 24, Kotlin DSL
2. Copie a pasta `app/src/main/java/com/example/myapplication/` deste repositório por cima da do seu projeto
   (substitui `MainActivity.kt` e `ui/theme/Theme.kt`; pode apagar `Color.kt` e `Type.kt` do template, não são mais usados).
3. Adicione a dependência do Navigation Compose em `app/build.gradle.kts`, dentro de `dependencies { ... }`:

   ```kotlin
   implementation("androidx.navigation:navigation-compose:2.10.2")
   ```

   (Se o Android Studio reclamar da versão, use a que ele sugerir ou `2.9.0`.) Clique em **Sync Now**.
   Se o projeto usa *version catalog* (`libs.versions.toml`), a forma equivalente é:
   ```toml
   [versions]
   navigationCompose = "2.10.2"
   [libraries]
   androidx-navigation-compose = { group = "androidx.navigation", name = "navigation-compose", version.ref = "navigationCompose" }
   ```
   e em `build.gradle.kts`: `implementation(libs.androidx.navigation.compose)`.
4. Escolha o emulador (ex.: Pixel 9 Pro, API 36) e clique em **Run**.

Nenhuma outra dependência é necessária (os ícones usados vêm do Material 3 que o template já inclui).

## Estrutura do código

```
MainActivity.kt        entrada do app (tema + AppNavigation)
AppNavigation.kt       Scaffold único: TopAppBar, NavigationBar e NavHost com as 8 rotas
Rotas.kt               objeto Rotas (const val) + funções que montam rotas com id
Modelos.kt             data class Receita, data class Ingrediente, object Dificuldades
Repositorio.kt         as 2 listas reativas (mutableStateListOf) e as funções de adicionar/remover/cruzar
telas/                 as 8 telas + componentes reutilizáveis
ui/theme/Theme.kt      paleta de cores do app
```

## As 8 telas

| # | Tela | Tipo | O que faz |
|---|------|------|-----------|
| 1 | Início | Aba | Resumo, **Canvas** (prato + gráfico de rosca por dificuldade), atalhos, sugestões do que dá para cozinhar |
| 2 | Receitas | Aba / Lista 1 | LazyColumn + Card, busca, favoritar, **remover**, abre Detalhes |
| 3 | Nova receita | Formulário | **Adicionar** receita (campos de texto, numéricos, multilinha, chips) |
| 4 | Detalhes da receita | Detalhe 1 | Dados da receita + cruzamento com a despensa (ver abaixo) |
| 5 | Modo cozinhar | Progresso | **Acompanhamento/Progresso**: passos com Checkbox e barra de progresso |
| 6 | Despensa | Aba / Lista 2 | LazyColumn + Card, formulário inline para **adicionar**, **remover**, clique longo, abre Detalhes |
| 7 | Detalhes do ingrediente | Detalhe 2 | Edita a quantidade e lista as receitas que usam o ingrediente |
| 8 | Favoritas | Aba | Receitas favoritadas (mesma lista, filtrada) |

## Complexidade extra na tela de Detalhes (seção 3.2)

A tela **Detalhes da receita** combina dados das duas listas: compara os ingredientes da receita com a Despensa,
calcula "você tem X de Y ingredientes" (com barra de progresso), marca cada ingrediente como *tem/falta* e permite
"Comprei", que altera a lista da despensa e atualiza a tela na hora. Também abre o Modo cozinhar (navegação secundária).
A tela **Detalhes do ingrediente** edita a quantidade ali mesmo e mostra as receitas que usam o ingrediente.

## Requisitos x onde estão no código

| Requisito | Onde |
|-----------|------|
| No mínimo 7 telas navegáveis | 8 telas, todas em `AppNavigation.kt` |
| NavHost central + objeto `Rotas` com `const val` | `AppNavigation.kt`, `Rotas.kt` |
| NavigationBar funcionando | `AppNavigation.kt` (`bottomBar`, 4 abas) |
| Botões chamando `navController.navigate(...)` | todas as telas em `telas/` |
| TopAppBar com voltar (`popBackStack()`) | `AppNavigation.kt` (aparece nas telas secundárias) |
| 2 data classes | `Modelos.kt` (`Receita`, `Ingrediente`) |
| 2 listas com LazyColumn + Card + `mutableStateListOf` | `ReceitasScreen.kt`, `DespensaScreen.kt`, `Repositorio.kt` |
| Adicionar em cada lista | `NovaReceitaScreen.kt` (formulário), `DespensaScreen.kt` (formulário inline) |
| Remover em cada lista | lixeira nos cartões (`CartaoReceita`, `CartaoIngrediente`) |
| 2 telas de Detalhes com o item certo (id na rota) | `DetalheReceitaScreen.kt`, `DetalheIngredienteScreen.kt` |
| Detalhe com algo a mais | seção acima |
| Canvas (Trabalho 1) | `InicioScreen.kt` (`PratoCanvas`, `GraficoDificuldade`) |
| Acompanhamento/Progresso (Trabalho 1) | `CozinharScreen.kt` |
| Variedade de componentes | OutlinedTextField (texto, numérico, multilinha), FilterChip, Checkbox, `combinedClickable`, LinearProgressIndicator, Toast |

Todos os arquivos têm comentários no topo ligando o que foi feito à aula correspondente e ao requisito do enunciado.

## Documentação do processo e das decisões (seção 4)

> Escreva com as suas palavras e **cole prints/vídeo do app em cada etapa**. Texto sem imagem não vale.
> Abaixo estão os tópicos pedidos, com as decisões que o código já reflete (adapte ao que realmente aconteceu).

### 1. Como estava o projeto no Trabalho 1 e o que mudou
- Trabalho 1: 3 telas estilizadas com botões "de mentirinha" e dados fixos, mais Canvas e Progresso.
- Agora: navegação real (NavHost + NavigationBar), dados que se movem (listas reativas), formulários, detalhes por id.
- `[PRINT: telas do Trabalho 1]` `[PRINT: app atual]`

### 2. Por que essas telas novas
- **Receitas / Nova receita / Detalhes da receita:** o centro de um app de culinária.
- **Despensa / Detalhes do ingrediente:** segunda lista exigida; faz sentido porque toda receita depende de ingredientes.
- **Modo cozinhar:** o "Acompanhamento/Progresso", acompanha o preparo passo a passo.
- **Favoritas:** 4ª aba, mostra a mesma lista de receitas filtrada.
- `[PRINT de cada tela]`

### 3. Decisões de organização do código
- **Rotas:** todas num `object Rotas` com `const val`; rotas com id têm função auxiliar (`Rotas.detalheReceita(id)`).
- **NavHost:** um único `Scaffold` em `AppNavigation.kt` controla TopAppBar, NavigationBar e NavHost, assim as telas ficam só com o conteúdo.
- **Onde ficam as listas:** num `object Repositorio`, para que todas as telas usem as mesmas listas e elas sobrevivam à rotação da tela. A persistência real fica para o próximo trabalho.
- **Passagem de dados:** só o `id` viaja na rota; a tela de Detalhes usa `Repositorio.buscarXxx(id)` para achar o item certo.
- `[PRINT do código / da estrutura de pastas]`

### 4. Complexidade extra na tela de Detalhes
- Ver a seção "Complexidade extra" acima e por que foi escolhida (`[escreva com suas palavras]`).
- `[PRINT: detalhe de uma receita com ingredientes faltando]` `[PRINT: depois de clicar em "Comprei"]`

### 5. Dificuldades e como foram resolvidas
- `[escreva aqui]`

## Roteiro para a apresentação em sala
1. Aba **Receitas** -> "Nova" -> preencher e salvar -> a receita aparece na lista.
2. Remover uma receita pela lixeira.
3. Abrir os **Detalhes** de duas receitas diferentes (mostrar que o item muda) -> "Comprei" em um ingrediente.
4. Aba **Despensa** -> adicionar e remover ingredientes -> abrir os Detalhes de um ingrediente -> usar +1 / -1.
5. **Modo cozinhar** até 100%.
6. Navegar por todas as abas do BottomNavigation e usar o botão de voltar.
