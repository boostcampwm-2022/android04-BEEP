package com.lighthouse.common.recognizer.recognizer.syrup

import com.lighthouse.common.recognizer.recognizer.TemplateRecognizer

class SyrupRecognizer : TemplateRecognizer() {

    override val parser = SyrupParser()

    override val processor = SyrupProcessor()
}
