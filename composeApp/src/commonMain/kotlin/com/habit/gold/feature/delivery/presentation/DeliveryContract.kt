package com.habit.gold.feature.delivery.presentation

import com.habit.gold.core.presentation.mvi.MviEffect
import com.habit.gold.core.presentation.mvi.MviIntent
import com.habit.gold.core.presentation.mvi.MviState
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutPhase
import com.habit.gold.feature.delivery.domain.model.DeliveryCheckoutQuote
import com.habit.gold.feature.delivery.domain.model.PhysicalCoin
import kotlin.math.roundToInt

data class DeliveryCatalogState(
    val isLoadingProducts: Boolean = false,
    val isRefreshingBalance: Boolean = false,
    val isCheckingOut: Boolean = false,
    
    val totalGoldBalanceGrams: Double = 0.0,
    val redeemableGoldGrams: Double = 0.0,
    val liveBuyPricePerGram: Double = 0.0,
    
    val coins: List<PhysicalCoin> = emptyList(),
    val cartItems: Map<String, Int> = emptyMap(), // coinId -> quantity
    
    val selectedAddressId: String? = null,
    
    val checkoutQuote: DeliveryCheckoutQuote? = null,
    val checkoutPhase: DeliveryCheckoutPhase = DeliveryCheckoutPhase.IDLE,
    val pendingPaymentSdkPayloadJson: String? = null,
    
    val couponCode: String? = null,
    val couponDiscountInr: Double = 0.0,
) : MviState {
    val totalCoinsSelected: Int get() = cartItems.values.sum()
    
    val totalWeightGm: Double get() = cartItems.mapNotNull { (id, qty) ->
        coins.find { it.id == id }?.let { it.weightGm * qty }
    }.sum()
    
    val netAmountPayable: Double get() = cartItems.mapNotNull { (id, qty) ->
        coins.find { it.id == id }?.let { it.makingCharge * qty }
    }.sum() - couponDiscountInr
    
    val isAddressEditingLocked: Boolean get() = checkoutPhase != DeliveryCheckoutPhase.IDLE && checkoutPhase != DeliveryCheckoutPhase.REVIEW_READY
}

sealed interface DeliveryIntent : MviIntent {
    data object LoadProducts : DeliveryIntent
    data class UpdateQuantity(val coinId: String, val delta: Int) : DeliveryIntent
    data class SelectAddress(val id: String) : DeliveryIntent
    data object RefreshGoldBalance : DeliveryIntent
    
    data class PrepareQuote(val couponCode: String? = null) : DeliveryIntent
    data object ConfirmOrder : DeliveryIntent
    data class HandlePaymentResult(val status: String, val payload: String? = null) : DeliveryIntent
    data object DiscardCheckout : DeliveryIntent
    data object ClearError : DeliveryIntent
    
    data class ApplyCoupon(val code: String) : DeliveryIntent
    data object RemoveCoupon : DeliveryIntent
}

sealed interface DeliveryEffect : MviEffect {
    data class ShowError(val message: String) : DeliveryEffect
    data class ShowToast(val message: String) : DeliveryEffect
    data object NavigateToCart : DeliveryEffect
    data object NavigateToCheckout : DeliveryEffect
    data class NavigateToOrderSummary(val orderId: String) : DeliveryEffect
    data class LaunchPaymentSdk(val payloadJson: String) : DeliveryEffect
    data object NavigateBack : DeliveryEffect
    data class OrderCompleted(val orderId: String) : DeliveryEffect
    data class NavigateToBuyGold(val shortfallGrams: Double) : DeliveryEffect
}
