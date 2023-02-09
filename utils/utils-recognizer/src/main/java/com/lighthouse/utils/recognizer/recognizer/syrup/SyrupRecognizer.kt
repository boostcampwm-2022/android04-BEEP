package com.lighthouse.utils.recognizer.recognizer.syrup

import com.lighthouse.utils.recognizer.recognizer.TemplateRecognizer

internal class SyrupRecognizer : TemplateRecognizer() {

    override val parser = SyrupParser()

    override val processor = SyrupProcessor()
}
