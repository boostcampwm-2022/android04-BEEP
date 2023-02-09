package com.lighthouse.utils.recognizer.recognizer.kakao

import com.lighthouse.utils.recognizer.recognizer.TemplateRecognizer

internal class KakaoRecognizer : TemplateRecognizer() {

    override val parser = KakaoParser()

    override val processor = KakaoProcessor()
}
