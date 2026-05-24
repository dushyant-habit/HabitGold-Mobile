package com.habit.gold.feature.delivery.presentation.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.habit.gold.core.designsystem.theme.AppColors
import com.habit.gold.feature.delivery.domain.model.SavedAddress
import com.habit.gold.feature.delivery.domain.model.compactAddressLine
import com.habit.gold.feature.delivery.domain.model.isPincodeServiceable
import habitgoldmobile.composeapp.generated.resources.Res
import habitgoldmobile.composeapp.generated.resources.delivery_address_add_new
import habitgoldmobile.composeapp.generated.resources.delivery_address_check_serviceability
import habitgoldmobile.composeapp.generated.resources.delivery_address_delete_action
import habitgoldmobile.composeapp.generated.resources.delivery_address_edit
import habitgoldmobile.composeapp.generated.resources.delivery_address_no_addresses
import habitgoldmobile.composeapp.generated.resources.delivery_address_serviceable
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyAddressState(onAddNewAddress: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            tint = AppColors.Purple200,
            modifier = Modifier.size(72.dp),
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(Res.string.delivery_address_no_addresses),
            fontSize = 15.sp,
            color = AppColors.Slate600,
            textAlign = TextAlign.Center,
        )
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onAddNewAddress,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Purple700),
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text(stringResource(Res.string.delivery_address_add_new))
        }
    }
}

@Composable
internal fun DeliveryAddressCard(
    address: SavedAddress,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onCheckServiceability: () -> Unit,
) {
    val isServiceable = address.isPincodeServiceable()
    val borderColor = if (isSelected) AppColors.Purple700 else AppColors.Divider

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() },
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        colors = CardDefaults.cardColors(containerColor = AppColors.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(
                    selected = isSelected,
                    onClick = onSelect,
                    colors = RadioButtonDefaults.colors(selectedColor = AppColors.Purple700),
                )
                Spacer(Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = address.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = AppColors.Slate900,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = address.phoneNo,
                        fontSize = 13.sp,
                        color = AppColors.Slate500,
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = stringResource(Res.string.delivery_address_edit),
                            tint = AppColors.Slate500,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.delivery_address_delete_action),
                            tint = AppColors.Danger,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }

            Text(
                text = address.compactAddressLine(),
                fontSize = 13.sp,
                color = AppColors.Slate600,
                modifier = Modifier.padding(start = 48.dp),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
            )

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 40.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (isServiceable) {
                    ServiceableChip()
                } else {
                    OutlinedButton(
                        onClick = onCheckServiceability,
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AppColors.Slate300),
                    ) {
                        Text(
                            stringResource(Res.string.delivery_address_check_serviceability),
                            fontSize = 12.sp,
                            color = AppColors.Slate700,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ServiceableChip() {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(AppColors.Green100)
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = null,
            tint = AppColors.Green600,
            modifier = Modifier.size(14.dp),
        )
        Text(
            text = stringResource(Res.string.delivery_address_serviceable),
            fontSize = 12.sp,
            color = AppColors.Green700,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
