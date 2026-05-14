# KMP Feature Template

Use this as the default starting point for new shared KMP features.

Primary location:

- `composeApp/src/commonMain/kotlin/com/habit/gold/feature/<feature-name>/`

## Folder Shape

```text
feature/<feature-name>/
  presentation/
    components/
    <Feature>Contract.kt
    <Feature>ViewModel.kt
    <Feature>Screen.kt
  domain/
    model/
    repository/
    usecase/
  data/
    model/
    remote/
    local/
    mapper/
    repository/
```

## Contract Template

```kotlin
package com.habit.gold.feature.example.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState

data class ExampleState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
) : MviState

sealed interface ExampleIntent : MviIntent {
    data object Load : ExampleIntent
    data object Retry : ExampleIntent
}

sealed interface ExampleEffect : MviEffect
```

## ViewModel Template

```kotlin
package com.habit.gold.feature.example.presentation

import com.habit.gold.core.presentation.mvi.MviViewModel

class ExampleViewModel : MviViewModel<ExampleState, ExampleIntent, ExampleEffect>(
    initialState = ExampleState(),
) {
    override fun onIntent(intent: ExampleIntent) {
        when (intent) {
            ExampleIntent.Load -> {
                updateState { it.copy(isLoading = true, errorMessage = null) }
            }
            ExampleIntent.Retry -> {
                updateState { it.copy(isLoading = true, errorMessage = null) }
            }
        }
    }
}
```

## Screen Template

```kotlin
package com.habit.gold.feature.example.presentation

import androidx.compose.runtime.Composable

@Composable
fun ExampleScreen(
    state: ExampleState,
    onIntent: (ExampleIntent) -> Unit,
) {
    // Build the screen from state and dispatch user actions through intents.
}
```

## Rules

- Keep feature DTOs out of presentation state.
- Move reusable UI into `presentation/components/` early.
- Split files before they become hard to scan.
- Add tests with the feature, not later.
- Do not add platform-specific logic to shared feature code.
