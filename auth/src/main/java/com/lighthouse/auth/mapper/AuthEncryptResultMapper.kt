package com.lighthouse.auth.mapper

import com.lighthouse.auth.model.AuthEncryptData
import com.lighthouse.beep.model.auth.EncryptData

internal fun AuthEncryptData.toDomain(): EncryptData {
    return EncryptData(data, iv)
}
