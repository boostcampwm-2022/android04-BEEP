package com.lighthouse.beep.model.exception.db

class NotFoundException(message: String? = "데이터를 찾을 수 없습니다.") : Exception(message)
