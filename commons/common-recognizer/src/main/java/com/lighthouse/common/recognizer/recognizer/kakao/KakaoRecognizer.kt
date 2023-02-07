package com.lighthouse.common.recognizer.recognizer.kakao

import com.lighthouse.common.recognizer.recognizer.TemplateRecognizer

class KakaoRecognizer : TemplateRecognizer() {

    override val parser = KakaoParser()

    override val processor = KakaoProcessor()
}
