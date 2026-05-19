package com.habit.gold.feature.delivery.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class DeliveryDtosTest {

    @Test
    fun `toDomain preserves address type and landmark`() {
        val dto = DeliveryAddressDto(
            id = "address-1",
            type = "WORK",
            recipientName = "Dushyant",
            phoneNumber = "+919876543210",
            addressLine1 = "Tower 1",
            addressLine2 = "Business Park",
            city = "Gurugram",
            state = "Haryana",
            pincode = "122001",
            landmark = "Near metro station",
            verificationStatus = "PINCODE_SERVICEABLE",
        )

        val address = dto.toDomain()

        assertEquals(AddressType.WORK, address.type)
        assertEquals("Near metro station", address.landmark)
        assertEquals("PINCODE_SERVICEABLE", address.verificationStatus)
    }
}
