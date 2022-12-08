package com.lighthouse.util.recognizer.recognizer.smilecon

import com.lighthouse.util.recognizer.recognizer.TemplateRecognizer

class SmileConRecognizer : TemplateRecognizer() {

    override val parser = SmileConParser()

    override val processor = SmileConProcessor()
}
