package com.lighthouse.beep.model.user

enum class SecurityOption(val text: String) {
    NONE("사용 안 함"),
    PIN("PIN"),
    FINGERPRINT("지문")
}
