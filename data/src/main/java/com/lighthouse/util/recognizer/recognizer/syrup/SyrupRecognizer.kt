package com.lighthouse.util.recognizer.recognizer.syrup

import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer

class SyrupRecognizer : TemplateRecognizer() {

    override val parser = SyrupParser()

    override val processor = SyrupProcessor()
}
