package com.lighthouse.utils.recognizer.recognizer.smilecon

import com.lighthouse.utils.recognizer.recognizer.TemplateRecognizer

internal class SmileConRecognizer : TemplateRecognizer() {

    override val parser = SmileConParser()

    override val processor = SmileConProcessor()
}
