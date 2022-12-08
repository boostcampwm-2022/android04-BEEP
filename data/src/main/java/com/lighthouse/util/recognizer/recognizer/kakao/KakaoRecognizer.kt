package com.lighthouse.util.recognizer.recognizer.kakao

import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer

class KakaoRecognizer : TemplateRecognizer() {

    override val parser = KakaoParser()

    override val processor = KakaoProcessor()
}
