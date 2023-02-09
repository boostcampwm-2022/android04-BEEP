package com.lighthouse.data.database.exception

internal class DBNotFoundException(message: String = "데이터를 찾을 수 없습니다.") : Exception(message)
